import React, { useState } from "react"
import { MoreHorizontal, Heart, MessageSquare, Send } from "lucide-react"
import postInteractionService from "../../services/postInteractionService"
import { useAuth } from "../../contexts/AuthContext"

export default function PostCard({ post }) {
  const { user } = useAuth()
  
  // Use backend properties or fallback to empty strings
  const authorName = post.authorName || "Người dùng ẩn danh";
  const avatar = post.authorAvatarUrl || "https://ui-avatars.com/api/?name=" + encodeURIComponent(authorName);
  const time = new Date(post.createdAt).toLocaleDateString("vi-VN", {
    hour: "2-digit", minute: "2-digit", day: "2-digit", month: "2-digit", year: "numeric"
  });

  // Interaction States
  const [isLiked, setIsLiked] = useState(post.isLiked || false)
  const [likeCount, setLikeCount] = useState(post.likeCount || 0)
  const [showComments, setShowComments] = useState(false)
  const [comments, setComments] = useState([])
  const [newComment, setNewComment] = useState("")
  const [commentsLoaded, setCommentsLoaded] = useState(false)
  const [isSubmitting, setIsSubmitting] = useState(false)

  const handleLike = async () => {
    // Optimistic UI Update
    setIsLiked(!isLiked)
    setLikeCount(prev => isLiked ? Math.max(0, prev - 1) : prev + 1)
    
    try {
      await postInteractionService.toggleLike(post.id)
    } catch (error) {
      // Revert on failure
      setIsLiked(!isLiked)
      setLikeCount(prev => isLiked ? prev + 1 : Math.max(0, prev - 1))
      console.error("Lỗi khi thả tim", error)
    }
  }

  const toggleComments = async () => {
    setShowComments(!showComments)
    if (!showComments && !commentsLoaded) {
      try {
        const response = await postInteractionService.getComments(post.id)
        if (response.code === 1000) {
          setComments(response.result)
          setCommentsLoaded(true)
        }
      } catch (error) {
        console.error("Lỗi khi tải bình luận", error)
      }
    }
  }

  const submitComment = async (e) => {
    e.preventDefault()
    if (!newComment.trim() || isSubmitting) return

    setIsSubmitting(true)
    try {
      const response = await postInteractionService.addComment(post.id, newComment)
      if (response.code === 1000) {
        // Appending to the end of the array (since descending visual order) or directly setting if we want it at the top. 
        // Let's append to bottom for a standard chat feel
        setComments([...comments, response.result])
        setNewComment("")
      }
    } catch (error) {
      console.error("Lỗi khi gửi bình luận", error)
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <div className="bg-white rounded-xl shadow-sm border border-slate-100 mb-6 flex flex-col overflow-hidden">
      {/* Header */}
      <div className="p-5 pb-3 flex justify-between items-start">
        <div className="flex gap-3">
          <img src={avatar} alt={authorName} className="w-10 h-10 rounded-full object-cover shrink-0" />
          <div>
            <h4 className="font-semibold text-slate-900 leading-tight">{authorName}</h4>
            <div className="flex items-center gap-1.5 text-xs text-slate-500 mt-0.5">
              <span>{time}</span>
            </div>
          </div>
        </div>
        <button className="text-slate-400 hover:text-slate-600 p-1">
          <MoreHorizontal className="w-5 h-5" />
        </button>
      </div>

      {/* Content */}
      <div className="px-5 mb-4">
        <p className="text-slate-700 text-sm leading-relaxed whitespace-pre-wrap">
          {post.content}
        </p>
      </div>

      {/* Images if available */}
      {post.imageUrls && post.imageUrls.length > 0 && (
        <div className="w-full bg-slate-50 border-t border-slate-100 flex overflow-x-auto snap-x">
          {post.imageUrls.map((url, idx) => (
             <img key={idx} src={url} alt={`Post image ${idx}`} className="w-full sm:w-auto h-auto sm:max-h-[400px] object-contain snap-center shrink-0 border-r border-slate-100 last:border-r-0" />
          ))}
        </div>
      )}

      {/* Actions */}
      <div className="px-5 py-3 border-t border-slate-100 mt-auto flex justify-between items-center bg-slate-50/50">
        <div className="flex gap-6">
          <button 
            onClick={handleLike}
            className={`flex items-center gap-2 font-medium text-sm transition-colors ${isLiked ? 'text-red-500' : 'text-slate-500 hover:text-red-500'}`}
          >
            <Heart className={`w-5 h-5 ${isLiked ? 'fill-current' : ''}`} />
            <span>Thích {likeCount > 0 && `(${likeCount})`}</span>
          </button>
          
          <button 
            onClick={toggleComments}
            className={`flex items-center gap-2 font-medium text-sm transition-colors ${showComments ? 'text-[#372660]' : 'text-slate-500 hover:text-[#372660]'}`}
          >
            <MessageSquare className="w-5 h-5" />
            <span>Bình luận</span>
          </button>
        </div>
      </div>

      {/* Comments Section */}
      {showComments && (
        <div className="px-5 pb-5 bg-slate-50/30 border-t border-slate-100">
          
          <div className="space-y-4 my-4 max-h-64 overflow-y-auto pr-2 custom-scrollbar">
            {comments.length === 0 && commentsLoaded ? (
               <p className="text-xs text-center text-slate-400 py-2">Chưa có bình luận nào. Hãy là người đầu tiên bình luận!</p>
            ) : (
               comments.map((comment) => (
                 <div key={comment.id} className="flex gap-3">
                   <img src={comment.userAvatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(comment.userName)}`} alt="Avatar" className="w-8 h-8 rounded-full flex-shrink-0" />
                   <div className="bg-slate-100 rounded-2xl rounded-tl-sm px-4 py-2.5 max-w-[85%]">
                     <p className="text-xs font-bold text-slate-800 mb-0.5">{comment.userName}</p>
                     <p className="text-sm text-slate-600 whitespace-pre-wrap">{comment.content}</p>
                   </div>
                 </div>
               ))
            )}
          </div>

          {/* Comment Input */}
          <form onSubmit={submitComment} className="flex gap-2 relative mt-4">
            <img src={user?.avatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(user?.name || 'U')}`} alt="Your avatar" className="w-9 h-9 rounded-full object-cover shrink-0" />
            <div className="relative flex-1">
              <input 
                type="text" 
                value={newComment}
                onChange={(e) => setNewComment(e.target.value)}
                placeholder="Viết bình luận..." 
                className="w-full bg-white border border-slate-200 rounded-full pl-4 pr-12 py-2 text-sm focus:outline-none focus:ring-1 focus:ring-[#372660] focus:border-[#372660]"
                disabled={isSubmitting}
              />
              <button 
                type="submit" 
                disabled={!newComment.trim() || isSubmitting}
                className="absolute right-1 top-1 bottom-1 w-8 flex items-center justify-center bg-[#372660] text-white rounded-full hover:bg-[#2b1d4c] transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              >
                <Send className="w-3.5 h-3.5" />
              </button>
            </div>
          </form>
        </div>
      )}

    </div>
  )
}
