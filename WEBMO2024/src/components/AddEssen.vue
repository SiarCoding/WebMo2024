<template>
  <div>
    <h2>Neues Essen hinzufügen</h2>
    <form @submit.prevent="hinzufuegen">
      <label for="name">Name:</label>
      <input type="text" v-model="essen.name" required />

      <label for="beschreibung">Beschreibung:</label>
      <input type="text" v-model="essen.beschreibung" required />

      <label for="preis">Preis:</label>
      <input type="number" v-model="essen.preis" required />

      <button type="submit">Hinzufügen</button>
    </form>
    <p v-if="message" style="color: green;">{{ message }}</p>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      essen: {
        name: '',
        beschreibung: '',
        preis: 0
      },
      message: ''
    };
  },
  methods: {
    async hinzufuegen() {
      console.log('Essen hinzufügen gestartet');
      try {
        const response = await axios.post('http://localhost:3000/api/essen', this.essen);
        console.log('Serverantwort:', response);
        if (response.data.success) {
          this.message = 'Essen erfolgreich hinzugefügt!';
          this.essen = { name: '', beschreibung: '', preis: 0 };  // Formular zurücksetzen
        }
      } catch (error) {
        console.error('Fehler beim Hinzufügen des Essens:', error);
      }
    }
  }
};
</script>
