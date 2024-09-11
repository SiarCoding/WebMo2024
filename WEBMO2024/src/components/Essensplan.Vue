<template>
  <div class="container mt-5">
    <h2 class="text-center mb-4">Bearbeite Essensplan für Woche {{ selectedWeek }}</h2>

    <!-- Dropdown zum Auswählen der Woche -->
    <div class="mb-4 text-center">
      <label for="week-select" class="form-label">Woche auswählen:</label>
      <select id="week-select" v-model="selectedWeek" class="form-select w-25 mx-auto">
        <option v-for="week in 8" :key="week" :value="week">Woche {{ week }}</option>
      </select>
    </div>

    <!-- Auswahl der Mahlzeiten für die Tage -->
    <div class="row">
      <div
        class="col-md-6 mb-3"
        v-for="(mealId, day) in plan"
        :key="day"
      >
        <div class="card">
          <div class="card-body">
            <h5 class="card-title">{{ day }}</h5>
            <select v-model="plan[day]" class="form-select">
              <option value="" disabled>Wählen Sie ein Essen</option>
              <option v-for="essen in essenList" :key="essen.id" :value="essen.id">{{ essen.name }}</option>
            </select>
          </div>
        </div>
      </div>
    </div>

    <!-- Buttons zum Speichern des Plans -->
    <div class="text-center">
      <button class="btn btn-primary me-2" @click="saveEditedPlan">Plan speichern</button>
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
      selectedWeek: 1, // Standardmäßig Woche 1 ausgewählt
      essenList: [], 
      plan: {
        Montag: null,
        Dienstag: null,
        Mittwoch: null,
        Donnerstag: null,
        Freitag: null,
      },
      errorMessage: '',
      successMessage: '',
    };
  },
  methods: {
    async loadEssen() {
      try {
        const response = await axios.get('http://localhost:3001/api/essen');
        this.essenList = response.data;
      } catch (error) {
        this.errorMessage = 'Fehler beim Laden der Essen';
      }
    },

    async loadPlan() {
      try {
        // Logik zum Laden des Plans für die ausgewählte Woche
        const response = await axios.get(`http://localhost:3001/api/essensplan/${this.selectedWeek}`);
        const plan = response.data;
        this.plan = plan.reduce((acc, item) => {
          acc[item.tag] = item.essen_id;
          return acc;
        }, {});
      } catch (error) {
        this.errorMessage = 'Fehler beim Laden des Essensplans';
      }
    },

    async saveEditedPlan() {
      // Zurücksetzen der Meldungen vor dem Speichern
      this.successMessage = '';
      this.errorMessage = '';

      try {
        const plan = Object.keys(this.plan).map(day => ({
          tag: day,
          essen_id: this.plan[day]
        }));

        console.log("Gesendete Daten:", plan); 

        const token = localStorage.getItem('token');
        if (!token) {
          this.errorMessage = 'Token fehlt. Bitte erneut einloggen.';
          this.$router.push('/login');
          return;
        }

        const response = await axios.put(`http://localhost:3001/api/essensplan/${this.selectedWeek}`, {
          plan
        }, {
          headers: {
            Authorization: `Bearer ${token}`,
          }
        });

        this.successMessage = response.data.message;

        // Plan erneut laden
        await this.loadPlan();
      } catch (error) {
        console.error("Fehler:", error); 
        this.errorMessage = error.response?.data?.message || 'Fehler beim Speichern des Essensplans';
      }
    }
  },

  mounted() {
    this.loadEssen();
    this.loadPlan(); // Lade den Plan beim Mounten der Komponente
  }
}
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
</style>