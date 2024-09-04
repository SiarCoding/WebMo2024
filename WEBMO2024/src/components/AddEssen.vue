<template>
  <div class="container mt-5">
    <h2 class="mb-4 text-center">🍽️ {{ $t('pages.add_food') }}</h2>
    <form @submit.prevent="hinzufuegen" class="card p-4 shadow-sm" style="background-color: #e3f2fd;">
      <div class="mb-3">
        <label for="name" class="form-label">{{ $t('pages.food_name') }}:</label>
        <input type="text" v-model="essen.name" class="form-control" id="name" required />
      </div>

      <div class="mb-3">
        <label for="preis" class="form-label">{{ $t('pages.food_price') }}:</label>
        <div class="input-group">
          <input type="number" step="0.01" v-model="essen.preis" class="form-control" id="preis" required />
          <span class="input-group-text">€</span>
        </div>
      </div>

      <div class="mb-4">
        <label for="art" class="form-label">{{ $t('pages.food_type') }}:</label>
        <select v-model="essen.art" class="form-select" id="art" required>
          <option disabled value="">{{ $t('pages.please_select') }}</option>
          <option value="vegetarisch">{{ $t('pages.vegetarian') }}</option>
          <option value="vegan">{{ $t('pages.vegan') }}</option>
          <option value="mit Fleisch">{{ $t('pages.meat') }}</option>
        </select>
      </div>

      <button type="submit" class="btn btn-primary w-100">{{ $t('pages.add_food') }}</button>
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
        this.message = this.$t('pages.fill_all_fields'); // Übersetzte Fehlermeldung
        return;
      }

      try {
        const token = localStorage.getItem('token');
        if (!token) {
          this.message = this.$t('pages.no_token'); // Übersetzte Fehlermeldung
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
          this.message = this.$t('pages.food_added'); // Erfolgsnachricht übersetzen
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
          this.message = this.$t('pages.server_error') + error.message; // Allgemeiner Fehler
        }
      }
    }
  }
};
</script>
