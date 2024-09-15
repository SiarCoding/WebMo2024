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

    <div class="text-center mt-4">
      <button @click="showEssen" class="btn btn-warning btn-lg shadow-sm">{{ $t('pages.view_food') }}</button>
    </div>

    <!-- Fehlermeldungen -->
    <p v-if="message" :class="messageType" class="mt-3 text-center fw-bold">{{ message }}</p>
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
      messageType: '' // Dynamische Klasse für Fehlermeldung (text-danger oder text-success)
    };
  },
  methods: {
    async hinzufuegen() {
      if (!this.isValidInput()) {
        this.setMessage(this.$t('pages.fill_all_fields'), 'text-danger');
        return;
      }

      try {
        const token = localStorage.getItem('token');
        if (!token) {
          this.setMessage(this.$t('pages.no_token'), 'text-danger');
          return;
        }

        const response = await axios.post(
          'http://localhost:3001/api/essen',
          this.essen,
          { headers: { Authorization: `Bearer ${token}` } }
        );

        if (response.data.success) {
          this.resetForm();
          this.setMessage(this.$t('pages.food_added'), 'text-success');
          this.$emit('essenAdded', response.data.essen);
        } else {
          this.setMessage(response.data.message, 'text-danger');
        }
      } catch (error) {
        this.handleError(error);
      }
    },
    showEssen() {
      this.$router.push('/essen');
    },
    resetForm() {
      this.essen = { name: '', preis: 0, art: '' };
    },
    isValidInput() {
      return this.essen.name && this.essen.preis > 0 && this.essen.art;
    },
    setMessage(message, type) {
      this.message = message;
      this.messageType = type;
    },
    handleError(error) {
      console.error('Fehler beim Hinzufügen des Essens:', error);
      const errorMessage = error.response?.data?.message || this.$t('pages.server_error') + error.message;
      this.setMessage(errorMessage, 'text-danger');
    }
  }
};
</script>

<style scoped>
.container {
  max-width: 800px;
}

.form-label {
  font-weight: bold;
}

.shadow-sm {
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.btn-primary {
  background-color: #007bff;
  border-color: #007bff;
}

.btn-warning {
  background-color: #ffc107;
  border-color: #ffc107;
}

.text-danger {
  font-weight: bold;
  color: red;
}

.text-success {
  font-weight: bold;
  color: green;
}

.text-center {
  margin-bottom: 1.5rem;
}
</style>
