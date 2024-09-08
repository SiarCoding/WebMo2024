<template>
  <div id="app">
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
      <div class="container">
        <a class="navbar-brand" href="#">Essensverwaltung</a>
        <div class="collapse navbar-collapse">
          <ul class="navbar-nav me-auto">
            <li class="nav-item">
              <router-link class="nav-link" to="/essen">Essen Liste</router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/add-essen">Neues Essen hinzufügen</router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/essensplan">Essensplan erstellen</router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/plaene">Essenspläne ansehen</router-link>
            </li>
          </ul>
          <span class="navbar-text">
            Angemeldet als: {{ userRole || 'Gast' }}
          </span>
        </div>
      </div>
    </nav>
    <router-view></router-view>
  </div>
</template>

<script>
export default {
  name: 'App',
  computed: {
    userRole() {
      return localStorage.getItem('role'); // Zeigt die Rolle des aktuellen Benutzers an
    }
  }
};
</script>

<style>
.navbar {
  margin-bottom: 20px;
}
</style>
