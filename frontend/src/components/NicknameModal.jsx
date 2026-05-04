import { useState } from 'react';
import api from '../api/axios';

function NicknameModal({ onComplete }) {
  const [newNickname, setNewNickname] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!newNickname.trim()) return;

    setIsLoading(true);
    setError('');

    try {
      const response = await api.post('/v1/profile/nickname', { newNickname });
      if (response.data.success) {
        onComplete();
      } else {
        setError(response.data.error?.message || '닉네임 설정에 실패했습니다.');
      }
    } catch (err) {
      setError(err.response?.data?.error?.message || '이미 사용 중인 닉네임이거나 서버 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div style={{
      position: 'fixed',
      top: 0,
      left: 0,
      width: '100%',
      height: '100%',
      backgroundColor: 'rgba(0, 0, 0, 0.85)',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      zIndex: 10000,
      backdropFilter: 'blur(10px)'
    }}>
      <div className="glass-panel animate-fade-in" style={{ width: '100%', maxWidth: '400px', textAlign: 'center' }}>
        <h2 style={{ fontSize: '2rem', marginBottom: '1rem', color: 'var(--primary)' }}>Welcome! 🎮</h2>
        <p style={{ color: 'var(--text-muted)', marginBottom: '2rem' }}>
          신나유 오락실에서 사용할<br />닉네임을 설정해 주세요.
        </p>

        {error && (
          <div style={{ 
            color: '#ef4444', 
            marginBottom: '1rem', 
            background: 'rgba(239,68,68,0.1)', 
            padding: '0.5rem', 
            borderRadius: '8px',
            fontSize: '0.9rem'
          }}>
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <input 
            type="text" 
            className="input-field" 
            placeholder="멋진 닉네임을 입력하세요" 
            value={newNickname}
            onChange={(e) => setNewNickname(e.target.value)}
            required
            autoFocus
            minLength={2}
            maxLength={20}
          />
          <button 
            type="submit" 
            className="btn" 
            style={{ width: '100%', marginTop: '1.5rem', fontSize: '1.1rem' }} 
            disabled={isLoading}
          >
            {isLoading ? '설정 중...' : '시작하기'}
          </button>
        </form>
      </div>
    </div>
  );
}

export default NicknameModal;
