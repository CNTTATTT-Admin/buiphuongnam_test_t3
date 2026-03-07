import React, { useState } from "react"
import { MOCK_FILTER_SKILLS } from "../../data/mockData"

export default function FilterSidebar() {
  const [priceRange, setPriceRange] = useState(500)

  return (
    <div className="w-64 shrink-0 hidden md:block bg-white rounded-xl shadow-sm border border-slate-100 p-5 sticky top-24 self-start">
      <div className="flex items-center justify-between mb-6 pb-4 border-b border-slate-100">
        <div className="flex items-center gap-2">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="text-[#372660]">
            <polygon points="22 3 2 3 10 12.46 10 19 14 21 14 12.46 22 3"></polygon>
          </svg>
          <h3 className="font-bold text-slate-800">Bộ lọc nâng cao</h3>
        </div>
        <button className="text-xs text-slate-500 hover:text-[#372660]">Xóa tất cả</button>
      </div>

      {/* Skills Filter */}
      <div className="mb-6">
        <h4 className="text-xs font-bold text-slate-500 uppercase tracking-wider mb-3">Kỹ Năng</h4>
        <div className="space-y-2.5">
          {MOCK_FILTER_SKILLS.map((skill, index) => (
            <label key={index} className="flex items-center gap-3 cursor-pointer group">
              <div className="relative flex items-center justify-center">
                <input 
                  type="checkbox" 
                  className="peer appearance-none w-4 h-4 rounded border border-slate-300 checked:bg-[#372660] checked:border-[#372660] cursor-pointer transition-colors"
                  defaultChecked={index === 0 || index === 2}
                />
                <svg className="absolute w-3 h-3 text-white opacity-0 peer-checked:opacity-100 pointer-events-none" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round">
                  <polyline points="20 6 9 17 4 12"></polyline>
                </svg>
              </div>
              <span className="text-sm text-slate-700 group-hover:text-slate-900">{skill}</span>
            </label>
          ))}
        </div>
      </div>

      {/* Price Filter */}
      <div className="mb-6">
        <h4 className="text-xs font-bold text-slate-500 uppercase tracking-wider mb-4">Mức Giá (VNĐ)</h4>
        <div className="px-1">
          {/* Mock Dual Slider Track */}
          <div className="relative h-1.5 bg-slate-200 rounded-full mb-4">
            <div className="absolute left-[20%] right-[30%] h-full bg-[#372660] rounded-full"></div>
            <div className="absolute left-[20%] top-1/2 -translate-y-1/2 -translate-x-1/2 w-4 h-4 bg-[#372660] rounded-full shadow-md border-2 border-white cursor-pointer"></div>
            <div className="absolute right-[30%] top-1/2 -translate-y-1/2 translate-x-1/2 w-4 h-4 bg-[#372660] rounded-full shadow-md border-2 border-white cursor-pointer"></div>
          </div>
          <div className="flex justify-between text-xs font-medium text-slate-500">
            <span>100k</span>
            <span>500k</span>
            <span>1M+</span>
          </div>
        </div>
      </div>

      {/* Rating Filter */}
      <div className="mb-6">
        <h4 className="text-xs font-bold text-slate-500 uppercase tracking-wider mb-3">Đánh Giá</h4>
        <div className="space-y-2.5">
          {[4.0, 5.0].map((rating, index) => (
            <label key={index} className="flex items-center gap-3 cursor-pointer group">
              <div className="relative flex items-center justify-center">
                <input 
                  type="radio" 
                  name="rating"
                  className="peer appearance-none w-4 h-4 rounded-full border border-slate-300 checked:border-[#372660] cursor-pointer transition-colors"
                  defaultChecked={index === 0}
                />
                <div className="absolute w-2 h-2 rounded-full bg-[#372660] opacity-0 peer-checked:opacity-100 pointer-events-none transition-opacity"></div>
              </div>
              <span className="text-sm text-slate-700 group-hover:text-slate-900 flex items-center gap-1.5">
                {rating.toFixed(1)}+ 
                <svg width="12" height="12" viewBox="0 0 24 24" fill="#EAB308" stroke="#EAB308" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon></svg>
              </span>
            </label>
          ))}
        </div>
      </div>

      {/* Status Filter */}
      <div>
        <h4 className="text-xs font-bold text-slate-500 uppercase tracking-wider mb-3">Trạng Thái</h4>
        <div className="space-y-2.5">
          {["Đang online", "Lịch trống tuần này"].map((status, index) => (
            <label key={index} className="flex items-center gap-3 cursor-pointer group">
              <div className="relative flex items-center justify-center">
                <input 
                  type="checkbox" 
                  className="peer appearance-none w-4 h-4 rounded border border-slate-300 checked:bg-[#372660] checked:border-[#372660] cursor-pointer transition-colors"
                />
                <svg className="absolute w-3 h-3 text-white opacity-0 peer-checked:opacity-100 pointer-events-none" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round">
                  <polyline points="20 6 9 17 4 12"></polyline>
                </svg>
              </div>
              <span className="text-sm text-slate-700 group-hover:text-slate-900">{status}</span>
            </label>
          ))}
        </div>
      </div>
    </div>
  )
}
