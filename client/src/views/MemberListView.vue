<template>
    <div>
        <button @click="router.push( `/projects/${ projectId }` )" class="btn btn-soft">
            Quay lại dự án
        </button>
        <h2 class="text-2xl font-semibold my-6">Thành viên dự án</h2>
        <div class="mb-4 flex gap-4">
            <input v-model=" newMemberUsername " type="text" placeholder="Tên đăng nhập"
                class="input border rounded-l px-3 py-2 flex-1" />
            <button @click=" addMember " class="btn btn-primary">Thêm thành viên</button>
        </div>
        <div v-if=" loading ">Đang tải...</div>
        <div v-else class="bg-white rounded shadow">
            <div v-for=" member in members " :key=" member.userId "
                class="flex justify-between items-center p-4 border-b last:border-b-0">
                <div>
                    <span class="font-medium">{{ member.username }}</span>
                    <span class="ml-2 text-sm text-gray-600">({{ statusLabels[ member.role ] }})</span>
                </div>
                <button v-if=" currentUserRole === 'MANAGER' && member.userId !== currentUserId "
                    @click="removeMember( member.userId )" class="text-red-500 hover:underline cursor-pointer">
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
import { push } from "notivue";

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

const statuses = ['MANAGER', 'MEMBER'];
const statusLabels = {
    MANAGER: 'Quản lý',
    MEMBER: 'Thành viên',
};

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
        push.success('Thêm thành viên thành công');
        await loadMembers();
    } catch (error) {
        push.error({
            title: 'Không thể thêm thành viên',
            message: 'Vui lòng kiểm tra lại tên đăng nhập'
        });
    }
};

const removeMember = async (userId) => {
    try {
        if (confirm('Xác nhận xóa thành viên này?')) {
            push.success('Xóa thành viên thành công')
            await apiRemoveMember(projectId, userId);
            await loadMembers();
        }
    } catch (error) {
        push.error('Không thể xóa thành viên');
    }
};
</script>