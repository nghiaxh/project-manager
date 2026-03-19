<template>
    <div v-if=" loading ">Đang tải...</div>
    <div v-else>
        <div class="bg-white p-6 rounded shadow">
            <div class="flex justify-between items-start">
                <button @click="router.push( `/projects/${ task.projectId }` )" class="btn btn-soft">Quay lại</button>
                <div class="space-x-4">
                    <button @click="handleDeleteTask(taskId)" class="btn btn-error text-white">Xóa công việc</button>
                    <button @click="showEditModal = true" class="btn btn-primary">Chỉnh sửa thông
                        tin</button>
                </div>
            </div>
            <h2 class="text-2xl font-semibold mt-8">{{ task.title }}</h2>
            <p class="text-gray-700 mt-4">{{ task.description }}</p>
            <div class="mt-8 grid gap-4 text-sm">
                <div><span class="font-medium">Trạng thái:</span> {{ statusLabel }}</div>
                <div><span class="font-medium">Hạn chót:</span> {{ new Date( task.deadline ).toLocaleDateString( "vi-VN"
                )
                    }}</div>
                <div><span class="font-medium">Người được giao việc:</span> {{ assignee?.name || 'Chưa gán' }}</div>
                <div><span class="font-medium">Người tạo:</span> {{ creator?.name }}</div>
            </div>
            <CommentSection :taskId=" task.id " :comments=" comments " :members=" members "
                @comment-added=" handleCommentAdded " />
        </div>
        <!-- Modal sửa task -->
        <div v-if=" showEditModal "
            class="fixed inset-0 backdrop-blur-xs bg-opacity-50 flex items-center justify-center">
            <div class="bg-white p-6 rounded w-96">
                <h3 class="text-xl font-semibold mb-4 cursor-pointer">Sửa task</h3>
                <TaskForm :initialData=" task " :members=" members " @submit=" updateTask "
                    @cancel="showEditModal = false" />
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getTask, updateTask as updateTaskApi, deleteTask as deleteTaskApi, getComments } from '../services/taskService';
import { getMembers } from '../services/projectService';
import { getUserById } from '../services/userService';
import CommentSection from '../components/CommentSection.vue';
import TaskForm from '../components/TaskForm.vue';
import { authState } from '../composables/useAuth';
import { push } from "notivue";

const route = useRoute();
const router = useRouter();
const taskId = Number(route.params.id);

const loading = ref(true);
const task = ref({});
const assignee = ref(null);
const creator = ref(null);
const comments = ref([]);
const members = ref([]);
const showEditModal = ref(false);

const statusLabels = {
    TODO: 'Cần làm',
    IN_PROGRESS: 'Đang làm',
    IN_REVIEW: 'Chờ duyệt',
    DONE: 'Hoàn thành',
};

const statusLabel = computed(() => statusLabels[task.value.status] || task.value.status);

onMounted(async () => {
    await loadTask();
    await loadComments();
    await loadMembers();

    loading.value = false;
});

const loadTask = async () => {
    if (!authState.isAuthenticated.value) {
        return;
    }
    task.value = await getTask(taskId);
    if (task.value.assigneeId) {
        assignee.value = await getUserById(task.value.assigneeId);
    }
    if (task.value.createdBy) {
        creator.value = await getUserById(task.value.createdBy);
    }
};

const loadComments = async () => {
    if (!authState.isAuthenticated.value) {
        return;
    }
    comments.value = await getComments(taskId);
};

const loadMembers = async () => {
    if (!authState.isAuthenticated.value) {
        return;
    }
    if (task.value.projectId) {
        members.value = await getMembers(task.value.projectId);
    }
};

const handleCommentAdded = (comment) => {
    comments.value.push(comment);
};

const updateTask = async (data) => {
    try {
        await updateTaskApi(taskId, data);
        showEditModal.value = false;
        push.success('Cập nhật task thành công');
        await loadTask();
    } catch (error) {
        push.error('Không thể cập nhật task');
    }
};

const handleDeleteTask = async (taskId) => {
    try {
        if (confirm("Xác nhận xóa task?")) {
            await deleteTaskApi(taskId);
            push.success("Xóa công việc thành công")
            router.push(`/projects/${task.value.projectId}`);
        }
    } catch (error) {
        alert('Không thể xóa task');
    }
};
</script>