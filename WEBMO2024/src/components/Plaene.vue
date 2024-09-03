<template>
  <div class="container mt-5">
    <h2 class="text-center mb-4">Essenspläne für 8 Wochen</h2>

    <!-- Erfolg- und Fehlermeldungen -->
    <div v-if="errorMessage" class="alert alert-danger">{{ errorMessage }}</div>
    <div v-if="successMessage" class="alert alert-success">{{ successMessage }}</div>

    <!-- Button zum Hinzufügen eines neuen Plans -->
    <div class="text-center mb-3">
      <button class="btn btn-success" @click="addPlan">Plan hinzufügen</button>
    </div>

    <!-- Liste der Essenspläne -->
    <div class="row">
      <div v-for="plan in essensplaene" :key="plan.week_number" class="col-md-6 mb-4">
        <div class="card">
          <div class="card-body">
            <h5 class="card-title">Essensplan für Woche {{ plan.week_number }}</h5>
            <ul class="list-group list-group-flush mb-3">
              <li v-for="day in plan.days" :key="day.day_of_week" class="list-group-item">
                <strong>{{ day.day_of_week }}:</strong>
                <span v-if="day.meal_id">Essen ID: {{ day.meal_id }}</span>
                <span v-else>Kein Essen ausgewählt</span>
              </li>
            </ul>
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
        console.log('Geladene Pläne:', this.essensplaene); // Debugging-Ausgabe
      } catch (error) {
        console.error('Fehler beim Laden der Essenspläne:', error);
        this.errorMessage = 'Fehler beim Laden der Essenspläne';
      }
    },
    addPlan() {
      this.$router.push('/essensplan'); // Navigation zur Seite zum Hinzufügen eines neuen Plans
    },
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
