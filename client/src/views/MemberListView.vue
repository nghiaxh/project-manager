<template>
    <div>
        <button @click="router.push( `/projects/${ projectId }` )" class="text-lg text-blue-600 hover:underline">
            Quay lại dự án
        </button>
        <div class="flex justify-center">
            <h2 class="text-2xl font-semibold mb-6">Thành viên dự án</h2>
        </div>
        <div class="mb-4 flex">
            <input v-model=" newMemberUsername " type="text" placeholder="Tên đăng nhập"
                class="border rounded-l px-3 py-2 flex-1" />
            <button @click=" addMember "
                class="bg-blue-500 text-white px-4 py-2 rounded-r hover:bg-blue-600">Thêm</button>
        </div>
        <div v-if=" loading ">Đang tải...</div>
        <div v-else class="bg-white rounded shadow">
            <div v-for=" member in members " :key=" member.userId "
                class="flex justify-between items-center p-4 border-b last:border-b-0">
                <div>
                    <span class="font-medium">{{ member.username }}</span>
                    <span class="ml-2 text-sm text-gray-600">({{ member.role }})</span>
                </div>
                <button v-if=" currentUserRole === 'MANAGER' && member.userId !== currentUserId "
                    @click="removeMember( member.userId )" class="text-red-500 hover:underline">
                    Xóa
                </button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { authState } from '../composables/useAuth';
import { getMembers, addMember as apiAddMember, removeMember as apiRemoveMember } from '../services/projectService';

const route = useRoute();
const router = useRouter();
const projectId = Number(route.params.id);

const currentUserId = computed(() => authState.user.value?.id);
const currentUserRole = computed(() => {
    const currentMember = members.value.find(m => m.userId === currentUserId.value);
    return currentMember ? currentMember.role : null;
});

const loading = ref(true);
const members = ref([]);
const newMemberUsername = ref('');

onMounted(async () => {
    await loadMembers();
    loading.value = false;
});

const loadMembers = async () => {
    if (!authState.isAuthenticated.value) {
        return;
    }
    members.value = await getMembers(projectId);
};

const addMember = async () => {
    if (!newMemberUsername.value) return;
    try {
        await apiAddMember(projectId, newMemberUsername.value);
        newMemberUsername.value = '';
        await loadMembers();
    } catch (error) {
        alert('Không thể thêm thành viên');
    }
};

const removeMember = async (userId) => {
    try {
        await apiRemoveMember(projectId, userId);
        await loadMembers();
    } catch (error) {
        alert('Không thể xóa thành viên');
    }
};
</script>