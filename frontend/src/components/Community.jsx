import { useState, useEffect } from 'react';
import { Navigate } from 'react-router-dom';
import api from '../api/axios';

function Community({ isAuthenticated }) {
  const [posts, setPosts] = useState([]);
  const [newPost, setNewPost] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    if (isAuthenticated) {
      // Mock fetch posts. In real app, replace with api.get('/v1/community/posts')
      setPosts([
        { id: 1, author: 'Player1', content: 'Just hit 10000 coins!', timestamp: '2 mins ago' },
        { id: 2, author: 'GamerGirl', content: 'Any tips for the ladder game?', timestamp: '5 mins ago' },
      ]);
    }
  }, [isAuthenticated]);

  if (!isAuthenticated) {
    return <Navigate to="/login" />;
  }

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!newPost.trim()) return;

    setIsLoading(true);
    // Mock create post
    setTimeout(() => {
      const post = {
        id: Date.now(),
        author: 'You',
        content: newPost,
        timestamp: 'Just now'
      };
      setPosts([post, ...posts]);
      setNewPost('');
      setIsLoading(false);
    }, 500);
  };

  return (
    <div className="glass-panel animate-fade-in" style={{ width: '100%', maxWidth: '800px' }}>
      <h2 style={{ fontSize: '2.5rem', marginBottom: '2rem', color: 'var(--secondary)' }}>Community Hub</h2>
      
      <form onSubmit={handleSubmit} style={{ marginBottom: '3rem' }}>
        <textarea 
          className="input-field" 
          rows="3" 
          placeholder="Share your achievements or ask a question..." 
          value={newPost}
          onChange={(e) => setNewPost(e.target.value)}
          style={{ resize: 'none' }}
        ></textarea>
        <button type="submit" className="btn" disabled={isLoading} style={{ float: 'right' }}>
          {isLoading ? 'Posting...' : 'Post Message'}
        </button>
        <div style={{ clear: 'both' }}></div>
      </form>

      <div className="posts-container" style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
        {posts.map(post => (
          <div key={post.id} style={{ padding: '1.5rem', background: 'rgba(255,255,255,0.03)', borderRadius: '12px', borderLeft: '4px solid var(--primary)' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
              <strong style={{ color: 'var(--text-main)' }}>{post.author}</strong>
              <small style={{ color: 'var(--text-muted)' }}>{post.timestamp}</small>
            </div>
            <p style={{ color: 'var(--text-muted)', lineHeight: '1.6' }}>{post.content}</p>
          </div>
        ))}
      </div>
    </div>
  );
}

export default Community;
