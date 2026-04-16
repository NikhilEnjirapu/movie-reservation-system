import { useState } from 'react';
import { Ticket, User as UserIcon } from 'lucide-react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import AuthModal from './AuthModal';

const Navbar = () => {
  const { user, logout } = useAuth();
  const [showAuthModal, setShowAuthModal] = useState(false);
  
  return (
    <>
      <nav className="glass-panel" style={{ 
        position: 'sticky', 
        top: 0, 
        zIndex: 50, 
        padding: '1rem 2rem',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        borderTop: 'none',
        borderLeft: 'none',
        borderRight: 'none',
        borderRadius: '0 0 16px 16px',
        marginBottom: '2rem'
      }}>
        <Link to="/" style={{ textDecoration: 'none', color: 'inherit', display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
          <Ticket size={28} color="var(--primary-accent)" />
          <h2 style={{ margin: 0, fontSize: '1.5rem', background: 'linear-gradient(90deg, #f8fafc, #94a3b8)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>
            CineReserve
          </h2>
        </Link>
        
        <div style={{ display: 'flex', gap: '1rem', alignItems: 'center' }}>
          {user ? (
            <>
              {user.role === 'ADMIN' && (
                <Link to="/admin" style={{ color: 'var(--primary-accent)', textDecoration: 'none', marginRight: '1rem', fontWeight: 'bold' }}>
                    Admin Panel
                </Link>
              )}
              <Link to="/my-bookings" style={{ color: 'var(--text-primary)', textDecoration: 'none', marginRight: '1rem' }}>
                  My Bookings
              </Link>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', background: 'rgba(255,255,255,0.1)', padding: '0.5rem 1rem', borderRadius: '99px' }}>
                  <UserIcon size={18} />
                  <span>{user.name}</span>
              </div>
              <button className="btn btn-accent" onClick={logout} style={{ padding: '0.5rem 1rem' }}>Logout</button>
            </>
          ) : (
            <button className="btn btn-primary" onClick={() => setShowAuthModal(true)}>
              Sign In
            </button>
          )}
        </div>
      </nav>

      {showAuthModal && <AuthModal onClose={() => setShowAuthModal(false)} />}
    </>
  );
};

export default Navbar;
