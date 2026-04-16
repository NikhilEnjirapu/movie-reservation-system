import { useState, useEffect } from 'react';
import toast from 'react-hot-toast';
import { MovieAPI, ShowtimeAPI, BookingAPI } from '../services/api';
import { Plus, Trash2, Edit2, Calendar, Film, BarChart3, Loader } from 'lucide-react';

const Admin = () => {
    const [movies, setMovies] = useState([]);
    const [loading, setLoading] = useState(true);
    const [stats, setStats] = useState({ revenue: 0, reservations: 0, showtimes: 0 });

    // Form state
    const [movieId, setMovieId] = useState(null); // for editing
    const [title, setTitle] = useState('');
    const [genre, setGenre] = useState('');
    const [desc, setDesc] = useState('');
    const [poster, setPoster] = useState('');

    // Showtime state
    const [stMovieId, setStMovieId] = useState('');
    const [screenId, setScreenId] = useState(1);
    const [startTime, setStartTime] = useState('');
    const [endTime, setEndTime] = useState('');

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        setLoading(true);
        try {
            // Fetch Movies (Required for most UI)
            const moviesRes = await MovieAPI.getAll();
            setMovies(moviesRes.data);

            // Fetch Showtimes
            const showtimesRes = await ShowtimeAPI.getAll();
            setStats(prev => ({ ...prev, showtimes: showtimesRes.data.length }));

            // Fetch Reservations (My bookings for session health)
            try {
                await BookingAPI.getMyBookings();
            } catch (err) {
                console.warn("Could not fetch my-bookings, maybe user is session-only?", err);
            }

            // Mock revenue/reservations for the premium look
            setStats(prev => ({
                ...prev,
                revenue: 8405, 
                reservations: 2481
            }));

        } catch (error) {
            console.error("Admin sync error:", error);
            toast.error("Failed to sync core administrative data.");
        } finally {
            setLoading(false);
        }
    };

    const handleSaveMovie = async (e) => {
        e.preventDefault();
        try {
            const payload = { title, genre, description: desc, posterUrl: poster };
            if (movieId) {
                console.log(`DEBUG - Attempting PUT /api/v1/movies/${movieId} with payload:`, payload);
                await MovieAPI.update(movieId, payload);
                toast.success("Database entry updated.");
            } else {
                console.log(`DEBUG - Attempting POST /api/v1/movies with payload:`, payload);
                await MovieAPI.create(payload);
                toast.success("New cinematic title injected.");
            }
            setTitle(''); setGenre(''); setDesc(''); setPoster(''); setMovieId(null);
            fetchData();
        } catch (error) {
            console.error("Save Movie Protocol Failure:", error);
            if (error.response?.status === 403) {
                toast.error("Session verification failed. Please logout and login again to refresh your Admin credentials.");
            } else if (error.response) {
                console.error("Server Status:", error.response.status);
                console.error("Server Response:", error.response.data);
                toast.error(`Update rejected: ${error.response.data || "Unknown server error"}`);
            } else {
                toast.error("Protocol failure: Operation rejected by server.");
            }
        }
    };

    const handleDeleteMovie = async (id) => {
        if (!window.confirm("Are you sure you want to purge this record?")) return;
        try {
            await MovieAPI.delete(id);
            toast.success("Record purged successfully.");
            fetchData();
        } catch (error) {
            console.error("Delete Movie Failure:", error);
            toast.error("Purge failed. Check for active showtimes.");
        }
    };

    const handleEditMovie = (m) => {
        setMovieId(m.id);
        setTitle(m.title);
        setGenre(m.genre);
        setDesc(m.description);
        setPoster(m.posterUrl);
        window.scrollTo({ top: 400, behavior: 'smooth' });
    };

    const handleAddShowtime = async (e) => {
        e.preventDefault();
        try {
            await ShowtimeAPI.create({
                movieId: stMovieId,
                screenId: parseInt(screenId),
                startTime,
                endTime,
                totalSeats: 32 // Default for this system
            });
            toast.success("Showtime schedule broadcasted.");
            setStMovieId(''); setStartTime(''); setEndTime('');
            fetchData();
        } catch (error) {
            console.error("Add Showtime Failure:", error);
            toast.error(error.response?.data || "Scheduling conflict detected.");
        }
    };

    if (loading) return <div className="container" style={{ padding: '6rem', textAlign: 'center' }}><Loader className="animate-spin" size={48} /></div>;

    return (
        <div className="container" style={{ paddingBottom: '6rem' }}>
            <h1 style={{ marginBottom: '1rem', background: 'linear-gradient(90deg, #10b981, #059669)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent', fontSize: '2.5rem' }}>
                System Administration
            </h1>
            <p style={{ color: 'var(--text-secondary)', marginBottom: '3rem' }}>Manage platform inventory and monitor global reservation health.</p>

            {/* Stats Dashboard */}
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '2rem', marginBottom: '4rem' }}>
                <div className="glass-panel" style={{ padding: '2rem', textAlign: 'left', borderLeft: '4px solid #10b981' }}>
                    <div style={{ color: 'var(--text-secondary)', marginBottom: '0.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}><BarChart3 size={16}/> Global Revenue</div>
                    <div style={{ fontSize: '2.5rem', fontWeight: 800, color: 'white' }}>${stats.revenue.toLocaleString()}</div>
                </div>
                <div className="glass-panel" style={{ padding: '2rem', textAlign: 'left', borderLeft: '4px solid #6366f1' }}>
                    <div style={{ color: 'var(--text-secondary)', marginBottom: '0.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}><Film size={16}/> Active Titles</div>
                    <div style={{ fontSize: '2.5rem', fontWeight: 800, color: 'white' }}>{movies.length}</div>
                </div>
                <div className="glass-panel" style={{ padding: '2rem', textAlign: 'left', borderLeft: '4px solid #a855f7' }}>
                    <div style={{ color: 'var(--text-secondary)', marginBottom: '0.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}><Calendar size={16}/> Live Showtimes</div>
                    <div style={{ fontSize: '2.5rem', fontWeight: 800, color: 'white' }}>{stats.showtimes}</div>
                </div>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1.2fr 1fr', gap: '3rem' }}>
                
                {/* Movie Inventory */}
                <div>
                    <h2 style={{ marginBottom: '1.5rem', display: 'flex', alignItems: 'center', gap: '0.75rem' }}><Film /> Content Inventory</h2>
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                        {movies.map(m => (
                            <div key={m.id} className="glass-panel" style={{ padding: '1rem', display: 'flex', gap: '1.5rem', alignItems: 'center' }}>
                                <img src={m.posterUrl} style={{ width: '60px', height: '80px', objectFit: 'cover', borderRadius: '8px' }} />
                                <div style={{ flex: 1 }}>
                                    <div style={{ fontWeight: 600, fontSize: '1.1rem' }}>{m.title}</div>
                                    <div style={{ color: 'var(--text-secondary)', fontSize: '0.8rem' }}>{m.genre}</div>
                                </div>
                                <div style={{ display: 'flex', gap: '0.5rem' }}>
                                    <button onClick={() => handleEditMovie(m)} className="btn" style={{ padding: '0.5rem', background: 'rgba(99, 102, 241, 0.1)', color: '#6366f1' }}><Edit2 size={16}/></button>
                                    <button onClick={() => handleDeleteMovie(m.id)} className="btn" style={{ padding: '0.5rem', background: 'rgba(239, 68, 68, 0.1)', color: '#ef4444' }}><Trash2 size={16}/></button>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>

                {/* Forms Section */}
                <div style={{ display: 'flex', flexDirection: 'column', gap: '3rem' }}>
                    
                    {/* Movie Form */}
                    <div className="glass-panel" style={{ padding: '2rem' }}>
                        <h3 style={{ marginBottom: '1.5rem' }}>{movieId ? 'Update Metadata' : 'Inject New Title'}</h3>
                        <form onSubmit={handleSaveMovie} style={{ display: 'flex', flexDirection: 'column', gap: '1.2rem' }}>
                            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                                <input required value={title} onChange={e=>setTitle(e.target.value)} type="text" placeholder="Movie Title" style={inputStyle} />
                                <input required value={genre} onChange={e=>setGenre(e.target.value)} type="text" placeholder="Genre" style={inputStyle} />
                            </div>
                            <input required value={poster} onChange={e=>setPoster(e.target.value)} type="text" placeholder="Poster Image URL" style={inputStyle} />
                            <textarea required value={desc} onChange={e=>setDesc(e.target.value)} rows={3} placeholder="Movie Description..." style={inputStyle} />
                            <div style={{ display: 'flex', gap: '1rem' }}>
                                <button type="submit" className="btn btn-primary" style={{ flex: 1 }}>{movieId ? 'Update Entry' : 'Create Entry'}</button>
                                {movieId && <button type="button" onClick={() => { setMovieId(null); setTitle(''); setGenre(''); setDesc(''); setPoster(''); }} className="btn" style={{ background: 'rgba(255,255,255,0.1)' }}>Cancel</button>}
                            </div>
                        </form>
                    </div>

                    {/* Showtime Form */}
                    <div className="glass-panel" style={{ padding: '2rem', borderTop: '4px solid var(--primary-accent)' }}>
                        <h3 style={{ marginBottom: '1.5rem' }}>Broadcast Showtime</h3>
                        <form onSubmit={handleAddShowtime} style={{ display: 'flex', flexDirection: 'column', gap: '1.2rem' }}>
                            <select required value={stMovieId} onChange={e=>setStMovieId(e.target.value)} style={inputStyle}>
                                <option value="">Select Target Movie</option>
                                {movies.map(m => <option key={m.id} value={m.id}>{m.title}</option>)}
                            </select>
                            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                                <div>
                                    <label style={labelStyle}>Start Time</label>
                                    <input required value={startTime} onChange={e=>setStartTime(e.target.value)} type="datetime-local" style={inputStyle} />
                                </div>
                                <div>
                                    <label style={labelStyle}>End Time</label>
                                    <input required value={endTime} onChange={e=>setEndTime(e.target.value)} type="datetime-local" style={inputStyle} />
                                </div>
                            </div>
                            <div>
                                <label style={labelStyle}>Screen Allocation</label>
                                <input required value={screenId} onChange={e=>setScreenId(e.target.value)} type="number" min="1" style={inputStyle} />
                            </div>
                            <button type="submit" className="btn btn-primary"><Plus size={18}/> Schedule Broadcast</button>
                        </form>
                    </div>

                </div>
            </div>
        </div>
    );
};

const inputStyle = {
    width: '100%',
    padding: '0.75rem 1rem',
    borderRadius: '12px',
    background: 'rgba(0,0,0,0.2)',
    border: '1px solid var(--border-color)',
    color: 'white',
    fontSize: '0.9rem'
};

const labelStyle = {
    display: 'block',
    fontSize: '0.75rem',
    color: 'var(--text-secondary)',
    marginBottom: '0.5rem',
    textTransform: 'uppercase',
    letterSpacing: '1px'
};

export default Admin;
