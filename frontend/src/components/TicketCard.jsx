import { QRCodeCanvas } from 'qrcode.react';

const TicketCard = ({ reservation }) => {
    const formattedDate = new Date(reservation.startTime).toLocaleString('en-US', {
        month: 'short',
        day: 'numeric',
        hour: 'numeric',
        minute: '2-digit',
        hour12: true
    });

    const isUpcoming = reservation.status === 'UPCOMING' || reservation.status === 'PENDING_PAYMENT';

    return (
        <div className="glass-panel" style={{ 
            display: 'flex', 
            overflow: 'hidden', 
            borderRadius: '24px',
            marginBottom: '1.5rem',
            borderLeft: `6px solid ${isUpcoming ? 'var(--success)' : 'var(--danger)'}`,
            background: 'rgba(255,255,255,0.03)'
        }}>
            <div style={{ padding: '2rem', flex: 1 }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '1.5rem' }}>
                    <div>
                        <h3 style={{ fontSize: '1.75rem', marginBottom: '0.25rem', color: 'white' }}>{reservation.movieTitle}</h3>
                        <p style={{ color: 'var(--text-secondary)', fontSize: '1.1rem' }}>{formattedDate}</p>
                    </div>
                    <div style={{ 
                        padding: '0.4rem 1rem', 
                        borderRadius: '999px', 
                        fontSize: '0.8rem', 
                        fontWeight: 700,
                        background: isUpcoming ? 'rgba(16, 185, 129, 0.1)' : 'rgba(239, 68, 68, 0.1)',
                        color: isUpcoming ? 'var(--success)' : 'var(--danger)',
                        textTransform: 'uppercase'
                    }}>
                        {reservation.status.replace('_', ' ')}
                    </div>
                </div>
                
                <div style={{ display: 'flex', gap: '3rem' }}>
                    <div>
                        <div style={{ fontSize: '0.75rem', color: 'var(--text-secondary)', textTransform: 'uppercase', letterSpacing: '1px', marginBottom: '0.4rem' }}>Seats</div>
                        <div style={{ fontWeight: 600, fontSize: '1.2rem' }}>{reservation.seatNumbers?.join(', ') || 'N/A'}</div>
                    </div>
                    <div>
                        <div style={{ fontSize: '0.75rem', color: 'var(--text-secondary)', textTransform: 'uppercase', letterSpacing: '1px', marginBottom: '0.4rem' }}>Total</div>
                        <div style={{ fontWeight: 600, fontSize: '1.2rem' }}>${reservation.totalPrice}</div>
                    </div>
                    <div>
                        <div style={{ fontSize: '0.75rem', color: 'var(--text-secondary)', textTransform: 'uppercase', letterSpacing: '1px', marginBottom: '0.4rem' }}>Reservation ID</div>
                        <div style={{ fontWeight: 600, fontSize: '1rem', fontFamily: 'monospace' }}>#{reservation.id.split('-')[0]}</div>
                    </div>
                </div>
            </div>
            
            <div style={{ 
                padding: '2rem', 
                background: 'rgba(255,255,255,0.02)', 
                borderLeft: '2px dashed rgba(255,255,255,0.1)',
                display: 'flex',
                flexDirection: 'column',
                justifyContent: 'center',
                alignItems: 'center',
                minWidth: '180px'
            }}>
                <div style={{ background: 'white', padding: '0.5rem', borderRadius: '12px' }}>
                    <QRCodeCanvas value={`TICKET-${reservation.id}`} size={100} bgColor="#ffffff" fgColor="#000000" />
                </div>
                <span style={{ fontSize: '0.7rem', color: 'var(--text-secondary)', marginTop: '1rem', fontWeight: 600 }}>SCAN AT ENTRANCE</span>
            </div>
        </div>
    );
};

export default TicketCard;
