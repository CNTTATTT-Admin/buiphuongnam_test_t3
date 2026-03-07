import React from "react"
import { LineChart, Line, XAxis, Tooltip, ResponsiveContainer } from "recharts"

const data = [
  { name: 'THÁNG 8', value: 30000000 },
  { name: 'THÁNG 9', value: 45000000 },
  { name: 'THÁNG 10', value: 60000000 },
  { name: 'THÁNG 11', value: 40000000 },
  { name: 'THÁNG 12', value: 80000000 },
  { name: 'THÁNG 1', value: 125000000 },
];

export default function RevenueChart() {
  return (
    <div className="bg-white p-6 rounded-2xl border border-slate-100 shadow-sm mb-6">
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-10 gap-4">
        <div>
          <h2 className="text-lg font-bold text-slate-800 mb-1">Doanh thu hoa hồng theo tháng</h2>
          <p className="text-sm text-slate-500">Thống kê doanh thu từ phí dịch vụ (6 tháng gần nhất)</p>
        </div>
        <div className="flex flex-col items-end">
          <div className="text-2xl font-bold text-[#372660]">125.000.000đ</div>
          <div className="text-xs font-semibold text-emerald-500 flex items-center gap-1 mt-1">
            <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round"><polyline points="22 7 13.5 15.5 8.5 10.5 2 17"></polyline><polyline points="16 7 22 7 22 13"></polyline></svg>
            +12% so với tháng trước
          </div>
        </div>
      </div>

      <div className="h-64 w-full">
        <ResponsiveContainer width="100%" height="100%">
          <LineChart data={data} margin={{ top: 5, right: 10, left: 10, bottom: 0 }}>
            <XAxis 
              dataKey="name" 
              axisLine={false} 
              tickLine={false} 
              tick={{ fill: '#94a3b8', fontSize: 10, fontWeight: 700 }} 
              dy={15}
            />
            <Tooltip 
              contentStyle={{ borderRadius: '8px', border: 'none', boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1)' }}
              formatter={(value) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value)}
            />
            <Line 
              type="monotone" 
              dataKey="value" 
              stroke="#372660" 
              strokeWidth={4}
              dot={{ r: 6, fill: 'white', stroke: '#372660', strokeWidth: 3 }}
              activeDot={{ r: 8, fill: '#372660', stroke: 'white', strokeWidth: 2 }}
            />
          </LineChart>
        </ResponsiveContainer>
      </div>
    </div>
  )
}
