import { Link } from 'react-router-dom';
import { Calendar } from 'lucide-react';

const MovieCard = ({ movie }) => {
  return (
    <div className="glass-panel p-card" style={{ 
        display: 'flex', 
        flexDirection: 'column', 
        overflow: 'hidden', 
        transition: 'var(--transition)',
        cursor: 'pointer'
    }}>
      <div style={{ height: '350px', width: '100%', overflow: 'hidden', position: 'relative' }}>
          {/* Overlay gradient */}
          <div style={{
              position: 'absolute', inset: 0, 
              background: 'linear-gradient(to top, var(--bg-secondary), transparent)',
              zIndex: 1
          }} />
          <img 
            src={movie.posterUrl} 
            alt={movie.title} 
            style={{ 
              width: '100%', 
              height: '100%', 
              objectFit: 'cover', 
              transition: 'var(--transition)'
            }} 
            className="card-img"
          />
      </div>
      
      <div style={{ padding: '1.5rem', flex: 1, display: 'flex', flexDirection: 'column', zIndex: 2 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '0.5rem' }}>
            <h3 style={{ fontSize: '1.25rem', marginBottom: '0.5rem' }}>{movie.title}</h3>
            <span style={{ 
                background: 'rgba(99, 102, 241, 0.2)', 
                color: 'var(--primary-accent)', 
                padding: '0.25rem 0.75rem', 
                borderRadius: '999px',
                fontSize: '0.75rem',
                fontWeight: 600
            }}>
                {movie.genre}
            </span>
        </div>
        
        <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', marginBottom: '1.5rem', flex: 1, display: '-webkit-box', WebkitLineClamp: 3, WebkitBoxOrient: 'vertical', overflow: 'hidden' }}>
          {movie.description}
        </p>

        <Link to={`/book/${movie.id}`} style={{ textDecoration: 'none' }}>
            <button className="btn btn-primary" style={{ width: '100%' }}>
                <Calendar size={18} /> View Showtimes
            </button>
        </Link>
      </div>

      <style>{`
        .p-card:hover {
            transform: translateY(-8px);
            box-shadow: 0 20px 40px rgba(0,0,0,0.4);
            border-color: var(--primary-accent);
        }
        .p-card:hover .card-img {
            transform: scale(1.05);
        }
      `}</style>
    </div>
  );
};

export default MovieCard;
