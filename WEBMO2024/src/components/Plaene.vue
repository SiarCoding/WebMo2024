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
      <div class="week-slider d-flex justify-content-center align-items-center p-2 rounded shadow bg-dark text-white" style="width: 300px;">
        <!-- Linker Pfeil -->
        <span class="week-arrow" :class="{ 'disabled-arrow': selectedWeek === 1 }" @click="changeWeek(-1)" style="cursor: pointer;">
          &lt;
        </span>
        <!-- Ändert die ausgewählte Woche um 1 zurück wenn der Benutzer darauf (<) klickt -->

        <!-- Wochentext -->
        <span class="week-text mx-3">{{ $t('pages.week') }} {{ selectedWeek }}</span>
        <!-- Zeigt die aktuelle ausgewählte Woche an -->

        <!-- Rechter Pfeil -->
        <span class="week-arrow" :class="{ 'disabled-arrow': selectedWeek === 8 }" @click="changeWeek(1)" style="cursor: pointer;">
          &gt;
        </span>
        <!-- Ändert die ausgewählte Woche um 1 vorwärts wenn der Benutzer darauf (>) klickt -->
      </div>
    </div>

    <!-- Gewählter Essensplan anzeigen -->
    <div v-if="selectedPlan" class="row justify-content-center">
      <div class="col-md-10 mb-4">
        <!--  Erstellungsdatum -->
        <h5 class="table-title text-center mb-5">
          {{ $t('pages.week') }} {{ selectedWeek }}
          <span class="text-muted small d-block" style="font-size: 0.9rem;">
            {{ $t('pages.plan_created_at') }}: {{ formatCreatedAt(selectedPlan.created_at) }}
          </span>
        </h5>

        <!-- Tabelle mit Essenseinträgen pro Woche -->
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
              <tr v-for="(meal, day, index) in selectedPlan.days" :key="day" :class="{ 'table-light': index % 2 === 0 }">
                <td class="text-center"><strong>{{ $t('pages.days.' + day) }}</strong></td>
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
                    <option value="vegetarisch">{{ $t('pages.vegetarian') }}</option>
                    <option value="vegan">{{ $t('pages.vegan') }}</option>
                    <option value="mit Fleisch">{{ $t('pages.meat') }}</option>
                  </select>
                </td>

                <!-- Spalte: Preis -->
                <td class="text-center">
                  <span v-if="!editing[day]">
                    {{ typeof meal?.price === 'number' ? meal.price.toFixed(2) : '0.00' }} €
                  </span>
                  <input v-if="editing[day]" v-model="editFields[day].price" type="number" step="0.01" class="form-control form-control-sm" />
                </td>

                <!-- Bearbeiten-Button nur für Admin -->
                <td class="text-center" v-if="isAdmin">
                  <button v-if="!editing[day]" class="btn btn-sm btn-outline-primary mx-1" @click="enableEdit(day)">
                    <i class="bi bi-pencil"></i>
                  </button>
                  <!-- Button zum Starten der Bearbeitung eines Tages -->
                  <button v-if="editing[day]" class="btn btn-sm btn-success mx-1" @click="saveEdit(day)">
                    <i class="bi bi-check-circle"></i>
                  </button>
                </td>
              </tr>
            </tbody>
          </table>

          <!-- Gesamtpreis anzeigen -->
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
      essensplaene: [], // Liste der Essenspläne
      selectedWeek: 1, // Die aktuell ausgewählte Woche (standardmäßig Woche 1)
      selectedPlan: null, // Der ausgewählte Essensplan für die aktuelle Woche
      editing: {}, // Speichert die Bearbeitungszustände der einzelnen Tage
      editFields: {}, // Speichert die bearbeiteten Werte der Tage
      errorMessage: '', // Speichert Fehlermeldungen
      successMessage: '', // Speichert Erfolgsnachrichten
      currentLocale: localStorage.getItem('locale') || 'de', // Die aktuell ausgewählte Sprache (standardmäßig Deutsch)
      isAdmin: localStorage.getItem('role') === 'admin', // Überprüft, ob der Benutzer ein Admin ist
    };
  },
  methods: {
    // Lädt die Essenspläne von server und speichert sie in essensplaene
    async loadPlans() {
      try {
        const response = await axios.get('http://localhost:3001/api/essensplan'); // Holt alle Essenspläne
        this.essensplaene = response.data; // Speichert die Essenspläne in der Komponente
        this.loadPlan(); // Lädt den ausgewählten Plan
      } catch (error) {
        this.errorMessage = this.$t('pages.server_error'); // Zeigt eine Fehlermeldung an wenn die API-Anfrage fehlschlägt
      }
    },

    // Lädt den Essensplan der ausgewählten Woche
    loadPlan() {
      this.selectedPlan = this.essensplaene.find(plan => plan.wochennummer == this.selectedWeek); // Sucht den Plan der ausgewählten Woche
      if (this.selectedPlan) {
        this.resetEditing(); // Setzt die Bearbeitungsfelder zurück
        this.errorMessage = ''; // Löscht die Fehlermeldung (falls vorhanden)
      } else {
        this.errorMessage = `${this.$t('pages.no_plan_for_week')} ${this.selectedWeek}`; // Zeigt eine Fehlermeldung an wenn kein Plan gefunden wurde
      }
    },

    // Ändert die Woche um eine Woche vorwärts oder rückwärts
    changeWeek(direction) {
      if ((this.selectedWeek === 1 && direction === -1) || (this.selectedWeek === 8 && direction === 1)) {
        return; // Verhindert das die Woche unter 1 oder über 8 geht
      }
      this.selectedWeek += direction; // Erhöht oder verringert die ausgewählte Woche
      this.loadPlan(); // Lädt den Plan der neuen Woche
    },

    // Aktiviert die Bearbeitung eines Eintrags
    enableEdit(day) {
      this.editing[day] = true; // Setzt den Bearbeitungszustand des wochentages auf "aktiv"
      this.editFields[day] = {
        name: this.selectedPlan.days[day]?.food_name || '', // Setzt den aktuellen Namen des Essens
        type: this.selectedPlan.days[day]?.type || 'vegetarisch', // Setzt den aktuellen Typ des Essens
        price: this.selectedPlan.days[day]?.price ? this.selectedPlan.days[day].price.toFixed(2) : '0.00', // Setzt den aktuellen Preis des Essens
      };
    },

    // Speichert die Änderungen an den Datensatz im Essensplan
    async saveEdit(day) {
      try {
        const token = localStorage.getItem('token'); // Holt den Token aus dem lokalen Speicher
        const foodId = this.selectedPlan.days[day]?.food_id; // Holt die ID des Essens

        if (!this.editFields[day].name || this.editFields[day].price === 0 || !this.editFields[day].type) {
          this.errorMessage = 'Alle Felder müssen ausgefüllt sein.'; // Fehlerbehandlung bei leeren Feldern
          return;
        }

        if (foodId) {
          const response = await axios.put(
            `http://localhost:3001/api/essen/${foodId}`, // Request zum Speichern der Änderungen
            {
              name: this.editFields[day].name,
              preis: parseFloat(this.editFields[day].price),
              art: this.editFields[day].type,
            },
            {
              headers: { Authorization: `Bearer ${token}` }, // Füge den Token für die Authentifizierung hinzu
            }
          );
          // Aktualisiert den Essensplan mit den neuen Werten
          if (response.data.success) {
            this.selectedPlan.days[day].food_name = response.data.essen.name;
            this.selectedPlan.days[day].price = parseFloat(response.data.essen.price);
            this.selectedPlan.days[day].type = response.data.essen.type;

            this.editing[day] = false; // Beendet den Bearbeitungsmodus für den Tag
            this.successMessage = this.$t('pages.food_updated'); // Zeigt eine Erfolgsnachricht an
            this.updateTotalPrice(); // Aktualisiert den Gesamtpreis des Plans

            setTimeout(() => {
              this.successMessage = ''; // Löscht die Erfolgsnachricht nach 3 Sekunden: (Vermeidung dauerhaft angezeigter Meldung)
            }, 3000);
          } else {
            this.errorMessage = this.$t('pages.update_error'); // Zeigt eine Fehlermeldung an wenn das Update fehlschlägt
          }
        }
      } catch (error) {
        this.errorMessage = this.$t('pages.server_error'); // Zeigt eine Fehlermeldung bei Serverfehlern an
      }
    },

    // Aktualisiert den Gesamtpreis des Essensplans
    updateTotalPrice() {
      let total = 0;
      Object.keys(this.selectedPlan.days).forEach(day => {
        if (typeof this.selectedPlan.days[day]?.price === 'number') {
          total += parseFloat(this.selectedPlan.days[day].price); // Preisaddierung
        }
      });
      this.selectedPlan.total_price = total; // Gesamtpreis
    },

    // Erstellungsdatum formatieren
    formatCreatedAt(dateString) {
      const date = new Date(dateString);
      return date.toLocaleDateString('de-DE') + ' ' + date.toLocaleTimeString('de-DE'); // Gibt das Datum und die Uhrzeit in deutscher Formatierung zurück
    },

    // Setzt den Bearbeitungsmodus und die Felder zurück
    resetEditing() {
      this.editing = {}; // Setzt alle Bearbeitungszustände zurück
      this.editFields = {}; // Setzt alle Bearbeitungsfelder zurück
    },

    // Typen mit Farben unterscheiden
    getBadgeClass(type) {
      if (type === 'vegan') return 'badge bg-lavender text-dark'; 
      if (type === 'vegetarisch') return 'badge bg-peach text-dark'; 
      if (type === 'mit Fleisch') return 'badge bg-lightpink text-white';
      return 'badge bg-secondary text-white';
    },

    // Leitet den Benutzer zur Bearbeitungsseite des Essensplans
    async editPlan(plan) {
      this.$router.push({ name: 'EditEssensplan', params: { week: plan.wochennummer } }); 
    },

    // Löscht den gesamten Essensplan
    async deletePlan(planId) {
      try {
        const token = localStorage.getItem('token'); // Holt den Token aus dem lokalen Speicher
        if (!token) {
          alert('Token fehlt. Bitte melden Sie sich erneut an.'); // Zeigt eine Fehlermeldung, wenn kein Token vorhanden ist
          this.$router.push('/login'); // Leitet zur Login-Seite weiter
          return;
        }

        const response = await axios.delete(`http://localhost:3001/api/essensplan/${planId}`, {
          headers: { Authorization: `Bearer ${token}` }, // Füge den Token zur Anfrage hinzu
        });

        if (response.data.success) {
          this.successMessage = this.$t('pages.plan_deleted'); // Zeigt eine Erfolgsnachricht an, wenn der Plan erfolgreich gelöscht wurde
          this.errorMessage = '';
          this.loadPlans(); // Lädt die verbleibenden Pläne neu
        } else {
          alert(response.data.message); // Zeigt eine Fehlermeldung, wenn das Löschen fehlschlägt
        }
      } catch (error) {
        this.errorMessage = this.$t('pages.server_error'); // Zeigt eine Fehlermeldung bei Serverfehlern an
      }
    },
  },
  // Wird beim Erstellen der Komponente aufgerufen und lädt die Pläne
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
  /* Zentrierung der Inhalte der Tabelle */
}

.table-light {
  background-color: #f0f0f0;
  /* Leichtes Grau für ungerade Zeilen der Tabelle */
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
