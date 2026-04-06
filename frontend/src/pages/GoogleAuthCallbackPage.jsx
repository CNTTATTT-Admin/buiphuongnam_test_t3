import React, { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";

export default function GoogleAuthCallbackPage() {
  const [error, setError] = useState("");
  const navigate = useNavigate();
  const { loginWithGoogleToken } = useAuth();
  const navigateRef = useRef(navigate);
  const loginWithGoogleTokenRef = useRef(loginWithGoogleToken);

  useEffect(() => {
    navigateRef.current = navigate;
    loginWithGoogleTokenRef.current = loginWithGoogleToken;
  }, [navigate, loginWithGoogleToken]);

  useEffect(() => {
    let cancelled = false;

    const handleOAuthCallback = async () => {
      const params = new URLSearchParams(window.location.search);
      const token = params.get("token");
      const refreshToken = params.get("refreshToken");
      const userName = params.get("userName") || "google_user";
      const callbackError = params.get("error");

      if (callbackError) {
        setError(callbackError);
        return;
      }

      if (!token) {
        setError("Khong nhan duoc token dang nhap tu Google");
        return;
      }

      try {
        const result = await loginWithGoogleTokenRef.current(
          token,
          userName,
          refreshToken,
        );
        if (cancelled) {
          return;
        }

        if (!result.success) {
          setError(result.message || "Dang nhap Google that bai");
          return;
        }

        if (result.role === "admin") {
          navigateRef.current("/admin", { replace: true });
        } else {
          navigateRef.current("/feed", { replace: true });
        }
      } catch (err) {
        if (!cancelled) {
          setError(err?.message || "Dang nhap Google that bai");
        }
      }
    };

    handleOAuthCallback();

    return () => {
      cancelled = true;
    };
  }, []);

  return (
    <div className="min-h-screen bg-slate-50 flex items-center justify-center px-4">
      <div className="w-full max-w-md bg-white border border-slate-200 rounded-2xl shadow-sm p-8 text-center">
        {!error ? (
          <>
            <div className="mx-auto mb-5 h-10 w-10 rounded-full border-2 border-slate-200 border-t-[#372660] animate-spin" />
            <h1 className="text-lg font-semibold text-slate-800">
              Dang xu ly dang nhap Google...
            </h1>
            <p className="mt-2 text-sm text-slate-500">
              Vui long doi trong giay lat
            </p>
          </>
        ) : (
          <>
            <h1 className="text-lg font-semibold text-red-600">
              Dang nhap that bai
            </h1>
            <p className="mt-2 text-sm text-slate-600 break-words">{error}</p>
            <button
              type="button"
              onClick={() => navigateRef.current("/auth", { replace: true })}
              className="mt-5 inline-flex items-center justify-center rounded-lg bg-[#372660] px-4 py-2 text-sm font-medium text-white hover:bg-[#2b1d4c]"
            >
              Quay lai dang nhap
            </button>
          </>
        )}
      </div>
    </div>
  );
}
