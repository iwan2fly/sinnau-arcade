import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import Login from './components/Login';
import Game from './components/Game';
import Community from './components/Community';
import NicknameModal from './components/NicknameModal';
import { useState, useEffect } from 'react';
import api from './api/axios';
import './App.css';

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [user, setUser] = useState(null);

  useEffect(() => {
    // Check authentication status on load
    checkAuth();
  }, []);

  const checkAuth = async () => {
    try {
      const response = await api.get('/v1/profile/me');
      if (response.data && response.data.success) {
        setIsAuthenticated(true);
        setUser(response.data.data);
      }
    } catch (error) {
      setIsAuthenticated(false);
    }
  };

  const handleLogout = () => {
    // Call logout endpoint if exists, then clear state
    api.post('/v1/auth/logout').finally(() => {
      setIsAuthenticated(false);
      setUser(null);
      window.location.href = '/login';
    });
  };

  return (
    <Router>
      <nav>
        <Link to="/" className="nav-brand">Sinnau Arcade</Link>
        <div className="nav-links">
          {isAuthenticated ? (
            <>
              <Link to="/game">Arcade</Link>
              <Link to="/community">Community</Link>
              <a href="#" onClick={handleLogout}>Logout ({user?.nickName})</a>
            </>
          ) : (
            <Link to="/login">Login</Link>
          )}
        </div>
      </nav>
      
      {isAuthenticated && user?.needsNicknameSetup && (
        <NicknameModal onComplete={checkAuth} />
      )}

      <main className="main-content">
        <Routes>
          <Route path="/login" element={<Login setAuth={setIsAuthenticated} setUser={setUser} />} />
          <Route path="/game" element={<Game isAuthenticated={isAuthenticated} />} />
          <Route path="/community" element={<Community isAuthenticated={isAuthenticated} />} />
          <Route path="/" element={<div className="glass-panel animate-fade-in" style={{textAlign: 'center'}}>
            <h1>Welcome to Sinnau Arcade</h1>
            <p style={{marginTop: '1rem', color: 'var(--text-muted)'}}>Experience the next generation of web gaming.</p>
            {!isAuthenticated && <Link to="/login"><button className="btn" style={{marginTop: '2rem'}}>Start Playing</button></Link>}
          </div>} />
        </Routes>
      </main>
    </Router>
  );
}

export default App;
