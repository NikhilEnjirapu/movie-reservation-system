import { Monitor } from 'lucide-react';

const SeatGrid = ({ seats, selectedSeats, onToggleSeat }) => {
  // Sort and group seats by row, assuming format 'A1', 'A2', 'B1' etc.
  const rowMap = seats.reduce((acc, seat) => {
    const row = seat.seatNumber.charAt(0);
    if (!acc[row]) acc[row] = [];
    acc[row].push(seat);
    return acc;
  }, {});

  const rows = Object.keys(rowMap).sort();

  return (
    <div style={{ padding: '2rem', display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
      
      <div style={{ 
        width: '100%', maxWidth: '600px', height: '40px', 
        background: 'linear-gradient(to bottom, rgba(255,255,255,0.2), transparent)',
        borderRadius: '50% 50% 0 0 / 100% 100% 0 0',
        display: 'flex', justifyContent: 'center', alignItems: 'center',
        marginBottom: '4rem',
        borderTop: '2px solid rgba(255,255,255,0.5)',
        position: 'relative'
      }}>
        <div style={{ position: 'absolute', top: '-30px', color: 'var(--text-secondary)' }}>
            <Monitor size={24} style={{ display: 'inline', marginRight: '8px' }}/>
            SCREEN
        </div>
      </div>

      <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
        {rows.map(row => (
          <div key={row} style={{ display: 'flex', gap: '1rem', alignItems: 'center' }}>
            <strong style={{ width: '20px', color: 'var(--text-secondary)' }}>{row}</strong>
            <div style={{ display: 'flex', gap: '0.8rem' }}>
              {rowMap[row].sort((a,b) => a.seatNumber.localeCompare(b.seatNumber)).map(seat => {
                const isSelected = selectedSeats.includes(seat.id);
                const isBooked = seat.status === 'BOOKED' || seat.status === 'RESERVED';
                
                return (
                  <button
                    key={seat.id}
                    disabled={isBooked}
                    onClick={() => !isBooked && onToggleSeat(seat.id)}
                    style={{
                      width: '40px', height: '40px',
                      borderRadius: '8px 8px 4px 4px',
                      border: 'none',
                      cursor: isBooked ? 'not-allowed' : 'pointer',
                      background: isBooked ? 'var(--seat-booked)' : 
                                  isSelected ? 'var(--seat-selected)' : 'var(--seat-available)',
                      color: isSelected ? 'white' : isBooked ? 'rgba(255,255,255,0.5)' : '#333',
                      fontWeight: 600,
                      transition: 'transform 0.2s, background 0.2s',
                      transform: isSelected ? 'scale(1.1)' : 'scale(1)'
                    }}
                    title={isBooked ? 'Booked' : 'Available'}
                  >
                    {seat.seatNumber.substring(1)}
                  </button>
                )
              })}
            </div>
            <strong style={{ width: '20px', color: 'var(--text-secondary)', textAlign: 'right' }}>{row}</strong>
          </div>
        ))}
      </div>

      {/* Legend */}
      <div style={{ display: 'flex', gap: '2rem', marginTop: '3rem', padding: '1rem', background: 'rgba(0,0,0,0.3)', borderRadius: '12px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <div style={{ width: '20px', height: '20px', background: 'var(--seat-available)', borderRadius: '4px' }}></div>
              <span style={{ fontSize: '0.9rem' }}>Available</span>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <div style={{ width: '20px', height: '20px', background: 'var(--seat-selected)', borderRadius: '4px' }}></div>
              <span style={{ fontSize: '0.9rem' }}>Selected</span>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <div style={{ width: '20px', height: '20px', background: 'var(--seat-booked)', borderRadius: '4px' }}></div>
              <span style={{ fontSize: '0.9rem' }}>Booked</span>
          </div>
      </div>
    </div>
  );
};

export default SeatGrid;
