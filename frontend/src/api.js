import axios from 'axios';

let baseURL = import.meta.env.VITE_API_BASE_URL || import.meta.env.VITE_API_URL || 'https://chowtime-upbeat-mandolin.ngrok-free.dev/api';

// Dynamically append /api if the environment variable doesn't include it
if (baseURL && !baseURL.endsWith('/api') && !baseURL.endsWith('/api/')) {
  baseURL = baseURL.endsWith('/') ? `${baseURL}api` : `${baseURL}/api`;
}

const api = axios.create({ baseURL });

api.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

export default api;