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
import AdminLayout from './layouts/AdminLayout'
import AdminDashboard from './pages/admin/AdminDashboard'
import { MOCK_POSTS } from './data/mockData'

function HomeFeed() {
  return (
    <div className="flex gap-8 justify-center max-w-7xl mx-auto">
      <Sidebar />
      <div className="flex-1 max-w-2xl min-w-0">
        <CreatePost />
        <div className="space-y-6">
          {MOCK_POSTS.map((post) => (
            <PostCard key={post.id} post={post} />
          ))}
        </div>
      </div>
      <RightSidebar />
    </div>
  )
}

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
          <Route path="/" element={<UserLayout><div className="container mx-auto px-4 py-8"><HomeFeed /></div></UserLayout>} />
          <Route path="/auth" element={<UserLayout><AuthPage /></UserLayout>} />
          <Route path="/login" element={<UserLayout><AuthPage /></UserLayout>} />
          <Route path="/search" element={<UserLayout><FindMentorPage /></UserLayout>} />
          <Route path="/mentor/:id" element={<UserLayout><MentorProfilePage /></UserLayout>} />
          <Route path="/settings" element={<UserLayout><ProfileSettingsPage /></UserLayout>} />
          <Route path="/register-mentor" element={<UserLayout><MentorRegistrationPage /></UserLayout>} />
          
          {/* Admin Routes */}
          <Route path="/admin" element={<AdminLayout />}>
            <Route index element={<AdminDashboard />} />
          </Route>

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  )
}

