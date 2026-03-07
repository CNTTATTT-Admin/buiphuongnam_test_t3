import React from "react"

const MOCK_ISSUES = [
  { 
    id: "#MM-1024", 
    userInitial: "NV", userName: "Nguyễn Văn A", 
    reason: "Mentor vắng mặt không lý do hoặc thông báo quá muộn (dưới 1 tiếng).",
    actionType: "refund_mentee", actionText: "Hoàn tiền Mentee"
  },
  { 
    id: "#MM-1025", 
    userInitial: "TT", userName: "Trần Thị B", 
    reason: "Nội dung buổi học không đúng mô tả, Mentor không có kiến thức chuyên môn.",
    actionType: "pay_mentor", actionText: "Chuyển tiền Mentor"
  },
  { 
    id: "#MM-1028", 
    userInitial: "LV", userName: "Lê Văn C", 
    reason: "Lỗi kết nối kỹ thuật từ phía nền tảng làm gián đoạn hơn 50% thời gian học.",
    actionType: "refund_mentee", actionText: "Hoàn tiền Mentee"
  },
  { 
    id: "#MM-1032", 
    userInitial: "PH", userName: "Phạm Hữu D", 
    reason: "Mentee không tham gia buổi học và yêu cầu hoàn tiền vô lý.",
    actionType: "pay_mentor", actionText: "Chuyển tiền Mentor"
  }
];

export default function IssueTable() {
  return (
    <div className="bg-white rounded-2xl border border-slate-100 shadow-sm overflow-hidden mb-6">
      <div className="p-6 flex items-center justify-between border-b border-slate-50">
        <h2 className="text-base font-bold text-slate-800">Danh sách Khiếu nại đang chờ xử lý</h2>
        <button className="text-sm font-semibold text-slate-500 hover:text-[#372660] transition-colors">
          Xem tất cả khiếu nại
        </button>
      </div>
      
      <div className="overflow-x-auto">
        <table className="w-full text-left border-collapse">
          <thead>
            <tr className="bg-slate-50 text-xs font-bold text-slate-500 uppercase tracking-wider">
              <th className="px-6 py-4 font-bold">ID Ca Học</th>
              <th className="px-6 py-4 font-bold">Người Khiếu Nại</th>
              <th className="px-6 py-4 font-bold">Lý Do</th>
              <th className="px-6 py-4 font-bold text-right">Hành Động</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-50">
            {MOCK_ISSUES.map((issue, index) => (
              <tr key={index} className="hover:bg-slate-50/50 transition-colors">
                <td className="px-6 py-5 text-sm font-semibold text-slate-700 whitespace-nowrap">
                  {issue.id}
                </td>
                <td className="px-6 py-5 whitespace-nowrap">
                  <div className="flex items-center gap-3">
                    <div className="w-8 h-8 rounded-full bg-slate-200 flex items-center justify-center text-xs font-bold text-slate-600">
                      {issue.userInitial}
                    </div>
                    <span className="text-sm font-medium text-slate-700">{issue.userName}</span>
                  </div>
                </td>
                <td className="px-6 py-5 text-sm text-slate-500 max-w-md">
                  {issue.reason}
                </td>
                <td className="px-6 py-5 whitespace-nowrap text-right">
                  <div className="flex items-center justify-end gap-2">
                    <button className={`px-4 py-2 rounded-lg text-xs font-bold transition-colors ${
                      issue.actionType === 'refund_mentee' 
                        ? 'bg-emerald-100 text-emerald-700 hover:bg-emerald-200' 
                        : 'bg-[#372660] text-white hover:bg-[#2b1d4c]'
                    }`}>
                      {issue.actionText}
                    </button>
                    <button className="px-4 py-2 rounded-lg text-xs font-bold bg-slate-100 text-slate-600 hover:bg-slate-200 transition-colors">
                      Chi tiết
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
