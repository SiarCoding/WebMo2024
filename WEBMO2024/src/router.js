import { createRouter, createWebHistory } from 'vue-router';
import Essen from './components/Essen.vue';
import Essensplan from './components/Essensplan.vue';
import Login from './components/Login.vue';
import AddEssen from './components/AddEssen.vue';

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: Login },
  { path: '/essen', component: Essen },
  { path: '/essensplan', component: Essensplan },
  { path: '/add-essen', component: AddEssen },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
