<template>
  <div class="container mt-5">
    <!-- Titel mit Bootstrap Icon -->
    <h2 class="mb-4 text-center display-5">
      <i class="bi bi-calendar-check"></i>
      {{ $t('pages.view_meal_plans') }}
    </h2>

    <!-- Erfolg- und Fehlermeldungen -->
    <div v-if="errorMessage" class="alert alert-danger">{{ errorMessage }}</div>
    <div v-if="successMessage" class="alert alert-success">{{ successMessage }}</div>

    <!-- Wochenslider zum Auswählen des Essensplans -->
    <div class="d-flex justify-content-center mb-5">
      <div class="week-slider d-flex justify-content-center align-items-center p-2 rounded shadow bg-dark text-white"
        style="width: 300px;">
        <!-- Linker Pfeil -->
        <span class="week-arrow" :class="{ 'disabled-arrow': selectedWeek === 1 }" @click="changeWeek(-1)"
          style="cursor: pointer;">
          &lt;
        </span>

        <!-- Wochentext -->
        <span class="week-text mx-3">{{ $t('pages.week') }} {{ selectedWeek }}</span>

        <!-- Rechter Pfeil -->
        <span class="week-arrow" :class="{ 'disabled-arrow': selectedWeek === 8 }" @click="changeWeek(1)"
          style="cursor: pointer;">
          &gt;
        </span>
      </div>
    </div>

    <!-- Gewählter Essensplan anzeigen -->
    <div v-if="selectedPlan" class="row justify-content-center">
      <div class="col-md-10 mb-4">
        <!-- Überschrift und Erstellungsdatum, zentriert -->
        <h5 class="table-title text-center mb-4">
          {{ $t('pages.week') }} {{ selectedWeek }}
          <span class="text-muted small d-block">
            Plan erstellt am: {{ formatCreatedAt(selectedPlan.created_at) }}
          </span>
        </h5>

        <!-- Tabelle mit Plan -->
        <div class="card shadow-sm p-4 mb-4">
          <table class="table table-hover table-bordered">
            <thead class="thead-dark">
              <tr class="text-center">
                <th>{{ $t('pages.days.wochentag') }}</th>
                <th>{{ $t('pages.food_name') }}</th>
                <th>{{ $t('pages.food_type') }}</th>
                <th>{{ $t('pages.food_price') }}</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(meal, day, index) in selectedPlan.days" :key="day"
                :class="{ 'table-light': index % 2 === 0 }">
                <!-- Spalte: Wochentag -->
                <td class="text-center"><strong>{{ $t('pages.days.' + day) }}</strong></td>

                <!-- Spalte: Name -->
                <td class="text-center">
                  <span v-if="!editing[day]">{{ meal?.food_name || $t('pages.choose_food') }}</span>
                  <input v-if="editing[day]" v-model="editFields[day].name" class="form-control form-control-sm" />
                </td>

                <!-- Spalte: Typ -->
                <td class="text-center">
                  <span v-if="!editing[day]" :class="getBadgeClass(meal?.type)">
                    {{ meal?.type }}
                  </span>
                  <select v-if="editing[day]" v-model="editFields[day].type" class="form-select form-select-sm">
                    <option value="vegetarisch">Vegetarisch</option>
                    <option value="vegan">Vegan</option>
                    <option value="mit Fleisch">Mit Fleisch</option>
                  </select>
                </td>

                <!-- Spalte: Preis -->
                <td class="text-center">
                  <span v-if="!editing[day]">
                    {{ typeof meal?.price === 'number' ? meal.price.toFixed(2) : '0.00' }} €
                  </span>
                  <input v-if="editing[day]" v-model="editFields[day].price" type="number" step="0.01"
                    class="form-control form-control-sm" />
                </td>

                <!-- Bearbeiten-Button nur für Admin -->
                <td class="text-center" v-if="isAdmin">
                  <button v-if="!editing[day]" class="btn btn-sm btn-outline-primary mx-1" @click="enableEdit(day)">
                    <i class="bi bi-pencil"></i>
                  </button>
                  <button v-if="editing[day]" class="btn btn-sm btn-success mx-1" @click="saveEdit(day)">
                    <i class="bi bi-check-circle"></i>
                  </button>
                </td>
              </tr>
            </tbody>
          </table>

          <!-- Gesamtpreis anzeigen, zentriert -->
          <div class="d-flex justify-content-center align-items-center fw-bold mt-3">
            <span class="me-2">{{ $t('pages.total_price') }}:</span>
            <span class="badge bg-success text-white p-2">{{ selectedPlan.total_price.toFixed(2) }} €</span>
          </div>
        </div>

        <!-- Buttons zum Bearbeiten und Löschen des gesamten Plans unter der Tabelle - nur für Admin -->
        <div class="d-flex justify-content-center mt-4 button-group" v-if="isAdmin">
          <button class="btn btn-lg btn-primary me-3" @click="editPlan(selectedPlan)">
            <i class="bi bi-pencil-square"></i> {{ $t('pages.edit') }}
          </button>
          <button class="btn btn-lg btn-danger" @click="deletePlan(selectedPlan.plan_id)">
            <i class="bi bi-trash3"></i> {{ $t('pages.delete') }}
          </button>
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
      editing: {}, // Speichert die Bearbeitungszustände der einzelnen Tage
      editFields: {}, // Speichert die bearbeiteten Werte der Tage
      errorMessage: '',
      successMessage: '',
      currentLocale: localStorage.getItem('locale') || 'de',
      isAdmin: localStorage.getItem('role') === 'admin', // Admin-Berechtigung prüfen
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
      if (this.selectedPlan) {
        this.resetEditing();
        this.errorMessage = '';
      } else {
        this.errorMessage = `${this.$t('pages.no_plan_for_week')} ${this.selectedWeek}`;
      }
    },
    changeWeek(direction) {
      if ((this.selectedWeek === 1 && direction === -1) || (this.selectedWeek === 8 && direction === 1)) {
        return;
      }
      this.selectedWeek += direction;
      this.loadPlan();
    },
    enableEdit(day) {
      this.editing[day] = true;
      this.editFields[day] = {
        name: this.selectedPlan.days[day]?.food_name || '',
        type: this.selectedPlan.days[day]?.type || 'vegetarisch',
        price: this.selectedPlan.days[day]?.price ? this.selectedPlan.days[day].price.toFixed(2) : '0.00',
      };
    },
    async saveEdit(day) {
      try {
        const token = localStorage.getItem('token');
        const foodId = this.selectedPlan.days[day]?.food_id;

        // Verhindern, dass leere Felder gesendet werden
        if (!this.editFields[day].name || this.editFields[day].price === 0 || !this.editFields[day].type) {
          this.errorMessage = 'Alle Felder müssen ausgefüllt sein.';
          return;
        }

        if (foodId) {
          const response = await axios.put(
            `http://localhost:3001/api/essen/${foodId}`,
            {
              name: this.editFields[day].name,
              preis: parseFloat(this.editFields[day].price),
              art: this.editFields[day].type,
            },
            {
              headers: { Authorization: `Bearer ${token}` },
            }
          );

          if (response.data.success) {
            this.selectedPlan.days[day].food_name = response.data.essen.name;
            this.selectedPlan.days[day].price = parseFloat(response.data.essen.price);
            this.selectedPlan.days[day].type = response.data.essen.type;

            this.editing[day] = false;
            this.successMessage = this.$t('pages.food_updated');
            this.updateTotalPrice();

            setTimeout(() => {
              this.successMessage = '';
            }, 3000);
          } else {
            this.errorMessage = this.$t('pages.update_error');
          }
        }
      } catch (error) {
        this.errorMessage = this.$t('pages.server_error');
      }
    },
    updateTotalPrice() {
      let total = 0;
      Object.keys(this.selectedPlan.days).forEach(day => {
        if (typeof this.selectedPlan.days[day]?.price === 'number') {
          total += parseFloat(this.selectedPlan.days[day].price);
        }
      });
      this.selectedPlan.total_price = total;
    },
    formatCreatedAt(dateString) {
      const date = new Date(dateString);
      return date.toLocaleDateString('de-DE') + ' ' + date.toLocaleTimeString('de-DE');
    },
    resetEditing() {
      this.editing = {};
      this.editFields = {};
    },
    getBadgeClass(type) {
      if (type === 'vegan') return 'badge bg-lavender text-dark'; // Helles Lavendel
      if (type === 'vegetarisch') return 'badge bg-peach text-dark'; // Hellorange
      if (type === 'mit Fleisch') return 'badge bg-lightpink text-white'; // Hellrosa
      return 'badge bg-secondary text-white'; // Default-Farbe
    },
    async editPlan(plan) {
      this.$router.push({ name: 'EditEssensplan', params: { week: plan.wochennummer } });
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
          headers: { Authorization: `Bearer ${token}` },
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
  },
  created() {
    this.loadPlans();
  },
};
</script>

<style scoped>
.table-title {
  font-size: 1.5rem;
  font-weight: bold;
}

.card {
  background-color: #f9f9f9;
  border-radius: 10px;
  padding: 20px;
}

.table th,
.table td {
  vertical-align: middle;
  text-align: center;
}

.table-light {
  background-color: #f0f0f0;
}

.badge {
  font-size: 1rem;
}

.bg-lavender {
  background-color: #E6E6FA;
}

.bg-peach {
  background-color: #FFDAB9;
}

.bg-lightpink {
  background-color: #FFB6C1;
}

.week-slider {
  font-size: 1.2rem;
  border-radius: 10px;
}

.button-group {
  gap: 15px;
}

.btn-lg {
  padding: 10px 20px;
  font-size: 1.2rem;
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 150px;
}

.btn-primary {
  background-color: #007bff;
  border-color: #007bff;
  color: white;
}

.btn-danger {
  background-color: #dc3545;
  border-color: #dc3545;
  color: white;
}

.btn i {
  margin-right: 10px;
}
</style>
