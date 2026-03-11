<template>
    <div class="mt-4">
        <h3 class="font-semibold mb-2">Bình luận</h3>
        <div class="space-y-2 mb-4">
            <div v-for=" comment in comments " :key=" comment.id " class="bg-gray-50 p-2 rounded">
                <div class="flex justify-between text-sm text-gray-600">
                    <span class="font-medium">{{ comment.username }}</span>
                    <span>{{ new Date( comment.createdAt ).toLocaleString() }}</span>
                </div>
                <p class="mt-1">{{ comment.content }}</p>
            </div>
        </div>
        <div class="flex items-center">
            <input v-model=" newComment " type="text" placeholder="Thêm bình luận..."
                class="flex-1 border rounded-l px-3 py-2 focus:outline-none">
            <button @click=" addComment "
                class="bg-blue-500 text-white px-4 py-2 rounded-r hover:bg-blue-600">Gửi</button>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue';
import { addComment as apiAddComment } from '../services/taskService';

const props = defineProps({
    taskId: Number,
    comments: Array
});

const emit = defineEmits(['comment-added']);
const newComment = ref('');

const addComment = async () => {
    if (!newComment.value.trim()) return;
    try {
        const comment = await apiAddComment(props.taskId, newComment.value);
        emit('comment-added', comment);
        newComment.value = '';
    } catch (error) {
        alert('Không thể thêm bình luận');
    }
};
</script>