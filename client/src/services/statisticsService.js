import api from './api';

export const getProjectStatistics = async (projectId) => {
    const response = await api.get(`/statistics/projects/${projectId}`);
    return response.data;
};