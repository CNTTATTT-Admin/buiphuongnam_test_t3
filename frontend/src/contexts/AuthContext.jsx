import React, { createContext, useContext, useState, useEffect } from 'react';
import authService from '../services/authService';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);

  // Check localStorage on mount
  useEffect(() => {
    const storedUser = localStorage.getItem('mentormatch_user');
    if (storedUser) {
      setUser(JSON.parse(storedUser));
    }
  }, []);

  const login = async (userName, password) => {
    try {
      const response = await authService.login(userName, password);
      if (response && response.code === 1000 && response.result && response.result.authenticated) {
        // Extract basic data (since the API returns `token` and `authenticated` inside `result`)
        const token = response.result.token;
        let role = 'mentee';
        
        // For testing purposes: if username contains 'men' but not 'mentee', make them a mentor
        if (userName.toLowerCase().includes('admin')) {
          role = 'admin';
        } else if (userName.toLowerCase().includes('mentor')) {
          role = 'mentor';
        } else if (userName.toLowerCase() === 'nam2' || userName.toLowerCase() === 'nam') {
            // Also defaulting this specific test user to mentor so they can see the settings
            role = 'mentor';
        }
        
        const userData = {
          id: userName,
          name: userName, // Use username as name for now
          userName: userName,
          role: role,
          avatar: `https://i.pravatar.cc/150?u=${userName}`,
          token: token
        };
        
        setUser(userData);
        localStorage.setItem('mentormatch_user', JSON.stringify(userData));
        return { success: true };
      }
      return { success: false, message: response?.message || 'Xác thực thất bại' };
    } catch (error) {
      console.error("Login error:", error);
      return { success: false, message: error.message || 'Sai tài khoản hoặc mật khẩu' };
    }
  };

  const register = async (userName, email, password) => {
    try {
      const response = await authService.register(userName, email, password);
      if (response && response.code === 1000) {
        return { success: true };
      }
      return { success: false, message: response?.message || 'Đăng ký thất bại' };
    } catch (error) {
       console.error("Register error:", error);
       return { success: false, message: error.message || 'Lỗi khi đăng ký tài khoản' };
    }
  };

  const logout = () => {
    setUser(null);
    localStorage.removeItem('mentormatch_user');
  };

  return (
    <AuthContext.Provider value={{ user, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
