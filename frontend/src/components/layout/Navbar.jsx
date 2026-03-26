import React from "react"
import { useAuth } from "../../contexts/AuthContext"
import NotificationDropdown from "./NotificationDropdown"
import { Button } from "../ui/button"

export default function Navbar() {
  const { user, logout } = useAuth()

  return (
    <nav className="sticky top-0 z-50 w-full border-b bg-white">
      <div className="container mx-auto flex h-16 items-center justify-between px-4">
        {/* Logo */}
        <div className="flex items-center gap-2 w-64 shrink-0">
          <div className="flex items-center gap-1 text-primary">
            <a href="/">
            <svg width="28" height="28" viewBox="0 0 24 24" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 3L1 9L12 15L21 10.09V17H23V9M5 13.18V17.18L12 21L19 17.18V13.18L12 17L5 13.18Z" />
            </svg>
            <span className="text-xl font-bold">MentorMatch</span>
            </a>
          </div>
        </div>

        {/* Global Search is removed */}

        {/* Actions & Profile */}
        <div className="flex items-center justify-end gap-5 w-64 shrink-0">
          {user && <NotificationDropdown />}

          {user ? (
            <div className="flex items-center gap-3">
              <span className="text-sm font-semibold text-slate-700 hidden md:block">{user.name}</span>
              <a href="/settings" className="relative group cursor-pointer" title="Cài đặt hồ sơ">
                <img 
                  src={user.avatar} 
                  alt="User Avatar" 
                  className="h-9 w-9 rounded-full ring-2 ring-transparent transition-all group-hover:ring-[#372660] object-cover"
                />
              </a>
              <Button variant="ghost" size="icon" onClick={() => { logout(); window.location.href='/'; }} className="text-slate-400 hover:text-red-600 rounded-full" title="Đăng xuất">
                 <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path><polyline points="16 17 21 12 16 7"></polyline><line x1="21" y1="12" x2="9" y2="12"></line></svg>
              </Button>
            </div>
          ) : (
            <Button className="bg-[#372660] hover:bg-[#2b1d4c] text-white px-5 rounded-full text-sm font-medium transition-colors" onClick={() => window.location.href = '/login'}>
              Đăng nhập
            </Button>
          )}
        </div>
      </div>
    </nav>
  )
}
