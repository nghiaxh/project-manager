<template>
    <aside class="flex flex-col w-80 min-h-screen bg-base-100 shadow-sm border-r border-base-200">
        <div class="px-5 py-5 border-b border-base-200">
            <div class="flex items-center gap-2">
            </div>
        </div>

        <nav class="flex flex-col flex-1 px-3 py-4 gap-1">
            <router-link to="/projects"
                class="flex items-center gap-3 px-3 py-2 rounded-lg text-sm text-base-content/70 hover:bg-base-200 hover:text-base-content transition-colors"
                active-class="bg-primary/10 text-primary font-medium hover:bg-primary/10 hover:text-primary">
                <PanelsTopLeft />
                Dự án
            </router-link>

            <router-link to="/notifications"
                class="flex items-center gap-3 px-3 py-2 rounded-lg text-sm text-base-content/70 hover:bg-base-200 hover:text-base-content transition-colors"
                active-class="bg-primary/10 text-primary font-medium hover:bg-primary/10 hover:text-primary">
                <Bell />
                Thông báo
            </router-link>

            <router-link to="/profile"
                class="flex items-center gap-3 px-3 py-2 rounded-lg text-sm text-base-content/70 hover:bg-base-200 hover:text-base-content transition-colors"
                active-class="bg-primary/10 text-primary font-medium hover:bg-primary/10 hover:text-primary">
                <CircleUserRound />
                Hồ sơ
            </router-link>

            <div class="divider my-1"></div>

            <button @click=" logout "
                class="flex items-center gap-3 px-3 py-2 rounded-lg text-sm text-error/80 hover:bg-error/10 hover:text-error transition-colors w-full text-left cursor-pointer">
                <LogOut />
                Đăng xuất
            </button>
        </nav>
    </aside>
</template>

<script setup>
import { useRouter } from 'vue-router';
import { authState } from '../composables/useAuth';
import { PanelsTopLeft, Bell, CircleUserRound, LogOut } from "lucide-vue-next";

const router = useRouter();

const logout = () => {
    authState.logout();
    router.push('/');
};
</script>