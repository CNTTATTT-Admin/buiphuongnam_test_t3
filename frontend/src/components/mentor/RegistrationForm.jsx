import React, { useState } from "react"
import { UploadCloud, X, Info } from "lucide-react"

export default function RegistrationForm() {
  const [bio, setBio] = useState("")
  const [skills, setSkills] = useState(["Java", "React", "Python", "UI/UX Design"])
  const [newSkill, setNewSkill] = useState("")

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
    alert("Hồ sơ Mentor của bạn đã được gửi để chờ phê duyệt!")
  }

  return (
    <div className="max-w-3xl w-full">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-slate-900">Cài đặt Hồ sơ Mentor</h1>
        <p className="text-slate-500 mt-1">Cập nhật thông tin chuyên môn của bạn để thu hút học viên phù hợp.</p>
      </div>

      <div className="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden mb-8">
        <div className="p-8 space-y-8">
          
          {/* Bio Section */}
          <div className="space-y-3">
            <label className="text-sm font-bold text-slate-700">Giới thiệu bản thân</label>
            <textarea 
              value={bio}
              onChange={(e) => setBio(e.target.value)}
              placeholder="Chia sẻ về kinh nghiệm làm việc, phong cách giảng dạy và các dự án tiêu biểu của bạn..."
              className="w-full min-h-[160px] p-4 bg-slate-50 border border-slate-200 rounded-xl focus:outline-none focus:ring-1 focus:ring-[#372660] resize-y text-slate-700 placeholder:text-slate-400"
            ></textarea>
            <p className="text-xs text-slate-400">Khuyên dùng: Tối thiểu 200 ký tự.</p>
          </div>

          {/* Skills Section */}
          <div className="space-y-3">
            <label className="text-sm font-bold text-slate-700">Kỹ năng chuyên môn</label>
            <div className="w-full min-h-[56px] p-2.5 bg-slate-50 border border-slate-200 rounded-xl flex flex-wrap gap-2 items-center focus-within:ring-1 focus-within:ring-[#372660]">
              {skills.map((skill, index) => (
                <span key={index} className="px-3 py-1.5 bg-[#372660] text-white text-sm font-medium rounded-full flex items-center gap-2">
                  {skill}
                  <button type="button" onClick={() => removeSkill(skill)} className="hover:bg-white/20 rounded-full p-0.5 transition-colors">
                    <X className="w-3.5 h-3.5" />
                  </button>
                </span>
              ))}
              <input 
                type="text"
                value={newSkill}
                onChange={(e) => setNewSkill(e.target.value)}
                onKeyDown={handleAddSkill}
                placeholder="Thêm kỹ năng..."
                className="flex-1 min-w-[120px] bg-transparent border-none outline-none text-sm text-slate-700 placeholder:text-slate-400 p-1"
              />
            </div>
          </div>

          {/* Certificates Section */}
          <div className="space-y-3">
            <label className="text-sm font-bold text-slate-700">Chứng chỉ & Bằng cấp</label>
            <div className="w-full h-[200px] bg-slate-50/50 border-2 border-dashed border-slate-200 hover:border-[#372660]/40 transition-colors rounded-xl flex flex-col items-center justify-center cursor-pointer group">
              <div className="w-12 h-12 bg-[#372660]/10 text-[#372660] rounded-full flex items-center justify-center mb-4 group-hover:scale-110 transition-transform">
                <UploadCloud className="w-6 h-6 fill-current opacity-80" />
              </div>
              <p className="text-sm text-slate-600 font-medium mb-1">
                <span className="font-bold text-slate-800">Kéo thả chứng chỉ/Bằng cấp</span> vào đây hoặc click để chọn tệp
              </p>
              <p className="text-xs text-slate-400">PNG, JPG, PDF (tối đa 10MB)</p>
            </div>
          </div>

        </div>

        {/* Footer Actions */}
        <div className="p-6 bg-slate-50/50 border-t border-slate-200 flex justify-end gap-3">
          <button className="px-6 py-2.5 text-sm font-semibold text-slate-600 hover:bg-slate-200 rounded-lg transition-colors">
            Hủy
          </button>
          <button onClick={handleSave} className="px-8 py-2.5 bg-[#372660] hover:bg-[#2b1d4c] text-white text-sm font-semibold rounded-lg shadow-sm transition-colors">
            Lưu thay đổi
          </button>
        </div>
      </div>

      {/* Info Alert */}
      <div className="bg-[#372660]/5 border border-[#372660]/10 rounded-xl p-5 flex gap-4">
        <div className="shrink-0 pt-0.5">
          <div className="w-6 h-6 rounded-full bg-[#372660] text-white flex items-center justify-center">
            <Info className="w-3.5 h-3.5" />
          </div>
        </div>
        <div>
          <h4 className="text-sm font-bold text-[#372660] mb-1">Tại sao hồ sơ lại quan trọng?</h4>
          <p className="text-xs text-slate-600 leading-relaxed">
            Một hồ sơ đầy đủ giúp chúng tôi kết nối bạn với đúng học viên có nhu cầu. Các chứng chỉ giúp tăng mức độ tin cậy và chuyên nghiệp của bạn lên 40%.
          </p>
        </div>
      </div>
    </div>
  )
}
