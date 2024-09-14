// src/main.js
import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap-icons/font/bootstrap-icons.css';
import i18n from './language'; // Language-Switcher

createApp(App)
  .use(router)
  .use(i18n)
  .mount('#app');
