<!--Das Bearbeiten der gespeicherten Esseneinträge findet hier statt (/edit-essen) Das heißt nach dem Bearbeiten Klick kommt man auf das edit-Essen. Verwendet wird hier Bootstrap. Im Templete kommt der HTML Code Abschnitt!
Den <script> nutzen wir für Syntax!!!-->
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
          name: '', //hier kommen die selben Dateneinträge wie bei AddEssen.vue, da wir logischerweise die Änderungen anhand der gespeicherten Daten vornehmen.
          preis: 0,
          art: ''
        },
        message: ''
      };
    },
    async created() {
      const id = this.$route.params.id;
      try {
        const response = await axios.get(`http://localhost:3001/api/essen/${id}`); //Zugriff auf die API-Endpoint von Essenseinträgen Anhand der ID
        this.essen = response.data;
      } catch (error) {
        console.error('Fehler beim Laden des Essens:', error); //FEHLERMELDUNG
      }
    },
    methods: {
        async updateEssen() {
  try {
    // Hier die URL überprüfen, ob sie korrekt ist
    const response = await axios.put(`http://localhost:3001/api/essen/${this.essen.id}`, this.essen);
    if (response.data.success) {
      this.message = 'Essen erfolgreich aktualisiert!';
      this.$router.push('/'); // Zurück zur Liste nach erfolgreichem Speichern
    } else {
      this.message = 'Fehler beim Aktualisieren des Essens';
    }
  } catch (error) {
    console.error('Fehler beim Aktualisieren des Essens:', error);
    this.message = 'Serverfehler: ' + error.message;
  }
}

    }
  };
  </script>
  