import api from './api';

export const getUserNotifications = async () => {
    const response = await api.get('/notifications');
    return response.data;
};

export const markAsRead = async (id) => {
    await api.put(`/notifications/${id}/read`);
};

export const markAllAsRead = async () => {
    await api.put("/notifications/read-all");
};

export const deleteNotification = async (id) => {
    await api.delete(`/notifications/${id}`);
};

export const deleteReadNotifications = async () => {
    await api.delete("/notifications/read");
};