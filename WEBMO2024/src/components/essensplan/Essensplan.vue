<template>
  <div class="container mt-5">
    <h2 class="mb-4 text-center display-5">
      <i class="bi bi-calendar3"></i>
      {{ $t('pages.create_meal_plan') }}
    </h2>

    <!-- Wochen-Auswahl -->
    <div class="mb-4 text-center">
      <label for="week-select" class="form-label fw-bold">{{ $t('pages.select_week') }}</label>
      <select id="week-select" v-model="selectedWeek" class="form-select w-25 mx-auto">
        <option v-for="week in 8" :key="week" :value="week">{{ $t('pages.week') }} {{ week }}</option>
      </select>
    </div>

    <!-- Mahlzeiten-Auswahl für die Tage -->
    <div class="row">
      <div class="col-md-6 mb-3" v-for="(mealId, day) in plan" :key="day">
        <div class="card h-100 shadow-sm">
          <div class="card-body">
            <h5 class="card-title">{{ $t('pages.days.' + day) }}</h5>
            <select v-model="plan[day]" class="form-select">
              <option value="" disabled>{{ $t('pages.choose_food') }}</option>
              <option v-for="essen in essenList" :key="essen.id" :value="essen.id">{{ essen.name }}</option>
            </select>
          </div>
        </div>
      </div>
    </div>

    <!-- Plan speichern Button -->
    <div class="text-center">
      <button class="btn btn-primary me-2" @click="saveNewPlan">{{ $t('pages.save_plan') }}</button>
      <p v-if="successMessage" class="text-success mt-3">{{ successMessage }}</p>
      <p v-if="errorMessage" class="text-danger mt-3">{{ errorMessage }}</p>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      selectedWeek: 1, // Startet mit Woche 1
      essenList: [], // Hier werden die Essensdaten in array gespeichert
      plan: { // Mahlzeiten für jeden Tag der Woche (default: null)
        Montag: null,
        Dienstag: null,
        Mittwoch: null,
        Donnerstag: null,
        Freitag: null,
      },
      errorMessage: '', // Fehlernachrichten für den Benutzer
      successMessage: '', // Erfolgsmeldungen für den Benutzer
      currentLocale: localStorage.getItem('locale') || 'de', // Sprachumschaltung
    };
  },

  methods: {
    // Lädt die Essensliste vom Server
    async loadEssen() {
      try {
        const response = await axios.get('http://localhost:3001/api/essen');
        this.essenList = response.data; // Speichert die Essensliste
      } catch (error) {
        this.errorMessage = this.$t('pages.server_error'); // Fehler anzeigen
      }
    },

    // Speichert den neuen Essensplan
    async saveNewPlan() {
      this.successMessage = ''; // Zurücksetzen der Nachrichten
      this.errorMessage = '';

      // Überprüft, ob Mahlzeiten doppelt gewählt wurden
      const selectedMeals = Object.values(this.plan).filter(meal => meal !== null);
      const duplicateMeals = selectedMeals.filter((item, index) => selectedMeals.indexOf(item) !== index);

      if (duplicateMeals.length > 0) {
        this.errorMessage = this.$t('pages.duplicate_error'); // Meldung bei doppelten Mahlzeiten
        return;
      }

      try {
        // Formatiert den Plan zum Speichern
        const plan = Object.keys(this.plan).map(day => ({
          tag: day,
          essen_id: this.plan[day],
        }));

        const token = localStorage.getItem('token'); // Holen des Tokens
        if (!token) {
          this.errorMessage = this.$t('pages.no_token');
          this.$router.push('/login'); // Weiterleitung zur Login-Seite
          return;
        }

        // Schickt den Essensplan an den Server
        const response = await axios.post(
          'http://localhost:3001/api/essensplan',
          {
            wochennummer: this.selectedWeek, // Ausgewählte Woche
            plan, // Essensplan
          },
          {
            headers: {
              Authorization: `Bearer ${token}`, // Token zur Authentifizierung mitschicken
            }
          }
        );

        this.successMessage = response.data.message; // Erfolgsmeldung anzeigen
      } catch (error) {
        this.errorMessage = error.response?.data?.message || this.$t('pages.server_error'); // Fehler anzeigen
      }
    },

    // Wechselt die Sprache
    changeLocale() {
      this.$i18n.locale = this.currentLocale; // Sprachwechsel
      localStorage.setItem('locale', this.currentLocale); // Speichern der Sprache im LocalStorage
    }
  },

  mounted() {
    this.loadEssen(); // Lädt die Essensliste, wenn die Seite geladen wird
  },
}
</script>

<style scoped>
.container {
  max-width: 800px; 
}

.card {
  border-radius: 10px; 
  background-color: #f8f9fa; 
}

.btn-primary:hover {
  background-color: #0056b3; 
}

.text-danger, .text-success {
  font-weight: bold; 
}
</style>
