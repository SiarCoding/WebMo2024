<template>
    <div>
      <h2>Essensplan für Woche {{ selectedWeek }}</h2>
      <select v-model="selectedWeek">
        <option v-for="week in 8" :key="week" :value="week">{{ week }}</option>
      </select>
  
      <div v-for="day in ['Mo', 'Di', 'Mi', 'Do', 'Fr']" :key="day">
        <h3>{{ day }}</h3>
        <select v-model="plan[day]">
          <option v-for="essen in essenList" :key="essen.id" :value="essen.id">{{ essen.name }}</option>
        </select>
      </div>
  
      <button @click="savePlan">Plan speichern</button>
    </div>
  </template>
  
  <script>
  export default {
    data() {
      return {
        selectedWeek: 1,
        essenList: [],
        plan: {
          Mo: null,
          Di: null,
          Mi: null,
          Do: null,
          Fr: null,
        },
      };
    },
    methods: {
      loadEssen() {
        // Essen vom Server laden
      },
      loadPlan() {
        // Plan für die ausgewählte Woche vom Server laden
      },
      savePlan() {
        // Plan auf dem Server speichern
      },
    },
    watch: {
      selectedWeek() {
        this.loadPlan();
      },
    },
    created() {
      this.loadEssen();
      this.loadPlan();
    },
  };
  </script>
  