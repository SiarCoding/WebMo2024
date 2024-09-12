// src/main.js
import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import 'bootstrap/dist/css/bootstrap.min.css';
import i18n from './language'; // Importiere die I18n-Instanz

createApp(App)
  .use(router)
  .use(i18n) // Verwende die I18n-Instanz
  .mount('#app');
