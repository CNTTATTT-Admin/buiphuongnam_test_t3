import React from "react"
import { Users, CreditCard, CheckCircle } from "lucide-react"

export default function StatCards() {
  const stats = [
    {
      title: "NGƯỜI DÙNG MỚI",
      value: "1,240",
      icon: Users,
      bgColor: "bg-blue-100",
      iconColor: "text-blue-600"
    },
    {
      title: "YÊU CẦU RÚT TIỀN",
      value: "45",
      icon: CreditCard,
      bgColor: "bg-amber-100",
      iconColor: "text-amber-600"
    },
    {
      title: "BUỔI HỌC HOÀN TẤT",
      value: "8,920",
      icon: CheckCircle,
      bgColor: "bg-emerald-100",
      iconColor: "text-emerald-600"
    }
  ]

  return (
    <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
      {stats.map((stat, index) => {
        const Icon = stat.icon;
        return (
          <div key={index} className="bg-white rounded-2xl border border-slate-100 shadow-sm p-6 flex items-center gap-5">
            <div className={`w-14 h-14 rounded-xl flex items-center justify-center shrink-0 ${stat.bgColor} ${stat.iconColor}`}>
              <Icon className="w-6 h-6" />
            </div>
            <div>
              <p className="text-xs font-bold text-slate-500 uppercase tracking-wider mb-1">{stat.title}</p>
              <h3 className="text-2xl font-bold text-slate-900">{stat.value}</h3>
            </div>
          </div>
        )
      })}
    </div>
  )
}
