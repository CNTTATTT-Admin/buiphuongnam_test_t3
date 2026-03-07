import React, { useState } from "react"
import { useAuth } from "../../contexts/AuthContext"
import { Camera, CheckCircle } from "lucide-react"
import { Input } from "../ui/input"

export default function BasicInfoForm() {
  const { user } = useAuth()
  const [formData, setFormData] = useState({
    name: user?.name || "",
    email: user?.email || "",
    phone: "0987654321",
    bio: user?.role === "mentor" 
      ? "Xin chào! Tôi là Senior Backend Engineer với hơn 5 năm kinh nghiệm..." 
      : "Sinh viên ĐH Bách Khoa đam mê công nghệ.",
  })

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value })
  }

  const handleSave = (e) => {
    e.preventDefault()
    alert("Đã lưu thông tin cơ bản!")
  }

  return (
    <div className="bg-white rounded-xl border border-slate-100 shadow-sm p-6 mb-6">
      <h3 className="text-lg font-bold text-slate-800 mb-6 pb-4 border-b border-slate-100">
        Thông tin Cơ bản
      </h3>

      <div className="flex flex-col md:flex-row gap-8 items-start mb-8">
        <div className="shrink-0 flex flex-col items-center gap-3">
          <div className="relative group cursor-pointer">
            <img 
              src={user?.avatar || "https://i.pravatar.cc/150"} 
              alt="Avatar" 
              className="w-24 h-24 rounded-full object-cover border-4 border-slate-50 shadow-sm group-hover:opacity-80 transition-opacity"
            />
            <div className="absolute inset-0 bg-black/40 rounded-full flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity">
              <Camera className="w-6 h-6 text-white" />
            </div>
          </div>
          <span className="text-xs font-semibold text-[#372660] cursor-pointer hover:underline">Thay đổi ảnh</span>
        </div>

        <form onSubmit={handleSave} className="flex-1 w-full space-y-5">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
            <div className="space-y-1.5">
              <label className="text-xs font-bold text-slate-700 uppercase tracking-wide">Họ và Tên</label>
              <Input 
                name="name"
                value={formData.name}
                onChange={handleChange}
                className="bg-slate-50 border-slate-200 focus-visible:ring-[#372660]"
              />
            </div>
            <div className="space-y-1.5">
              <label className="text-xs font-bold text-slate-700 uppercase tracking-wide">Số điện thoại</label>
              <Input 
                name="phone"
                value={formData.phone}
                onChange={handleChange}
                className="bg-slate-50 border-slate-200 focus-visible:ring-[#372660]"
              />
            </div>
          </div>

          <div className="space-y-1.5">
            <label className="text-xs font-bold text-slate-700 uppercase tracking-wide flex items-center gap-2">
              Email
              <span className="bg-emerald-100 text-emerald-700 text-[10px] px-1.5 py-0.5 rounded font-bold inline-flex items-center gap-1">
                <CheckCircle className="w-3 h-3" /> Đã xác thực
              </span>
            </label>
            <Input 
              name="email"
              value={formData.email}
              disabled
              className="bg-slate-100 border-slate-200 text-slate-500 cursor-not-allowed"
            />
            <p className="text-[11px] text-slate-400 mt-1">Để thay đổi email, vui lòng liên hệ CSKH.</p>
          </div>

          <div className="space-y-1.5">
            <label className="text-xs font-bold text-slate-700 uppercase tracking-wide">Giới thiệu ngắn (Bio)</label>
            <textarea 
              name="bio"
              value={formData.bio}
              onChange={handleChange}
              rows={4}
              className="w-full bg-slate-50 border border-slate-200 rounded-lg p-3 text-sm focus:outline-none focus:ring-1 focus:ring-[#372660] resize-none"
            ></textarea>
          </div>

          <div className="flex justify-end pt-4">
            <button type="submit" className="bg-[#372660] hover:bg-[#2b1d4c] text-white px-6 py-2.5 rounded-lg text-sm font-semibold transition-colors shadow-sm">
              Lưu thay đổi
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
