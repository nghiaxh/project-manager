import { ref, computed } from "vue";
import {
  login as apiLogin,
  register as apiRegister,
} from "../services/authService";
import { getCurrentUser } from "../services/userService";

const user = ref(JSON.parse(sessionStorage.getItem("user")) || null);
const token = ref(sessionStorage.getItem("token") || null);

export const authState = {
  user,
  token,
  isAuthenticated: computed(() => !!token.value),

  async login(username, password) {
    const data = await apiLogin(username, password);
    user.value = {
      id: data.userId,
      username: data.username,
      name: data.name,
      email: data.email,
    };
    token.value = data.token;
    sessionStorage.setItem("user", JSON.stringify(user.value));
    sessionStorage.setItem("token", token.value);
  },

  async register(userData) {
    await apiRegister(userData);
  },

  logout() {
    user.value = null;
    token.value = null;
    sessionStorage.removeItem("user");
    sessionStorage.removeItem("token");
  },

  async fetchCurrentUser() {
    const u = await getCurrentUser();
    user.value = u;
    sessionStorage.setItem("user", JSON.stringify(u));
  },
};
