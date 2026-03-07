import React, { createContext, useContext, useState, useEffect } from 'react';

const AuthContext = createContext(null);

// Mock Users
const MOCK_USERS = {
  admin: {
    id: 'admin1',
    email: 'admin@mentormatch.com',
    password: 'password123',
    name: 'Admin User',
    role: 'admin',
    avatar: 'https://i.pravatar.cc/150?u=admin'
  },
  mentee: {
    id: 'mentee1',
    email: 'mentee@mentormatch.com',
    password: 'password123',
    name: 'Mentee User',
    role: 'mentee',
    avatar: 'https://i.pravatar.cc/150?u=mentee1'
  }
};

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);

  // Check localStorage on mount
  useEffect(() => {
    const storedUser = localStorage.getItem('mentormatch_user');
    if (storedUser) {
      setUser(JSON.parse(storedUser));
    }
  }, []);

  const login = (email, password) => {
    // Simple mock auth logic
    const foundUser = Object.values(MOCK_USERS).find(
      (u) => u.email === email && u.password === password
    );

    if (foundUser) {
      const userData = {
        id: foundUser.id,
        name: foundUser.name,
        email: foundUser.email,
        role: foundUser.role,
        avatar: foundUser.avatar
      };
      setUser(userData);
      localStorage.setItem('mentormatch_user', JSON.stringify(userData));
      return { success: true };
    }
    return { success: false, message: 'Sai email hoặc mật khẩu' };
  };

  const logout = () => {
    setUser(null);
    localStorage.removeItem('mentormatch_user');
  };

  return (
    <AuthContext.Provider value={{ user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
