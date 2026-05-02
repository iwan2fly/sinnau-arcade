import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';

function Login({ setAuth, setUser }) {
  const [email, setEmail] = useState('');
  const [code, setCode] = useState('');
  const [step, setStep] = useState(1); // 1: Email, 2: Code
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const handleSendCode = async (e) => {
    e.preventDefault();
    if (!email) return;
    setIsLoading(true);
    setError('');
    setMessage('');
    
    try {
      await api.post(`/v1/auth/send-code/${encodeURIComponent(email)}`);
      setStep(2);
      setMessage('인증 코드가 이메일로 전송되었습니다.');
    } catch (err) {
      setError(err.response?.data?.message || '코드 전송에 실패했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleVerifyCode = async (e) => {
    e.preventDefault();
    if (!code) return;
    setIsLoading(true);
    setError('');
    setMessage('');
    
    try {
      const response = await api.post('/v1/auth/verify', { email, keyString: code });
      if (response.status === 200) {
        setAuth(true);
        try {
          const profileRes = await api.get('/v1/profile/me');
          setUser(profileRes.data);
        } catch (profileErr) {
          console.error("Profile fetch error:", profileErr);
        }
        navigate('/game');
      }
    } catch (err) {
      setError(err.response?.data?.message || '인증에 실패했습니다. 코드를 다시 확인해주세요.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="glass-panel animate-fade-in" style={{ width: '100%', maxWidth: '400px' }}>
      <h2 style={{ textAlign: 'center', marginBottom: '2rem', fontSize: '2rem' }}>Sign In</h2>
      
      {error && <div style={{ color: '#ef4444', marginBottom: '1rem', textAlign: 'center', background: 'rgba(239,68,68,0.1)', padding: '0.5rem', borderRadius: '8px' }}>{error}</div>}
      {message && <div style={{ color: '#4ade80', marginBottom: '1rem', textAlign: 'center', background: 'rgba(74,222,128,0.1)', padding: '0.5rem', borderRadius: '8px' }}>{message}</div>}
      
      {step === 1 ? (
        <form onSubmit={handleSendCode}>
          <input 
            type="email" 
            className="input-field" 
            placeholder="이메일 주소 (Email Address)" 
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required 
          />
          <button type="submit" className="btn" style={{ width: '100%', marginTop: '1rem' }} disabled={isLoading}>
            {isLoading ? 'Sending...' : '인증 코드 받기'}
          </button>
        </form>
      ) : (
        <form onSubmit={handleVerifyCode}>
          <p style={{ textAlign: 'center', marginBottom: '1rem', color: 'var(--text-muted)' }}>
            {email} 로 발송된 6자리 코드를 입력하세요.
          </p>
          <input 
            type="text" 
            className="input-field" 
            placeholder="인증 코드 (Verification Code)" 
            value={code}
            onChange={(e) => setCode(e.target.value)}
            required 
          />
          <button type="submit" className="btn" style={{ width: '100%', marginTop: '1rem' }} disabled={isLoading}>
            {isLoading ? 'Verifying...' : '로그인'}
          </button>
          <button 
            type="button" 
            onClick={() => setStep(1)} 
            style={{ background: 'transparent', border: 'none', color: 'var(--text-muted)', width: '100%', marginTop: '1rem', cursor: 'pointer', textDecoration: 'underline' }}
          >
            이메일 다시 입력하기
          </button>
        </form>
      )}
    </div>
  );
}

export default Login;
