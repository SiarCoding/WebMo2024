<template>
  <div class="container mt-5">
    <h2 class="mb-4 text-center">🍽️ Essen bearbeiten</h2>
    <form @submit.prevent="updateEssen" class="card p-4 shadow-sm" style="background-color: #e3f2fd;">
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

      <button type="submit" class="btn btn-primary w-100">Speichern</button>
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

  async created() {
  const id = this.$route.params.id;
  try {
    const response = await axios.get(`http://localhost:3001/api/essen/${id}`);
    
    // Stelle sicher, dass alle Daten korrekt geladen werden
    this.essen = {
      name: response.data.name,
      preis: response.data.price, // Preis
      art: response.data.type     // Art (vegetarisch, vegan, etc.)
    };
  } catch (error) {
    console.error('Fehler beim Laden des Essens:', error);
    this.message = 'Fehler beim Laden der Daten.';
  }
},


  methods: {
    isAuthenticated() {
      const token = localStorage.getItem('token');
      return !!token; // Gibt true zurück, wenn der Token vorhanden ist
    },

    async updateEssen() {
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          this.$router.push('/login');
          return;
        }

        const response = await axios.put(`http://localhost:3001/api/essen/${this.$route.params.id}`, this.essen, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (response.data.success) {
          this.message = 'Essen erfolgreich aktualisiert!';
          this.$router.push('/essen'); // Zurück zur Liste nach erfolgreichem Speichern
        } else {
          this.message = 'Fehler beim Aktualisieren des Essens';
        }
      } catch (error) {
        console.error('Fehler beim Aktualisieren des Essens:', error);
        this.message = 'Serverfehler: ' + error.message;

        if (error.response && error.response.status === 401) {
          localStorage.removeItem('token'); // Token löschen
          this.$router.push('/login'); // Weiterleitung zur Login-Seite
        }
      }
    }
  }
};
</script>
