import { BrowserRouter } from 'react-router-dom';
import { AuthProvider } from './auth/AuthContext';
import AppShell from './components/AppShell';
import AppRouter from './router/AppRouter';

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <AppShell>
          <AppRouter />
        </AppShell>
      </AuthProvider>
    </BrowserRouter>
  );
}
