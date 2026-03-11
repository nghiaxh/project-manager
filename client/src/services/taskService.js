import api from './api';

export const getTasksByProject = async (projectId) => {
    const response = await api.get(`/projects/${projectId}/tasks`);
    return response.data;
};

export const createTask = async (projectId, task) => {
    const response = await api.post(`/projects/${projectId}/tasks`, task);
    return response.data;
};

export const getTask = async (id) => {
    const response = await api.get(`/tasks/${id}`);
    return response.data;
};

export const updateTask = async (id, task) => {
    const response = await api.put(`/tasks/${id}`, task);
    return response.data;
};

export const deleteTask = async (id) => {
    await api.delete(`/tasks/${id}`);
};

export const addComment = async (taskId, content) => {
    const response = await api.post(`/tasks/${taskId}/comments`, content, {
        headers: { 'Content-Type': 'text/plain' }
    });
    return response.data;
};

export const getComments = async (taskId) => {
    const response = await api.get(`/tasks/${taskId}/comments`);
    return response.data;
};