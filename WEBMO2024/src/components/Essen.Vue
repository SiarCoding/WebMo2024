<template>
  <div class="container mt-5">
    <h2 class="title mb-4 text-center">Essen Liste</h2>
    <div class="row">
      <div class="col-md-4 mb-4" v-for="item in essenList" :key="item.id">
        <div class="card h-100">
          <div class="card-body">
            <h5 class="card-title">{{ item.name }}</h5>
            <h6 class="card-subtitle mb-2 text-muted">{{ item.preis }}€</h6>
            <p class="card-text">{{ item.beschreibung }}</p>
            <p class="badge bg-info">{{ item.art }}</p>
          </div>
          <div class="card-footer d-flex justify-content-between">
            <button @click="editEssen(item)" class="btn btn-primary">Bearbeiten</button>
            <button @click="deleteEssen(item.id)" class="btn btn-danger">Löschen</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import AddEssen from './AddEssen.vue';
import axios from 'axios';

export default {
  components: { AddEssen },
  data() {
    return {
      essenList: [],
      selectedEssen: null,
    };
  },
  methods: {
    async loadEssen() {
      try {
        const response = await axios.get('http://localhost:3001/api/essen');
        this.essenList = response.data;
      } catch (error) {
        console.error('Fehler beim Laden der Essen:', error);
      }
    },
    addEssen(neuesEssen) {
      this.essenList.push(neuesEssen);
    },
    async deleteEssen(id) {
      try {
        await axios.delete(`http://localhost:3001/api/essen/${id}`);
        this.loadEssen(); // Nach dem Löschen die Liste neu laden
      } catch (error) {
        console.error('Fehler beim Löschen des Essens:', error);
      }
    },
    async updateEssen(essen) {
      try {
        await axios.put(`http://localhost:3001/api/essen/${essen.id}`, essen);
        this.loadEssen(); // Nach dem Update die Liste neu laden
      } catch (error) {
        console.error('Fehler beim Aktualisieren des Essens:', error);
      }
    },
    editEssen(essen) {
      this.selectedEssen = { ...essen };
    },
  },
  created() {
    this.loadEssen(); // Beim Laden der Seite die Essensliste abrufen
  },
};
</script>

<style>
.container {
  max-width: 1200px;
}

.card {
  transition: transform 0.2s ease-in-out;
}

.card:hover {
  transform: scale(1.05);
}

.card-title {
  font-size: 1.25rem;
  font-weight: bold;
}

.card-subtitle {
  font-size: 1rem;
}

.card-text {
  font-size: 0.9rem;
}

.badge {
  font-size: 0.85rem;
}

.btn {
  font-size: 0.85rem;
}

h2.title {
  font-size: 2rem;
  color: #333;
  text-align: center;
  margin-bottom: 20px;
}

</style>
