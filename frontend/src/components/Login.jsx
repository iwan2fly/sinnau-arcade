import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';

function Login({ setAuth, setUser }) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setError('');
    
    try {
      // Platform's existing login endpoint is likely /v1/auth/login or similar
      const response = await api.post('/v1/auth/login', { email, password });
      if (response.status === 200) {
        setAuth(true);
        // Try fetching user profile right after login
        const profileRes = await api.get('/v1/profile');
        setUser(profileRes.data);
        navigate('/game');
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Login failed. Please check your credentials.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="glass-panel animate-fade-in" style={{ width: '100%', maxWidth: '400px' }}>
      <h2 style={{ textAlign: 'center', marginBottom: '2rem', fontSize: '2rem' }}>Sign In</h2>
      {error && <div style={{ color: '#ef4444', marginBottom: '1rem', textAlign: 'center', background: 'rgba(239,68,68,0.1)', padding: '0.5rem', borderRadius: '8px' }}>{error}</div>}
      <form onSubmit={handleLogin}>
        <input 
          type="email" 
          className="input-field" 
          placeholder="Email Address" 
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required 
        />
        <input 
          type="password" 
          className="input-field" 
          placeholder="Password" 
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required 
        />
        <button type="submit" className="btn" style={{ width: '100%', marginTop: '1rem' }} disabled={isLoading}>
          {isLoading ? 'Authenticating...' : 'Enter Arcade'}
        </button>
      </form>
    </div>
  );
}

export default Login;
