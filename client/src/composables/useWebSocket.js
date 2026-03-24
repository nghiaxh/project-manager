import { ref, onUnmounted } from 'vue';
import { Client } from '@stomp/stompjs';

export function useWebSocket(taskId, onMessageCallback) {
    const stompClient = ref(null);
    const isConnected = ref(false);

    const connect = () => {
        const token = sessionStorage.getItem('token');
        if (!token) return;

        const client = new Client({
            webSocketFactory: () => new WebSocket('ws://localhost:8080/ws'),
            connectHeaders: {
                Authorization: `Bearer ${token}`,
            },
            // debug: (str) => console.log(str),
            reconnectDelay: 5000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
        });

        client.onConnect = () => {
            isConnected.value = true;
            stompClient.value = client;
            client.subscribe(`/topic/tasks/${taskId}/comments`, (message) => {
                if (onMessageCallback) {
                    const comment = JSON.parse(message.body);
                    onMessageCallback(comment);
                }
            });
        };

        client.activate();
        stompClient.value = client;
    };

    const disconnect = () => {
        if (stompClient.value && isConnected.value) {
            stompClient.value.deactivate();
            isConnected.value = false;
        }
    };

    onUnmounted(() => {
        disconnect();
    });

    return {
        connect,
        disconnect,
        isConnected,
    };
}