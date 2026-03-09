import React from "react"
import { MoreHorizontal } from "lucide-react"

export default function PostCard({ post }) {
  // Use backend properties or fallback to empty strings
  const authorName = post.authorName || "Người dùng ẩn danh";
  const avatar = post.authorAvatarUrl || "https://ui-avatars.com/api/?name=" + encodeURIComponent(authorName);
  const time = new Date(post.createdAt).toLocaleDateString("vi-VN", {
    hour: "2-digit", minute: "2-digit", day: "2-digit", month: "2-digit", year: "numeric"
  });

  return (
    <div className="bg-white rounded-xl shadow-sm border border-slate-100 mb-6 flex flex-col overflow-hidden">
      {/* Header */}
      <div className="p-5 pb-3 flex justify-between items-start">
        <div className="flex gap-3">
          <img src={avatar} alt={authorName} className="w-10 h-10 rounded-full object-cover shrink-0" />
          <div>
            <h4 className="font-semibold text-slate-900 leading-tight">{authorName}</h4>
            <div className="flex items-center gap-1.5 text-xs text-slate-500 mt-0.5">
              <span>{time}</span>
            </div>
          </div>
        </div>
        <button className="text-slate-400 hover:text-slate-600 p-1">
          <MoreHorizontal className="w-5 h-5" />
        </button>
      </div>

      {/* Content */}
      <div className="px-5 mb-4">
        <p className="text-slate-700 text-sm leading-relaxed whitespace-pre-wrap">
          {post.content}
        </p>
      </div>

      {/* Images if available */}
      {post.imageUrls && post.imageUrls.length > 0 && (
        <div className="w-full bg-slate-50 border-t border-slate-100 flex overflow-x-auto snap-x">
          {post.imageUrls.map((url, idx) => (
             <img key={idx} src={url} alt={`Post image ${idx}`} className="w-full sm:w-auto h-auto sm:max-h-[400px] object-contain snap-center shrink-0 border-r border-slate-100 last:border-r-0" />
          ))}
        </div>
      )}

      {/* Actions */}
      <div className="px-5 py-4 border-t border-slate-100 mt-auto flex justify-between items-center bg-slate-50/50">
        <div className="flex gap-6">
           {/* Placeholder for interactions */}
          <button className="flex items-center gap-2 text-slate-500 hover:text-[#372660] font-medium text-sm transition-colors">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M14 9V5a3 3 0 0 0-3-3l-4 9v11h11.28a2 2 0 0 0 2-1.7l1.38-9a2 2 0 0 0-2-2.3zM7 22H4a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2h3"></path></svg>
            <span>Thích</span>
          </button>
          <button className="flex items-center gap-2 text-slate-500 hover:text-[#372660] font-medium text-sm transition-colors">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path></svg>
            <span>Bình luận</span>
          </button>
        </div>
      </div>
    </div>
  )
}
