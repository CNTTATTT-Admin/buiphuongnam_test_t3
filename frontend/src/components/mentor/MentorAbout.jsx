import React from "react"

export default function MentorAbout({ user }) {
  const bio = user?.mentorProfile?.bio || "Chưa cập nhật thông tin giới thiệu.";
  
  return (
    <div className="bg-white p-8 rounded-xl border border-slate-100 shadow-sm mb-6">
      <h2 className="text-base font-bold text-slate-800 mb-4">Giới thiệu về tôi</h2>
      
      <div className="text-sm text-slate-600 leading-relaxed whitespace-pre-wrap">
        {bio}
      </div>
    </div>
  )
}
