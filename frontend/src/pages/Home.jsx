import { useState, useEffect } from 'react';
import MovieCard from '../components/MovieCard';
import { Search, Filter } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { MovieAPI } from '../services/api';
import toast from 'react-hot-toast';

const Home = () => {
  const { user } = useAuth();
  const [movies, setMovies] = useState([]);
  const [recommendations, setRecommendations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [genre, setGenre] = useState('');

  useEffect(() => {
    fetchMovies();
    if (user) {
        fetchRecommendations();
    }
  }, [user]);

  const fetchMovies = async () => {
    try {
        setLoading(true);
        const response = await MovieAPI.getAll();
        setMovies(response.data);
    } catch (error) {
        toast.error("Failed to load movies. Please try again.");
        console.error("Fetch movies error:", error);
    } finally {
        setLoading(false);
    }
  };

  const fetchRecommendations = async () => {
    try {
        const response = await MovieAPI.getRecommendations();
        setRecommendations(response.data);
    } catch (error) {
        console.error("Fetch recommendations error:", error);
    }
  };

  const filteredMovies = movies.filter(m => 
      m.title.toLowerCase().includes(search.toLowerCase()) && 
      (genre === '' || m.genre.includes(genre))
  );

  if (loading) {
    return <div className="container animate-pulse" style={{ padding: '4rem 1.5rem', textAlign: 'center', fontSize: '1.5rem' }}>Synchronizing Cinematic Library...</div>;
  }

  return (
    <div className="container" style={{ paddingBottom: '4rem' }}>
      <div style={{ marginBottom: '3rem', textAlign: 'center' }}>
        <h1 style={{ fontSize: '3rem', marginBottom: '1rem', background: 'linear-gradient(90deg, #6366f1, #a855f7)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>
          Now Showing
        </h1>
        <p style={{ color: 'var(--text-secondary)', fontSize: '1.2rem', maxWidth: '600px', margin: '0 auto' }}>
          Experience cinema like never before. Book your exact seats and secure your premium experience in seconds.
        </p>

        {/* Search & Filter UI */}
        <div style={{ display: 'flex', justifyContent: 'center', gap: '1rem', marginTop: '2rem', maxWidth: '600px', margin: '2rem auto 0' }}>
            <div style={{ position: 'relative', flex: 1 }}>
                <Search size={18} style={{ position: 'absolute', left: '1rem', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-secondary)' }} />
                <input 
                    type="text" 
                    placeholder="Search movies..." 
                    value={search}
                    onChange={(e) => setSearch(e.target.value)}
                    style={{ width: '100%', padding: '0.8rem 1rem 0.8rem 2.8rem', borderRadius: '12px', border: '1px solid var(--border-color)', background: 'rgba(255,255,255,0.05)', color: 'white' }} 
                />
            </div>
            <div style={{ position: 'relative', width: '200px' }}>
                <Filter size={18} style={{ position: 'absolute', left: '1rem', top: '50%', transform: 'translateY(-50%)', color: '#fff' }} />
                <select 
                    value={genre} 
                    onChange={(e) => setGenre(e.target.value)}
                    style={{ width: '100%', padding: '0.8rem 1rem 0.8rem 2.8rem', borderRadius: '12px', border: '1px solid var(--border-color)', background: 'var(--primary-accent)', color: 'white', WebkitAppearance: 'none', cursor: 'pointer' }}>
                    <option value="">All Genres</option>
                    <option value="Action">Action</option>
                    <option value="Sci-Fi">Sci-Fi</option>
                    <option value="Thriller">Thriller</option>
                    <option value="Drama">Drama</option>
                </select>
            </div>
        </div>
      </div>
      
      {/* AI Recommendations */}
      {user && recommendations.length > 0 && (
          <div style={{ marginBottom: '4rem', padding: '2rem', background: 'rgba(99, 102, 241, 0.05)', borderRadius: '24px', border: '1px solid rgba(99, 102, 241, 0.2)' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '2rem' }}>
                  <div style={{ background: 'linear-gradient(135deg, #6366f1, #a855f7)', padding: '0.5rem', borderRadius: '8px' }}>
                      <span style={{ color: 'white', fontWeight: 'bold' }}>AI</span>
                  </div>
                  <h3 style={{ fontSize: '1.5rem', margin: 0 }}>Recommended For You</h3>
              </div>
              <div style={{ 
                display: 'grid', 
                gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', 
                gap: '2rem' 
              }}>
                {recommendations.map(movie => (
                  <MovieCard key={`rec-${movie.id}`} movie={movie} />
                ))}
              </div>
          </div>
      )}

      {/* Standard Grid */}
      <h3 style={{ fontSize: '1.5rem', marginBottom: '2rem' }}>All Shows</h3>
      {filteredMovies.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '4rem', color: 'var(--text-secondary)' }}>No movies match your search criteria.</div>
      ) : (
          <div style={{ 
            display: 'grid', 
            gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', 
            gap: '2rem' 
          }}>
            {filteredMovies.map(movie => (
              <MovieCard key={movie.id} movie={movie} />
            ))}
          </div>
      )}
    </div>
  );
};

export default Home;
