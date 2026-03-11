<template>
    <div class="bg-white p-3 rounded shadow mb-2 cursor-move"
        :class=" { 'border-l-4 border-green-500': task.status === 'DONE' } ">
        <h4 class="font-medium">{{ task.title }}</h4>
        <p class="text-sm text-gray-600 truncate">{{ task.description }}</p>
        <div class="flex justify-between items-center mt-2 text-xs text-gray-500">
            <span>📅 {{ task.deadline }}</span>
            <span v-if=" assignee " class="bg-blue-100 text-blue-800 px-2 py-1 rounded">
                {{ assignee.name }}
            </span>
        </div>
        <div class="mt-2 flex justify-end">
            <router-link :to=" `/tasks/${ task.id }` " class="text-blue-500 hover:underline text-sm">Xem chi
                tiết</router-link>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { getUserById } from '../services/userService';

const props = defineProps({
    task: Object
});

const assignee = ref(null);

onMounted(async () => {
    if (props.task.assigneeId) {
        try {
            assignee.value = await getUserById(props.task.assigneeId);
        } catch (error) {
            console.error('Không thể tải thông tin người được gán');
        }
    }
});

watch(() => props.task.assigneeId, async (newId) => {
    if (newId) {
        assignee.value = await getUserById(newId);
    } else {
        assignee.value = null;
    }
});
</script>