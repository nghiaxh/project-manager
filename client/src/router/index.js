import { createRouter, createWebHistory } from 'vue-router';
import { authState } from '../composables/useAuth';
import LoginView from '../views/LoginView.vue';
import RegisterView from '../views/RegisterView.vue';
import ProjectListView from '../views/ProjectListView.vue';
import ProjectView from '../views/ProjectView.vue';
import TaskDetailView from '../views/TaskDetailView.vue';
import StatisticsView from '../views/StatisticsView.vue';
import NotificationsView from '../views/NotificationsView.vue';
import MemberListView from '../views/MemberListView.vue';
import UserProfileView from '../views/UserProfileView.vue';
import AdminView from '../views/AdminView.vue';

const routes = [
    { path: '/', name: 'login', component: LoginView },
    { path: '/register', name: 'register', component: RegisterView },
    { path: '/projects', name: 'projects', component: ProjectListView, meta: { requiresAuth: true } },
    { path: '/projects/:id', name: 'project', component: ProjectView, meta: { requiresAuth: true } },
    { path: '/projects/:id/members', name: 'members', component: MemberListView, meta: { requiresAuth: true } },
    { path: '/projects/:id/statistics', name: 'statistics', component: StatisticsView, meta: { requiresAuth: true } },
    { path: '/tasks/:id', name: 'task', component: TaskDetailView, meta: { requiresAuth: true } },
    { path: '/notifications', name: 'notifications', component: NotificationsView, meta: { requiresAuth: true } },
    { path: "/admin", name: "admin", component: AdminView, meta: { requiresAuth: true } },
    { path: '/profile', name: 'profile', component: UserProfileView, meta: { requiresAuth: true } }
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

router.beforeEach((to, from) => {
    if (to.meta.requiresAuth && !authState.isAuthenticated.value) {
        return '/';
    }
    return true;
});

export default router;