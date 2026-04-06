import axios from "axios";

// Update baseURL to point to Spring Boot backend
const API_URL = import.meta.env.VITE_API_URL || "http://localhost:8080/api";

const api = axios.create({
  baseURL: API_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

const isNgrokTunnel =
  API_URL.includes(".ngrok-free.dev") ||
  API_URL.includes(".ngrok.io") ||
  API_URL.includes(".ngrok.app");

if (isNgrokTunnel) {
  // Prevent ngrok warning/interstitial page from breaking browser API calls.
  api.defaults.headers.common["ngrok-skip-browser-warning"] = "true";
}

let isRefreshing = false;
let requestQueue = [];

const getStoredAuthUser = () => {
  const rawUser = localStorage.getItem("mentormatch_user");
  if (!rawUser) return null;

  try {
    return JSON.parse(rawUser);
  } catch {
    localStorage.removeItem("mentormatch_user");
    return null;
  }
};

const updateStoredAuthUser = (user) => {
  localStorage.setItem("mentormatch_user", JSON.stringify(user));
};

const processQueue = (error, token = null) => {
  requestQueue.forEach((pendingRequest) => {
    if (error) {
      pendingRequest.reject(error);
    } else {
      pendingRequest.resolve(token);
    }
  });

  requestQueue = [];
};

const clearAuthAndRedirect = () => {
  localStorage.removeItem("mentormatch_user");

  const currentPath = window.location.pathname;
  if (
    currentPath !== "/login" &&
    currentPath !== "/auth" &&
    currentPath !== "/register"
  ) {
    window.location.href = "/auth";
  }
};

const isRefreshableRequest = (requestUrl = "") => {
  return (
    !requestUrl.includes("/auth/login") &&
    !requestUrl.includes("/auth/register") &&
    !requestUrl.includes("/auth/refresh")
  );
};

// Request interceptor to add token
api.interceptors.request.use(
  (config) => {
    const user = getStoredAuthUser();
    if (user) {
      const { token } = user;
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  },
);

// Response interceptor
api.interceptors.response.use(
  (response) => response.data,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status !== 401 || !originalRequest) {
      return Promise.reject(error);
    }

    const requestUrl = originalRequest.url || "";
    if (!isRefreshableRequest(requestUrl)) {
      clearAuthAndRedirect();
      return Promise.reject(error);
    }

    if (originalRequest._retry) {
      clearAuthAndRedirect();
      return Promise.reject(error);
    }

    const storedUser = getStoredAuthUser();
    if (!storedUser?.refreshToken) {
      clearAuthAndRedirect();
      return Promise.reject(error);
    }

    if (isRefreshing) {
      return new Promise((resolve, reject) => {
        requestQueue.push({ resolve, reject });
      })
        .then((newToken) => {
          originalRequest.headers.Authorization = `Bearer ${newToken}`;
          return api(originalRequest);
        })
        .catch((queueError) => Promise.reject(queueError));
    }

    originalRequest._retry = true;
    isRefreshing = true;

    try {
      const refreshHeaders = { "Content-Type": "application/json" };
      if (isNgrokTunnel) {
        refreshHeaders["ngrok-skip-browser-warning"] = "true";
      }

      const refreshResponse = await axios.post(
        `${API_URL}/auth/refresh`,
        { refreshToken: storedUser.refreshToken },
        { headers: refreshHeaders },
      );

      const refreshPayload = refreshResponse?.data;
      const newToken = refreshPayload?.result?.token;
      const newRefreshToken =
        refreshPayload?.result?.refreshToken || storedUser.refreshToken;

      if (!newToken) {
        throw new Error("Refresh token request did not return access token");
      }

      const updatedUser = {
        ...storedUser,
        token: newToken,
        refreshToken: newRefreshToken,
      };
      updateStoredAuthUser(updatedUser);

      processQueue(null, newToken);
      originalRequest.headers.Authorization = `Bearer ${newToken}`;
      return api(originalRequest);
    } catch (refreshError) {
      processQueue(refreshError, null);
      clearAuthAndRedirect();
      return Promise.reject(refreshError);
    } finally {
      isRefreshing = false;
    }
  },
);

export default api;
