<template>
  <div class="container mt-5">
    <!-- Titel mit Bootstrap Icon -->
    <h2 class="mb-4 text-center display-5">
      <i class="bi bi-calendar-check"></i> <!-- Bootstrap Icon für Pläne -->
      {{ $t('pages.view_meal_plans') }}
    </h2>

    <!-- Erfolg- und Fehlermeldungen -->
    <div v-if="errorMessage" class="alert alert-danger">{{ errorMessage }}</div>
    <div v-if="successMessage" class="alert alert-success">{{ successMessage }}</div>

    <!-- Wochenslider zum Auswählen des Essensplans -->
    <div class="d-flex justify-content-center mb-5">
      <div class="week-slider d-flex justify-content-center align-items-center p-2 rounded shadow bg-dark text-white" style="width: 300px;">
        <!-- Linker Pfeil -->
        <span
          class="week-arrow"
          :class="{ 'disabled-arrow': selectedWeek === 1 }"
          @click="changeWeek(-1)"
          style="cursor: pointer;"
        >
          &lt;
        </span>

        <!-- Wochentext -->
        <span class="week-text mx-3">{{ $t('pages.week') }} {{ selectedWeek }}</span>

        <!-- Rechter Pfeil -->
        <span
          class="week-arrow"
          :class="{ 'disabled-arrow': selectedWeek === 8 }"
          @click="changeWeek(1)"
          style="cursor: pointer;"
        >
          &gt;
        </span>
      </div>
    </div>

    <!-- Gewählter Essensplan anzeigen -->
    <div v-if="selectedPlan" class="row justify-content-center">
      <div class="col-md-8 mb-4">
        <div class="card shadow-lg border-0">
          <div class="card-body">
            <h5 class="card-title text-center">{{ $t('pages.week') }} {{ selectedWeek }}</h5>

            <!-- Spaltenüberschriften -->
            <div class="row mb-2 fw-bold text-center">
              <div class="col-3">{{ $t('pages.days.wochentag') }}</div>
              <div class="col-3">{{ $t('pages.food_name') }}</div>
              <div class="col-3">{{ $t('pages.food_type') }}</div>
              <div class="col-3">{{ $t('pages.food_price') }}</div>
            </div>

            <ul class="list-group list-group-flush mb-3">
              <li v-for="(meal, day, index) in selectedPlan.days" :key="day" class="list-group-item d-flex justify-content-between align-items-center" :class="{ 'bg-light': index % 2 === 0 }">
                <div class="col-3 text-center"><strong>{{ $t('pages.days.' + day) }}</strong></div>
                <div class="col-3 text-center">
                  <span v-if="meal">{{ meal.food_name }}</span>
                  <span v-else>{{ $t('pages.choose_food') }}</span>
                </div>
                <div class="col-3 text-center">
                  <span v-if="meal" class="badge bg-info text-dark">{{ $t('pages.' + translateType(meal.type)) }}</span>
                </div>
                <div class="col-3 text-center">
                  <span v-if="meal">{{ meal.price.toFixed(2) }} €</span>
                </div>
              </li>
            </ul>

            <!-- Gesamtpreis anzeigen in der Mitte -->
            <div class="d-flex justify-content-center align-items-center fw-bold mb-3">
              <span class="me-2">{{ $t('pages.total_price') }}:</span>
              <span class="badge bg-success text-white p-2">{{ selectedPlan.total_price.toFixed(2) }} €</span>
            </div>

            <!-- Buttons zum Bearbeiten und Löschen nur für Admins -->
            <div class="d-flex justify-content-between mt-3" v-if="isAdmin">
              <button class="btn btn-primary" @click="editPlan(selectedPlan)">{{ $t('pages.edit') }}</button>
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
      isAdmin: localStorage.getItem('role') === 'admin',
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
        const token = localStorage.getItem('token');
        if (!token) {
          alert('Token fehlt. Bitte melden Sie sich erneut an.');
          this.$router.push('/login');
          return;
        }

        const response = await axios.delete(`http://localhost:3001/api/essensplan/${planId}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (response.data.success) {
          this.successMessage = this.$t('pages.plan_deleted');
          this.errorMessage = '';
          this.loadPlans();
        } else {
          alert(response.data.message);
        }
      } catch (error) {
        this.errorMessage = this.$t('pages.server_error');
      }
    },
    editPlan(plan) {
      if (this.isAdmin) {
        this.$router.push({ name: 'EditEssensplan', params: { week: plan.wochennummer } });
      }
    },
    translateType(type) {
      switch (type) {
        case 'mit Fleisch':
          return 'meat';
        case 'vegetarisch':
          return 'vegetarian';
        case 'vegan':
          return 'vegan';
        default:
          return type;
      }
    }
  },
  created() {
    this.loadPlans();
  }
};
</script>

<style scoped>
.card {
  background-color: #ffe6f0;
  border-radius: 10px;
}

.week-slider {
  background-color: #000;
  color: #fff;
  font-size: 1.2rem;
  padding: 10px;
  border-radius: 10px;
}

.list-group-item {
  display: flex;
  justify-content: space-between;
  padding: 0.75rem 1.25rem;
  text-align: center;
}

.text-center {
  text-align: center;
}

.text-end {
  text-align: right;
}

/* Added space below the week-slider */
.mb-5 {
  margin-bottom: 3rem; /* Increased margin-bottom */
}
</style>
