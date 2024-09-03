<template>
  <div class="container mt-5">
    <h2 class="title mb-4 text-center">Essen Liste</h2>
    
    <div class="d-flex justify-content-between mb-3">
      <button v-if="isAdmin" @click="goToAddPage" class="btn btn-success">Neues Essen hinzufügen</button>
    </div>

    <div class="row">
      <div class="col-md-4 mb-4" v-for="item in essenList" :key="item.id">
        <div class="card h-100">
          <div class="card-body">
            <h5 class="card-title">{{ item.name }}</h5>
            <h6 class="card-subtitle mb-2 text-muted">{{ item.price }}€</h6>
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
      isAdmin: localStorage.getItem('role') === 'admin',
    };
  },
  methods: {
    async loadEssen() {
      try {
        console.log('Lade Daten von Backend...');
        const response = await axios.get('http://localhost:3001/api/essen');
        console.log('Daten von Backend erhalten:', response.data);
        this.essenList = response.data;
      } catch (error) {
        console.error('Fehler beim Laden der Essen:', error);
        if (error.response) {
          console.error('Fehlerdaten:', error.response.data);
        }
      }
    },
    async deleteEssen(id) {
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          alert('Token fehlt. Bitte melden Sie sich erneut an.');
          this.$router.push('/login');
          return;
        }
        
        const response = await axios.delete(`http://localhost:3001/api/essen/${id}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (response.data.success) {
          this.message = 'Essen erfolgreich gelöscht!';
          this.loadEssen();
        } else {
          this.message = response.data.message;
        }

      } catch (error) {
        console.error('Fehler beim Löschen des Essens:', error);
        this.message = 'Fehler beim Löschen des Essens';
      }
    },
    goToEditPage(item) {
      if (this.isAdmin) {
        this.$router.push(`/essen/edit/${item.id}`);
      }
    },
    goToAddPage() {
      this.$router.push('/add-essen');
    },
  },
  created() {
    this.loadEssen();
  },
};
</script>

<style scoped>
.container {
  max-width: 1200px;
}

.card {
  transition: transform 0.2s ease-in-out;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
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

.badge {
  font-size: 0.85rem;
}

.btn {
  font-size: 0.85rem;
  margin-right: 5px;
}

h2.title {
  font-size: 2rem;
  color: #333;
  text-align: center;
  margin-bottom: 20px;
}

.alert {
  margin-top: 10px;
}
</style>
