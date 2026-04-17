import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_URL || '/api/v1';

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

apiClient.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const MovieAPI = {
  getAll: () => apiClient.get('/movies'),
  getById: (id) => apiClient.get(`/movies/${id}`),
  create: (data) => apiClient.post('/movies', data),
  update: (id, data) => apiClient.put(`/movies/${id}`, data),
  delete: (id) => apiClient.delete(`/movies/${id}`),
  getRecommendations: () => apiClient.get('/movies/recommendations')
};

export const ShowtimeAPI = {
  getAll: (movieId) => apiClient.get(movieId ? `/showtimes?movieId=${movieId}` : '/showtimes'),
  getById: (id) => apiClient.get(`/showtimes/${id}`),
  create: (data) => apiClient.post('/showtimes', data),
  update: (id, data) => apiClient.put(`/showtimes/${id}`, data),
  delete: (id) => apiClient.delete(`/showtimes/${id}`),
  getSeats: (showtimeId) => apiClient.get(`/showtimes/${showtimeId}/seats`)
};

export const BookingAPI = {
  reserve: (payload) => apiClient.post('/reservations', payload),
  getMyBookings: () => apiClient.get('/reservations/my-bookings'),
  getAllBookings: () => apiClient.get('/reservations/all'),
  cancel: (id) => apiClient.post(`/reservations/${id}/cancel`)
};

export const PaymentAPI = {
  confirm: (payload) => apiClient.post('/payments/confirm', payload)
};

export const AuthAPI = {
  login: (credentials) => apiClient.post('/auth/login', credentials),
  register: (data) => apiClient.post('/auth/register', data)
};
