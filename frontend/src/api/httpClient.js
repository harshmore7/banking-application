import { parseApiError } from '../types/api';

const API_BASE = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080/api/v1';

export class ApiClient {
  constructor(getSession, onSessionUpdate, onUnauthorized) {
    this.getSession = getSession;
    this.onSessionUpdate = onSessionUpdate;
    this.onUnauthorized = onUnauthorized;
    this.refreshInFlight = null;
  }

  async request(path, options = {}) {
    const session = this.getSession();
    const headers = {
      'Content-Type': 'application/json',
      ...(session?.accessToken ? { Authorization: `Bearer ${session.accessToken}` } : {}),
      ...(options.headers || {})
    };

    const response = await fetch(`${API_BASE}${path}`, { ...options, headers });
    if (response.status !== 401) {
      return this.handleResponse(response);
    }

    if (!session?.refreshToken || options._retry) {
      this.onUnauthorized();
      return this.handleResponse(response);
    }

    const refreshed = await this.refreshToken(session.refreshToken);
    if (!refreshed) {
      this.onUnauthorized();
      return this.handleResponse(response);
    }

    return this.request(path, { ...options, _retry: true });
  }

  async refreshToken(refreshToken) {
    if (this.refreshInFlight) {
      return this.refreshInFlight;
    }

    this.refreshInFlight = (async () => {
      try {
        const response = await fetch(`${API_BASE}/auth/refresh`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ refreshToken })
        });

        if (!response.ok) {
          return false;
        }

        const payload = await response.json();
        this.onSessionUpdate(payload);
        return true;
      } catch {
        return false;
      } finally {
        this.refreshInFlight = null;
      }
    })();

    return this.refreshInFlight;
  }

  async handleResponse(response) {
    const payload = await response.json().catch(() => null);
    if (response.ok) {
      return payload;
    }

    const parsed = parseApiError(payload);
    const error = new Error(parsed.message);
    error.traceId = parsed.traceId;
    error.status = parsed.status;
    error.path = parsed.path;
    throw error;
  }
}
