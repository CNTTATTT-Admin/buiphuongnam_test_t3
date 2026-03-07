import React, { useState } from "react"
import { Calendar as CalendarIcon, CreditCard } from "lucide-react"

const MOCK_DATES = [
  { day: 'T2', date: '20' },
  { day: 'T3', date: '21' },
  { day: 'T4', date: '22', active: true },
  { day: 'T5', date: '23' },
  { day: 'T6', date: '24' },
  { day: 'T7', date: '25' },
  { day: 'CN', date: '26' },
]

const MOCK_SLOTS = [
  { time: '08:00 - 09:00', price: '200.000đ', selected: true },
  { time: '10:00 - 11:00', price: '200.000đ', selected: false },
  { time: '20:00 - 21:00', price: '250.000đ', selected: false },
  { time: '21:00 - 22:00', price: '250.000đ', selected: false },
]

export default function BookingWidget() {
  const [selectedSlot, setSelectedSlot] = useState("08:00 - 09:00")

  return (
    <div className="bg-white p-6 rounded-xl border border-slate-100 shadow-sm">
      <h3 className="text-base font-bold text-slate-900 flex items-center gap-2 mb-6">
        <CalendarIcon className="w-5 h-5 text-[#372660]" />
        Đặt lịch hẹn
      </h3>

      {/* Mini Calendar */}
      <div className="mb-6">
        <div className="text-sm font-bold text-slate-700 mb-3">Tháng 5, 2024</div>
        <div className="flex items-center justify-between text-center max-w-sm mx-auto">
          {MOCK_DATES.map((d, i) => (
            <div 
              key={i} 
              className={`flex flex-col items-center justify-center w-9 h-12 rounded-lg cursor-pointer transition-colors ${
                d.active ? 'bg-[#372660] text-white shadow-md' : 'hover:bg-slate-50 text-slate-600'
              }`}
            >
              <span className={`text-[10px] font-bold mb-1 ${d.active ? 'text-blue-100' : 'text-slate-400'}`}>{d.day}</span>
              <span className="text-sm font-bold">{d.date}</span>
            </div>
          ))}
        </div>
      </div>

      {/* Time Slots */}
      <div className="mb-6">
        <div className="text-sm font-bold text-slate-700 mb-3">Khung giờ trống (Thứ 4, 22/05)</div>
        <div className="space-y-2">
          {MOCK_SLOTS.map((slot, i) => (
            <div 
              key={i}
              onClick={() => setSelectedSlot(slot.time)}
              className={`flex items-center justify-between p-3.5 rounded-lg border-2 cursor-pointer transition-colors ${
                selectedSlot === slot.time 
                  ? 'border-[#372660] bg-[#372660]/5' 
                  : 'border-slate-100 hover:border-slate-300'
              }`}
            >
              <span className={`text-sm font-semibold ${selectedSlot === slot.time ? 'text-[#372660]' : 'text-slate-600'}`}>
                {slot.time}
              </span>
              <span className={`text-sm font-bold ${selectedSlot === slot.time ? 'text-[#372660]' : 'text-slate-800'}`}>
                {slot.price}
              </span>
            </div>
          ))}
        </div>
      </div>

      {/* CTA */}
      <button className="w-full bg-[#372660] hover:bg-[#2b1d4c] text-white py-3.5 rounded-xl font-bold flex items-center justify-center gap-2 transition-all shadow-md shadow-[#372660]/20">
        <CreditCard className="w-5 h-5" />
        Đặt lịch & Thanh toán
      </button>

      <p className="text-xs text-slate-400 text-center mt-4 px-4 leading-relaxed">
        Bằng cách nhấn nút trên, bạn đồng ý với Điều khoản dịch vụ và Chính sách hoàn tiền của MentorMatch.
      </p>
    </div>
  )
}
