import React, { useState } from "react"
import { ShieldAlert, Plus, X } from "lucide-react"
import { Input } from "../ui/input"

export default function MentorSettingsForm() {
  const [skills, setSkills] = useState(["Java", "Spring Boot", "System Design"])
  const [newSkill, setNewSkill] = useState("")
  const [price, setPrice] = useState("200000")

  const handleAddSkill = (e) => {
    if (e.key === 'Enter' && newSkill.trim() && !skills.includes(newSkill.trim())) {
      e.preventDefault()
      setSkills([...skills, newSkill.trim()])
      setNewSkill("")
    }
  }

  const removeSkill = (skillToRemove) => {
    setSkills(skills.filter(s => s !== skillToRemove))
  }

  const handleSave = (e) => {
    e.preventDefault()
    alert("Đã lưu thiết lập Mentor!")
  }

  return (
    <div className="bg-white rounded-xl border border-slate-100 shadow-sm p-6 mb-6 relative overflow-hidden">
      {/* Decorative background for Mentor section */}
      <div className="absolute top-0 right-0 w-32 h-32 bg-amber-50 rounded-bl-full -z-0"></div>

      <div className="relative z-10">
        <div className="flex items-center gap-3 mb-6 pb-4 border-b border-slate-100">
          <div className="w-8 h-8 rounded-lg bg-amber-100 flex items-center justify-center text-amber-600">
            <ShieldAlert className="w-5 h-5" />
          </div>
          <div>
            <h3 className="text-lg font-bold text-slate-800 leading-tight">Thiết lập Mentor</h3>
            <p className="text-xs font-semibold text-slate-500">Chỉ hiển thị cho người dùng có vai trò Mentor</p>
          </div>
        </div>

        <form onSubmit={handleSave} className="space-y-6">
          <div className="space-y-1.5 max-w-sm">
            <label className="text-xs font-bold text-slate-700 uppercase tracking-wide">Mức giá / 1 giờ học (VNĐ)</label>
            <div className="relative">
              <Input 
                type="number"
                value={price}
                onChange={(e) => setPrice(e.target.value)}
                className="bg-slate-50 border-slate-200 focus-visible:ring-amber-500 font-bold text-lg text-slate-900"
              />
              <span className="absolute right-4 top-2.5 text-slate-400 font-semibold text-sm">VNĐ</span>
            </div>
            <p className="text-[11px] text-slate-400 mt-1">Nền tảng sẽ thu chiết khấu 10% trên mỗi giao dịch thành công.</p>
          </div>

          <div className="space-y-3">
            <label className="text-xs font-bold text-slate-700 uppercase tracking-wide">Kỹ năng Chuyên môn</label>
            <div className="flex flex-wrap gap-2 mb-2">
              {skills.map((skill, index) => (
                <span key={index} className="pl-3 pr-2 py-1.5 bg-amber-50 text-amber-700 text-sm font-semibold rounded-lg flex items-center gap-1.5 border border-amber-100">
                  {skill}
                  <button type="button" onClick={() => removeSkill(skill)} className="hover:bg-amber-200 rounded-full p-0.5 transition-colors">
                    <X className="w-3 h-3" />
                  </button>
                </span>
              ))}
            </div>
            <div className="relative max-w-md">
              <Input 
                value={newSkill}
                onChange={(e) => setNewSkill(e.target.value)}
                onKeyDown={handleAddSkill}
                placeholder="Nhập kỹ năng mới (Ví dụ: Python) và nhấn Enter"
                className="bg-slate-50 border-slate-200 focus-visible:ring-amber-500 pr-10"
              />
              <Plus className="absolute right-3 top-2.5 w-4 h-4 text-slate-400" />
            </div>
          </div>

          <div className="flex justify-end pt-4 border-t border-slate-100">
            <button type="submit" className="bg-amber-500 hover:bg-amber-600 text-white px-6 py-2.5 rounded-lg text-sm font-semibold transition-colors shadow-sm">
              Lưu thiết lập Mentor
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
