import React, { useState, useEffect } from "react"
import { ShieldAlert, Plus, X } from "lucide-react"
import { Input } from "../ui/input"
import profileService from "../../services/profileService"

export default function MentorSettingsForm({ profile, onUpdate }) {
  const [formData, setFormData] = useState({
    title: "",
    bio: "",
    yearsOfExperience: 0
  })
  const [skills, setSkills] = useState([])
  const [newSkill, setNewSkill] = useState("")
  const [isSaving, setIsSaving] = useState(false)
  const [message, setMessage] = useState(null)

  useEffect(() => {
    if (profile && profile.mentorProfile) {
      setFormData({
        title: profile.mentorProfile.title || "",
        bio: profile.mentorProfile.bio || "",
        yearsOfExperience: profile.mentorProfile.yearsOfExperience || 0
      })
      setSkills(profile.mentorProfile.skills || [])
    }
  }, [profile])

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

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value })
  }

  const handleSave = async (e) => {
    e.preventDefault()
    setIsSaving(true)
    setMessage(null)

    try {
      const result = await profileService.updateMentorProfile({
        title: formData.title,
        bio: formData.bio,
        yearsOfExperience: parseInt(formData.yearsOfExperience) || 0,
        skills: skills,
        certificates: profile?.mentorProfile?.certificates || [] // Preserve existing certs for now
      })
      if (result.code === 1000) {
        setMessage({ type: 'success', text: 'Cập nhật thiết lập Mentor thành công!' })
        if (onUpdate) onUpdate()
      } else {
         setMessage({ type: 'error', text: result.message || 'Cập nhật thất bại' })
      }
    } catch (error) {
      console.error(error)
      setMessage({ type: 'error', text: 'Lỗi máy chủ khi cập nhật thiết lập Mentor' })
    } finally {
      setIsSaving(false)
    }
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

        {message && (
          <div className={`mb-6 p-3 text-sm rounded-lg font-medium border ${
            message.type === 'success' ? 'bg-emerald-50 text-emerald-700 border-emerald-100' : 'bg-red-50 text-red-600 border-red-100'
          }`}>
            {message.text}
          </div>
        )}

        <form onSubmit={handleSave} className="space-y-6">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
            <div className="space-y-1.5">
              <label className="text-xs font-bold text-slate-700 uppercase tracking-wide">Chức danh (*)</label>
              <Input 
                name="title"
                value={formData.title}
                onChange={handleChange}
                placeholder="VD: Senior Frontend Engineer"
                required
                className="bg-slate-50 border-slate-200 focus-visible:ring-amber-500"
              />
            </div>
            <div className="space-y-1.5">
              <label className="text-xs font-bold text-slate-700 uppercase tracking-wide">Năm kinh nghiệm</label>
              <div className="relative">
                <Input 
                  type="number"
                  name="yearsOfExperience"
                  value={formData.yearsOfExperience}
                  onChange={handleChange}
                  min="0"
                  className="bg-slate-50 border-slate-200 focus-visible:ring-amber-500 font-bold text-lg text-slate-900 pr-12"
                />
                <span className="absolute right-4 top-2.5 text-slate-400 font-semibold text-sm">Năm</span>
              </div>
            </div>
          </div>

          <div className="space-y-1.5">
            <label className="text-xs font-bold text-slate-700 uppercase tracking-wide">Giới thiệu bản thân (Bio)</label>
            <textarea 
              name="bio"
              value={formData.bio}
              onChange={handleChange}
              rows={4}
              placeholder="Chia sẻ về kinh nghiệm làm việc và định hướng giảng dạy của bạn..."
              className="w-full bg-slate-50 border border-slate-200 rounded-lg p-3 text-sm focus:outline-none focus:ring-1 focus:ring-amber-500 resize-none"
            ></textarea>
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
            <button type="submit" disabled={isSaving} className="bg-amber-500 hover:bg-amber-600 text-white px-6 py-2.5 rounded-lg text-sm font-semibold transition-colors shadow-sm disabled:opacity-70 disabled:cursor-not-allowed">
              {isSaving ? 'Đang lưu...' : 'Lưu thiết lập Mentor'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
