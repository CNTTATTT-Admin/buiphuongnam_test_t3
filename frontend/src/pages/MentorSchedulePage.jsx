import React, { useState, useEffect } from "react"
import { Calendar, Clock, ChevronLeft, ChevronRight, Video, FileText, PlusCircle, CheckCircle, Clock3, X } from "lucide-react"
import scheduleService from "../services/scheduleService"

export default function MentorSchedulePage() {
  const [bookings, setBookings] = useState([])
  const [timeSlots, setTimeSlots] = useState([])
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [loading, setLoading] = useState(true)

  // Modal form state
  const [slotDate, setSlotDate] = useState("")
  const [startTime, setStartTime] = useState("")
  const [endTime, setEndTime] = useState("")
  const [price, setPrice] = useState("500000")
  const [submitError, setSubmitError] = useState("")

  useEffect(() => {
    fetchData()
  }, [])

  const fetchData = async () => {
    try {
      setLoading(true)
      const [bookingsRes, slotsRes] = await Promise.all([
        scheduleService.getMyBookings(),
        scheduleService.getMyTimeSlots()
      ])
      
      if (bookingsRes.code === 1000) setBookings(bookingsRes.result)
      if (slotsRes.code === 1000) setTimeSlots(slotsRes.result)
    } catch (error) {
      console.error("Failed to fetch schedule data", error)
    } finally {
      setLoading(false)
    }
  }

  const handleBookingAction = async (bookingId, action) => {
    try {
      const res = await scheduleService.processBooking(bookingId, action)
      if (res.code === 1000) {
        fetchData() // Refresh list after action
      } else {
        alert(res.message || "Có lỗi xảy ra")
      }
    } catch (error) {
      alert("Lỗi kết nối Server")
    }
  }

  const handleMeetingLink = async (bookingId, link) => {
    try {
      const res = await scheduleService.updateMeetingLink(bookingId, link)
      if (res.code === 1000) {
        fetchData() // Refresh list
      } else {
        alert(res.message || "Có lỗi xảy ra")
      }
    } catch (error) {
      alert("Lỗi kết nối Server")
    }
  }

  const handleCreateSlot = async (e) => {
    e.preventDefault()
    setSubmitError("")
    
    if (!slotDate || !startTime || !endTime || !price) {
      setSubmitError("Vui lòng điền đủ thông tin")
      return
    }

    try {
      const startDateTime = `${slotDate}T${startTime}:00`
      const endDateTime = `${slotDate}T${endTime}:00`
      
      const res = await scheduleService.createTimeSlot({
        startTime: startDateTime,
        endTime: endDateTime,
        price: parseFloat(price)
      })

      if (res.code === 1000) {
        setIsModalOpen(false)
        fetchData() // Refresh list
        setSlotDate("")
        setStartTime("")
        setEndTime("")
      } else {
        setSubmitError(res.message || "Có lỗi xảy ra")
      }
    } catch (error) {
      setSubmitError(error.response?.data?.message || "Lỗi kết nối Server")
    }
  }

  const getStatusDisplay = (status) => {
    switch (status) {
      case 'PENDING': return { text: "Chờ xác nhận", color: "bg-amber-100 text-amber-700" }
      case 'CONFIRMED': return { text: "Đã xác nhận", color: "bg-blue-100 text-blue-700" }
      case 'COMPLETED': return { text: "Đã hoàn thành", color: "bg-green-100 text-green-700" }
      case 'REJECTED': return { text: "Đã từ chối", color: "bg-red-100 text-red-700" }
      case 'CANCELLED': return { text: "Đã hủy", color: "bg-slate-100 text-slate-600" }
      default: return { text: status, color: "bg-slate-100 text-slate-600" }
    }
  }

  const formatDate = (dateString) => {
    if (!dateString) return ""
    const d = new Date(dateString)
    return d.toLocaleDateString('vi-VN')
  }

  const formatTime = (dateString) => {
    if (!dateString) return ""
    const d = new Date(dateString)
    return d.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' })
  }

  return (
    <div className="flex gap-8 max-w-6xl mx-auto pb-10">
      <div className="flex-1">
        
        {/* Header Section */}
        <div className="flex justify-between items-center mb-8">
          <div>
             <h1 className="text-2xl font-bold text-slate-900">Lịch dạy của tôi</h1>
             <p className="text-slate-500 text-sm mt-1">Quản lý thời gian và các ca học sắp tới của bạn.</p>
          </div>
          <button 
             onClick={() => setIsModalOpen(true)}
             className="flex items-center gap-2 bg-[#372660] hover:bg-[#2b1d4c] text-white px-5 py-2.5 rounded-lg text-sm font-semibold transition-colors shadow-sm">
            <Calendar className="w-4 h-4" />
            Cập nhật khung giờ rảnh
          </button>
        </div>

        <div className="flex flex-col lg:flex-row gap-6">
          
          {/* LEFT COLUMN: Stats & Calendar */}
          <div className="w-full lg:w-1/3 space-y-6">
            
            {/* Stat Box */}
            <div className="bg-white rounded-2xl shadow-sm border border-slate-100 p-6">
              <div className="flex justify-between items-start mb-2">
                <p className="text-sm font-medium text-slate-500">Khung giờ đang mở</p>
                <div className="p-2 bg-slate-50 rounded-lg text-slate-400">
                  <Clock className="w-5 h-5" />
                </div>
              </div>
              <h3 className="text-3xl font-bold text-slate-900 mb-2">{timeSlots.filter(s => s.status === 'AVAILABLE').length} slot</h3>
            </div>

            {/* Stat Box 2 */}
            <div className="bg-white rounded-2xl shadow-sm border border-slate-100 p-6">
              <div className="flex justify-between items-start mb-2">
                <p className="text-sm font-medium text-slate-500">Tổng ca học</p>
                <div className="p-2 bg-slate-50 rounded-lg text-slate-400">
                  <Calendar className="w-5 h-5" />
                </div>
              </div>
              <h3 className="text-3xl font-bold text-slate-900">{bookings.length} ca</h3>
            </div>

            {/* Mini Calendar Widget (Mock View for aesthetics) */}
            <div className="bg-white rounded-2xl shadow-sm border border-slate-100 p-6 hidden md:block">
               <div className="flex justify-between items-center mb-6">
                 <h4 className="font-bold text-slate-900">Tháng này</h4>
                 <div className="flex gap-2">
                   <button className="text-slate-400 hover:text-slate-600"><ChevronLeft className="w-4 h-4" /></button>
                   <button className="text-slate-400 hover:text-slate-600"><ChevronRight className="w-4 h-4" /></button>
                 </div>
               </div>
               
               <div className="grid grid-cols-7 text-center gap-y-4">
                 {['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7'].map(d => (
                   <div key={d} className="text-[10px] font-bold text-slate-400">{d}</div>
                 ))}
                 
                 {/* Mock Dates row 1 */}
                 <div className="text-sm text-slate-300">24</div>
                 <div className="text-sm text-slate-300">25</div>
                 <div className="text-sm text-slate-300">26</div>
                 <div className="text-sm text-slate-300">27</div>
                 <div className="text-sm text-slate-300">28</div>
                 <div className="text-sm text-slate-300">29</div>
                 <div className="text-sm font-medium text-slate-700">1</div>

                 {/* Mock Dates row 2 */}
                 <div className="text-sm font-medium text-slate-700">2</div>
                 <div className="text-sm font-medium text-slate-700 relative">
                   3
                   <span className="absolute bottom-0 left-1/2 -translate-x-1/2 translate-y-2 w-1 h-1 bg-[#372660] rounded-full"></span>
                 </div>
                 <div className="text-sm font-medium text-slate-700">4</div>
                 <div className="text-sm font-medium bg-[#372660] text-white w-7 h-7 flex items-center justify-center rounded-full mx-auto shadow-sm">5</div>
                 <div className="text-sm font-medium text-slate-700">6</div>
                 <div className="text-sm font-medium text-slate-700 relative">
                   7
                   <span className="absolute bottom-0 left-1/2 -translate-x-1/2 translate-y-2 w-1 h-1 bg-[#372660] rounded-full"></span>
                 </div>
                 <div className="text-sm font-medium text-slate-700">8</div>
               </div>
            </div>

          </div>

          {/* RIGHT COLUMN: Upcoming Classes */}
          <div className="w-full lg:w-2/3">
             <div className="flex justify-between items-center mb-4 px-1">
               <h3 className="font-bold text-lg text-slate-900">Danh sách Ca học</h3>
             </div>

             <div className="space-y-4">
                {loading ? (
                  <div className="text-center py-10 text-slate-400">Đang tải lịch học...</div>
                ) : bookings.length === 0 ? (
                  <div 
                    onClick={() => setIsModalOpen(true)}
                    className="rounded-2xl border-2 border-dashed border-slate-200 p-10 flex flex-col items-center justify-center text-center bg-slate-50/50 mt-6 group hover:border-[#372660]/30 hover:bg-[#372660]/5 transition-colors cursor-pointer">
                    <div className="w-12 h-12 bg-white rounded-full shadow-sm flex items-center justify-center text-[#372660] mb-4">
                      <PlusCircle className="w-6 h-6" />
                    </div>
                    <p className="text-sm font-medium text-slate-400 mb-2">Chưa có ca học nào được đặt.</p>
                    <p className="text-sm font-bold text-[#372660]">Mở thêm khung giờ rảnh?</p>
                  </div>
                ) : (
                  bookings.map((cls) => {
                     const statusInfo = getStatusDisplay(cls.status)
                     return (
                     <div key={cls.id} className="bg-white rounded-2xl shadow-sm border border-slate-100 p-5 flex flex-col sm:flex-row gap-5 items-start sm:items-center justify-between hover:shadow-md transition-shadow">
                       
                       {/* User & Info */}
                       <div className="flex gap-4 items-center">
                         <div className="relative shrink-0">
                            <img src={cls.menteeAvatar || "https://i.pravatar.cc/150"} alt="Avatar" className="w-14 h-14 rounded-full object-cover border-2 border-white shadow-sm" />
                         </div>
                         
                         <div>
                           <div className="flex items-center gap-3 mb-1">
                             <h4 className="font-bold text-slate-900 text-lg">{cls.menteeName || `Học viên #${cls.menteeId}`}</h4>
                             <span className={`text-[10px] uppercase font-bold px-2 py-0.5 rounded-full ${statusInfo.color}`}>
                               {statusInfo.text}
                             </span>
                           </div>
                           <p className="text-sm font-semibold text-[#372660] mb-2 line-clamp-1">{cls.menteeNotes || "Không có ghi chú"}</p>
                           <div className="flex flex-wrap gap-4 text-xs font-medium text-slate-500">
                             <span className="flex items-center gap-1.5 border border-slate-100 bg-slate-50 px-2 py-1 rounded-md"><Calendar className="w-3.5 h-3.5" /> {formatDate(cls.startTime)}</span>
                             <span className="flex items-center gap-1.5 border border-slate-100 bg-slate-50 px-2 py-1 rounded-md"><Clock3 className="w-3.5 h-3.5" /> {formatTime(cls.startTime)} - {formatTime(cls.endTime)}</span>
                           </div>
                         </div>
                       </div>

                       {/* Action Buttons for Bookings */}
                       <div className="w-full sm:w-auto mt-2 sm:mt-0 flex flex-col sm:flex-row gap-2">
                         {cls.status === 'PENDING' && (
                            <>
                              <button 
                                onClick={() => handleBookingAction(cls.id, 'REJECT')}
                                className="px-4 py-2 border border-red-200 text-red-600 rounded-lg text-sm font-bold hover:bg-red-50 transition-colors">
                                Từ chối
                              </button>
                              <button 
                                onClick={() => handleBookingAction(cls.id, 'CONFIRM')}
                                className="px-4 py-2 bg-[#372660] text-white rounded-lg text-sm font-bold hover:bg-[#2b1d4c] transition-colors">
                                Xác nhận
                              </button>
                            </>
                         )}
                         {cls.status === 'CONFIRMED' && (
                            <>
                              {!cls.meetingLink ? (
                                <button 
                                  onClick={() => {
                                    const link = prompt("Nhập link phòng học (Google Meet, Zoom...):")
                                    if (link) handleMeetingLink(cls.id, link)
                                  }}
                                  className="px-4 py-2 border border-blue-200 text-blue-600 rounded-lg text-sm font-bold hover:bg-blue-50 transition-colors flex items-center gap-2">
                                  <PlusCircle className="w-4 h-4" /> Thêm Link Meet
                                </button>
                              ) : (
                                <a 
                                  href={cls.meetingLink} target="_blank" rel="noreferrer"
                                  className="px-4 py-2 bg-[#372660] text-white rounded-lg text-sm font-bold hover:bg-[#2b1d4c] transition-colors flex items-center gap-2">
                                  <Video className="w-4 h-4" /> Vào phòng học
                                </a>
                              )}
                            </>
                         )}
                         {cls.status === 'COMPLETED' && (
                            <div className="text-xs font-semibold text-emerald-600 border border-emerald-200 px-3 py-1.5 rounded bg-emerald-50">
                               Ca học đã hoàn thành
                            </div>
                         )}
                         {cls.status === 'REJECTED' && (
                            <div className="text-xs font-semibold text-red-600 border border-red-200 px-3 py-1.5 rounded bg-red-50">
                               Đã được từ chối
                            </div>
                         )}
                       </div>
                     </div>
                   )})
                )}
             </div>
          </div>
        </div>
      </div>

      {/* CREATE TIMESLOT MODAL */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/50 backdrop-blur-sm">
          <div className="bg-white rounded-2xl w-full max-w-md shadow-2xl overflow-hidden animate-in fade-in zoom-in-95 duration-200">
            <div className="px-6 py-4 border-b border-slate-100 flex justify-between items-center bg-slate-50/50">
              <h3 className="font-bold text-lg text-slate-900">Mở khung giờ rảnh</h3>
              <button onClick={() => setIsModalOpen(false)} className="text-slate-400 hover:text-slate-600 bg-white p-1 rounded-md shadow-sm border border-slate-100">
                <X className="w-5 h-5" />
              </button>
            </div>
            
            <form onSubmit={handleCreateSlot} className="p-6 space-y-4">
              {submitError && (
                <div className="bg-red-50 text-red-600 p-3 rounded-lg text-sm border border-red-100 font-medium">
                  {submitError}
                </div>
              )}

              <div>
                <label className="block text-sm font-semibold text-slate-700 mb-1.5">Ngày dạy <span className="text-red-500">*</span></label>
                <input 
                  type="date" 
                  value={slotDate}
                  onChange={(e) => setSlotDate(e.target.value)}
                  className="w-full p-2.5 border border-slate-200 rounded-lg focus:ring-2 focus:ring-[#372660] focus:border-[#372660] outline-none text-sm transition-all"
                  required
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-semibold text-slate-700 mb-1.5">Giờ Bắt đầu <span className="text-red-500">*</span></label>
                  <input 
                    type="time" 
                    value={startTime}
                    onChange={(e) => setStartTime(e.target.value)}
                    className="w-full p-2.5 border border-slate-200 rounded-lg focus:ring-2 focus:ring-[#372660] focus:border-[#372660] outline-none text-sm transition-all"
                    required
                  />
                </div>
                <div>
                  <label className="block text-sm font-semibold text-slate-700 mb-1.5">Giờ Kết thúc <span className="text-red-500">*</span></label>
                  <input 
                    type="time" 
                    value={endTime}
                    onChange={(e) => setEndTime(e.target.value)}
                    className="w-full p-2.5 border border-slate-200 rounded-lg focus:ring-2 focus:ring-[#372660] focus:border-[#372660] outline-none text-sm transition-all"
                    required
                  />
                </div>
              </div>

              <div>
                <label className="block text-sm font-semibold text-slate-700 mb-1.5">Giá tiền (VNĐ) <span className="text-red-500">*</span></label>
                <input 
                  type="number" 
                  step="10000"
                  value={price}
                  onChange={(e) => setPrice(e.target.value)}
                  className="w-full p-2.5 border border-slate-200 rounded-lg focus:ring-2 focus:ring-[#372660] focus:border-[#372660] outline-none text-sm transition-all bg-slate-50"
                  required
                />
              </div>

              <div className="pt-4 flex gap-3">
                <button 
                  type="button" 
                  onClick={() => setIsModalOpen(false)}
                  className="flex-1 px-4 py-2.5 bg-white border border-slate-200 text-slate-700 font-bold rounded-lg hover:bg-slate-50 transition-colors"
                >
                  Hủy
                </button>
                <button 
                  type="submit"
                  className="flex-1 px-4 py-2.5 bg-[#372660] text-white font-bold rounded-lg hover:bg-[#2b1d4c] shadow-md transition-colors"
                >
                  Lưu khung giờ
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

    </div>
  )
}
