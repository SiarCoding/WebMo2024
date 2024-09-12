<template>
  <div class="container mt-5">
    <h2 class="mb-4 text-center display-4">{{ $t('pages.view_meal_plans') }}</h2>

    <!-- Erfolg- und Fehlermeldungen -->
    <div v-if="errorMessage" class="alert alert-danger">{{ errorMessage }}</div>
    <div v-if="successMessage" class="alert alert-success">{{ successMessage }}</div>

    <!-- Wochenslider zum Auswählen des Essensplans -->
    <div class="d-flex justify-content-center mb-3">
      <div class="week-slider d-flex justify-content-center align-items-center p-2 rounded shadow" style="background-color: #000; width: 300px;">
        <!-- Linker Pfeil -->
        <span
          class="week-arrow text-white"
          :class="{ 'disabled-arrow': selectedWeek === 1 }"
          @click="changeWeek(-1)"
        >
          &lt;
        </span>

        <!-- Wochentext -->
        <span class="week-text mx-3 text-white">{{ $t('pages.week') }} {{ selectedWeek }}</span>

        <!-- Rechter Pfeil -->
        <span
          class="week-arrow text-white"
          :class="{ 'disabled-arrow': selectedWeek === 8 }"
          @click="changeWeek(1)"
        >
          &gt;
        </span>
      </div>
    </div>

    <!-- Gewählter Essensplan anzeigen -->
    <div v-if="selectedPlan" class="row justify-content-center">
      <div class="col-md-8 mb-4">
        <div class="card" style="background-color: #ffe6f0;">
          <div class="card-body">
            <h5 class="card-title text-center">{{ $t('pages.week') }} {{ selectedWeek }}</h5>

            <!-- Spaltenüberschriften -->
            <div class="row mb-2 fw-bold text-center">
              <div class="col-4">{{ $t('pages.days.Montag') }}</div>
              <div class="col-5">{{ $t('pages.food_name') }}</div>
              <div class="col-3">{{ $t('pages.food_price') }}</div>
            </div>

            <ul class="list-group list-group-flush mb-3">
              <li v-for="(meal, day, index) in selectedPlan.days" :key="day" class="list-group-item" :class="{ 'bg-light': index % 2 === 0 }">
                <div class="row text-center">
                  <div class="col-4"><strong>{{ $t('pages.days.' + day) }}</strong></div>
                  <div class="col-5">
                    <span v-if="meal">{{ meal.food_name }}</span>
                    <span v-else>{{ $t('pages.choose_food') }}</span>
                  </div>
                  <div class="col-3">
                    <span v-if="meal">{{ meal.price.toFixed(2) }} €</span>
                  </div>
                </div>
              </li>
            </ul>

            <!-- Gesamtpreis anzeigen -->
            <div class="d-flex justify-content-between mb-2 fw-bold">
              <span>{{ $t('pages.total_price') }}:</span>
              <span>{{ selectedPlan.total_price.toFixed(2) }} €</span>
            </div>

            <!-- Buttons zum Bearbeiten und Löschen -->
            <div class="d-flex justify-content-between">
              <button class="btn btn-primary me-2" @click="editPlan(selectedPlan)">{{ $t('pages.edit') }}</button>
              <button class="btn btn-danger" @click="deletePlan(selectedPlan.plan_id)">{{ $t('pages.delete') }}</button>
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
      selectedWeek: 1,
      selectedPlan: null,
      errorMessage: '',
      successMessage: '',
      currentLocale: localStorage.getItem('locale') || 'de',
    };
  },
  methods: {
    async loadPlans() {
      try {
        const response = await axios.get('http://localhost:3001/api/essensplan');
        this.essensplaene = response.data;
        this.loadPlan();
      } catch (error) {
        this.errorMessage = this.$t('pages.server_error');
      }
    },
    loadPlan() {
      this.selectedPlan = this.essensplaene.find(plan => plan.wochennummer == this.selectedWeek);
      if (!this.selectedPlan) {
        this.errorMessage = `${this.$t('pages.no_plan_for_week')} ${this.selectedWeek}`;
        this.successMessage = '';
      } else {
        this.errorMessage = '';
      }
    },
    changeWeek(direction) {
      if ((this.selectedWeek === 1 && direction === -1) || (this.selectedWeek === 8 && direction === 1)) {
        return;
      }
      this.selectedWeek += direction;
      this.loadPlan();
    },
    async deletePlan(planId) {
      try {
        if (!planId) {
          this.errorMessage = this.$t('pages.invalid_plan_id');
          return;
        }
        await axios.delete(`http://localhost:3001/api/essensplan/${planId}`);
        this.successMessage = this.$t('pages.plan_deleted');
        this.errorMessage = '';
        this.loadPlans();
      } catch (error) {
        this.errorMessage = this.$t('pages.server_error');
      }
    },
    editPlan(plan) {
      this.$router.push({ name: 'EditEssensplan', params: { week: plan.wochennummer } });
    },
    changeLocale() {
      this.$i18n.locale = this.currentLocale;
      localStorage.setItem('locale', this.currentLocale);
    }
  },
  created() {
    this.loadPlans();
  }
};
</script>

<style scoped>
.container {
  max-width: 900px;
}

.card {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  background-color: #ffe6f0;
}

.card-title {
  font-size: 1.5rem;
  margin-bottom: 15px;
}

.list-group-item {
  padding: 0.75rem 1.25rem;
}

.row.text-center > div {
  padding: 5px 0;
}

.d-flex {
  padding: 0.75rem 0;
}

.btn {
  min-width: 120px;
}

.alert {
  margin-top: 20px;
}

/* Slider Style */
.week-slider {
  background-color: #000;
  color: #fff;
  font-size: 1.2rem;
  width: 300px;
  padding: 10px;
  border-radius: 10px;
}

.week-text {
  font-weight: bold;
}

.week-arrow {
  cursor: pointer;
  user-select: none;
  padding: 5px;
}

.week-arrow:hover {
  color: #007bff;
}

.disabled-arrow {
  color: #ccc;
  pointer-events: none;
}

/* Zusätzliche Abstände */
.mt-4 {
  margin-top: 1.5rem !important;
}

.mb-4 {
  margin-bottom: 1.5rem !important;
}

.row.fw-bold {
  border-bottom: 1px solid #ddd;
  padding-bottom: 5px;
}
</style>
