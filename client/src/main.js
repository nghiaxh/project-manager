import { createApp } from 'vue';
import { createNotivue } from "notivue";
import App from './App.vue';
import router from './router';

import './index.css';
import 'notivue/notification.css';
import 'notivue/animations.css';

const app = createApp(App);
const notivue = createNotivue({
    position: 'top-right',
    limit: 5,
    enqueue: true,

});
app.use(router);
app.use(notivue);

app.mount('#app');