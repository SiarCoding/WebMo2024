<template>
  <div class="container mt-5">
    <h2 class="text-center mb-4">Essensplan für Woche {{ selectedWeek }}</h2>

    <!-- Week selection -->
    <div class="row justify-content-center mb-4">
      <div class="col-md-6">
        <label for="week-select" class="form-label">Woche auswählen:</label>
        <select id="week-select" v-model="selectedWeek" class="form-select" @change="loadPlan">
          <option v-for="week in 8" :key="week" :value="week">Woche {{ week }}</option>
        </select>
      </div>
    </div>

    <!-- Day and meal selection -->
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

    <!-- Buttons for save plan and view plans -->
    <div class="text-center">
      <button class="btn btn-primary me-2" @click="savePlan">Plan speichern</button>
      <button class="btn btn-secondary" @click="viewPlans">Essenspläne ansehen</button>
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
      selectedWeek: 1, // Default selected week
      essenList: [], // List of available meals
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
      console.log('loadEssen Methode aufgerufen');
      try {
        const response = await axios.get('http://localhost:3001/api/essen');
        this.essenList = response.data;
        console.log('Essen geladen:', this.essenList);
      } catch (error) {
        console.error('Fehler beim Laden der Essen:', error);
        this.errorMessage = 'Fehler beim Laden der Essen';
      }
    },
    
    async loadPlan() {
      try {
        const response = await axios.get(`http://localhost:3001/api/essensplan/${this.selectedWeek}`);
        const currentPlan = response.data;

        this.resetPlan(); // Plan zurücksetzen

        if (currentPlan) {
          for (const [day, meal] of Object.entries(currentPlan.days)) {
            this.plan[day] = meal ? meal.meal_id : null; 
          }
        }

        console.log('Essensplan geladen:', this.plan);
        this.errorMessage = ''; // Fehlernachricht löschen
      } catch (error) {
        if (error.response && error.response.status === 404) {
          this.resetPlan(); 
          console.log(`Kein Essensplan für Woche ${this.selectedWeek} gefunden. Standardauswahl wird angezeigt.`);
          this.errorMessage = ''; 
        } else {
          this.errorMessage = 'Fehler beim Laden des Essensplans.';
          console.error('Fehler beim Laden des Essensplans:', error);
        }
      }
    },

    async savePlan() {
      try {
        const plan = Object.keys(this.plan).map(day => ({
          day_of_week: day,
          meal_id: this.plan[day]
        }));

        const mealIds = plan.map(item => item.meal_id);
        const uniqueMeals = new Set(mealIds);

        if (uniqueMeals.size !== mealIds.length) {
          this.errorMessage = 'Ein Essensplan darf nicht dasselbe Gericht mehr als einmal pro Woche anbieten.';
          return;
        }

        const token = localStorage.getItem('token');
        if (!token) {
          this.errorMessage = 'Token fehlt. Bitte erneut einloggen.';
          this.$router.push('/login');
          return;
        }

        const response = await axios.post('http://localhost:3001/api/essensplan', {
          week_number: this.selectedWeek,
          plan
        }, {
          headers: {
            Authorization: `Bearer ${token}`,
          }
        });

        this.successMessage = response.data.message;
        this.errorMessage = ''; // Lösche die Fehlermeldung, falls vorhanden
        console.log('Essensplan gespeichert:', response.data);
        this.loadPlan();
      } catch (error) {
        console.error('Fehler beim Speichern des Essensplans:', error);
        this.errorMessage = error.response?.data?.message || 'Fehler beim Speichern des Essensplans';
      }
    },
    
    viewPlans() {
      this.$router.push('/plaene');
    },

    resetPlan() {
      this.plan = {
        Montag: null,
        Dienstag: null,
        Mittwoch: null,
        Donnerstag: null,
        Freitag: null,
      };
    }
  },
  
  mounted() {
    this.loadEssen();
    this.loadPlan();
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
