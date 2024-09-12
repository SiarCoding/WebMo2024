<template>
  <div class="container mt-5">
    <h2 class="mb-4 text-center display-5">{{ $t('pages.create_meal_plan') }}</h2>
    <div class="mb-4 text-center">
      <label for="week-select" class="form-label">{{ $t('pages.select_week') }}</label>
      <select id="week-select" v-model="selectedWeek" class="form-select w-25 mx-auto">
        <option v-for="week in 8" :key="week" :value="week">{{ $t('pages.week') }} {{ week }}</option>
      </select>
    </div>

    <!-- Auswahl der Mahlzeiten für die Tage -->
    <div class="row">
      <div class="col-md-6 mb-3" v-for="(mealId, day) in plan" :key="day">
        <div class="card">
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

    <!-- Button zum Speichern des Plans -->
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
      selectedWeek: 1,
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
      }
    },

    async saveNewPlan() {
      this.successMessage = '';
      this.errorMessage = '';

      // Überprüfung auf doppelte Einträge
      const selectedMeals = Object.values(this.plan).filter(meal => meal !== null);
      const duplicateMeals = selectedMeals.filter((item, index) => selectedMeals.indexOf(item) !== index);

      if (duplicateMeals.length > 0) {
        this.errorMessage = this.$t('pages.duplicate_error');
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

        const response = await axios.post(
          'http://localhost:3001/api/essensplan',
          {
            wochennummer: this.selectedWeek,
            plan,
          },
          {
            headers: {
              Authorization: `Bearer ${token}`,
            }
          }
        );

        this.successMessage = response.data.message;
      } catch (error) {
        this.errorMessage = error.response?.data?.message || this.$t('pages.server_error');
      }
    },

    changeLocale() {
      this.$i18n.locale = this.currentLocale;
      localStorage.setItem('locale', this.currentLocale);
    }
  },

  mounted() {
    this.loadEssen();
  },
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
