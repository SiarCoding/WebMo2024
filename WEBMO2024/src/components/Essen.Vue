<template>
    <div>
      <h2>Essen Liste</h2>
      <ul>
        <li v-for="item in essenList" :key="item.id">
          {{ item.name }} - {{ item.preis }}€ - {{ item.art }}
          <button @click="editEssen(item)">Bearbeiten</button>
          <button @click="deleteEssen(item.id)">Löschen</button>
        </li>
      </ul>
      <AddEssen v-if="selectedEssen" :essen="selectedEssen" :isEdit="true" @save="updateEssen"/>
    </div>
  </template>
  
  <script>
  import AddEssen from './AddEssen.vue';
  
  export default {
    components: { AddEssen },
    data() {
      return {
        essenList: [],
        selectedEssen: null,
      };
    },
    methods: {
      loadEssen() {
        // Daten vom Server laden
      },
      editEssen(essen) {
        this.selectedEssen = { ...essen };
      },
      deleteEssen(id) {
        // Löschen auf dem Server
      },
      updateEssen(essen) {
        // Update auf dem Server
      },
    },
    created() {
      this.loadEssen();
    },
  };
  </script>
  