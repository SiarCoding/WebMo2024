<template>
  <div class="container mt-5">
    <h2 class="text-center mb-4">
      <i class="bi bi-pencil-square"></i> <!-- Passendes Icon für Bearbeitung -->
      {{ $t('pages.view_meal_plans') }} {{ selectedWeek }}
    </h2>

    <!-- Dropdown zum Auswählen der Woche -->
    <div class="mb-4 text-center">
      <label for="week-select" class="form-label">{{ $t('pages.select_week') }}</label>
      <select id="week-select" v-model="selectedWeek" @change="loadPlanForWeek" class="form-select w-25 mx-auto">
        <option v-for="week in 8" :key="week" :value="week">{{ $t('pages.week') }} {{ week }}</option>
      </select>
    </div>

    <!-- Auswahl der Mahlzeiten für die Tage -->
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

    <!-- Buttons zum Speichern und zur Ansicht der Pläne -->
    <div class="text-center">
      <button class="btn btn-primary me-2" @click="saveEditedPlan">{{ $t('pages.save_plan') }}</button>
      <button class="btn btn-warning" @click="viewPlans">{{ $t('pages.view_meal_plans') }}</button>
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
      currentLocale: localStorage.getItem('locale') || 'de', // Sprachumschalter
    };
  },
  methods: {
    async loadEssen() {
      try {
        const response = await axios.get('http://localhost:3001/api/essen');
        this.essenList = response.data;
      } catch (error) {
        this.errorMessage = this.$t('pages.server_error');
        this.successMessage = '';
      }
    },

    async loadPlanForWeek() {
      try {
        const response = await axios.get(`http://localhost:3001/api/essensplan/${this.selectedWeek}`);
        const planData = response.data;

        // Plan in das richtige Format konvertieren
        this.plan = {
          Montag: planData.days.Montag?.food_id || null,
          Dienstag: planData.days.Dienstag?.food_id || null,
          Mittwoch: planData.days.Mittwoch?.food_id || null,
          Donnerstag: planData.days.Donnerstag?.food_id || null,
          Freitag: planData.days.Freitag?.food_id || null,
        };

        this.successMessage = '';
        this.errorMessage = '';
      } catch (error) {
        this.errorMessage = this.$t('pages.server_error');
        this.successMessage = '';
      }
    },

    checkForDuplicates() {
      const selectedMeals = Object.values(this.plan).filter(meal => meal !== null);
      const duplicateMeals = selectedMeals.filter((item, index) => selectedMeals.indexOf(item) !== index);
      if (duplicateMeals.length > 0) {
        this.errorMessage = this.$t('pages.duplicate_error');
        return true; // Doppelte Einträge gefunden
      }
      return false; // Keine doppelten Einträge
    },

    async saveEditedPlan() {
      this.errorMessage = '';

      if (this.checkForDuplicates()) {
        return;
      }

      try {
        const plan = Object.keys(this.plan).map(day => ({
          tag: day,
          essen_id: this.plan[day],
        }));

        const token = localStorage.getItem('token');
        if (!token) {
          this.errorMessage = this.$t('pages.no_token');
          this.$router.push('/login');
          return;
        }

        const response = await axios.put(`http://localhost:3001/api/essensplan/${this.selectedWeek}`, {
          plan,
        }, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        // Erfolgsmeldung setzen und bestehen lassen
        this.successMessage = this.$t('pages.plan_saved');

        await this.loadPlanForWeek();
      } catch (error) {
        this.successMessage = '';
        this.errorMessage = error.response?.data?.message || this.$t('pages.server_error');
      }
    },

    viewPlans() {
      this.$router.push('/plaene'); // Navigiere zur Seite mit den Essensplänen
    },

    changeLocale() {
      this.$i18n.locale = this.currentLocale;
      localStorage.setItem('locale', this.currentLocale);
    }
  },

  mounted() {
    this.loadEssen();
    this.loadPlanForWeek(); // Lade den Plan für die Standardwoche (Woche 1)
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
