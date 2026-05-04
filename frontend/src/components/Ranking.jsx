import { useState, useEffect } from 'react';
import api from '../api/axios';

function Ranking({ userCoin, userNickName, refreshTrigger }) {
  const [period, setPeriod] = useState('today');
  const [category, setCategory] = useState('profit'); // profit, games, winRate
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchRankings();
  }, [period, category, userNickName, refreshTrigger]);

  const fetchRankings = async () => {
    setLoading(true);
    try {
      const response = await api.get(`/v1/ranking?gameId=COIN_FLIP&category=${category}&period=${period}`);
      if (response.data.success) {
        // Map backend snake_case to frontend camelCase if necessary, or just use as is
        const rankings = response.data.data.map((item, index) => ({
          rank: index + 1,
          nickName: item.nick_name,
          value: Number(item.value)
        }));
        setData(rankings);
      }
    } catch (error) {
      console.error("Failed to fetch rankings", error);
      setData([]);
    } finally {
      setLoading(false);
    }
  };

  const userRank = data.find(item => item.nickName === userNickName)?.rank || '-';

  const formatValue = (val) => {
    if (category === 'profit') return `${val.toLocaleString()} 💰`;
    if (category === 'winRate') return `${val}% 🎯`;
    return `${val} 🎮`;
  };

  return (
    <div className="glass-panel animate-fade-in" style={{ 
      width: '100%', 
      maxWidth: '400px', 
      minHeight: '730px', 
      display: 'flex',
      flexDirection: 'column',
      position: 'relative'
    }}>
      {loading && (
        <div style={{ 
          position: 'absolute', 
          top: 0, left: 0, right: 0, bottom: 0, 
          background: 'rgba(0,0,0,0.3)', 
          borderRadius: '20px', 
          zIndex: 10,
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center'
        }}>
          <span>Loading...</span>
        </div>
      )}

      <div style={{ marginBottom: '1.5rem' }}>
        <h3 style={{ fontSize: '1.5rem', color: 'var(--primary)', marginBottom: '1rem' }}>Hall of Fame</h3>
        
        <div style={{ display: 'flex', gap: '0.5rem' }}>
          <select 
            className="input-field" 
            style={{ 
              flex: 1,
              marginBottom: 0, 
              padding: '0.5rem', 
              fontSize: '0.85rem',
              backgroundColor: '#1e1b4b',
              color: 'white',
              border: '1px solid var(--primary)'
            }}
            value={category}
            onChange={(e) => setCategory(e.target.value)}
          >
            <option value="profit">Top Earners</option>
            <option value="games">Most Played</option>
            <option value="winRate">Best Win Rate</option>
          </select>

          <select 
            className="input-field" 
            style={{ 
              flex: 1,
              marginBottom: 0, 
              padding: '0.5rem', 
              fontSize: '0.85rem',
              backgroundColor: '#1e1b4b',
              color: 'white',
              border: '1px solid var(--glass-border)'
            }}
            value={period}
            onChange={(e) => setPeriod(e.target.value)}
          >
            <option value="today">Today</option>
            <option value="week">Weekly</option>
            <option value="month">Monthly</option>
            <option value="year">Yearly</option>
          </select>
        </div>
      </div>

      <div style={{ display: 'flex', flexDirection: 'column', gap: '0.4rem', flex: 1 }}>
        {data.length > 0 ? data.map((item) => (
          <div 
            key={item.nickName} 
            className={item.nickName === userNickName ? 'animate-pulse' : ''}
            style={{ 
              display: 'flex', 
              alignItems: 'center', 
              padding: '0.5rem 1rem', 
              background: item.nickName === userNickName ? 'rgba(139, 92, 246, 0.2)' : 'rgba(255,255,255,0.03)', 
              borderRadius: '10px',
              border: item.nickName === userNickName 
                ? '1px solid var(--primary)' 
                : (item.rank <= 3 ? `1px solid ${item.rank === 1 ? '#fbbf24' : item.rank === 2 ? '#94a3b8' : '#b45309'}` : '1px solid transparent'),
            }}
          >
            <span style={{ 
              width: '25px', 
              fontWeight: 'bold', 
              color: item.rank === 1 ? '#fbbf24' : item.rank === 2 ? '#94a3b8' : item.rank === 3 ? '#b45309' : 'var(--text-muted)'
            }}>
              {item.rank}
            </span>
            <span style={{ flex: 1, fontWeight: item.nickName === userNickName ? 'bold' : 500, fontSize: '0.9rem', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
              {item.nickName} {item.nickName === userNickName && '(You)'}
            </span>
            <span style={{ color: 'var(--secondary)', fontWeight: 'bold', fontSize: '0.9rem' }}>{formatValue(item.value)}</span>
          </div>
        )) : (
          <div style={{ textAlign: 'center', color: 'var(--text-muted)', marginTop: '2rem' }}>
            No rankings found for this period.
          </div>
        )}
      </div>

      <div style={{ 
        marginTop: '1.5rem', 
        padding: '1rem', 
        background: 'rgba(139, 92, 246, 0.1)', 
        borderRadius: '12px', 
        border: '1px dashed var(--primary)',
        textAlign: 'center'
      }}>
        <p style={{ fontSize: '0.9rem', color: 'var(--text-muted)' }}>
          Your <span style={{ color: 'var(--primary)' }}>{category === 'profit' ? 'Profit' : category === 'games' ? 'Games' : 'Win Rate'}</span> Rank: <span style={{ color: 'white', fontWeight: 'bold' }}>{userRank}</span>
        </p>
      </div>
    </div>
  );
}

export default Ranking;
