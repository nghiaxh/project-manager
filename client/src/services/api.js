import axios from 'axios';
import { authState } from '../composables/useAuth';

const api = axios.create({
    baseURL: 'http://localhost:8080/api',
    headers: { 'Content-Type': 'application/json' }
});

api.interceptors.request.use(
    config => {
        const token = sessionStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    error => Promise.reject(error)
);

api.interceptors.response.use(
    response => response,
    async error => {
        if (error.response && error.response.status === 401) {
            authState.logout();
            try {
                const routerModule = await import('../router');
                const router = routerModule.default;
                router.push('/');
            } catch (importError) {
                window.location.href = '/';
            }
        }
        return Promise.reject(error);
    }
);

export default api;