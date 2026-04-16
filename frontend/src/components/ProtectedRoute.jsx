import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useEffect } from 'react';
import toast from 'react-hot-toast';

const ProtectedRoute = () => {
    const { user, loading } = useAuth();

    useEffect(() => {
        if (!loading && !user) {
            toast.error('Please login to continue', { id: 'auth-error' });
        }
    }, [user, loading]);

    if (loading) return <div>Loading...</div>;

    return user ? <Outlet /> : <Navigate to="/" replace />;
};

export default ProtectedRoute;
