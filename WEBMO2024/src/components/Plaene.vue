<template>
    <div class="container mt-5">
      <h2 class="text-center mb-4">Verwalte Essenspläne</h2>
  
      <!-- Erfolg- und Fehlermeldungen -->
      <div v-if="errorMessage" class="alert alert-danger">{{ errorMessage }}</div>
      <div v-if="successMessage" class="alert alert-success">{{ successMessage }}</div>
  
      <!-- Liste der Essenspläne -->
      <div class="row">
        <div v-for="plan in essensplaene" :key="plan.week_number" class="col-md-6 mb-4">
          <div class="card">
            <div class="card-body">
              <h5 class="card-title">Essensplan für Woche {{ plan.week_number }}</h5>
              <ul class="list-group list-group-flush mb-3">
                <li v-for="(meal, day) in plan.days" :key="day" class="list-group-item">
                  <strong>{{ day }}:</strong> {{ meal.meal_name }}
                </li>
              </ul>
              <div class="d-flex justify-content-between">
                <button class="btn btn-primary me-2" @click="editPlan(plan)">Bearbeiten</button>
                <button class="btn btn-danger" @click="deletePlan(plan.week_number)">Löschen</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
  
  export default {
    data() {
      return {
        essensplaene: [],
        errorMessage: '',
        successMessage: '',
      };
    },
    methods: {
      async loadPlans() {
        try {
          const response = await axios.get('http://localhost:3001/api/essensplan');
          this.essensplaene = response.data;
        } catch (error) {
          console.error('Fehler beim Laden der Essenspläne:', error);
          this.errorMessage = 'Fehler beim Laden der Essenspläne';
        }
      },
      editPlan(plan) {
        this.$router.push({ name: 'EditEssensplan', params: { week: plan.week_number } });
      },
      async deletePlan(week) {
  try {
    if (!week) {
      this.errorMessage = 'Ungültige Woche. Bitte versuchen Sie es erneut.';
      return;
    }
    await axios.delete(`http://localhost:3001/api/essensplan/${week}`);
    this.successMessage = 'Essensplan erfolgreich gelöscht';
    this.errorMessage = '';
    this.loadPlans();
  } catch (error) {
    console.error('Fehler beim Löschen des Essensplans:', error);
    this.successMessage = '';
    this.errorMessage = 'Fehler beim Löschen des Essensplans';
  }
}

    },
    created() {
      this.loadPlans();
    }
  };
  </script>
  
  <style scoped>
  .container {
    max-width: 800px;
  }
  
  .card {
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  }
  
  .card-title {
    font-size: 1.25rem;
    margin-bottom: 15px;
  }
  
  .list-group-item {
    padding: 0.75rem 1.25rem;
  }
  
  .btn {
    min-width: 100px;
  }
  
  .alert {
    margin-top: 20px;
  }
  </style>
  