<!--Hier kommt die Essensplan Erstellen Seite (/essensplan). Verwendet wird hier kein Bootstrap für das Design. Im Templete kommt der HTML Code Abschnitt!
Den <script> nutzen wir für Syntax!!!-->
<template>
  <div class="essen-plan-container">
    <h2>Essensplan für Woche {{ selectedWeek }}</h2> <!-- gewählte woche wird angezeigt -->
    
    <div class="week-selector">
      <label for="week-select">Woche auswählen:</label> <!-- woche über dropdown auswählen -->
      <select id="week-select" v-model="selectedWeek">
        <option v-for="week in 8" :key="week" :value="week">Woche {{ week }}</option>
      </select>
    </div>

    <div class="days-container">
      <div class="day" v-for="day in ['Mo', 'Di', 'Mi', 'Do', 'Fr']" :key="day"> <!-- Wochentage -->
        <h3>{{ day }}</h3>
        <select v-model="plan[day]">
          <option v-for="essen in essenList" :key="essen.id" :value="essen.id">{{ essen.name }}</option>
        </select>
      </div>
    </div>

    <button class="save-button" @click="savePlan">Plan speichern</button> <!-- Button fürs Speichern des Planes -->
  </div>
</template>

<script>
export default {
  data() { // daten definition per default 
    return {
      selectedWeek: 1, //woche 1 per default gewählt
      essenList: [],
      plan: {
        Mo: null, // Die Wochentage enthalten kein Essen per default und sind erstmal als NULL definiert
        Di: null,
        Mi: null,
        Do: null,
        Fr: null,
      },
    };
  },
  methods: {
    loadEssen() {
      // Essen vom Server laden (NOCH NICHT ANGEFANGEN)
    },
    loadPlan() {
      // Plan für die ausgewählte Woche vom Server laden (NOCH NICHT ANGEFANGEN)
    },
    savePlan() {
      // Plan auf dem Server speichern (NOCH NICHT ANGEFANGEN)
    },
  },
  watch: {
    selectedWeek() {
      this.loadPlan(); //hier schauen wir für die Ausgewählte Woche die Essenseinträge 
    },
  },
  created() {
    this.loadEssen();
    this.loadPlan();
  },
};
</script>

<style scoped>
.essen-plan-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

h2 {
  text-align: center;
  margin-bottom: 20px;
  color: #333;
}

.week-selector {
  margin-bottom: 20px;
  text-align: center;
}

#week-select {
  padding: 8px;
  border-radius: 4px;
  border: 1px solid #ccc;
  margin-top: 10px;
}

.days-container {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.day {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.day h3 {
  margin-bottom: 5px;
  color: #555;
}

.day select {
  padding: 8px;
  border-radius: 4px;
  border: 1px solid #ccc;
}

.save-button {
  display: block;
  width: 100%;
  padding: 10px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-top: 20px;
  font-size: 16px;
}

.save-button:hover {
  background-color: #0056b3;
}
</style>
