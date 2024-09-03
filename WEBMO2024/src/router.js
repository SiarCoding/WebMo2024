// Hier definieren wir unsere Routen!!

// als erstes importieren wir unsere Komponenten aus dem components-Ordner
import { createRouter, createWebHistory } from 'vue-router';
import Essen from './components/Essen.vue';
import EssenUser from './components/EssenUser.vue';
import Essensplan from './components/Essensplan.vue';
import Login from './components/Login.vue';
import AddEssen from './components/AddEssen.vue';
import EditEssen from './components/EditEssen.vue';
import Plaene from './components/Plaene.vue';
import EditEssensplan from './components/EditEssensplan.vue'; // Korrekt importieren

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: Login },
  { path: '/essen', component: Essen, meta: { requiresAuth: true } },
  { path: '/essen-user', component: EssenUser, meta: { requiresAuth: true } },
  { path: '/essensplan', component: Essensplan, meta: { requiresAuth: true } },
  { path: '/add-essen', component: AddEssen, meta: { requiresAuth: true } },
  { path: '/plaene', component: Plaene, meta: { requiresAuth: true } },
  { path: '/essen/edit/:id', component: EditEssen, props: true, meta: { requiresAuth: true, requiresAdmin: true } },
  { path: '/essensplan/edit/:week', name: 'EditEssensplan', component: EditEssensplan, props: true, meta: { requiresAuth: true, requiresAdmin: true } }, // Korrekte Definition der Route
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token');
  const role = localStorage.getItem('role');

  if (to.matched.some(record => record.meta.requiresAuth) && !token) {
    next('/login');
  } else if (to.matched.some(record => record.meta.requiresAdmin) && role !== 'admin') {
    next('/login');
  } else {
    next();
  }
});

export default router;


