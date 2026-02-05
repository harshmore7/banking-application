import { useState } from 'react';
import { useAuth } from '../auth/AuthContext';

export default function useDashboardData() {
  const { apiClient, setApiError } = useAuth();
  const [overview, setOverview] = useState(null);
  const [statement, setStatement] = useState([]);
  const [loading, setLoading] = useState(false);

  async function loadOverview() {
    setLoading(true);
    try {
      const data = await apiClient.request('/accounts/me');
      setOverview(data);
      return data;
    } catch (error) {
      setApiError(error);
      throw error;
    } finally {
      setLoading(false);
    }
  }

  async function loadStatement(accountNumber) {
    if (!accountNumber) return;
    setLoading(true);
    try {
      const data = await apiClient.request(`/accounts/${accountNumber}/statement?page=0&size=10`);
      setStatement(data.content || []);
    } catch (error) {
      setApiError(error);
      throw error;
    } finally {
      setLoading(false);
    }
  }

  async function transfer(transferForm) {
    setLoading(true);
    try {
      const data = await apiClient.request('/accounts/transfer', {
        method: 'POST',
        headers: transferForm.idempotencyKey ? { 'Idempotency-Key': transferForm.idempotencyKey } : {},
        body: JSON.stringify({
          fromAccount: transferForm.fromAccount,
          toAccount: transferForm.toAccount,
          amount: Number(transferForm.amount)
        })
      });
      return data;
    } finally {
      setLoading(false);
    }
  }

  return {
    overview,
    statement,
    loading,
    loadOverview,
    loadStatement,
    transfer
  };
}
