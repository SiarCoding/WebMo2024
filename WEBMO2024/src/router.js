// Hier definieren wir unsere Routen!!

// als erstes importieren wir unsere Komponenten aus dem components-Ordner
import { createRouter, createWebHistory } from 'vue-router';
import Essen from './components/Essen.vue';
import EssenUser from './components/EssenUser.vue'; // Neue User-Komponente
import Essensplan from './components/Essensplan.vue';
import Login from './components/Login.vue';
import AddEssen from './components/AddEssen.vue';
import EditEssen from './components/EditEssen.vue';

// Hier kommt die Definition der Pfade mit den jeweiligen Routen
const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: Login }, // login
  { path: '/essen', component: Essen }, // essensliste für Admin
  { path: '/essen-user', component: EssenUser }, // essensliste für User
  { path: '/essensplan', component: Essensplan }, // essensplan
  { path: '/add-essen', component: AddEssen }, // essen hinzufügen
  { path: '/essen/edit/:id', component: EditEssen, props: true }, // essen bearbeiten
];

// Router wird hergestellt: Der Router ermöglicht es, verschiedene Routen (URLs) zu definieren, die zu unterschiedlichen Komponenten in der Anwendung führen
const router = createRouter({
  history: createWebHistory(), // createWebHistory(): Dies ist eine Methode, die den "HTML5 History Mode" für die Navigation verwendet.

  routes,
});

export default router;
