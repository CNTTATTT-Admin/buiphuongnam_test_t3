import React from 'react'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './contexts/AuthContext'
import Navbar from './components/layout/Navbar'
import Sidebar from './components/layout/Sidebar'
import RightSidebar from './components/layout/RightSidebar'
import CreatePost from './components/feed/CreatePost'
import PostCard from './components/feed/PostCard'
import AuthPage from './components/auth/AuthPage'
import FindMentorPage from './pages/FindMentorPage'
import MentorProfilePage from './pages/MentorProfilePage'
import ProfileSettingsPage from './pages/ProfileSettingsPage'
import MentorRegistrationPage from './pages/MentorRegistrationPage'
import MenteeBookingsPage from './pages/MenteeBookingsPage'
import AdminLayout from './layouts/AdminLayout'
import AdminDashboard from './pages/admin/AdminDashboard'
import AdminMentorRequestsPage from './pages/admin/AdminMentorRequestsPage'
import FeedPage from './pages/FeedPage'
import MentorSchedulePage from './pages/MentorSchedulePage'

// User App Layout (with top Navbar)
function UserLayout({ children }) {
  return (
    <div className="min-h-screen flex flex-col font-sans bg-slate-50">
      <Navbar />
      <main className="flex-1">
        {children}
      </main>
    </div>
  )
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          {/* User Routes */}
          <Route path="/" element={<UserLayout><div className="container mx-auto px-4 py-8"><FeedPage /></div></UserLayout>} />
          <Route path="/auth" element={<UserLayout><AuthPage /></UserLayout>} />
          <Route path="/login" element={<UserLayout><AuthPage /></UserLayout>} />
          <Route path="/search" element={<UserLayout><FindMentorPage /></UserLayout>} />
          <Route path="/mentor/:id" element={<UserLayout><MentorProfilePage /></UserLayout>} />
          <Route path="/settings" element={<UserLayout><ProfileSettingsPage /></UserLayout>} />
          <Route path="/schedule" element={<UserLayout><div className="container mx-auto px-4 py-8"><div className="flex gap-8"><Sidebar /><div className="flex-1"><MentorSchedulePage /></div></div></div></UserLayout>} />
          <Route path="/my-bookings" element={<UserLayout><div className="container mx-auto px-4 py-8"><div className="flex gap-8"><Sidebar /><div className="flex-1"><MenteeBookingsPage /></div></div></div></UserLayout>} />
          <Route path="/register-mentor" element={<UserLayout><MentorRegistrationPage /></UserLayout>} />
          
          {/* Admin Routes */}
          <Route path="/admin" element={<AdminLayout />}>
            <Route index element={<AdminDashboard />} />
            <Route path="mentor-requests" element={<AdminMentorRequestsPage />} />
          </Route>

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  )
}

