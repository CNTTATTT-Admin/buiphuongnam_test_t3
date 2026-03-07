import React from "react"
import { MoreHorizontal } from "lucide-react"

export default function PostCard({ post }) {
  return (
    <div className="bg-white rounded-xl shadow-sm border border-slate-100 mb-6 flex flex-col">
      {/* Header */}
      <div className="p-5 pb-3 flex justify-between items-start">
        <div className="flex gap-3">
          <img src={post.author.avatar} alt={post.author.name} className="w-10 h-10 rounded-full object-cover" />
          <div>
            <h4 className="font-semibold text-slate-900 leading-tight">{post.author.name}</h4>
            <div className="flex items-center gap-1.5 text-xs text-slate-500 mt-0.5">
              <span>{post.time}</span>
              <span>•</span>
              <span className={`font-semibold ${post.author.isMentor ? "text-emerald-600" : "text-[#372660]"}`}>
                {post.author.role}
              </span>
            </div>
          </div>
        </div>
        <button className="text-slate-400 hover:text-slate-600 p-1">
          <MoreHorizontal className="w-5 h-5" />
        </button>
      </div>

      {/* Content */}
      <div className="px-5 mb-4">
        <h3 className="font-bold text-lg text-[#372660] mb-2">{post.title}</h3>
        <p className="text-slate-700 text-sm leading-relaxed">
          {post.content}
        </p>
      </div>

      {/* Image if available */}
      {post.image && (
        <div className="w-full bg-slate-100 mt-2">
          <img src={post.image} alt="Post content" className="w-full h-auto max-h-[400px] object-cover" />
        </div>
      )}

      {/* Actions */}
      <div className="px-5 py-4 border-t border-slate-100 mt-auto flex justify-between items-center">
        <div className="flex gap-6">
          <button className="flex items-center gap-2 text-slate-500 hover:text-[#372660] font-medium text-sm transition-colors">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M14 9V5a3 3 0 0 0-3-3l-4 9v11h11.28a2 2 0 0 0 2-1.7l1.38-9a2 2 0 0 0-2-2.3zM7 22H4a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2h3"></path></svg>
            <span>{post.likes}</span>
          </button>
          <button className="flex items-center gap-2 text-slate-500 hover:text-[#372660] font-medium text-sm transition-colors">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path></svg>
            <span>{post.comments}</span>
          </button>
        </div>
        <button className="bg-[#372660] hover:bg-[#2b1d4c] text-white px-5 py-2 rounded-lg text-sm font-medium transition-colors">
          {post.actionText}
        </button>
      </div>
    </div>
  )
}
