import api from "./api";

const API_URL = import.meta.env.VITE_API_URL || "http://localhost:8080/api";
const BACKEND_BASE_URL = API_URL.replace(/\/api\/?$/, "");

const authService = {
  login: async (userName, password) => {
    try {
      const response = await api.post("/auth/login", { userName, password });
      return response;
    } catch (error) {
      throw (
        error.response?.data || {
          message: "Network error or server unavailable",
        }
      );
    }
  },

  register: async (userName, email, password) => {
    try {
      const response = await api.post("/auth/register", {
        userName,
        email,
        password,
      });
      return response;
    } catch (error) {
      throw (
        error.response?.data || {
          message: "Network error or server unavailable",
        }
      );
    }
  },

  logout: async (token) => {
    try {
      const config = token
        ? { headers: { Authorization: `Bearer ${token}` } }
        : undefined;
      const response = await api.post("/auth/logout", {}, config);
      return response;
    } catch (error) {
      throw (
        error.response?.data || {
          message: "Network error or server unavailable",
        }
      );
    }
  },

  refreshToken: async (refreshToken) => {
    try {
      const response = await api.post("/auth/refresh", { refreshToken });
      return response;
    } catch (error) {
      throw (
        error.response?.data || {
          message: "Network error or server unavailable",
        }
      );
    }
  },

  getGoogleLoginUrl: () => `${BACKEND_BASE_URL}/oauth2/authorization/google`,
};

export default authService;
