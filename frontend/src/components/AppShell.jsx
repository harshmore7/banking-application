import { Link, NavLink } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';

const linkClass = ({ isActive }) =>
  `rounded px-3 py-2 text-sm ${isActive ? 'bg-slate-700 text-white' : 'text-slate-300 hover:bg-slate-800'}`;

export default function AppShell({ children }) {
  const { isAuthenticated, session, logout } = useAuth();

  return (
    <main className="min-h-screen bg-slate-950 px-4 py-8 text-slate-200">
      <div className="mx-auto max-w-6xl">
        <header className="mb-6 flex flex-wrap items-center justify-between gap-3 rounded-xl border border-slate-800 bg-slate-900 p-4">
          <Link to="/" className="text-lg font-bold text-white">Banking App</Link>
          <nav className="flex items-center gap-2">
            {!isAuthenticated ? (
              <>
                <NavLink to="/login" className={linkClass}>Login</NavLink>
                <NavLink to="/register" className={linkClass}>Register</NavLink>
              </>
            ) : (
              <>
                <NavLink to="/dashboard" className={linkClass}>Dashboard</NavLink>
                <button className="rounded bg-rose-600 px-3 py-2 text-sm font-medium text-white hover:bg-rose-500" onClick={logout}>Logout</button>
              </>
            )}
          </nav>
        </header>

        {isAuthenticated && (
          <p className="mb-4 text-sm text-slate-400">Signed in as <span className="text-slate-200">{session?.username}</span></p>
        )}

        {children}
      </div>
    </main>
  );
}
