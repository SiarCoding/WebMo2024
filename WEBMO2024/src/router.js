//hier definieren wir unsere Routen!!


//als erstes importieren wir unsere Komponenten aus dem components-Ordner
import { createRouter, createWebHistory } from 'vue-router';
import Essen from './components/Essen.vue';
import Essensplan from './components/Essensplan.vue';
import Login from './components/Login.vue';
import AddEssen from './components/AddEssen.vue';
import EditEssen from './components/EditEssen.vue';

//hier kommt die definition der pfad mit dem jeweiligen route
const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: Login }, //login
  { path: '/essen', component: Essen }, // essensliste
  { path: '/essensplan', component: Essensplan }, //essensplan
  { path: '/add-essen', component: AddEssen }, // essen hinzufügen
  { path: '/essen/edit/:id', component: EditEssen, props: true }, // essen bearbeiten 
];

// VORDEFINIERTE ZEILEN VON VUE: Router wird herstellt: Der Router ermöglicht es, verschiedene Routen (URLs) zu definieren, die zu unterschiedlichen Komponenten in der Anwendung führen
const router = createRouter({
  history: createWebHistory(), //createWebHistory(): Dies ist eine Methode, die den "HTML5 History Mode" für die Navigation verwendet.

  routes,
});

export default router;
