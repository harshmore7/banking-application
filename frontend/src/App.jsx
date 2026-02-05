import { useMemo, useState } from 'react';
import Card from './components/Card';

const API_BASE = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080/api/v1';

const initialRegister = {
  fullName: '',
  email: '',
  username: '',
  password: '',
  phoneNumber: '',
  accountType: 'SAVINGS',
  initialDeposit: 0
};

export default function App() {
  const [accessToken, setAccessToken] = useState('');
  const [authMessage, setAuthMessage] = useState('');
  const [registerForm, setRegisterForm] = useState(initialRegister);
  const [loginForm, setLoginForm] = useState({ username: '', password: '' });
  const [overview, setOverview] = useState(null);
  const [transfer, setTransfer] = useState({ fromAccount: '', toAccount: '', amount: 0, idempotencyKey: '' });
  const [statement, setStatement] = useState([]);

  const authHeader = useMemo(
    () => (accessToken ? { Authorization: `Bearer ${accessToken}` } : {}),
    [accessToken]
  );

  async function callApi(path, options = {}) {
    const response = await fetch(`${API_BASE}${path}`, {
      headers: {
        'Content-Type': 'application/json',
        ...authHeader,
        ...(options.headers || {})
      },
      ...options
    });

    const data = await response.json().catch(() => ({}));
    if (!response.ok) {
      throw new Error(data.message || 'Request failed');
    }
    return data;
  }

  async function register(e) {
    e.preventDefault();
    try {
      const data = await callApi('/auth/register', {
        method: 'POST',
        body: JSON.stringify({
          ...registerForm,
          initialDeposit: Number(registerForm.initialDeposit)
        })
      });
      setAccessToken(data.accessToken);
      setAuthMessage(`Registered as ${data.username}`);
    } catch (error) {
      setAuthMessage(error.message);
    }
  }

  async function login(e) {
    e.preventDefault();
    try {
      const data = await callApi('/auth/login', {
        method: 'POST',
        body: JSON.stringify(loginForm)
      });
      setAccessToken(data.accessToken);
      setAuthMessage(`Logged in as ${data.username}`);
    } catch (error) {
      setAuthMessage(error.message);
    }
  }

  async function loadOverview() {
    try {
      const data = await callApi('/accounts/me');
      setOverview(data);
      if (data.accounts?.[0]) {
        setTransfer((current) => ({ ...current, fromAccount: data.accounts[0].accountNumber }));
      }
    } catch (error) {
      setAuthMessage(error.message);
    }
  }

  async function makeTransfer(e) {
    e.preventDefault();
    try {
      const data = await callApi('/accounts/transfer', {
        method: 'POST',
        headers: transfer.idempotencyKey ? { 'Idempotency-Key': transfer.idempotencyKey } : {},
        body: JSON.stringify({
          fromAccount: transfer.fromAccount,
          toAccount: transfer.toAccount,
          amount: Number(transfer.amount)
        })
      });
      setAuthMessage(data.message);
      await loadStatement(transfer.fromAccount);
      await loadOverview();
    } catch (error) {
      setAuthMessage(error.message);
    }
  }

  async function loadStatement(accountNumber) {
    if (!accountNumber) return;
    try {
      const data = await callApi(`/accounts/${accountNumber}/statement?page=0&size=10`);
      setStatement(data.content || []);
    } catch (error) {
      setAuthMessage(error.message);
    }
  }

  return (
    <main className="min-h-screen bg-slate-950 px-4 py-10 text-slate-200">
      <div className="mx-auto grid max-w-6xl gap-6 lg:grid-cols-2">
        <Card title="Register">
          <form className="grid gap-3" onSubmit={register}>
            <input className="input" placeholder="Full name" value={registerForm.fullName} onChange={(e) => setRegisterForm({ ...registerForm, fullName: e.target.value })} />
            <input className="input" placeholder="Email" value={registerForm.email} onChange={(e) => setRegisterForm({ ...registerForm, email: e.target.value })} />
            <input className="input" placeholder="Username" value={registerForm.username} onChange={(e) => setRegisterForm({ ...registerForm, username: e.target.value })} />
            <input className="input" type="password" placeholder="Password" value={registerForm.password} onChange={(e) => setRegisterForm({ ...registerForm, password: e.target.value })} />
            <input className="input" placeholder="Phone" value={registerForm.phoneNumber} onChange={(e) => setRegisterForm({ ...registerForm, phoneNumber: e.target.value })} />
            <input className="input" placeholder="Initial Deposit" type="number" value={registerForm.initialDeposit} onChange={(e) => setRegisterForm({ ...registerForm, initialDeposit: e.target.value })} />
            <button className="btn">Create account</button>
          </form>
        </Card>

        <Card title="Login">
          <form className="grid gap-3" onSubmit={login}>
            <input className="input" placeholder="Username" value={loginForm.username} onChange={(e) => setLoginForm({ ...loginForm, username: e.target.value })} />
            <input className="input" type="password" placeholder="Password" value={loginForm.password} onChange={(e) => setLoginForm({ ...loginForm, password: e.target.value })} />
            <button className="btn">Login</button>
          </form>
          <p className="mt-3 text-sm text-emerald-400">{authMessage}</p>
          <button className="btn mt-3" type="button" onClick={loadOverview}>Load my dashboard</button>
        </Card>

        <Card title="Account Overview">
          {!overview ? <p className="text-slate-400">Login and click “Load my dashboard”.</p> : (
            <>
              <p className="text-sm text-slate-300">{overview.fullName} ({overview.customerId})</p>
              <ul className="mt-4 space-y-2">
                {overview.accounts.map((account) => (
                  <li className="rounded border border-slate-700 p-3 text-sm" key={account.accountNumber}>
                    <p>{account.accountNumber} · {account.accountType} · {account.status}</p>
                    <p className="text-emerald-400">Balance: {account.balance}</p>
                    <button className="mt-2 text-xs text-sky-400 underline" onClick={() => loadStatement(account.accountNumber)}>Load statement</button>
                  </li>
                ))}
              </ul>
            </>
          )}
        </Card>

        <Card title="Transfer">
          <form className="grid gap-3" onSubmit={makeTransfer}>
            <input className="input" placeholder="From Account" value={transfer.fromAccount} onChange={(e) => setTransfer({ ...transfer, fromAccount: e.target.value })} />
            <input className="input" placeholder="To Account" value={transfer.toAccount} onChange={(e) => setTransfer({ ...transfer, toAccount: e.target.value })} />
            <input className="input" type="number" placeholder="Amount" value={transfer.amount} onChange={(e) => setTransfer({ ...transfer, amount: e.target.value })} />
            <input className="input" placeholder="Idempotency key (optional)" value={transfer.idempotencyKey} onChange={(e) => setTransfer({ ...transfer, idempotencyKey: e.target.value })} />
            <button className="btn">Send transfer</button>
          </form>
        </Card>

        <Card title="Recent Statement (Top 10)">
          <ul className="space-y-2 text-sm">
            {statement.map((entry) => (
              <li key={entry.id} className="rounded border border-slate-700 p-3">
                <p>{entry.type} · {entry.amount}</p>
                <p className="text-slate-400">{entry.note}</p>
                <p className="text-slate-500">Ref: {entry.referenceId}</p>
              </li>
            ))}
            {!statement.length && <li className="text-slate-500">No statement loaded yet.</li>}
          </ul>
        </Card>
      </div>
    </main>
  );
}
