<template>
  <div class="container mt-5">
    <h2 class="mb-4 text-center">Neues Essen hinzufügen</h2>
    <form @submit.prevent="hinzufuegen" class="card p-4 shadow-sm">
      <div class="mb-3">
        <label for="name" class="form-label">Name:</label>
        <input type="text" v-model="essen.name" class="form-control" id="name" required />
      </div>

      <div class="mb-3">
        <label for="beschreibung" class="form-label">Beschreibung:</label>
        <input type="text" v-model="essen.beschreibung" class="form-control" id="beschreibung" required />
      </div>

      <div class="mb-3">
        <label for="preis" class="form-label">Preis:</label>
        <input type="number" v-model="essen.preis" class="form-control" id="preis" required />
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
        beschreibung: '',
        preis: 0,
        art: ''  // Neues Feld für die Art des Essens
      },
      message: ''
    };
  },
  methods: {
    async hinzufuegen() {
  console.log('Essen hinzufügen gestartet');
  // Einfache Validierung vor dem Senden
  if (!this.essen.name || !this.essen.beschreibung || this.essen.preis <= 0 || !this.essen.art) {
    this.message = 'Bitte alle Felder korrekt ausfüllen!';
    return;
  }

  try {
    const response = await axios.post('http://localhost:3001/api/essen', this.essen);  // Port auf 3001 ändern
    console.log('Serverantwort:', response);
    if (response.data.success) {
      this.message = 'Essen erfolgreich hinzugefügt!';
      this.$emit('essenAdded', response.data.essen);  // Neues Essen über ein Event melden
      this.essen = { name: '', beschreibung: '', preis: 0, art: '' };  // Formular zurücksetzen
    } else {
      this.message = 'Fehler beim Hinzufügen des Essens';
    }
  } catch (error) {
    console.error('Fehler beim Hinzufügen des Essens:', error);
    this.message = 'Serverfehler: ' + error.message;
  }
}

  }
};
</script>
