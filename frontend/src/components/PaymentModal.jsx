import { useState } from 'react';
import { CreditCard, Loader, X } from 'lucide-react';
import toast from 'react-hot-toast';

const PaymentModal = ({ total, onConfirm, onCancel }) => {
    const [processing, setProcessing] = useState(false);

    const handleConfirmPayment = async () => {
        setProcessing(true);
        // The actual API call is handled by the parent (Booking.jsx) 
        // using the onConfirm callback which we can trigger here.
        // Or we could have passed the reservationId here.
        // For simplicity and decoupling, Booking.jsx handles the API call.
        
        await onConfirm();
        setProcessing(false);
    };

    return (
        <div style={{
            position: 'fixed', inset: 0, 
            background: 'rgba(0,0,0,0.8)', 
            backdropFilter: 'blur(8px)',
            display: 'flex', justifyContent: 'center', alignItems: 'center',
            zIndex: 1000
        }}>
            <div className="glass-panel" style={{ width: '100%', maxWidth: '400px', padding: '2.5rem', borderRadius: '24px', position: 'relative', border: '1px solid rgba(255,255,255,0.1)' }}>
                <button onClick={onCancel} style={{ position: 'absolute', top: '1.5rem', right: '1.5rem', background: 'transparent', border: 'none', color: 'var(--text-secondary)', cursor: 'pointer' }}>
                    <X size={24} />
                </button>

                <h3 style={{ marginBottom: '1rem', fontSize: '1.5rem' }}>Secure Checkout</h3>
                <p style={{ color: 'var(--text-secondary)', marginBottom: '2rem' }}>Total due: <strong style={{ color: 'white', fontSize: '1.2rem' }}>${total.toFixed(2)}</strong></p>

                <div style={{ display: 'flex', flexDirection: 'column', gap: '1.2rem', marginBottom: '2.5rem' }}>
                    <div>
                        <label style={{ fontSize: '0.8rem', color: 'var(--text-secondary)', display: 'block', marginBottom: '0.5rem' }}>CARD NUMBER</label>
                        <div style={{ position: 'relative' }}>
                            <CreditCard size={18} style={{ position: 'absolute', left: '1rem', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-secondary)' }} />
                            <input type="text" placeholder="#### #### #### ####" defaultValue="4242 4242 4242 4242" disabled style={{ width: '100%', padding: '0.8rem 1rem 0.8rem 2.8rem', borderRadius: '12px', background: 'rgba(0,0,0,0.3)', border: '1px solid var(--border-color)', color: 'white' }} />
                        </div>
                    </div>
                    <div style={{ display: 'flex', gap: '1rem' }}>
                        <div style={{ flex: 1 }}>
                            <label style={{ fontSize: '0.8rem', color: 'var(--text-secondary)', display: 'block', marginBottom: '0.5rem' }}>EXPIRY</label>
                            <input type="text" placeholder="MM/YY" defaultValue="12/26" disabled style={{ width: '100%', padding: '0.8rem 1rem', borderRadius: '12px', background: 'rgba(0,0,0,0.3)', border: '1px solid var(--border-color)', color: 'white' }} />
                        </div>
                        <div style={{ width: '100px' }}>
                            <label style={{ fontSize: '0.8rem', color: 'var(--text-secondary)', display: 'block', marginBottom: '0.5rem' }}>CVC</label>
                            <input type="text" placeholder="CVC" defaultValue="123" disabled style={{ width: '100%', padding: '0.8rem 1rem', borderRadius: '12px', background: 'rgba(0,0,0,0.3)', border: '1px solid var(--border-color)', color: 'white' }} />
                        </div>
                    </div>
                </div>

                <button className="btn btn-primary" onClick={handleConfirmPayment} disabled={processing} style={{ width: '100%', padding: '1rem', fontSize: '1.1rem', background: 'linear-gradient(135deg, #10b981, #059669)', border: 'none' }}>
                    {processing ? <Loader className="animate-spin" size={20} /> : <><CreditCard size={20} /> Authorize Payment</>}
                </button>
                
                <p style={{ textAlign: 'center', color: 'var(--text-secondary)', fontSize: '0.75rem', marginTop: '1.5rem' }}>
                    🔒 SSL Encrypted & Secure
                </p>
            </div>
        </div>
    );
};

export default PaymentModal;
