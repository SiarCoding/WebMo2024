// main.js
import { createApp } from 'vue';
import App from './App.vue'; // Hier wird die Hauptkomponente importiert
import router from './router';
import 'bootstrap/dist/css/bootstrap.min.css';

createApp(App)
  .use(router)
  .mount('#app');
