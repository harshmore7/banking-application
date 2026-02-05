import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Card from '../components/Card';
import ErrorBanner from '../components/ErrorBanner';
import { useAuth } from '../auth/AuthContext';

const initialRegister = {
  fullName: '',
  email: '',
  username: '',
  password: '',
  phoneNumber: '',
  accountType: 'SAVINGS',
  initialDeposit: 0
};

export default function RegisterPage() {
  const navigate = useNavigate();
  const { register, authMessage, apiError } = useAuth();
  const [form, setForm] = useState(initialRegister);

  async function onSubmit(e) {
    e.preventDefault();
    try {
      await register(form);
      navigate('/dashboard');
    } catch {
      // handled in auth context
    }
  }

  return (
    <Card title="Register">
      <form className="grid gap-3" onSubmit={onSubmit}>
        <input className="input" placeholder="Full name" value={form.fullName} onChange={(e) => setForm({ ...form, fullName: e.target.value })} />
        <input className="input" placeholder="Email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} />
        <input className="input" placeholder="Username" value={form.username} onChange={(e) => setForm({ ...form, username: e.target.value })} />
        <input className="input" type="password" placeholder="Password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} />
        <input className="input" placeholder="Phone" value={form.phoneNumber} onChange={(e) => setForm({ ...form, phoneNumber: e.target.value })} />
        <select className="input" value={form.accountType} onChange={(e) => setForm({ ...form, accountType: e.target.value })}>
          <option value="SAVINGS">SAVINGS</option>
          <option value="CURRENT">CURRENT</option>
        </select>
        <input className="input" type="number" placeholder="Initial Deposit" value={form.initialDeposit} onChange={(e) => setForm({ ...form, initialDeposit: e.target.value })} />
        <button className="btn">Create account</button>
      </form>
      {authMessage && <p className="mt-3 text-sm text-emerald-400">{authMessage}</p>}
      <div className="mt-3">
        <ErrorBanner error={apiError} />
      </div>
    </Card>
  );
}
