import { useState, useEffect } from 'react';
import { Navigate } from 'react-router-dom';
import api from '../api/axios';
import './Game.css';

function Game({ isAuthenticated }) {
  const [betAmount, setBetAmount] = useState(100);
  const [choice, setChoice] = useState('Heads'); // Heads or Tails
  const [isFlipping, setIsFlipping] = useState(false);
  const [result, setResult] = useState(null);
  const [balance, setBalance] = useState(0);

  useEffect(() => {
    if (isAuthenticated) {
      fetchBalance();
    }
  }, [isAuthenticated]);

  const fetchBalance = async () => {
    try {
      const response = await api.get('/v1/profile/me');
      if (response.data.success) {
        setBalance(Number(response.data.data.coin));
      }
    } catch (error) {
      console.error("Failed to fetch balance", error);
    }
  };

  if (!isAuthenticated) {
    return <Navigate to="/login" />;
  }

  const handlePlay = async () => {
    if (balance < betAmount) {
      alert("Not enough coins!");
      return;
    }

    setIsFlipping(true);
    setResult(null);

    try {
      // 1. Start Game (Deduct balance)
      const startResponse = await api.post('/v1/game/start');
      if (!startResponse.data.success) {
        throw new Error(startResponse.data.error?.message || "Failed to start game");
      }

      // Local update for immediate feedback
      setBalance(prev => prev - 100); // Standard bet is 100 for now based on backend

      // 2. Simulate Flip Animation
      setTimeout(async () => {
        const outcome = Math.random() > 0.5 ? 'Heads' : 'Tails';
        setResult(outcome);
        setIsFlipping(false);

        const isSuccess = outcome === choice;

        // 3. Finish Game (Reward balance)
        try {
          const finishResponse = await api.post(`/v1/game/finish?isSuccess=${isSuccess}`);
          if (finishResponse.data.success) {
            // Re-fetch balance to get the source of truth
            fetchBalance();
          }
        } catch (error) {
          console.error("Failed to finish game", error);
        }
      }, 2000);

    } catch (error) {
      alert(error.message);
      setIsFlipping(false);
      fetchBalance();
    }
  };

  return (
    <div className="glass-panel animate-fade-in" style={{ width: '100%', maxWidth: '600px', textAlign: 'center' }}>
      <h2 style={{ fontSize: '2.5rem', marginBottom: '1rem', color: 'var(--primary)' }}>Coin Flip</h2>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '2rem', padding: '1rem', background: 'rgba(0,0,0,0.3)', borderRadius: '12px' }}>
        <span style={{ fontSize: '1.2rem', fontWeight: 'bold' }}>Balance:</span>
        <span style={{ fontSize: '1.2rem', color: 'var(--secondary)', fontWeight: 'bold' }}>{balance.toLocaleString()} 💰</span>
      </div>

      <div className="coin-container" style={{ margin: '3rem auto', height: '150px', perspective: '1000px' }}>
        <div className={`coin ${isFlipping ? 'flipping' : ''} ${result === 'Heads' ? 'heads' : result === 'Tails' ? 'tails' : ''}`}>
          <div className="coin-face front">Heads</div>
          <div className="coin-face back">Tails</div>
        </div>
      </div>

      {result && !isFlipping && (
        <div className="result-announcement animate-fade-in" style={{ marginBottom: '2rem' }}>
          <h3 style={{ fontSize: '2rem', color: result === choice ? '#4ade80' : '#ef4444' }}>
            {result === choice ? 'You Won!' : 'You Lost!'}
          </h3>
          <p style={{ color: 'var(--text-muted)' }}>The coin landed on {result}</p>
        </div>
      )}

      <div className="controls" style={{ display: 'grid', gap: '1rem', gridTemplateColumns: '1fr 1fr' }}>
        <div>
          <label style={{ display: 'block', marginBottom: '0.5rem', color: 'var(--text-muted)' }}>Bet Amount</label>
          <input 
            type="number" 
            className="input-field" 
            value={betAmount} 
            onChange={(e) => setBetAmount(Number(e.target.value))}
            min="100"
            max="100"
            disabled={true} // Backend currently fixed at 100
          />
        </div>
        <div>
          <label style={{ display: 'block', marginBottom: '0.5rem', color: 'var(--text-muted)' }}>Your Pick</label>
          <select 
            className="input-field" 
            value={choice} 
            onChange={(e) => setChoice(e.target.value)}
            disabled={isFlipping}
          >
            <option value="Heads">Heads</option>
            <option value="Tails">Tails</option>
          </select>
        </div>
      </div>

      <button className="btn" style={{ width: '100%', marginTop: '2rem', fontSize: '1.2rem', padding: '1rem' }} onClick={handlePlay} disabled={isFlipping || balance < 100}>
        {isFlipping ? 'Flipping...' : 'FLIP COIN'}
      </button>
    </div>
  );
}

export default Game;
