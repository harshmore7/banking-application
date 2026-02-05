import { useEffect, useState } from 'react';
import Card from '../components/Card';
import ErrorBanner from '../components/ErrorBanner';
import { useAuth } from '../auth/AuthContext';
import useDashboardData from '../hooks/useDashboardData';

export default function DashboardPage() {
  const { apiError, setApiError, setAuthMessage } = useAuth();
  const { overview, statement, loading, loadOverview, loadStatement, transfer } = useDashboardData();
  const [transferForm, setTransferForm] = useState({ fromAccount: '', toAccount: '', amount: 0, idempotencyKey: '' });

  useEffect(() => {
    (async () => {
      try {
        const data = await loadOverview();
        if (data?.accounts?.[0]) {
          setTransferForm((c) => ({ ...c, fromAccount: data.accounts[0].accountNumber }));
        }
      } catch {
        // handled in state
      }
    })();
  }, []);

  async function onTransfer(e) {
    e.preventDefault();
    setApiError(null);
    try {
      const data = await transfer(transferForm);
      setAuthMessage(data.message);
      await loadOverview();
      await loadStatement(transferForm.fromAccount);
    } catch (error) {
      setApiError(error);
    }
  }

  return (
    <div className="grid gap-6 lg:grid-cols-2">
      <Card title="Account Overview" right={loading ? <span className="text-xs text-slate-400">Loading...</span> : null}>
        {!overview ? <p className="text-slate-400">No account data yet.</p> : (
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
        <form className="grid gap-3" onSubmit={onTransfer}>
          <input className="input" placeholder="From Account" value={transferForm.fromAccount} onChange={(e) => setTransferForm({ ...transferForm, fromAccount: e.target.value })} />
          <input className="input" placeholder="To Account" value={transferForm.toAccount} onChange={(e) => setTransferForm({ ...transferForm, toAccount: e.target.value })} />
          <input className="input" type="number" placeholder="Amount" value={transferForm.amount} onChange={(e) => setTransferForm({ ...transferForm, amount: e.target.value })} />
          <input className="input" placeholder="Idempotency key (optional)" value={transferForm.idempotencyKey} onChange={(e) => setTransferForm({ ...transferForm, idempotencyKey: e.target.value })} />
          <button className="btn" disabled={loading}>Send transfer</button>
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

      <ErrorBanner error={apiError} />
    </div>
  );
}
