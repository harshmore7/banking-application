import { createContext, useContext, useEffect, useMemo, useState } from 'react';
import { ApiClient } from '../api/httpClient';

const STORAGE_KEY = 'banking.session';
const AuthContext = createContext(null);

function toSession(authPayload) {
  return {
    accessToken: authPayload.accessToken,
    refreshToken: authPayload.refreshToken,
    username: authPayload.username,
    customerId: authPayload.customerId,
    primaryAccountNumber: authPayload.primaryAccountNumber,
    expiresInSeconds: authPayload.expiresInSeconds
  };
}

export function AuthProvider({ children }) {
  const [session, setSession] = useState(null);
  const [authMessage, setAuthMessage] = useState('');
  const [apiError, setApiError] = useState(null);

  useEffect(() => {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (raw) {
      try {
        setSession(JSON.parse(raw));
      } catch {
        localStorage.removeItem(STORAGE_KEY);
      }
    }
  }, []);

  useEffect(() => {
    if (session) {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(session));
    } else {
      localStorage.removeItem(STORAGE_KEY);
    }
  }, [session]);

  const onUnauthorized = () => {
    setSession(null);
    setAuthMessage('Session expired. Please login again.');
  };

  const apiClient = useMemo(
    () =>
      new ApiClient(
        () => session,
        (refreshPayload) => setSession((current) => ({ ...(current || {}), ...toSession(refreshPayload) })),
        onUnauthorized
      ),
    [session]
  );

  async function register(form) {
    try {
      setApiError(null);
      const payload = await apiClient.request('/auth/register', {
        method: 'POST',
        body: JSON.stringify({ ...form, initialDeposit: Number(form.initialDeposit) })
      });
      setSession(toSession(payload));
      setAuthMessage(`Registered as ${payload.username}`);
    } catch (error) {
      setApiError(error);
      setAuthMessage(error.message);
      throw error;
    }
  }

  async function login(form) {
    try {
      setApiError(null);
      const payload = await apiClient.request('/auth/login', {
        method: 'POST',
        body: JSON.stringify(form)
      });
      setSession(toSession(payload));
      setAuthMessage(`Logged in as ${payload.username}`);
    } catch (error) {
      setApiError(error);
      setAuthMessage(error.message);
      throw error;
    }
  }

  function logout() {
    setSession(null);
    setAuthMessage('Logged out');
    setApiError(null);
  }

  return (
    <AuthContext.Provider
      value={{
        session,
        isAuthenticated: Boolean(session?.accessToken),
        authMessage,
        apiError,
        register,
        login,
        logout,
        apiClient,
        setAuthMessage,
        setApiError
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
}
