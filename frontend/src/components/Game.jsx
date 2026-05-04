import { useState, useEffect } from 'react';
import { Navigate } from 'react-router-dom';
import api from '../api/axios';
import Ranking from './Ranking';
import './Game.css';

function Game({ isAuthenticated }) {
  const [betAmount, setBetAmount] = useState(100);
  const [choice, setChoice] = useState('Heads'); // Heads or Tails
  const [isFlipping, setIsFlipping] = useState(false);
  const [result, setResult] = useState(null);
  const [balance, setBalance] = useState(0);
  const [nickName, setNickName] = useState('');

  const [gameRefreshTrigger, setGameRefreshTrigger] = useState(0);

  useEffect(() => {
    if (isAuthenticated) {
      fetchProfile();
    }
  }, [isAuthenticated, gameRefreshTrigger]);

  const fetchProfile = async () => {
    try {
      const response = await api.get('/v1/profile/me');
      if (response.data.success) {
        setBalance(Number(response.data.data.coin));
        setNickName(response.data.data.nickName);
      }
    } catch (error) {
      console.error("Failed to fetch profile", error);
    }
  };

  const fetchBalanceOnly = async () => {
    try {
      const response = await api.get('/v1/profile/me');
      if (response.data.success) {
        setBalance(Number(response.data.data.coin));
        setGameRefreshTrigger(prev => prev + 1); // Trigger ranking refresh
      }
    } catch (error) {
      console.error("Failed to fetch balance", error);
    }
  };

  if (!isAuthenticated) {
    return <Navigate to="/login" />;
  }

  const handlePlay = async () => {
    // Round bet amount to nearest 100 just in case
    const finalBet = Math.max(100, Math.floor(betAmount / 100) * 100);
    setBetAmount(finalBet);

    if (balance < finalBet) {
      alert("Not enough coins!");
      return;
    }

    setIsFlipping(true);
    setResult(null);

    try {
      // 1. Start Game (Deduct balance)
      const startResponse = await api.post(`/v1/game/start?gameId=COIN_FLIP&betAmount=${finalBet}`);
      if (!startResponse.data.success) {
        throw new Error(startResponse.data.error?.message || "Failed to start game");
      }

      // Local update for immediate feedback
      setBalance(prev => prev - finalBet);

      // 2. Simulate Flip Animation
      setTimeout(async () => {
        const outcome = Math.random() > 0.5 ? 'Heads' : 'Tails';
        setResult(outcome);
        setIsFlipping(false);

        const isSuccess = outcome === choice;

        // 3. Finish Game (Reward balance)
        try {
          const finishResponse = await api.post(`/v1/game/finish?gameId=COIN_FLIP&isSuccess=${isSuccess}&betAmount=${finalBet}`);
          if (finishResponse.data.success) {
            // Re-fetch balance to update both UI and Ranking
            fetchBalanceOnly();
          }
        } catch (error) {
          console.error("Failed to finish game", error);
        }
      }, 2000);

    } catch (error) {
      alert(error.message);
      setIsFlipping(false);
      fetchBalanceOnly();
    }
  };

  const adjustBet = (amount) => {
    setBetAmount(prev => Math.max(100, prev + amount));
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', width: '100%', gap: '2rem' }}>
      {/* Google Ad Placeholder */}
      <div className="glass-panel" style={{ 
        width: '100%', 
        maxWidth: '1100px', 
        height: '100px', 
        display: 'flex', 
        justifyContent: 'center', 
        alignItems: 'center',
        background: 'rgba(0,0,0,0.4)',
        border: '1px dashed var(--glass-border)',
        padding: 0
      }}>
        <span style={{ color: 'var(--text-muted)', fontSize: '0.9rem', letterSpacing: '2px' }}>GOOGLE ADSENSE PLACEHOLDER</span>
      </div>

      <div className="game-layout-container" style={{ display: 'flex', gap: '2rem', width: '100%', maxWidth: '1100px', alignItems: 'stretch' }}>
        {/* Game Panel */}
      <div className="glass-panel animate-fade-in" style={{ flex: '1.2', textAlign: 'center', minHeight: '730px', display: 'flex', flexDirection: 'column' }}>
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
            <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
              <button 
                className="btn" 
                style={{ padding: '0.5rem 0.8rem', minWidth: '40px' }} 
                onClick={() => adjustBet(-100)}
                disabled={isFlipping || betAmount <= 100}
              > - </button>
              <input 
                type="number" 
                className="input-field" 
                style={{ marginBottom: 0, textAlign: 'center' }}
                value={betAmount} 
                onChange={(e) => setBetAmount(Number(e.target.value))}
                min="100"
                step="100"
                disabled={isFlipping}
              />
              <button 
                className="btn" 
                style={{ padding: '0.5rem 0.8rem', minWidth: '40px' }} 
                onClick={() => adjustBet(100)}
                disabled={isFlipping}
              > + </button>
            </div>
          </div>
          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', color: 'var(--text-muted)' }}>Your Pick</label>
            <select 
              className="input-field" 
              style={{ marginBottom: 0 }}
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

      {/* Ranking Panel */}
      <div style={{ flex: '0.8' }}>
        <Ranking userCoin={balance} userNickName={nickName} refreshTrigger={gameRefreshTrigger} />
      </div>
    </div>
  </div>
  );
}

export default Game;
