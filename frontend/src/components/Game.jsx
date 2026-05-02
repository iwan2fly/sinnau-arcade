import { useState } from 'react';
import { Navigate } from 'react-router-dom';
import './Game.css';

function Game({ isAuthenticated }) {
  const [betAmount, setBetAmount] = useState(100);
  const [choice, setChoice] = useState('Heads'); // Heads or Tails
  const [isFlipping, setIsFlipping] = useState(false);
  const [result, setResult] = useState(null);
  const [balance, setBalance] = useState(1000); // Mock balance

  if (!isAuthenticated) {
    return <Navigate to="/login" />;
  }

  const handlePlay = () => {
    if (balance < betAmount) {
      alert("Not enough coins!");
      return;
    }

    setIsFlipping(true);
    setResult(null);
    setBalance(prev => prev - betAmount);

    // Simulate game logic and animation delay
    setTimeout(() => {
      const outcome = Math.random() > 0.5 ? 'Heads' : 'Tails';
      setResult(outcome);
      setIsFlipping(false);

      if (outcome === choice) {
        setBalance(prev => prev + betAmount * 2);
      }
    }, 2000); // 2 seconds spin
  };

  return (
    <div className="glass-panel animate-fade-in" style={{ width: '100%', maxWidth: '600px', textAlign: 'center' }}>
      <h2 style={{ fontSize: '2.5rem', marginBottom: '1rem', color: 'var(--primary)' }}>Coin Flip</h2>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '2rem', padding: '1rem', background: 'rgba(0,0,0,0.3)', borderRadius: '12px' }}>
        <span style={{ fontSize: '1.2rem', fontWeight: 'bold' }}>Balance:</span>
        <span style={{ fontSize: '1.2rem', color: 'var(--secondary)', fontWeight: 'bold' }}>{balance} 💰</span>
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
            min="10"
            max={balance}
            disabled={isFlipping}
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

      <button className="btn" style={{ width: '100%', marginTop: '2rem', fontSize: '1.2rem', padding: '1rem' }} onClick={handlePlay} disabled={isFlipping || balance < betAmount}>
        {isFlipping ? 'Flipping...' : 'FLIP COIN'}
      </button>
    </div>
  );
}

export default Game;
