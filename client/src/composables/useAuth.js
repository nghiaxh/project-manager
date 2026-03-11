import { ref, computed } from 'vue';
import { login as apiLogin, register as apiRegister } from '../services/authService';
import { getCurrentUser } from '../services/userService';

const user = ref(JSON.parse(localStorage.getItem('user')) || null);
const token = ref(localStorage.getItem('token') || null);

export const authState = {
    user,
    token,
    isAuthenticated: computed(() => !!token.value),

    async login(username, password) {
        const data = await apiLogin(username, password);
        user.value = { id: data.userId, username: data.username, name: data.name, email: data.email };
        token.value = data.token;
        localStorage.setItem('user', JSON.stringify(user.value));
        localStorage.setItem('token', token.value);
    },

    async register(userData) {
        await apiRegister(userData);
    },

    logout() {
        user.value = null;
        token.value = null;
        localStorage.removeItem('user');
        localStorage.removeItem('token');
    },

    async fetchCurrentUser() {
        const u = await getCurrentUser();
        user.value = u;
        localStorage.setItem('user', JSON.stringify(u));
    }
};