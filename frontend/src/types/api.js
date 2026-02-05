export function parseApiError(payload, fallbackMessage = 'Request failed') {
  if (!payload || typeof payload !== 'object') {
    return { message: fallbackMessage, traceId: null, status: null };
  }

  return {
    message: payload.message || payload.error || fallbackMessage,
    traceId: payload.traceId || null,
    status: payload.status || null,
    path: payload.path || null
  };
}
