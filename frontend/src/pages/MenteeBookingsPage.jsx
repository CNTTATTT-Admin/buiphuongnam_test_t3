import React, { useState, useEffect } from 'react';
import { Clock, Calendar, Video, CheckCircle, Clock3 } from 'lucide-react';
import { bookingService } from '../services/bookingService';

export default function MenteeBookingsPage() {
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchBookings = async () => {
      try {
        setLoading(true);
        const res = await bookingService.getMyTraineeBookings();
        if (res.code === 1000) {
          setBookings(res.result);
        }
      } catch (error) {
        console.error("Failed to fetch mentee bookings", error);
      } finally {
        setLoading(false);
      }
    };
    fetchBookings();
  }, []);

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
            Chờ xác nhận
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
          <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold bg-slate-100 text-slate-600 border border-slate-200">
            Đã hoàn thành
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
                  <div className="flex md:w-1/3 items-start gap-3">
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
                  <div className="md:w-1/3 space-y-3 border-t md:border-t-0 md:border-l border-slate-100 pt-4 md:pt-0 md:pl-5">
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

                  {/* Right: Status & Action */}
                  <div className="flex md:w-1/3 flex-col justify-between items-start md:items-end border-t md:border-t-0 md:border-l border-slate-100 pt-4 md:pt-0 md:pl-5">
                    <div className="mb-4">
                      {getStatusBadge(booking.status)}
                    </div>

                    {booking.status === 'CONFIRMED' && booking.meetingLink ? (
                      <a 
                        href={booking.meetingLink.startsWith('http') ? booking.meetingLink : `https://${booking.meetingLink}`} 
                        target="_blank" 
                        rel="noreferrer"
                        className="inline-flex w-full md:w-auto items-center justify-center gap-2 px-4 py-2 bg-blue-50 text-blue-600 hover:bg-blue-100 rounded-lg text-sm font-semibold transition-colors"
                      >
                        <Video className="w-4 h-4" />
                        Vào lớp học
                      </a>
                    ) : (
                      <div className="flex items-center justify-center gap-2 px-4 py-2 bg-slate-50 text-slate-400 rounded-lg text-sm font-medium w-full md:w-auto cursor-not-allowed">
                        <Video className="w-4 h-4" />
                        Chưa có link
                      </div>
                    )}
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
