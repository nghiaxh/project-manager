<template>
  <Notivue v-slot=" item ">
    <NotivueSwipe :item=" item ">
      <Notification :item=" item ">
        <NotificationProgress :item=" item " />
      </Notification>
    </NotivueSwipe>
  </Notivue>
  <div v-if=" authState.isAuthenticated.value " class="flex h-screen bg-gray-100">
    <Sidebar />
    <div class="flex-1 flex flex-col overflow-hidden">
      <Navbar />
      <main class="flex-1 overflow-x-hidden overflow-y-auto bg-gray-100 p-4">
        <router-view />
      </main>
    </div>
  </div>
  <router-view v-else />
</template>

<script setup>
import {
  Notivue,
  NotivueSwipe,
  Notification,
  NotificationProgress,
} from 'notivue';
import { authState } from './composables/useAuth';
import Navbar from './components/Navbar.vue';
import Sidebar from './components/Sidebar.vue';
</script>