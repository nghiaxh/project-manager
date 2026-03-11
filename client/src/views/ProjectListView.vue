<template>
    <div>
        <div class="flex justify-between items-center mb-6">
            <h2 class="text-2xl font-semibold">Dự án của tôi</h2>
            <button @click="showCreateModal = true" class="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600">
                + Dự án mới
            </button>
        </div>
        <div v-if=" loading " class="text-center py-10">Đang tải...</div>
        <div v-else-if=" projects.length === 0 " class="text-center py-10 text-gray-500">
            Bạn chưa tham gia dự án nào.
        </div>
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            <div v-for=" project in projects " :key=" project.id " class="bg-white p-4 rounded shadow hover:shadow-md">
                <router-link :to=" `/projects/${ project.id }` ">
                    <h3 class="font-semibold text-lg">{{ project.name }}</h3>
                    <p class="text-gray-600 text-sm mt-1">{{ project.description || 'Không có mô tả' }}</p>
                    <div class="mt-4 flex justify-between items-center">
                        <span class="text-xs text-gray-500">Tạo bởi: {{ project.createdBy }}</span>
                    </div>
                </router-link>
            </div>
        </div>
        <!-- Modal tạo dự án -->
        <div v-if=" showCreateModal "
            class="fixed inset-0 backdrop-blur-xs bg-opacity-50 flex items-center justify-center">
            <div class="bg-white p-6 rounded w-96">
                <h3 class="text-xl font-semibold mb-4">Tạo dự án mới</h3>
                <form @submit.prevent=" createProject ">
                    <div class="mb-4">
                        <label class="block text-sm font-medium mb-1">Tên dự án</label>
                        <input v-model=" newProject.name " type="text" required
                            class="w-full border rounded px-3 py-2" />
                    </div>
                    <div class="mb-6">
                        <label class="block text-sm font-medium mb-1">Mô tả</label>
                        <textarea v-model=" newProject.description " rows="3"
                            class="w-full border rounded px-3 py-2"></textarea>
                    </div>
                    <div class="flex justify-end space-x-2">
                        <button type="button" @click="showCreateModal = false"
                            class="px-4 py-2 border rounded">Hủy</button>
                        <button type="submit" class="px-4 py-2 bg-blue-500 text-white rounded">Tạo</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { getUserProjects, createProject as apiCreateProject } from '../services/projectService';
import { authState } from '../composables/useAuth';

const projects = ref([]);
const loading = ref(true);
const showCreateModal = ref(false);
const newProject = ref({ name: '', description: '' });

onMounted(async () => {
    await loadProjects();
});

const loadProjects = async () => {
    loading.value = true;
    if (!authState.isAuthenticated.value) {
        return;
    }
    try {
        projects.value = await getUserProjects();
    } finally {
        loading.value = false;
    }
};

const createProject = async () => {
    try {
        await apiCreateProject(newProject.value);
        showCreateModal.value = false;
        newProject.value = { name: '', description: '' };
        await loadProjects();
    } catch (error) {
        alert('Không thể tạo dự án');
    }
};
</script>