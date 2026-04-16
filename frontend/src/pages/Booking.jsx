import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import SeatGrid from '../components/SeatGrid';
import { CreditCard, CheckCircle, Calendar, Film, Loader } from 'lucide-react';
import { MovieAPI, ShowtimeAPI, BookingAPI, PaymentAPI } from '../services/api';
import PaymentModal from '../components/PaymentModal';
import toast from 'react-hot-toast';

const Booking = () => {
  const { movieId } = useParams();
  const navigate = useNavigate();
  
  const [movie, setMovie] = useState(null);
  const [showtimes, setShowtimes] = useState([]);
  const [selectedShowtime, setSelectedShowtime] = useState(null);
  const [seats, setSeats] = useState([]);
  const [selectedSeats, setSelectedSeats] = useState([]);
  const [loading, setLoading] = useState(true);
  const [bookingSuccess, setBookingSuccess] = useState(false);
  const [showPayment, setShowPayment] = useState(false);
  const [reservation, setReservation] = useState(null);

  useEffect(() => {
    fetchInitialData();
  }, [movieId]);

  const fetchInitialData = async () => {
    try {
        setLoading(true);
        const [movieRes, showtimesRes] = await Promise.all([
            MovieAPI.getById(movieId),
            ShowtimeAPI.getAll(movieId)
        ]);
        setMovie(movieRes.data);
        setShowtimes(showtimesRes.data);
    } catch (error) {
        toast.error("Error loading movie data.");
        console.error(error);
    } finally {
        setLoading(false);
    }
  };

  const handleSelectShowtime = async (st) => {
      try {
          setLoading(true);
          setSelectedShowtime(st);
          setSelectedSeats([]);
          const seatsRes = await ShowtimeAPI.getSeats(st.id);
          setSeats(seatsRes.data);
      } catch (error) {
          toast.error("Error loading seating chart.");
      } finally {
          setLoading(false);
      }
  };

  const handleToggleSeat = (seatId) => {
    if (selectedSeats.includes(seatId)) {
        setSelectedSeats(selectedSeats.filter(id => id !== seatId));
    } else {
        setSelectedSeats([...selectedSeats, seatId]);
    }
  };

  const handleCheckoutClick = async () => {
    try {
        setLoading(true);
        const response = await BookingAPI.reserve({
            showtimeId: selectedShowtime.id,
            seatIds: selectedSeats
        });
        setReservation(response.data);
        setShowPayment(true);
    } catch (error) {
        toast.error(error.response?.data?.message || "Failed to create reservation.");
    } finally {
        setLoading(false);
    }
  };

  const handleConfirmPayment = async () => {
      try {
          setLoading(true);
          await PaymentAPI.confirm({
              reservationId: reservation.id,
              cardData: "4242 4242 4242 4242" // Mock data
          });
          setShowPayment(false);
          setBookingSuccess(true);
      } catch (error) {
          toast.error("Payment confirmation failed.");
      } finally {
          setLoading(false);
      }
  };

  if (loading && !bookingSuccess && !showPayment && !selectedShowtime) {
      return <div className="container animate-pulse" style={{ padding: '6rem 1.5rem', textAlign: 'center', fontSize: '1.2rem' }}>Fetching Cinematic Data...</div>;
  }

  if (bookingSuccess) {
      return (
          <div className="container" style={{ textAlign: 'center', padding: '6rem 1rem' }}>
              <div style={{ background: 'rgba(16, 185, 129, 0.1)', width: '120px', height: '120px', borderRadius: '50%', display: 'flex', justifyContent: 'center', alignItems: 'center', margin: '0 auto 2rem' }}>
                <CheckCircle size={64} style={{ color: 'var(--success)' }} />
              </div>
              <h1>Reservation Confirmed!</h1>
              <p style={{ color: 'var(--text-secondary)', marginTop: '1rem', fontSize: '1.2rem', maxWidth: '500px', margin: '1rem auto' }}>
                  Your seats for <strong>{movie?.title}</strong> have been secured. You can view your ticket in the My Bookings section.
              </p>
              <button className="btn btn-primary" style={{ marginTop: '3rem', padding: '1rem 3rem' }} onClick={() => navigate('/my-bookings')}>
                  View My Tickets
              </button>
          </div>
      );
  }

  const pricePerSeat = 15;
  const total = selectedSeats.length * pricePerSeat;

  return (
    <div className="container" style={{ paddingBottom: '6rem' }}>
      <div style={{ display: 'grid', gridTemplateColumns: '300px 1fr', gap: '3rem', marginTop: '2rem' }}>
          
          {/* Movie Sidebar */}
          <div>
              <div className="glass-panel" style={{ padding: '0', overflow: 'hidden', position: 'sticky', top: '2rem' }}>
                  <img src={movie?.posterUrl} alt={movie?.title} style={{ width: '100%', height: '400px', objectFit: 'cover' }} />
                  <div style={{ padding: '1.5rem' }}>
                      <h2 style={{ fontSize: '1.5rem', marginBottom: '0.5rem' }}>{movie?.title}</h2>
                      <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '1rem' }}>
                        <span className="badge">{movie?.genre}</span>
                      </div>
                      <p style={{ fontSize: '0.9rem', color: 'var(--text-secondary)', lineHeight: 1.6 }}>{movie?.description}</p>
                  </div>
              </div>
          </div>

          {/* Booking Area */}
          <div>
              {!selectedShowtime ? (
                  <div className="glass-panel" style={{ padding: '2rem' }}>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', marginBottom: '2rem' }}>
                          <Calendar style={{ color: 'var(--primary-accent)' }} />
                          <h2 style={{ margin: 0 }}>Available Showtimes</h2>
                      </div>
                      
                      {showtimes.length === 0 ? (
                          <div style={{ textAlign: 'center', padding: '3rem', color: 'var(--text-secondary)' }}>
                             No showtimes available for this title at the moment.
                          </div>
                      ) : (
                        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))', gap: '1rem' }}>
                            {showtimes.map(st => (
                                <button 
                                    key={st.id} 
                                    className="glass-panel" 
                                    onClick={() => handleSelectShowtime(st)}
                                    style={{ 
                                        padding: '1.5rem', 
                                        textAlign: 'center', 
                                        cursor: 'pointer', 
                                        border: '1px solid var(--border-color)',
                                        transition: 'all 0.3s ease',
                                        background: 'rgba(255,255,255,0.02)'
                                    }}
                                >
                                    <div style={{ fontWeight: 600, fontSize: '1.2rem', marginBottom: '0.5rem' }}>
                                        {new Date(st.startTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                                    </div>
                                    <div style={{ color: 'var(--text-secondary)', fontSize: '0.8rem' }}>
                                        Screen {st.screenId} • {new Date(st.startTime).toLocaleDateString()}
                                    </div>
                                </button>
                            ))}
                        </div>
                      )}
                  </div>
              ) : (
                  <div className="glass-panel" style={{ padding: '2rem' }}>
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '3rem' }}>
                          <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                              <button onClick={() => setSelectedShowtime(null)} style={{ background: 'transparent', border: 'none', color: 'var(--text-secondary)', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                                  ← Change Time
                              </button>
                              <h2 style={{ margin: 0 }}>Select Your Seats</h2>
                          </div>
                          <div style={{ textAlign: 'right' }}>
                              <div style={{ fontWeight: 600 }}>{new Date(selectedShowtime.startTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</div>
                              <div style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>Screen {selectedShowtime.screenId}</div>
                          </div>
                      </div>

                      {loading ? (
                          <div style={{ textAlign: 'center', padding: '4rem' }}>
                             <Loader className="animate-spin" style={{ margin: '0 auto' }} />
                          </div>
                      ) : (
                        <SeatGrid seats={seats} selectedSeats={selectedSeats} onToggleSeat={handleToggleSeat} />
                      )}
                  </div>
              )}
          </div>
      </div>

      {/* Floating Checkout Bar */}
      <div className="glass-panel" style={{ 
          position: 'fixed', 
          bottom: 0, 
          left: 0, 
          right: 0, 
          padding: '1.5rem',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          borderBottom: 'none',
          borderRadius: '24px 24px 0 0',
          transform: selectedSeats.length > 0 ? 'translateY(0)' : 'translateY(100%)',
          transition: 'transform 0.4s cubic-bezier(0.4, 0, 0.2, 1)',
          zIndex: 100,
          background: 'rgba(15, 23, 42, 0.9)',
          backdropFilter: 'blur(16px)'
      }}>
          <div className="container" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', width: '100%' }}>
              <div>
                  <div style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>{selectedSeats.length} Seats Selected</div>
                  <div style={{ fontSize: '1.8rem', fontWeight: 800, color: 'white' }}>${total.toFixed(2)}</div>
              </div>
              <button 
                className="btn btn-primary" 
                onClick={handleCheckoutClick} 
                disabled={loading} 
                style={{ padding: '1rem 2.5rem', fontSize: '1.1rem', boxShadow: '0 8px 20px rgba(99, 102, 241, 0.4)' }}
              >
                  {loading ? <Loader className="animate-spin" /> : <><CreditCard size={20} /> Checkout Securely</>}
              </button>
          </div>
      </div>
      
      {showPayment && !bookingSuccess && (
          <PaymentModal 
            total={total} 
            onConfirm={handleConfirmPayment} 
            onCancel={() => setShowPayment(false)} 
          />
      )}

      <style>{`
          .badge {
              background: rgba(99, 102, 241, 0.2);
              color: var(--primary-accent);
              padding: 0.3rem 0.8rem;
              border-radius: 999px;
              font-size: 0.8rem;
              font-weight: 600;
          }
      `}</style>
    </div>
  );
};

export default Booking;
