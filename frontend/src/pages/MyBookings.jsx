import { useState, useEffect } from 'react';
import TicketCard from '../components/TicketCard';
import { useAuth } from '../context/AuthContext';
import { BookingAPI } from '../services/api';
import { Loader, Ticket } from 'lucide-react';
import toast from 'react-hot-toast';

const MyBookings = () => {
    const { user } = useAuth();
    const [reservations, setReservations] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (user) {
            fetchBookings();
        }
    }, [user]);

    const fetchBookings = async () => {
        try {
            setLoading(true);
            const response = await BookingAPI.getMyBookings();
            setReservations(response.data);
        } catch (error) {
            toast.error("Failed to retrieve your cinematic passes.");
            console.error(error);
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return <div className="container" style={{ padding: '6rem', textAlign: 'center' }}><Loader className="animate-spin" size={48} /></div>;
    }

    return (
        <div className="container" style={{ paddingBottom: '6rem' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', marginBottom: '3rem' }}>
                <div style={{ background: 'var(--primary-accent)', padding: '0.75rem', borderRadius: '12px' }}>
                    <Ticket color="white" />
                </div>
                <h1 style={{ fontSize: '2.5rem', margin: 0 }}>My Reservations</h1>
            </div>

            <div style={{ maxWidth: '900px' }}>
                {reservations.length > 0 ? (
                    <div>
                        <p style={{ color: 'var(--text-secondary)', marginBottom: '2rem' }}>
                            Showing {reservations.length} active and past reservations. Present these QR codes at the theater entrance.
                        </p>
                        {reservations.map(res => (
                            <TicketCard key={res.id} reservation={res} />
                        ))}
                    </div>
                ) : (
                    <div className="glass-panel" style={{ padding: '4rem', textAlign: 'center' }}>
                        <Ticket size={48} style={{ color: 'var(--text-secondary)', marginBottom: '1.5rem', opacity: 0.3 }} />
                        <h2 style={{ color: 'var(--text-secondary)' }}>No active passes found.</h2>
                        <p style={{ color: 'var(--text-secondary)', marginTop: '0.5rem' }}>Your journey hasn't started yet. Browse the current shows and secure your seat!</p>
                        <button className="btn btn-primary" style={{ marginTop: '2rem' }} onClick={() => window.location.href = '/'}>Explore Now</button>
                    </div>
                )}
            </div>
        </div>
    );
};

export default MyBookings;
