import React, { useEffect, useMemo, useRef, useState } from "react";
import { MessageCircle, SendHorizontal, Users } from "lucide-react";
import { useAuth } from "../contexts/AuthContext";
import followService from "../services/followService";
import chatService from "../services/chatService";
import { userService } from "../services/userService";

const formatTimestamp = (value) => {
  if (!value) return "";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return "";
  }
  return date.toLocaleTimeString("vi-VN", {
    hour: "2-digit",
    minute: "2-digit",
  });
};

export default function ChatPage() {
  const { user } = useAuth();
  const [contacts, setContacts] = useState([]);
  const [selectedContactId, setSelectedContactId] = useState(null);
  const [messages, setMessages] = useState([]);
  const [draftMessage, setDraftMessage] = useState("");
  const [loadingContacts, setLoadingContacts] = useState(true);
  const [loadingMessages, setLoadingMessages] = useState(false);
  const [isConnected, setIsConnected] = useState(false);
  const [error, setError] = useState("");

  const selectedContactIdRef = useRef(selectedContactId);

  useEffect(() => {
    selectedContactIdRef.current = selectedContactId;
  }, [selectedContactId]);

  const selectedContact = useMemo(
    () => contacts.find((contact) => contact.id === selectedContactId) || null,
    [contacts, selectedContactId],
  );

  const currentUserId = Number(user?.id);
  const hasValidCurrentUserId = Number.isInteger(currentUserId) && currentUserId > 0;

  useEffect(() => {
    let cancelled = false;

    const loadContacts = async () => {
      setLoadingContacts(true);
      setError("");

      try {
        const followingResponse = await followService.getFollowing();
        let nextContacts = Array.isArray(followingResponse?.result)
          ? followingResponse.result
          : [];

        if (nextContacts.length === 0) {
          const mentorsResponse = await userService.getMentors();
          nextContacts = Array.isArray(mentorsResponse?.result)
            ? mentorsResponse.result.filter((mentor) => mentor.id !== currentUserId)
            : [];
        }

        if (cancelled) {
          return;
        }

        setContacts(nextContacts);
        setSelectedContactId((prev) => prev ?? nextContacts[0]?.id ?? null);
      } catch (loadError) {
        if (!cancelled) {
          setError(loadError?.message || "Khong the tai danh sach nguoi de chat");
        }
      } finally {
        if (!cancelled) {
          setLoadingContacts(false);
        }
      }
    };

    if (!user) {
      setLoadingContacts(false);
      setContacts([]);
      setSelectedContactId(null);
      return () => {
        cancelled = true;
      };
    }

    loadContacts();

    return () => {
      cancelled = true;
    };
  }, [user, currentUserId]);

  useEffect(() => {
    let cancelled = false;

    const loadConversation = async () => {
      if (!selectedContactId) {
        setMessages([]);
        return;
      }

      setLoadingMessages(true);
      setError("");

      try {
        const conversationResponse = await chatService.getConversation(selectedContactId);
        if (!cancelled) {
          setMessages(Array.isArray(conversationResponse?.result) ? conversationResponse.result : []);
        }
      } catch (conversationError) {
        if (!cancelled) {
          setError(conversationError?.message || "Khong the tai lich su hoi thoai");
        }
      } finally {
        if (!cancelled) {
          setLoadingMessages(false);
        }
      }
    };

    loadConversation();

    return () => {
      cancelled = true;
    };
  }, [selectedContactId]);

  useEffect(() => {
    if (!user?.token || !hasValidCurrentUserId) {
      return;
    }

    let unsubscribe = () => {};

    chatService.connect(user.token, {
      onConnect: () => {
        setIsConnected(true);
        setError("");

        unsubscribe = chatService.subscribeToUserMessages(currentUserId, (incomingMessage) => {
          const activeContactId = selectedContactIdRef.current;
          if (!activeContactId) {
            return;
          }

          const belongsToCurrentConversation =
            incomingMessage.senderId === activeContactId ||
            incomingMessage.receiverId === activeContactId;

          if (!belongsToCurrentConversation) {
            return;
          }

          setMessages((prev) => {
            if (prev.some((item) => item.id === incomingMessage.id)) {
              return prev;
            }
            return [...prev, incomingMessage];
          });
        });
      },
      onError: (socketError) => {
        setIsConnected(false);
        setError(socketError?.message || "Khong the ket noi websocket");
      },
    });

    return () => {
      unsubscribe();
      chatService.disconnect();
      setIsConnected(false);
    };
  }, [currentUserId, hasValidCurrentUserId, user?.token]);

  const handleSendMessage = () => {
    if (!selectedContactId) {
      setError("Vui long chon nguoi nhan truoc khi gui tin");
      return;
    }

    const content = draftMessage.trim();
    if (!content) {
      return;
    }

    try {
      chatService.sendPrivateMessage(selectedContactId, content);
      setDraftMessage("");
      setError("");
    } catch (sendError) {
      setError(sendError?.message || "Gui tin nhan that bai");
    }
  };

  const onPressEnter = (event) => {
    if (event.key === "Enter" && !event.shiftKey) {
      event.preventDefault();
      handleSendMessage();
    }
  };

  return (
    <div className="grid grid-cols-1 gap-4 md:grid-cols-12">
        <section className="md:col-span-4 lg:col-span-3 rounded-2xl border border-slate-200 bg-white shadow-sm overflow-hidden">
          <header className="px-4 py-3 border-b border-slate-200 flex items-center gap-2">
            <Users className="h-4 w-4 text-slate-500" />
            <h2 className="text-sm font-semibold text-slate-800">Nguoi co the chat</h2>
          </header>

          <div className="max-h-[65vh] overflow-y-auto">
            {loadingContacts ? (
              <p className="px-4 py-4 text-sm text-slate-500">Dang tai danh sach...</p>
            ) : !user ? (
              <p className="px-4 py-4 text-sm text-slate-500">
                Vui long dang nhap de su dung tinh nang chat.
              </p>
            ) : contacts.length === 0 ? (
              <p className="px-4 py-4 text-sm text-slate-500">
                Ban chua follow ai. Hay follow mentor de bat dau chat.
              </p>
            ) : (
              contacts.map((contact) => {
                const isActive = contact.id === selectedContactId;
                return (
                  <button
                    key={contact.id}
                    type="button"
                    onClick={() => setSelectedContactId(contact.id)}
                    className={`w-full flex items-center gap-3 px-4 py-3 text-left transition-colors ${
                      isActive ? "bg-[#f3efff]" : "hover:bg-slate-50"
                    }`}
                  >
                    <img
                      src={contact.avatarUrl || `https://i.pravatar.cc/100?u=${contact.userName}`}
                      alt={contact.fullName || contact.userName}
                      className="h-10 w-10 rounded-full object-cover"
                    />
                    <div className="min-w-0">
                      <p className="text-sm font-semibold text-slate-800 truncate">
                        {contact.fullName || contact.userName}
                      </p>
                      <p className="text-xs text-slate-500 truncate">@{contact.userName}</p>
                    </div>
                  </button>
                );
              })
            )}
          </div>
        </section>

        <section className="md:col-span-8 lg:col-span-9 rounded-2xl border border-slate-200 bg-white shadow-sm flex flex-col min-h-[65vh]">
          <header className="px-4 py-3 border-b border-slate-200 flex items-center justify-between">
            <div className="min-w-0">
              <h2 className="text-sm font-semibold text-slate-800 truncate">
                {selectedContact
                  ? `Chat voi ${selectedContact.fullName || selectedContact.userName}`
                  : "Chon nguoi de bat dau chat"}
              </h2>
              <p className="text-xs text-slate-500 mt-0.5">
                {isConnected ? "Da ket noi realtime" : "Dang ket noi realtime..."}
              </p>
            </div>
            <MessageCircle className="h-5 w-5 text-[#372660]" />
          </header>

          <div className="flex-1 overflow-y-auto px-4 py-4 space-y-3 bg-slate-50">
            {loadingMessages ? (
              <p className="text-sm text-slate-500">Dang tai tin nhan...</p>
            ) : messages.length === 0 ? (
              <p className="text-sm text-slate-500">Chua co tin nhan. Hay gui loi chao dau tien.</p>
            ) : (
              messages.map((message) => {
                const isMine = message.senderId === currentUserId;
                return (
                  <div
                    key={message.id}
                    className={`flex ${isMine ? "justify-end" : "justify-start"}`}
                  >
                    <div
                      className={`max-w-[78%] rounded-2xl px-3 py-2 shadow-sm ${
                        isMine
                          ? "bg-[#372660] text-white"
                          : "bg-white border border-slate-200 text-slate-800"
                      }`}
                    >
                      {!isMine && (
                        <p className="text-[11px] font-semibold text-slate-500 mb-1">
                          {message.senderFullName || message.senderUserName}
                        </p>
                      )}
                      <p className="text-sm leading-relaxed whitespace-pre-wrap">{message.content}</p>
                      <p
                        className={`mt-1 text-[10px] ${
                          isMine ? "text-indigo-200" : "text-slate-400"
                        }`}
                      >
                        {formatTimestamp(message.createdAt)}
                      </p>
                    </div>
                  </div>
                );
              })
            )}
          </div>

          <footer className="border-t border-slate-200 px-4 py-3">
            {error ? <p className="mb-2 text-xs text-red-600">{error}</p> : null}
            <div className="flex items-end gap-2">
              <textarea
                value={draftMessage}
                onChange={(event) => setDraftMessage(event.target.value)}
                onKeyDown={onPressEnter}
                rows={2}
                placeholder="Nhap tin nhan..."
                className="flex-1 resize-none rounded-xl border border-slate-300 bg-white px-3 py-2 text-sm text-slate-800 outline-none focus:border-[#372660] focus:ring-2 focus:ring-[#372660]/20"
              />
              <button
                type="button"
                onClick={handleSendMessage}
                disabled={!selectedContactId || !draftMessage.trim()}
                className="h-11 w-11 shrink-0 inline-flex items-center justify-center rounded-xl bg-[#372660] text-white disabled:opacity-50 disabled:cursor-not-allowed hover:bg-[#2b1d4c] transition-colors"
              >
                <SendHorizontal className="h-4 w-4" />
              </button>
            </div>
          </footer>
        </section>
    </div>
  );
}
