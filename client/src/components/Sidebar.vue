<template>
    <aside class="w-64 bg-white shadow-md">
        <div class="p-4">
            <h2 class="text-2xl font-bold text-gray-800">ProjectManager</h2>
        </div>
        <nav class="mt-4">
            <router-link to="/projects" class="block px-4 py-2 text-gray-700 hover:bg-gray-200"
                active-class="bg-gray-200">
                Dự án
            </router-link>
            <router-link to="/notifications" class="block px-4 py-2 text-gray-700 hover:bg-gray-200"
                active-class="bg-gray-200">
                Thông báo
            </router-link>
            <router-link to="/profile" class="block px-4 py-2 text-gray-700 hover:bg-gray-200"
                active-class="bg-gray-200">
                Hồ sơ
            </router-link>
        </nav>
    </aside>
</template>