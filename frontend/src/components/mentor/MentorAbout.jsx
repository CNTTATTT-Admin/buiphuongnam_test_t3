import React from "react"

export default function MentorAbout() {
  return (
    <div className="bg-white p-8 rounded-xl border border-slate-100 shadow-sm mb-6">
      <h2 className="text-base font-bold text-slate-800 mb-4">Giới thiệu về tôi</h2>
      
      <div className="text-sm text-slate-600 leading-relaxed space-y-4">
        <p>
          Xin chào! Tôi là A, hiện đang đảm nhiệm vị trí Senior Frontend Engineer tại TechCorp với hơn 8 năm kinh nghiệm thực chiến trong việc xây dựng các hệ thống quy mô lớn sử dụng hệ sinh thái JavaScript và Java.
        </p>
        <p>
          Tôi tin rằng việc học lập trình không chỉ là đọc cú pháp, mà là học cách giải quyết vấn đề và tư duy hệ thống. Trong suốt quá trình làm việc, tôi đã hướng dẫn thành công hơn 50 mentees từ trình độ Junior lên Senior thông qua các lộ trình học tập được cá nhân hóa.
        </p>
        <p className="font-semibold text-slate-700 pt-2">Tôi có thể giúp bạn:</p>
        <ul className="list-disc pl-5 space-y-1.5">
          <li>Xây dựng nền tảng React & TypeScript vững chắc.</li>
          <li>Tối ưu hiệu suất Front-end và thiết kế hệ thống (System Design).</li>
          <li>Luyện tập phỏng vấn tại các tập đoàn lớn (Big Tech).</li>
          <li>Cải thiện tiếng Anh chuyên ngành công nghệ (IELTS 8.0).</li>
        </ul>
      </div>
    </div>
  )
}
