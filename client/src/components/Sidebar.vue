<template>
    <aside class="flex flex-col min-h-screen bg-base-100 shadow-sm border-r border-base-200 transition-all duration-300"
        :class="isCollapsed ? 'w-20' : 'w-80'">
        <!-- Header với nút toggle -->
        <div class="flex items-center justify-between px-5 py-5 border-b border-base-200">
            <div v-if="!isCollapsed" class="flex items-center gap-2">
            </div>
            <button @click="toggleSidebar" class="p-1 rounded-lg hover:bg-base-200 transition-colors cursor-pointer"
                :class="{ 'mx-auto': isCollapsed }">
                <Menu v-if="isCollapsed" size="20" />
                <ChevronLeft v-else size="20" />
            </button>
        </div>

        <nav class="flex flex-col flex-1 px-3 py-4 gap-1">
            <router-link v-if="authState.user.value?.role !== 'ADMIN'" to="/projects"
                class="flex items-center gap-3 px-3 py-2 rounded-lg text-sm text-base-content/70 hover:bg-base-200 hover:text-base-content transition-colors"
                active-class="bg-primary/10 text-primary font-medium hover:bg-primary/10 hover:text-primary">
                <PanelsTopLeft :size="20" />
                <span v-if="!isCollapsed">Dự án</span>
            </router-link>

            <router-link v-if="authState.user.value?.role === 'ADMIN'" to="/admin"
                class="flex items-center gap-3 px-3 py-2 rounded-lg text-sm text-base-content/70 hover:bg-base-200 hover:text-base-content transition-colors"
                active-class="bg-primary/10 text-primary font-medium hover:bg-primary/10 hover:text-primary">
                <Shield :size="20" />
                <span v-if="!isCollapsed">Quản trị</span>
            </router-link>

            <router-link to="/notifications"
                class="flex items-center gap-3 px-3 py-2 rounded-lg text-sm text-base-content/70 hover:bg-base-200 hover:text-base-content transition-colors"
                active-class="bg-primary/10 text-primary font-medium hover:bg-primary/10 hover:text-primary">
                <Bell :size="20" />
                <span v-if="!isCollapsed">Thông báo</span>
            </router-link>

            <router-link to="/profile"
                class="flex items-center gap-3 px-3 py-2 rounded-lg text-sm text-base-content/70 hover:bg-base-200 hover:text-base-content transition-colors"
                active-class="bg-primary/10 text-primary font-medium hover:bg-primary/10 hover:text-primary">
                <CircleUserRound :size="20" />
                <span v-if="!isCollapsed">Hồ sơ</span>
            </router-link>

            <div class="divider my-1"></div>

            <button @click="logout"
                class="flex items-center gap-3 px-3 py-2 rounded-lg text-sm text-error/80 hover:bg-error/10 hover:text-error transition-colors w-full text-left cursor-pointer">
                <LogOut :size="20" />
                <span v-if="!isCollapsed">Đăng xuất</span>
            </button>
        </nav>
    </aside>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { authState } from '../composables/useAuth';
import {
    PanelsTopLeft,
    Bell,
    CircleUserRound,
    LogOut,
    Menu,
    ChevronLeft,
    Shield
} from 'lucide-vue-next';

const router = useRouter();
const isCollapsed = ref(false);

onMounted(() => {
    const saved = localStorage.getItem('sidebar_collapsed');
    if (saved !== null) {
        isCollapsed.value = saved === 'true';
    }
});

const toggleSidebar = () => {
    isCollapsed.value = !isCollapsed.value;
    localStorage.setItem('sidebar_collapsed', isCollapsed.value);
};

const logout = () => {
    authState.logout();
    router.push('/');
};
</script>