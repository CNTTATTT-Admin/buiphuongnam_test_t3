import React from "react"

export default function MentorAbout({ user }) {
  const bio = user?.mentorProfile?.bio || "Chưa cập nhật thông tin giới thiệu.";
  
  return (
    <div className="bg-white p-8 rounded-xl border border-slate-100 shadow-sm mb-6">
      <h2 className="text-base font-bold text-slate-800 mb-4">Giới thiệu về tôi</h2>
      
      <div className="text-sm text-slate-600 leading-relaxed whitespace-pre-wrap mb-8">
        {bio}
      </div>

      <h2 className="text-base font-bold text-slate-800 mb-4 pt-6 border-t border-slate-100">Bằng cấp & Chứng chỉ</h2>
      
      {!user?.mentorProfile?.certificates || user.mentorProfile.certificates.length === 0 ? (
        <p className="text-sm text-slate-500 italic">Chưa có chứng chỉ nào được cung cấp.</p>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          {user.mentorProfile.certificates.map(cert => (
            <div key={cert.id} className="flex items-start gap-3 p-4 rounded-xl border border-slate-100 bg-slate-50 hover:border-[#372660]/20 transition-colors">
              <div className="p-2 bg-white rounded-lg shadow-sm">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="text-[#372660]"><path d="M12 15V3"/><path d="M5.5 8h13"/><path d="M16 11l-4 4-4-4"/><path d="M9 21h6"/></svg>
              </div>
              <div className="flex-1">
                 <p className="text-sm font-bold text-slate-900 line-clamp-2" title={cert.name}>{cert.name}</p>
                 <a href={cert.fileUrl} target="_blank" rel="noreferrer" className="text-xs font-semibold text-blue-600 hover:text-blue-800 underline mt-1 inline-block">Xem chứng chỉ</a>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
