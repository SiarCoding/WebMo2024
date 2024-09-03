<template>
    <div class="container mt-5">
      <h2 class="text-center mb-4">Essensplan bearbeiten für Woche {{ selectedWeek }}</h2>
  
      <div class="row">
        <div
          class="col-md-6 mb-3"
          v-for="day in ['Montag', 'Dienstag', 'Mittwoch', 'Donnerstag', 'Freitag']"
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
  
      <div class="text-center">
        <button class="btn btn-primary me-2" @click="savePlan">Plan speichern</button>
        <button class="btn btn-secondary" @click="goToPlaene">Pläne anzeigen</button> <!-- Neuer Button hinzugefügt -->
        <p v-if="errorMessage" class="text-danger mt-3">{{ errorMessage }}</p>
        <p v-if="successMessage" class="text-success mt-3">{{ successMessage }}</p>
      </div>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
  
  export default {
    data() {
      return {
        selectedWeek: this.$route.params.week, // Woche aus Routen-Parameter erhalten
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
      loadPlan() {
        const planData = this.$router.currentRoute.value.state?.plan;
        if (planData) {
          this.selectedWeek = planData.week_number;
          for (const [day, meal] of Object.entries(planData.days)) {
            this.plan[day] = meal ? meal.meal_id : null;
          }
        } else {
          this.fetchPlanFromAPI();
        }
      },
      async fetchPlanFromAPI() {
        try {
          const response = await axios.get(`http://localhost:3001/api/essensplan/${this.selectedWeek}`);
          const currentPlan = response.data;
          this.resetPlan();
  
          if (currentPlan) {
            for (const [day, meal] of Object.entries(currentPlan.days)) {
              this.plan[day] = meal ? meal.meal_id : null;
            }
          }
        } catch (error) {
          this.errorMessage = 'Fehler beim Laden des Essensplans';
        }
      },
      async savePlan() {
        try {
          const plan = Object.keys(this.plan).map(day => ({
            day_of_week: day,
            meal_id: this.plan[day]
          }));
  
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
          this.loadPlan();
        } catch (error) {
          this.errorMessage = 'Fehler beim Speichern des Essensplans';
        }
      },
      goToPlaene() {
        // Methode, um zur /plaene-Seite zu navigieren
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
  </style>
  