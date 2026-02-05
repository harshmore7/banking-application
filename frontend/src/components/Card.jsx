export default function Card({ title, children }) {
  return (
    <section className="rounded-xl border border-slate-800 bg-slate-900 p-5 shadow-lg shadow-slate-900/60">
      {title && <h2 className="mb-4 text-lg font-semibold text-white">{title}</h2>}
      {children}
    </section>
  );
}
