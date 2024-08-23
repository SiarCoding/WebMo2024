<template>
  <div class="container mt-5">
    <h2 class="title mb-4 text-center">Essen Liste</h2>
    <button v-if="isAdmin" @click="goToAddPage" class="btn btn-success mb-3">Neues Essen hinzufügen</button>
    <div class="row">
      <div class="col-md-4 mb-4" v-for="item in essenList" :key="item.id">
        <div class="card h-100">
          <div class="card-body">
            <h5 class="card-title">{{ item.name }}</h5>
            <h6 class="card-subtitle mb-2 text-muted">{{ item.price }}€</h6>
            <p class="card-text">{{ item.beschreibung }}</p>
            <p class="badge bg-info">{{ item.type }}</p>
          </div>
          <div class="card-footer d-flex justify-content-between" v-if="isAdmin">
            <button @click="goToEditPage(item)" class="btn btn-primary">Bearbeiten</button>
            <button @click="deleteEssen(item.id)" class="btn btn-danger">Löschen</button>
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
      essenList: [],
      isAdmin: false, // Zustand für Admin-Rechte
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
    async deleteEssen(id) {
      try {
        await axios.delete(`http://localhost:3001/api/essen/${id}`);
        this.loadEssen();
      } catch (error) {
        console.error('Fehler beim Löschen des Essens:', error);
      }
    },
    goToEditPage(item) {
      this.$router.push(`/essen/edit/${item.id}`);
    },
    goToAddPage() {
      this.$router.push('/add-essen');
    },
  },
  created() {
    this.loadEssen();
    this.isAdmin = localStorage.getItem('role') === 'admin'; // Überprüfen, ob der Benutzer ein Admin ist
  },
};
</script>

<style scoped>
/* Stil bleibt gleich */
</style>

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
