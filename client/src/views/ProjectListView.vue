<template>
    <div>
        <div class="flex justify-start items-center mb-6">
            <button @click="showCreateModal = true" class="btn btn-primary">Tạo dự án mới</button>
        </div>
        <div v-if=" loading " class="text-center py-10">Đang tải...</div>
        <div v-else-if=" projects.length === 0 " class="text-center py-10 text-gray-500">
            Bạn chưa tham gia dự án nào.
        </div>
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            <div v-for=" project in projects " :key=" project.id "
                class="bg-white p-4 transition rounded shadow hover:shadow-lg">
                <router-link :to=" `/projects/${ project.id }` ">
                    <h3 class="font-semibold text-lg">{{ project.name }}</h3>
                    <p class="text-gray-600 text-sm mt-1">{{ project.description || 'Không có mô tả' }}</p>
                    <div class="mt-4 flex items-center">
                        <span class="text-xs text-gray-500">Ngày tạo: {{ new Date( project.createdAt
                        ).toLocaleDateString( "vi-VN" )
                            }}</span>
                    </div>
                </router-link>
            </div>
        </div>
        <!-- Modal tạo dự án -->
        <div v-if=" showCreateModal "
            class="fixed inset-0 backdrop-blur-xs bg-opacity-50 flex items-center justify-center">
            <div class="bg-white p-6 rounded w-96">
                <h3 class="text-xl font-semibold mb-4">Tạo dự án mới</h3>
                <form @submit.prevent=" onSubmit ">
                    <div class="mb-4">
                        <label class="label block text-sm font-medium mb-1">Tên dự án</label>
                        <input v-model=" name " type="text" required class="input w-full border rounded px-3 py-2"
                            placeholder="Tên dự án" />
                        <span class="text-red-500 text-sm">{{ errors.name }}</span>
                    </div>
                    <div class="mb-6">
                        <label class="label block text-sm font-medium mb-1">Mô tả</label>
                        <input v-model=" description " rows="3" class="input w-full border rounded px-3 py-2"
                            placeholder="Mô tả dự án" />
                        <span class="text-red-500 text-sm">{{ errors.description }}</span>
                    </div>
                    <div class="flex justify-end space-x-2">
                        <button type="button" @click="showCreateModal = false" class="btn btn-soft">Hủy</button>
                        <button type="submit" class="btn btn-primary">Tạo</button>
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
import { push } from "notivue";
import { projectSchema } from '../validation/projectValidation';
import { useForm, useField } from "vee-validate";

const projects = ref([]);
const loading = ref(true);
const showCreateModal = ref(false);

const { handleSubmit, errors } = useForm({
    validationSchema: projectSchema,
});

const { value: name } = useField('name');
const { value: description } = useField('description');

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

const onSubmit = handleSubmit(async (values) => {
    try {
        await apiCreateProject(values);
        showCreateModal.value = false;
        push.success("Tạo dự án mới thành công");
        await loadProjects();
    } catch (error) {
        push.error('Không thể tạo dự án');
        console.log(error);
    }
});
</script>