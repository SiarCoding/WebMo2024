<template>
  <div class="container mt-5">
    <h2 class="mb-4 text-center display-5">
      <i class="bi bi-egg-fried"></i> <!-- Bootstrap Icon für Essen -->
      {{ $t('pages.add_food') }}
    </h2>
    
    <form @submit.prevent="hinzufuegen" class="card p-4 shadow-lg" style="background-color: #f8f9fa; border-radius: 10px;">
      <div class="mb-3">
        <label for="name" class="form-label fw-bold">{{ $t('pages.food_name') }}:</label>
        <input 
          type="text" 
          v-model="essen.name" 
          class="form-control form-control-lg shadow-sm" 
          id="name" 
          required
        />
      </div>

      <div class="mb-3">
        <label for="preis" class="form-label fw-bold">{{ $t('pages.food_price') }}:</label>
        <div class="input-group input-group-lg shadow-sm">
          <input 
            type="number" 
            step="0.01" 
            v-model="essen.preis" 
            class="form-control" 
            id="preis" 
            placeholder="10.99"
            required
          />
          <span class="input-group-text">€</span>
        </div>
      </div>

      <div class="mb-4">
        <label for="art" class="form-label fw-bold">{{ $t('pages.food_type') }}:</label>
        <select 
          v-model="essen.art" 
          class="form-select form-select-lg shadow-sm" 
          id="art" 
          required
        >
          <option disabled value="">{{ $t('pages.please_select') }}</option>
          <option value="vegetarisch">{{ $t('pages.vegetarian') }}</option>
          <option value="vegan">{{ $t('pages.vegan') }}</option>
          <option value="mit Fleisch">{{ $t('pages.meat') }}</option>
        </select>
      </div>

      <button type="submit" class="btn btn-primary w-100 btn-lg shadow-sm">{{ $t('pages.save_food') }}</button>
    </form>

    <!-- "Essen anzeigen" Button -->
    <div class="text-center mt-4">
      <button @click="showEssen" class="btn btn-warning btn-lg shadow-sm">{{ $t('pages.view_food') }}</button>
    </div>

    <p v-if="message" class="mt-3 text-success text-center fw-bold">{{ message }}</p>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      currentLocale: this.$i18n.locale, // Aktuelle Sprache
      essen: {
        name: '',
        preis: 0,
        art: ''
      },
      message: ''
    };
  },
  methods: {
    changeLocale() {
      this.$i18n.locale = this.currentLocale; // Sprache ändern
    },
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
    },
    showEssen() {
      this.$router.push('/essen'); // Navigiert zur Seite '/essen'
    }
  }
};
</script>

<style scoped>
.container {
  max-width: 800px;
}

.card-title {
  margin-bottom: 15px;
}

.text-danger,
.text-success {
  font-weight: bold;
}

.me-2 {
  margin-right: 0.5rem;
}

.form-select {
  font-size: 1.1rem;
  padding: 10px;
  border-radius: 5px;
}

.btn-primary {
  background-color: #007bff;
  border-color: #007bff;
}

.btn-warning {
  background-color: #ffc107;
  border-color: #ffc107;
}

.text-center {
  margin-bottom: 1.5rem;
}

.card {
  border-radius: 10px;
  background-color: #f8f9fa;
}

.text-success {
  color: green;
}
</style>
