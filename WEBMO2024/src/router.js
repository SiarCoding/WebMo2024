import { createRouter, createWebHistory } from 'vue-router';
import Essen from './components/Essen.vue';
import Essensplan from './components/Essensplan.vue';
import Login from './components/Login.vue';
import AddEssen from './components/AddEssen.vue';  // Import der neuen Komponente

const routes = [
  { path: '/', component: Login },
  { path: '/essen', component: Essen },
  { path: '/essensplan', component: Essensplan },
  { path: '/add-essen', component: AddEssen }  // Route für Essen hinzufügen
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
