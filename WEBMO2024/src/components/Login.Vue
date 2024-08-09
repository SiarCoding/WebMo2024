<template>
  <div>
    <h2>Login</h2>
    <form @submit.prevent="login">
      <label for="username">Username:</label>
      <input type="text" v-model="username" required />

      <label for="password">Password:</label>
      <input type="password" v-model="password" required />

      <button type="submit">Login</button>
    </form>
    <p v-if="errorMessage" style="color: red;">{{ errorMessage }}</p>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      username: '',
      password: '',
      errorMessage: '',  // Für Fehlermeldungen
    };
  },
  methods: {
    async login() {
      try {
        const response = await axios.post('http://localhost:3000/login', {
          username: this.username,
          password: this.password,
        });

        if (response.data.success) {
          alert('Login erfolgreich!');
          // Weiterleitung oder andere Aktionen
        } else {
          this.errorMessage = 'Ungültige Anmeldedaten';
        }
      } catch (error) {
        this.errorMessage = 'Ein Fehler ist aufgetreten';
        console.error('Login-Fehler:', error);
      }
    },
  },
};
</script>
