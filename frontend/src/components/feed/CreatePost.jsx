import React from "react"
import { Image, Video } from "lucide-react"
import { useAuth } from "../../contexts/AuthContext"
import { useNavigate } from "react-router-dom"

export default function CreatePost() {
  const { user } = useAuth()
  const navigate = useNavigate()

  if (!user) {
    return (
      <div className="bg-white p-6 rounded-xl shadow-sm border border-slate-100 mb-6 text-center">
        <h3 className="text-lg font-bold text-slate-800 mb-2">Bạn có câu hỏi hay muốn mở lớp?</h3>
        <p className="text-sm text-slate-500 mb-4">Vui lòng đăng nhập để đăng bài viết và tương tác với cộng đồng.</p>
        <button 
          onClick={() => navigate('/login')}
          className="bg-[#372660] hover:bg-[#2b1d4c] text-white px-6 py-2.5 rounded-lg text-sm font-semibold transition-colors shadow-sm"
        >
          Đăng nhập ngay
        </button>
      </div>
    )
  }

  return (
    <div className="bg-white p-5 rounded-xl shadow-sm border border-slate-100 mb-6">
      <div className="flex gap-4">
        <img 
          src={user.avatar} 
          alt="Avatar" 
          className="w-10 h-10 rounded-full object-cover shrink-0" 
        />
        <div className="flex-1">
          <textarea 
            placeholder="Bạn đang tìm khóa học hay muốn mở lớp?"
            className="w-full bg-slate-50 border-none rounded-xl p-4 text-sm text-slate-800 focus:outline-none focus:ring-1 focus:ring-slate-200 resize-none h-24"
          ></textarea>
        </div>
      </div>
      
      <div className="flex items-center justify-between mt-4 pl-14">
        <div className="flex gap-4">
          <button className="flex items-center gap-2 text-slate-500 hover:text-blue-600 transition-colors">
            <Image className="w-5 h-5 text-blue-500" />
            <span className="text-sm font-medium">Ảnh</span>
          </button>
          <button className="flex items-center gap-2 text-slate-500 hover:text-red-600 transition-colors">
            <Video className="w-5 h-5 text-red-500" />
            <span className="text-sm font-medium">Video</span>
          </button>
        </div>
        
        <button className="bg-[#372660] hover:bg-[#2b1d4c] text-white px-6 py-2 rounded-lg text-sm font-medium transition-colors">
          Đăng bài
        </button>
      </div>
    </div>
  )
}
