<template>
  <div class="container mt-5">
    <h2 class="mb-4 text-center">
      <i class="bi bi-pencil-square"></i> 
      {{ $t('pages.edit_food') }}
    </h2>

    <form @submit.prevent="updateEssen" class="card p-4 shadow-sm" style="background-color: #e3f2fd;">
      <div class="mb-3">
        <label for="name" class="form-label">
          {{ $t('pages.food_name') }}:
        </label>
        <input type="text" v-model="essen.name" class="form-control" id="name" required />
      </div>

      <div class="mb-3">
        <label for="preis" class="form-label">
          {{ $t('pages.food_price') }}:
        </label>
        <div class="input-group">
          <input type="number" step="0.01" v-model="essen.preis" class="form-control" id="preis" required />
          <span class="input-group-text">€</span>
        </div>
      </div>

      <div class="mb-4">
        <label for="art" class="form-label">
          {{ $t('pages.food_type') }}:
        </label>
        <select v-model="essen.art" class="form-select" id="art" required>
          <option disabled value="">{{ $t('pages.please_select') }}</option>
          <option value="vegetarisch">{{ $t('pages.vegetarian') }}</option>
          <option value="vegan">{{ $t('pages.vegan') }}</option>
          <option value="mit Fleisch">{{ $t('pages.meat') }}</option>
        </select>
      </div>

      <button type="submit" class="btn btn-primary w-100">
        {{ $t('pages.save_food') }}
      </button>
    </form>

    <p v-if="message" class="mt-3 text-success text-center">
      <i class="bi bi-check-circle-fill"></i> <!-- Bootstrap Icon für Erfolg -->
      {{ message }}
    </p>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  //default werte
  data() {
    return {
      essen: {
        name: '',
        preis: 0,
        art: ''
      },
      message: '',
    };
  },

  // Wird beim Erstellen der Komponente aufgerufen
  async created() {
    const id = this.$route.params.id; // Holt die id des Essens aus der URL
    await this.loadEssen(id); // essen daten werden geladen aus der loadEssen funktion
  },

  methods: {
    // Lädt die daten eines bestimmten Essens basierend auf der ID
    async loadEssen(id) {
      try {
        const response = await axios.get(`http://localhost:3001/api/essen/${id}`); // Holt die Essen-Daten von der /api/essen/id
        this.essen = {
          name: response.data.name,  // Setzt den Namen des Essens
          preis: response.data.price, // Setzt den Preis des Essens
          art: response.data.type,   // Setzt die Art des Essens (z.B. vegetarisch, vegan oder mit Fleisch)
        };
      } catch (error) {
        console.error('Fehler beim Laden des Essens:', error); // Loggt den Fehler in der Konsole
        this.setMessage(this.$t('pages.server_error'), 'text-danger'); // Setzt die Fehlermeldung
      }
    },

    // Aktualisiert die Details eines bestehenden Essens
    async updateEssen() {
      try {
        const token = localStorage.getItem('token'); // Holt den Token aus dem localStorage
        if (!token) {
          this.$router.push('/login'); // Leitet auf die Login-Seite weiter falls kein Token vorhanden ist
          return;
        }

        const response = await axios.put(
          `http://localhost:3001/api/essen/${this.$route.params.id}`, // Sende die aktualisierten Essen-Daten mit PUT
          this.essen,
          {
            headers: { Authorization: `Bearer ${token}` } // Fügt den Auth-Token in die Headers ein
          }
        );

        if (response.data.success) {
          this.setMessage(this.$t('pages.food_updated'), 'text-success'); // Erfolgsnachricht bei erfolgreichem Update
          this.$router.push('/essen'); // Leitet zurück zur Essen-Liste
        } else {
          this.setMessage(this.$t('pages.update_error'), 'text-danger'); // Fehlermeldung, falls das Update fehlschlägt
        }
      } catch (error) {
        this.handleError(error); // Fehlerbehandlung aufrufen
      }
    },

    // Zentrale Fehlerbehandlung
    handleError(error) {
      console.error('Fehler beim Aktualisieren des Essens:', error); // Loggt den Fehler in der Konsole
      if (error.response && error.response.status === 401) {
        localStorage.removeItem('token'); // Löscht den Token, falls der Status 401 (Unauthorized) zurückkommt
        this.$router.push('/login'); // Leitet den Benutzer auf die Login-Seite weiter
      } else {
        this.setMessage(this.$t('pages.server_error') + error.message, 'text-danger'); // Setzt eine generelle Fehlermeldung
      }
    },

    // Setzt eine Nachricht und den Typ der Nachricht (Erfolg oder Fehler)
    setMessage(message, type) {
      this.message = message; // Setzt die Nachricht
      this.messageType = type; // Setzt den Nachrichtentyp (z.B. "text-danger" oder "text-success")
    }
  }
};
</script>

<!--css für design -->

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
