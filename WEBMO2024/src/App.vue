<template>
  <div id="app">
    <!-- Sprachumschaltung -->
    <LanguageSwitcher />
    <router-view></router-view>
  </div>
</template>

<script>
// Importieren Sie die Sprachumschaltungskomponente
import LanguageSwitcher from './components/LanguageSwitcher.vue';

export default {
  name: 'App',
  components: {
    LanguageSwitcher // Registrieren Sie die Sprachumschaltungskomponente
  }
};
</script>
