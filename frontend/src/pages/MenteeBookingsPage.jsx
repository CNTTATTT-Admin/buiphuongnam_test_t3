import React, { useState, useEffect } from 'react';
import { Clock, Calendar, Video, CheckCircle, Clock3, Star, AlertTriangle, X, Send, ChevronLeft, ChevronRight } from 'lucide-react';
import { bookingService } from '../services/bookingService';

export default function MenteeBookingsPage() {
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState(null);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [statusFilter, setStatusFilter] = useState("ALL");
  const size = 10;

  // Review modal state
  const [reviewModal, setReviewModal] = useState(null); // bookingId
  const [reviewForm, setReviewForm] = useState({ rating: 5, comment: '' });
  const [submittingReview, setSubmittingReview] = useState(false);

  // Track which bookings have been reviewed
  const [reviewedBookings, setReviewedBookings] = useState(new Set());

  useEffect(() => {
    fetchBookings();
  }, [page, statusFilter]);

  const handleStatusChange = (newStatus) => {
    setStatusFilter(newStatus);
    setPage(0);
  };

  const fetchBookings = async () => {
    try {
      setLoading(true);
      const res = await bookingService.getMyTraineeBookings(page, size, statusFilter);
      if (res.code === 1000) {
        setBookings(res.result.content);
        setTotalPages(res.result.totalPages);
        // Check which completed bookings already have reviews
        const completed = res.result.content.filter(b => b.status === 'COMPLETED');
        const reviewed = new Set();
        for (const b of completed) {
          try {
            const rRes = await bookingService.getReviewByBooking(b.id);
            if (rRes.code === 1000 && rRes.result) {
              reviewed.add(b.id);
            }
          } catch (e) { /* ignore */ }
        }
        setReviewedBookings(reviewed);
      }
    } catch (error) {
      console.error("Failed to fetch mentee bookings", error);
    } finally {
      setLoading(false);
    }
  };

  const handleComplete = async (bookingId) => {
    if (!window.confirm('Xác nhận hoàn thành ca học này? Số tiền sẽ được chuyển vào ví của Mentor.')) return;
    try {
      const res = await bookingService.completeBooking(bookingId);
      if (res.code === 1000) {
        setMessage({ type: 'success', text: 'Đã hoàn thành ca học! Số tiền đã được chuyển vào ví Mentor.' });
        fetchBookings();
      }
    } catch (err) {
      setMessage({ type: 'error', text: err.response?.data?.message || 'Có lỗi xảy ra' });
    }
  };

  const handleOpenReview = (bookingId) => {
    setReviewModal(bookingId);
    setReviewForm({ rating: 5, comment: '' });
  };

  const handleSubmitReview = async (e) => {
    e.preventDefault();
    try {
      setSubmittingReview(true);
      const res = await bookingService.submitReview({
        bookingId: reviewModal,
        rating: reviewForm.rating,
        comment: reviewForm.comment
      });
      if (res.code === 1000) {
        setMessage({ type: 'success', text: 'Cảm ơn bạn đã đánh giá Mentor!' });
        setReviewedBookings(prev => new Set([...prev, reviewModal]));
        setReviewModal(null);
      }
    } catch (err) {
      setMessage({ type: 'error', text: err.response?.data?.message || 'Có lỗi xảy ra khi gửi đánh giá' });
    } finally {
      setSubmittingReview(false);
    }
  };

  const formatTime = (isoString) => {
    return new Date(isoString).toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
  };

  const formatDate = (isoString) => {
    return new Date(isoString).toLocaleDateString('vi-VN');
  };

  const getStatusBadge = (status) => {
    switch (status) {
      case 'PENDING':
        return (
          <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold bg-amber-50 text-amber-600 border border-amber-200">
            <Clock3 className="w-3.5 h-3.5" />
            Chờ thanh toán
          </span>
        );
      case 'PAID':
        return (
          <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold bg-indigo-50 text-indigo-600 border border-indigo-200">
            <Clock3 className="w-3.5 h-3.5" />
            Đã thanh toán, chờ Mentor xác nhận
          </span>
        );
      case 'CONFIRMED':
        return (
          <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold bg-emerald-50 text-emerald-600 border border-emerald-200">
            <CheckCircle className="w-3.5 h-3.5" />
            Đã xác nhận
          </span>
        );
      case 'REJECTED':
        return (
          <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold bg-rose-50 text-rose-600 border border-rose-200">
            Đã từ chối
          </span>
        );
      case 'COMPLETED':
        return (
          <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold bg-blue-50 text-blue-600 border border-blue-200">
            <CheckCircle className="w-3.5 h-3.5" />
            Đã hoàn thành
          </span>
        );
      case 'CANCELLED':
        return (
          <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold bg-slate-100 text-slate-500 border border-slate-200">
            Đã hủy
          </span>
        );
      default:
        return null;
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center p-12">
        <div className="w-8 h-8 border-4 border-[#372660]/20 border-t-[#372660] rounded-full animate-spin" />
      </div>
    );
  }

  return (
    <div className="bg-white rounded-2xl shadow-sm border border-slate-100 overflow-hidden">
      <div className="p-6 border-b border-slate-100">
        <h2 className="text-xl font-bold text-slate-900">Lịch học của tôi</h2>
        <p className="text-sm text-slate-500 mt-1">Quản lý các ca học bạn đã đặt với Mentor</p>
      </div>

      {message && (
        <div className={`mx-6 mt-4 p-4 rounded-xl flex items-center justify-between text-sm font-medium border ${
          message.type === 'success' ? 'bg-emerald-50 text-emerald-700 border-emerald-100' : 'bg-red-50 text-red-700 border-red-100'
        }`}>
          <span>{message.text}</span>
          <button onClick={() => setMessage(null)}><X className="w-4 h-4" /></button>
        </div>
      )}

      {/* Status Filter */}
      <div className="px-6 py-3 border-b border-slate-100 bg-slate-50/50">
        <div className="flex flex-nowrap overflow-x-auto gap-2 scrollbar-hide">
          {[
            { id: 'ALL', label: 'Tất cả' },
            { id: 'PENDING', label: 'Chờ thanh toán' },
            { id: 'PAID', label: 'Đã thanh toán' },
            { id: 'CONFIRMED', label: 'Đã xác nhận' },
            { id: 'COMPLETED', label: 'Đã hoàn thành' },
            { id: 'CANCELLED', label: 'Đã hủy' },
            { id: 'REJECTED', label: 'Đã từ chối' }
          ].map(st => (
            <button 
              key={st.id}
              onClick={() => handleStatusChange(st.id)}
              className={`px-4 py-1.5 rounded-full text-xs font-bold whitespace-nowrap transition-colors ${
                statusFilter === st.id 
                  ? 'bg-[#372660] text-white shadow-sm' 
                  : 'bg-white border border-slate-200 text-slate-600 hover:bg-slate-50'
              }`}
            >
              {st.label}
            </button>
          ))}
        </div>
      </div>

      <div className="p-6">
        {bookings.length === 0 ? (
          <div className="text-center py-12">
            <div className="w-16 h-16 bg-slate-50 rounded-full flex items-center justify-center mx-auto mb-4">
              <Calendar className="w-8 h-8 text-slate-400" />
            </div>
            <h3 className="text-base font-semibold text-slate-900 mb-1">Chưa có lịch học nào</h3>
            <p className="text-sm text-slate-500 mb-6">Bạn chưa đặt lịch với Mentor nào. Hãy tìm kiếm Mentor và bắt đầu học ngay nhé!</p>
            <a href="/search" className="inline-flex items-center gap-2 px-6 py-2.5 bg-[#372660] text-white text-sm font-semibold rounded-xl hover:bg-[#2b1d4c] transition-colors">
              Tìm Mentor
            </a>
          </div>
        ) : (
          <div className="space-y-4">
            {bookings.map((booking) => (
              <div key={booking.id} className="bg-white border text-sm border-slate-200 rounded-xl p-5 hover:border-[#372660]/30 transition-colors">
                <div className="flex flex-col md:flex-row gap-5">
                  {/* Left: Mentor Info */}
                  <div className="flex md:w-1/4 items-start gap-3">
                    <img 
                      src={booking.mentorAvatar || "https://ui-avatars.com/api/?name=" + booking.mentorName} 
                      alt="" 
                      className="w-12 h-12 rounded-full object-cover border border-slate-100"
                    />
                    <div>
                      <p className="text-xs text-slate-500 font-medium mb-0.5">Mentor</p>
                      <h4 className="font-bold text-slate-900">{booking.mentorName}</h4>
                      <a href={`/mentor/${booking.mentorId}`} className="text-xs text-[#372660] hover:underline font-medium">Xem hồ sơ</a>
                    </div>
                  </div>

                  {/* Middle: Time & Notes */}
                  <div className="md:w-1/4 space-y-3 border-t md:border-t-0 md:border-l border-slate-100 pt-4 md:pt-0 md:pl-5">
                    <div className="flex items-center gap-2 text-slate-700 font-medium text-sm">
                      <Calendar className="w-4 h-4 text-[#372660]" />
                      {formatDate(booking.startTime)}
                    </div>
                    <div className="flex items-center gap-2 text-slate-600 text-sm">
                      <Clock className="w-4 h-4 text-slate-400" />
                      {formatTime(booking.startTime)} - {formatTime(booking.endTime)}
                    </div>
                    {booking.menteeNotes && (
                      <div className="mt-2 bg-slate-50 rounded-lg p-3 border border-slate-100">
                        <p className="text-xs font-semibold text-slate-600 mb-1">Lời nhắn của bạn:</p>
                        <p className="text-sm text-slate-700 italic">"{booking.menteeNotes}"</p>
                      </div>
                    )}
                  </div>

                  {/* Right: Status & Actions */}
                  <div className="flex md:w-1/2 flex-col justify-between items-start md:items-end border-t md:border-t-0 md:border-l border-slate-100 pt-4 md:pt-0 md:pl-5">
                    <div className="mb-3">
                      {getStatusBadge(booking.status)}
                    </div>

                    <div className="flex flex-wrap gap-2 w-full md:w-auto justify-end">
                      {/* CONFIRMED → show meeting link + complete button */}
                      {booking.status === 'CONFIRMED' && (
                        <>
                          {booking.meetingLink ? (
                            <a 
                              href={booking.meetingLink.startsWith('http') ? booking.meetingLink : `https://${booking.meetingLink}`} 
                              target="_blank" 
                              rel="noreferrer"
                              className="inline-flex items-center gap-2 px-4 py-2 bg-blue-50 text-blue-600 hover:bg-blue-100 rounded-lg text-sm font-semibold transition-colors"
                            >
                              <Video className="w-4 h-4" />
                              Vào lớp học
                            </a>
                          ) : (
                            <div className="flex items-center gap-2 px-4 py-2 bg-slate-50 text-slate-400 rounded-lg text-sm font-medium cursor-not-allowed">
                              <Video className="w-4 h-4" />
                              Chưa có link
                            </div>
                          )}
                          <button
                            onClick={() => handleComplete(booking.id)}
                            className="inline-flex items-center gap-2 px-4 py-2 bg-emerald-500 text-white hover:bg-emerald-600 rounded-lg text-sm font-bold transition-colors shadow-sm"
                          >
                            <CheckCircle className="w-4 h-4" />
                            Hoàn thành
                          </button>
                        </>
                      )}

                      {/* PENDING / PAID → show waiting state */}
                      {(booking.status === 'PENDING' || booking.status === 'PAID') && (
                        <div className="flex items-center gap-2 px-4 py-2 bg-slate-50 text-slate-400 rounded-lg text-sm font-medium cursor-not-allowed">
                          <Clock3 className="w-4 h-4" />
                          {booking.status === 'PAID'
                            ? 'Đã thanh toán, chờ Mentor xác nhận'
                            : 'Đang chờ thanh toán'}
                        </div>
                      )}

                      {/* COMPLETED → show review + complaint buttons */}
                      {booking.status === 'COMPLETED' && (
                        <>
                          {reviewedBookings.has(booking.id) ? (
                            <span className="inline-flex items-center gap-1.5 px-4 py-2 bg-amber-50 text-amber-700 rounded-lg text-sm font-semibold border border-amber-200">
                              <Star className="w-4 h-4 fill-current" />
                              Đã đánh giá
                            </span>
                          ) : (
                            <button
                              onClick={() => handleOpenReview(booking.id)}
                              className="inline-flex items-center gap-2 px-4 py-2 bg-amber-500 text-white hover:bg-amber-600 rounded-lg text-sm font-bold transition-colors shadow-sm"
                            >
                              <Star className="w-4 h-4" />
                              Đánh giá Mentor
                            </button>
                          )}
                          <button
                            onClick={() => alert('Tính năng Khiếu nại / Hoàn tiền sẽ được phát triển trong phiên bản tiếp theo.')}
                            className="inline-flex items-center gap-2 px-4 py-2 bg-slate-100 text-slate-600 hover:bg-slate-200 rounded-lg text-sm font-semibold transition-colors"
                          >
                            <AlertTriangle className="w-4 h-4" />
                            Khiếu nại
                          </button>
                        </>
                      )}
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {totalPages > 1 && (
        <div className="flex items-center justify-center gap-2 p-6 pt-0">
          <button 
            onClick={() => setPage(p => Math.max(0, p - 1))}
            disabled={page === 0}
            className="p-2 rounded-lg border border-slate-200 text-slate-600 hover:bg-slate-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            <ChevronLeft className="w-5 h-5" />
          </button>
          <div className="flex gap-1">
            {[...Array(totalPages)].map((_, i) => (
              <button
                key={i}
                onClick={() => setPage(i)}
                className={`w-8 h-8 flex items-center justify-center rounded-lg text-sm font-semibold transition-colors ${
                  page === i 
                    ? 'bg-[#372660] text-white' 
                    : 'text-slate-600 hover:bg-slate-50'
                }`}
              >
                {i + 1}
              </button>
            ))}
          </div>
          <button 
            onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))}
            disabled={page === totalPages - 1}
            className="p-2 rounded-lg border border-slate-200 text-slate-600 hover:bg-slate-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            <ChevronRight className="w-5 h-5" />
          </button>
        </div>
      )}

      {/* Review Modal */}
      {reviewModal && (
        <div className="fixed inset-0 bg-black/50 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-md overflow-hidden">
            <div className="flex justify-between items-center p-6 border-b border-slate-100">
              <h2 className="text-xl font-bold text-slate-800">Đánh giá Mentor</h2>
              <button onClick={() => setReviewModal(null)} className="text-slate-400 hover:text-slate-600"><X className="w-6 h-6" /></button>
            </div>
            <form onSubmit={handleSubmitReview} className="p-6 space-y-5">
              {/* Star Rating */}
              <div className="space-y-2">
                <label className="text-xs font-bold text-slate-700 uppercase tracking-wide">Đánh giá sao (*)</label>
                <div className="flex gap-1">
                  {[1, 2, 3, 4, 5].map(star => (
                    <button
                      key={star}
                      type="button"
                      onClick={() => setReviewForm({ ...reviewForm, rating: star })}
                      className="p-1 transition-transform hover:scale-110"
                    >
                      <Star className={`w-8 h-8 ${star <= reviewForm.rating ? 'text-amber-500 fill-current' : 'text-slate-200'}`} />
                    </button>
                  ))}
                </div>
                <p className="text-xs text-slate-500">{reviewForm.rating}/5 sao</p>
              </div>

              {/* Comment */}
              <div className="space-y-1.5">
                <label className="text-xs font-bold text-slate-700 uppercase tracking-wide">Nhận xét</label>
                <textarea
                  rows={4}
                  placeholder="Chia sẻ trải nghiệm học tập của bạn..."
                  value={reviewForm.comment}
                  onChange={(e) => setReviewForm({ ...reviewForm, comment: e.target.value })}
                  className="w-full bg-slate-50 border border-slate-200 rounded-xl p-3 text-sm focus:outline-none focus:ring-2 focus:ring-[#372660]/30 resize-none"
                />
              </div>

              <div className="flex gap-3 pt-2">
                <button
                  type="button"
                  onClick={() => setReviewModal(null)}
                  className="flex-1 px-4 py-2.5 border border-slate-200 text-slate-600 font-bold rounded-xl hover:bg-slate-50 transition-all"
                >
                  Hủy
                </button>
                <button
                  type="submit"
                  disabled={submittingReview}
                  className="flex-1 px-4 py-2.5 bg-[#372660] text-white font-bold rounded-xl hover:bg-[#2b1d4c] transition-all flex items-center justify-center gap-2 disabled:opacity-50"
                >
                  <Send className="w-4 h-4" />
                  {submittingReview ? 'Đang gửi...' : 'Gửi đánh giá'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
