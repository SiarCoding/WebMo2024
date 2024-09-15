// vue router wird erstellt
import { createRouter, createWebHistory } from 'vue-router';

// importierung der komponenten
import Essen from './components/essen/Essen.vue';
import Essensplan from './components/essensplan/Essensplan.vue';
import Login from './components/Login.vue';
import AddEssen from './components/essen/AddEssen.vue';
import EditEssen from './components/essen/EditEssen.vue';
import Plaene from './components/essensplan/Plaene.vue';
import EditEssensplan from './components/essensplan/EditEssensplan.vue';

// routen definieren für verschiedene seiten
const routes = [
  { path: '/', redirect: '/login' }, // Wenn der Nutzer auf "/" geht, leite ihn zum Login um
  { path: '/login', component: Login }, // Login-Seite
  { path: '/essen', component: Essen, meta: { requiresAuth: true } }, // Essen-Seite für admin und user erreichbar)
  { 
    path: '/essensplan', 
    component: Essensplan, 
    meta: { requiresAuth: true, requiresAdmin: true } // Essensplan nur für Admins und mit Authentifizierung erreichbar
  },
  { 
    path: '/add-essen', 
    component: AddEssen, 
    meta: { requiresAuth: true, requiresAdmin: true } // Hinzufügen von Essen, nur für Admins
  },
  { path: '/plaene', component: Plaene, meta: { requiresAuth: true } }, // Plaene-Seite (für admin und user erreichbar)
  { 
    path: '/essen/edit/:id', 
    component: EditEssen, 
    props: true, 
    meta: { requiresAuth: true, requiresAdmin: true } // Essen bearbeiten -> nur für Admins
  },
  { 
    path: '/essensplan/edit/:week', 
    name: 'EditEssensplan', 
    component: EditEssensplan, 
    props: true, 
    meta: { requiresAuth: true, requiresAdmin: true } // Essensplan für eine bestimmte Woche bearbeiten -> nur für Admins
  },
];

// Erstelle den Router mit dem Web-History-Modus
const router = createRouter({
  history: createWebHistory(),
  routes, 
});

// identifizierung der role und token
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token'); // Token aus dem Local Storage
  const role = localStorage.getItem('role'); // Rolle des Benutzers aus dem Local Storage

  // Login weiterleitung nach fehlender token
  if (to.matched.some(record => record.meta.requiresAuth) && !token) {
    next('/login');
  } 
  // Login weiterleitung nach fehlender token
  else if (to.matched.some(record => record.meta.requiresAdmin) && role !== 'admin') {
    next('/login');
  } 
  else {
    next();
  }
});

export default router;
