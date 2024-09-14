<template>
  <div class="container mt-5">
    <h2 class="mb-4 text-center">🍽️ {{ $t('pages.edit_food') }}</h2>


    <form @submit.prevent="updateEssen" class="card p-4 shadow-sm" style="background-color: #e3f2fd;">
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

      <button type="submit" class="btn btn-primary w-100">{{ $t('pages.save_food') }}</button>
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
      message: '',
      currentLocale: localStorage.getItem('locale') || 'de', // Sprachumschalter
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
      this.message = this.$t('pages.server_error');
    }
  },

  methods: {
    changeLocale() {
      this.$i18n.locale = this.currentLocale; // Aktualisiere die Sprache
      localStorage.setItem('locale', this.currentLocale); // Speichere die gewählte Sprache
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
          this.message = this.$t('pages.food_updated');
          this.$router.push('/essen'); // Zurück zur Liste nach erfolgreichem Speichern
        } else {
          this.message = this.$t('pages.update_error');
        }
      } catch (error) {
        console.error('Fehler beim Aktualisieren des Essens:', error);
        this.message = this.$t('pages.server_error') + error.message;

        if (error.response && error.response.status === 401) {
          localStorage.removeItem('token'); // Token löschen
          this.$router.push('/login'); // Weiterleitung zur Login-Seite
        }
      }
    }
  }
};
</script>

<style scoped>
.container {
  max-width: 700px;
}

.form-label {
  font-size: 1.1rem;
}

.input-group-text {
  font-size: 1.1rem;
}

button.btn-primary {
  font-size: 1.2rem;
}
</style>
