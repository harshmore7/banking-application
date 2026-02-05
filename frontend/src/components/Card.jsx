export default function Card({ title, children, right }) {
  return (
    <section className="rounded-xl border border-slate-800 bg-slate-900 p-5 shadow-lg shadow-slate-900/60">
      {(title || right) && (
        <div className="mb-4 flex items-center justify-between gap-4">
          {title && <h2 className="text-lg font-semibold text-white">{title}</h2>}
          {right}
        </div>
      )}
      {children}
    </section>
  );
}
