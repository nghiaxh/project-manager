<template>
    <div class="mt-8">
        <h3 class="font-semibold mb-2">Bình luận</h3>
        <div class="space-y-2 mb-4 max-h-96 overflow-y-auto">
            <div v-for=" comment in comments " :key=" comment.id " class="bg-base-200 p-2 rounded-xl">
                <div class="flex justify-between text-sm text-gray-600">
                    <div class="flex items-center gap-2">
                        <span class="font-bold">{{ comment.username }}</span>
                        <span class="text-xs">{{ new Date( comment.createdAt ).toLocaleString() }}</span>
                    </div>
                    <button v-if=" comment.userId === currentUserId " @click="handleDeleteComment( comment.id )"
                        class="text-red-500 hover:text-red-700 text-xs cursor-pointer" title="Xóa bình luận">
                        <Trash2 size="20" />
                    </button>
                </div>
                <p class="mt-1">{{ comment.content }}</p>
            </div>
        </div>
        <div class="flex items-center space-x-2">
            <input v-model=" newComment " type="text" placeholder="Thêm bình luận..." class="input w-full">
            <button @click=" addComment " class="btn btn-primary w-16">Gửi</button>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue';
import { Trash2 } from 'lucide-vue-next';
import { addComment as apiAddComment, deleteComment as apiDeleteComment } from '../services/taskService';
import { authState } from '../composables/useAuth';
import { push } from 'notivue';

const props = defineProps({
    taskId: Number,
    comments: Array
});

const emit = defineEmits(['comment-added', 'comment-deleted']);

const currentUserId = authState.user.value?.id;
const newComment = ref('');

const addComment = async () => {
    if (!newComment.value.trim()) return;
    try {
        const comment = await apiAddComment(props.taskId, newComment.value);
        newComment.value = '';
    } catch (error) {
        push.error('Không thể thêm bình luận');
    }
};

const handleDeleteComment = async (commentId) => {
    try {
        await apiDeleteComment(commentId);
        push.success('Đã xóa bình luận');
    } catch (error) {
        push.error('Không thể xóa bình luận');
    }
};
</script>