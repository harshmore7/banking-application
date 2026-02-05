import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Card from '../components/Card';
import ErrorBanner from '../components/ErrorBanner';
import { useAuth } from '../auth/AuthContext';

export default function LoginPage() {
  const navigate = useNavigate();
  const { login, authMessage, apiError } = useAuth();
  const [form, setForm] = useState({ username: '', password: '' });

  async function onSubmit(e) {
    e.preventDefault();
    try {
      await login(form);
      navigate('/dashboard');
    } catch {
      // handled in auth context
    }
  }

  return (
    <Card title="Login">
      <form className="grid gap-3" onSubmit={onSubmit}>
        <input className="input" placeholder="Username" value={form.username} onChange={(e) => setForm({ ...form, username: e.target.value })} />
        <input className="input" type="password" placeholder="Password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} />
        <button className="btn">Login</button>
      </form>
      {authMessage && <p className="mt-3 text-sm text-emerald-400">{authMessage}</p>}
      <div className="mt-3">
        <ErrorBanner error={apiError} />
      </div>
    </Card>
  );
}
