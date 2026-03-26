import React, { useState, useEffect } from 'react';
import { AlertTriangle, CheckCircle, XCircle, Search, X } from 'lucide-react';
import { Input } from '../../components/ui/input';
import { disputeService } from '../../services/disputeService';

export default function AdminDisputesPage() {
  const [disputes, setDisputes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState('ALL');
  const [searchTerm, setSearchTerm] = useState('');
  const [message, setMessage] = useState(null);
  const [resolvingId, setResolvingId] = useState(null);
  const [adminNote, setAdminNote] = useState('');

  useEffect(() => { fetchData(); }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      const res = await disputeService.getAllDisputes({ page: 0, size: 50 });
      if (res.code === 1000) setDisputes(res.result.content);
    } catch (err) { console.error(err); } finally { setLoading(false); }
  };

  const handleResolve = async (id, acceptRefund) => {
    if (!adminNote.trim()) {
      setMessage({ type: 'error', text: 'Vui lòng nhập nhận xét/lý do xử lý' });
      return;
    }
    if (!window.confirm(`Xác nhận ${acceptRefund ? 'HOÀN TIỀN' : 'TỪ CHỐI'} khiếu nại này?`)) return;
    
    try {
      const res = await disputeService.resolveDispute(id, { adminNote, acceptRefund });
      if (res.code === 1000) {
        setMessage({ type: 'success', text: `Đã xử lý khiếu nại thành công (${acceptRefund ? 'Hoàn tiền' : 'Từ chối'})` });
        setResolvingId(null);
        setAdminNote('');
        fetchData();
      }
    } catch (err) {
      setMessage({ type: 'error', text: err.response?.data?.message || 'Có lỗi xảy ra' });
    }
  };

  const statusConfig = {
    PENDING: { label: 'Chờ xử lý', color: 'bg-amber-50 text-amber-700 border-amber-200', icon: AlertTriangle },
    RESOLVED_REFUND: { label: 'Đã hoàn tiền', color: 'bg-emerald-50 text-emerald-700 border-emerald-200', icon: CheckCircle },
    RESOLVED_NO_REFUND: { label: 'Từ chối hoàn tiền', color: 'bg-slate-50 text-slate-700 border-slate-200', icon: XCircle },
    REJECTED: { label: 'Đã hủy', color: 'bg-red-50 text-red-700 border-red-200', icon: XCircle },
  };

  const filtered = disputes
    .filter(d => filter === 'ALL' || d.status === filter)
    .filter(d => d.creatorName?.toLowerCase().includes(searchTerm.toLowerCase()) || d.reason?.toLowerCase().includes(searchTerm.toLowerCase()));

  return (
    <div className="p-6 max-w-6xl mx-auto">
      <h1 className="text-2xl font-bold text-slate-800 mb-1">Quản lý Khiếu nại</h1>
      <p className="text-slate-500 mb-6">Xử lý các đơn khiếu nại và hoàn tiền cho học viên</p>

      {message && (
        <div className={`mb-6 p-4 rounded-xl flex items-center justify-between text-sm font-medium border ${
          message.type === 'success' ? 'bg-emerald-50 text-emerald-700 border-emerald-100' : 'bg-red-50 text-red-700 border-red-100'
        }`}>
          <span>{message.text}</span>
          <button onClick={() => setMessage(null)}><X className="w-4 h-4" /></button>
        </div>
      )}

      <div className="bg-white rounded-xl border border-slate-100 shadow-sm overflow-hidden">
        {/* Filters */}
        <div className="p-4 border-b border-slate-100 bg-slate-50/50 flex flex-col sm:flex-row items-center gap-3">
          <div className="relative flex-1 max-w-sm">
            <Search className="absolute left-3 top-2.5 w-4 h-4 text-slate-400" />
            <Input placeholder="Tìm theo tên hoặc lý do..." value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} className="pl-9 bg-white" />
          </div>
          <div className="flex gap-2 flex-wrap">
            {['ALL', 'PENDING', 'RESOLVED_REFUND', 'RESOLVED_NO_REFUND'].map(s => (
              <button key={s} onClick={() => setFilter(s)} className={`px-3 py-1.5 text-xs font-bold rounded-lg transition-colors ${filter === s ? 'bg-[#372660] text-white' : 'bg-white text-slate-600 border border-slate-200 hover:bg-slate-50'}`}>
                {s === 'ALL' ? 'Tất cả' : statusConfig[s].label}
              </button>
            ))}
          </div>
        </div>

        {/* Table */}
        <div className="overflow-x-auto">
          <table className="w-full text-left">
            <thead>
              <tr className="bg-slate-50 text-slate-500 text-xs uppercase font-bold tracking-wider">
                <th className="px-5 py-3">ID Ca học</th>
                <th className="px-5 py-3">Người khiếu nại</th>
                <th className="px-5 py-3">Lý do</th>
                <th className="px-5 py-3">Trạng thái</th>
                <th className="px-5 py-3">Thời gian</th>
                <th className="px-5 py-3 text-right">Thao tác</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {loading ? (
                <tr><td colSpan="6" className="px-5 py-10 text-center text-slate-400">Đang tải...</td></tr>
              ) : filtered.length === 0 ? (
                <tr><td colSpan="6" className="px-5 py-10 text-center text-slate-400">Không có đơn khiếu nại nào</td></tr>
              ) : (
                filtered.map(d => {
                  const config = statusConfig[d.status] || statusConfig.PENDING;
                  const StatusIcon = config.icon;
                  return (
                    <React.Fragment key={d.id}>
                      <tr className="hover:bg-slate-50/50 transition-colors">
                        <td className="px-5 py-4 text-sm font-bold text-slate-900">#{d.bookingId}</td>
                        <td className="px-5 py-4 text-sm font-bold text-slate-700">{d.creatorName}</td>
                        <td className="px-5 py-4 text-sm text-slate-600 line-clamp-2 max-w-xs">{d.reason}</td>
                        <td className="px-5 py-4">
                          <span className={`inline-flex items-center gap-1 px-2.5 py-1 text-xs font-bold rounded-full border ${config.color}`}>
                            <StatusIcon className="w-3 h-3" /> {config.label}
                          </span>
                        </td>
                        <td className="px-5 py-4 text-xs text-slate-500">{new Date(d.createdAt).toLocaleDateString('vi-VN')}</td>
                        <td className="px-5 py-4 text-right">
                          {d.status === 'PENDING' ? (
                            <button onClick={() => setResolvingId(resolvingId === d.id ? null : d.id)} className="px-3 py-1.5 bg-[#372660] text-white text-xs font-bold rounded-lg hover:bg-opacity-90 transition-colors">
                              Xử lý
                            </button>
                          ) : (
                            <span className="text-xs text-slate-400 truncate max-w-[150px] inline-block" title={d.adminNote}>Lưu ý: {d.adminNote}</span>
                          )}
                        </td>
                      </tr>
                      {resolvingId === d.id && (
                        <tr>
                          <td colSpan="6" className="px-5 py-4 bg-slate-50/80 border-t border-slate-100">
                            <div className="flex flex-col gap-3">
                              <p className="text-sm"><strong>Lý do chi tiết:</strong> {d.reason}</p>
                              <div className="flex items-center gap-3">
                                <Input placeholder="Nhập nhận xét của Admin bắt buộc..." value={adminNote} onChange={(e) => setAdminNote(e.target.value)} className="bg-white flex-1 max-w-md" />
                                <button onClick={() => handleResolve(d.id, true)} className="px-4 py-2 bg-emerald-600 text-white text-xs font-bold rounded-lg hover:bg-emerald-700 transition-colors">Đồng ý Hoàn Tiền</button>
                                <button onClick={() => handleResolve(d.id, false)} className="px-4 py-2 bg-red-600 text-white text-xs font-bold rounded-lg hover:bg-red-700 transition-colors">Từ chối Không Hoàn Tiền</button>
                                <button onClick={() => { setResolvingId(null); setAdminNote(''); }} className="text-slate-500 hover:text-slate-700"><X className="w-4 h-4" /></button>
                              </div>
                            </div>
                          </td>
                        </tr>
                      )}
                    </React.Fragment>
                  );
                })
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
