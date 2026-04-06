import { Client } from "@stomp/stompjs";
import api from "./api";

const API_URL = import.meta.env.VITE_API_URL || "http://localhost:8080/api";
const BACKEND_BASE_URL = API_URL.replace(/\/api\/?$/, "");
const WEBSOCKET_URL = `${BACKEND_BASE_URL.replace(/^http/i, "ws")}/ws`;

class ChatRealtimeService {
  constructor() {
    this.client = null;
    this.connected = false;
    this.subscriptions = [];
  }

  connect(token, { onConnect, onError } = {}) {
    if (this.client?.active) {
      return;
    }

    this.client = new Client({
      brokerURL: WEBSOCKET_URL,
      reconnectDelay: 4000,
      heartbeatIncoming: 10000,
      heartbeatOutgoing: 10000,
      connectHeaders: token ? { Authorization: `Bearer ${token}` } : {},
      debug: () => {},
      onConnect: (frame) => {
        this.connected = true;
        onConnect?.(frame);
      },
      onDisconnect: () => {
        this.connected = false;
      },
      onStompError: (frame) => {
        onError?.(
          new Error(frame.headers?.message || "STOMP broker reported an error"),
        );
      },
      onWebSocketError: () => {
        onError?.(new Error("WebSocket connection error"));
      },
    });

    this.client.activate();
  }

  subscribeToUserMessages(userId, callback) {
    if (!this.client || !this.connected || !userId) {
      return () => {};
    }

    const subscription = this.client.subscribe(`/topic/private.${userId}`, (msg) => {
      try {
        const body = JSON.parse(msg.body);
        callback?.(body);
      } catch (error) {
        console.error("Could not parse STOMP message payload", error);
      }
    });

    this.subscriptions.push(subscription);

    return () => {
      subscription.unsubscribe();
      this.subscriptions = this.subscriptions.filter((item) => item !== subscription);
    };
  }

  sendPrivateMessage(receiverId, content) {
    if (!this.client || !this.connected) {
      throw new Error("WebSocket is not connected");
    }

    this.client.publish({
      destination: "/app/chat.private",
      body: JSON.stringify({ receiverId, content }),
    });
  }

  disconnect() {
    if (!this.client) {
      return;
    }

    this.subscriptions.forEach((subscription) => subscription.unsubscribe());
    this.subscriptions = [];

    this.client.deactivate();
    this.client = null;
    this.connected = false;
  }
}

const chatRealtimeService = new ChatRealtimeService();

const chatService = {
  getConversation: async (targetUserId) => {
    const response = await api.get(`/chat/conversations/${targetUserId}`);
    return response;
  },

  connect: (token, handlers) => chatRealtimeService.connect(token, handlers),

  subscribeToUserMessages: (userId, callback) =>
    chatRealtimeService.subscribeToUserMessages(userId, callback),

  sendPrivateMessage: (receiverId, content) =>
    chatRealtimeService.sendPrivateMessage(receiverId, content),

  disconnect: () => chatRealtimeService.disconnect(),
};

export default chatService;
