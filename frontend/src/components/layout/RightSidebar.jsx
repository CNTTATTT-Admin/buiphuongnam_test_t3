import React from "react"
import { Star } from "lucide-react"
import { MOCK_TOP_MENTORS, MOCK_TAGS } from "../../data/mockData"

export default function RightSidebar() {
  return (
    <div className="w-80 shrink-0 hidden lg:block space-y-6 sticky top-24">
      {/* Top Mentors */}
      <div className="bg-white rounded-xl shadow-sm border border-slate-100 p-5">
        <div className="flex items-center justify-between mb-5">
          <h3 className="font-bold text-slate-800">Top Mentors nổi bật</h3>
          <a href="#" className="text-xs text-[#372660] font-medium hover:underline">
            Xem tất cả
          </a>
        </div>
        
        <div className="space-y-4">
          {MOCK_TOP_MENTORS.map((mentor) => (
            <div key={mentor.id} className="flex items-center gap-3">
              <img src={mentor.avatar} alt={mentor.name} className="w-10 h-10 rounded-full object-cover" />
              <div className="flex-1">
                <h4 className="text-sm font-semibold text-slate-900">{mentor.name}</h4>
                <div className="flex flex-col gap-0.5 mt-0.5">
                  <span className="text-[10px] font-bold text-slate-600 bg-slate-100 px-2 py-0.5 rounded-sm w-fit">
                    {mentor.tag}
                  </span>
                  <div className="flex items-center gap-1 text-xs text-amber-500 font-medium">
                    <Star className="w-3 h-3 fill-current" />
                    {mentor.rating.toFixed(1)}
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Suggested Topics */}
      <div className="bg-white rounded-xl shadow-sm border border-slate-100 p-5">
        <h3 className="font-bold text-slate-800 mb-4">Chủ đề quan tâm</h3>
        <div className="flex flex-wrap gap-2">
          {MOCK_TAGS.map((tag) => (
            <a 
              key={tag} 
              href="#" 
              className="text-xs font-medium text-slate-600 bg-slate-50 border border-slate-200 px-3 py-1.5 rounded-md hover:bg-slate-100 transition-colors"
            >
              {tag}
            </a>
          ))}
        </div>
      </div>

      {/* Mini Footer */}
      <div className="text-center space-y-3 pt-2">
        <div className="flex items-center justify-center gap-4 text-xs text-slate-400">
          <a href="#" className="hover:text-slate-600">Điều khoản</a>
          <a href="#" className="hover:text-slate-600">Bảo mật</a>
          <a href="#" className="hover:text-slate-600">Liên hệ</a>
        </div>
        <p className="text-[11px] text-slate-400">© 2024 MentorMatch Vietnam</p>
      </div>
    </div>
  )
}
