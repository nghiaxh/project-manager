import api from './api';

export const getUserProjects = async () => {
    const response = await api.get('/projects');
    return response.data;
};

export const createProject = async (project) => {
    const response = await api.post('/projects', project);
    return response.data;
};

export const getProject = async (id) => {
    const response = await api.get(`/projects/${id}`);
    return response.data;
};

export const updateProject = async (id, project) => {
    const response = await api.put(`/projects/${id}`, project);
    return response.data;
};

export const deleteProject = async (id) => {
    await api.delete(`/projects/${id}`);
};

export const addMember = async (projectId, username) => {
    await api.post(`/projects/${projectId}/members?username=${username}`);
};

export const removeMember = async (projectId, userId) => {
    await api.delete(`/projects/${projectId}/members/${userId}`);
};

export const getMembers = async (projectId) => {
    const response = await api.get(`/projects/${projectId}/members`);
    return response.data;
};