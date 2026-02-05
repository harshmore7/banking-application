export default function ErrorBanner({ error }) {
  if (!error) return null;

  return (
    <div className="rounded-lg border border-rose-700 bg-rose-950/40 p-3 text-sm text-rose-200">
      <p className="font-medium">{error.message}</p>
      {error.traceId && <p className="mt-1 text-rose-300">Trace ID: {error.traceId}</p>}
    </div>
  );
}
