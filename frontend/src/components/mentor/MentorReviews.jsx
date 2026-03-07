import React from "react"
import { Star, ArrowRight, ShieldCheck } from "lucide-react"

const MOCK_REVIEWS = [
  {
    id: 1,
    name: "Trần Thị B",
    avatar: "https://i.pravatar.cc/150?u=tranthib",
    rating: 5,
    comment: "Anh A hướng dẫn rất tận tâm, cách truyền đạt dễ hiểu. Buổi System Design thực sự giúp mình mở mang rất nhiều về cách tư duy kiến trúc ứng dụng."
  },
  {
    id: 2,
    name: "Lê Văn C",
    avatar: "https://i.pravatar.cc/150?u=levanc",
    rating: 5,
    comment: "Cảm ơn anh đã giúp em sửa CV và mockup interview. Nhờ anh mà em đã tự tin hơn và nhận được offer từ công ty mình mong muốn!"
  }
];

export default function MentorReviews() {
  return (
    <div className="bg-white p-8 rounded-xl border border-slate-100 shadow-sm mb-6 lg:mb-0">
      <div className="flex items-center justify-between mb-6 pb-4 border-b border-slate-100">
        <h2 className="text-base font-bold text-slate-800">
          Đánh giá từ học viên (120)
        </h2>
        <button className="text-sm font-semibold text-slate-500 hover:text-[#372660] transition-colors flex items-center gap-1 group">
          Xem tất cả 
          <ArrowRight className="w-4 h-4 group-hover:translate-x-1 transition-transform" />
        </button>
      </div>
      
      <div className="space-y-6">
        {MOCK_REVIEWS.map((review) => (
          <div key={review.id} className="flex gap-4">
            <img 
              src={review.avatar} 
              alt={review.name} 
              className="w-10 h-10 rounded-full object-cover shrink-0 bg-slate-100 p-0.5" 
            />
            <div className="flex-1">
              <div className="flex items-center justify-between mb-1">
                <div>
                  <h4 className="text-sm font-bold text-slate-900">{review.name}</h4>
                  <div className="flex items-center gap-1 mt-0.5 text-[10px] font-bold text-emerald-600 bg-emerald-50 px-1.5 py-0.5 rounded-sm inline-flex">
                    <ShieldCheck className="w-3 h-3" />
                    ĐÃ HỌC QUA NỀN TẢNG
                  </div>
                </div>
                <div className="flex text-amber-500">
                  {[...Array(review.rating)].map((_, i) => (
                    <Star key={i} className="w-3 h-3 fill-amber-500" />
                  ))}
                </div>
              </div>
              <p className="text-sm text-slate-600 mt-2 leading-relaxed">
                {review.comment}
              </p>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
