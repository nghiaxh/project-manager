import { createApp } from "vue";
import { createNotivue } from "notivue";
import App from "./App.vue";
import router from "./router";

import "./index.css";
import "notivue/notification.css";
import "notivue/animations.css";
import "notivue/notification-progress.css";

const app = createApp(App);
const notivue = createNotivue({
  position: "top-center",
  limit: 2,
  enqueue: true,
});
app.use(notivue);
app.use(router);

app.mount("#app");
