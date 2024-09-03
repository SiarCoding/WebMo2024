<template>
  <div class="container mt-5">
    <h2 class="mb-4 text-center">🍽️ Neues Essen hinzufügen</h2>
    <form @submit.prevent="hinzufuegen" class="card p-4 shadow-sm" style="background-color: #e3f2fd;">
      <div class="mb-3">
        <label for="name" class="form-label">Name:</label>
        <input type="text" v-model="essen.name" class="form-control" id="name" required />
      </div>

      <div class="mb-3">
        <label for="preis" class="form-label">Preis:</label>
        <div class="input-group">
          <input type="number" step="0.01" v-model="essen.preis" class="form-control" id="preis" required />
          <span class="input-group-text">€</span>
        </div>
      </div>

      <div class="mb-4">
        <label for="art" class="form-label">Art:</label>
        <select v-model="essen.art" class="form-select" id="art" required>
          <option disabled value="">Bitte wählen</option>
          <option value="vegetarisch">Vegetarisch</option>
          <option value="vegan">Vegan</option>
          <option value="mit Fleisch">Mit Fleisch</option>
        </select>
      </div>

      <button type="submit" class="btn btn-primary w-100">Hinzufügen</button>
    </form>
    <p v-if="message" class="mt-3 text-success text-center">{{ message }}</p>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      essen: {
        name: '',
        preis: 0,
        art: ''
      },
      message: ''
    };
  },
  methods: {
    async hinzufuegen() {
      console.log('Essen hinzufügen gestartet');

      if (!this.essen.name || this.essen.preis <= 0 || !this.essen.art) {
        this.message = 'Bitte alle Felder korrekt ausfüllen!';
        return;
      }

      try {
        // Token aus dem Local Storage abrufen
        const token = localStorage.getItem('token');
        if (!token) {
          this.message = 'Kein Token vorhanden';
          return;
        }

        const response = await axios.post(
          'http://localhost:3001/api/essen',
          this.essen,
          {
            headers: {
              Authorization: `Bearer ${token}`, // Token im Header hinzufügen
            },
          }
        );

        console.log('Serverantwort:', response);
        if (response.data.success) {
          this.message = 'Essen erfolgreich hinzugefügt!';
          this.$emit('essenAdded', response.data.essen);
          this.essen = { name: '', preis: 0, art: '' };
        } else {
          this.message = response.data.message; // Server-Fehlermeldung anzeigen
        }
      } catch (error) {
        console.error('Fehler beim Hinzufügen des Essens:', error);
        if (error.response && error.response.data && error.response.data.message) {
          this.message = error.response.data.message; // Zeige die Server-Nachricht an
        } else {
          this.message = 'Serverfehler: ' + error.message; // Allgemeiner Fehler
        }
      }
    }
  }
};
</script>
