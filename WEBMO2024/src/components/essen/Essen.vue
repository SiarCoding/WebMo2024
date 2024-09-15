<template>
  <div class="container mt-5">
    <!-- Titel mit Bootstrap Icon -->
    <h2 class="mb-4 text-center display-5">
      <i class="bi bi-basket"></i>  
      {{ $t('pages.essen_list') }}
    </h2>

    <div class="row">
      <div class="col-md-4 mb-4" v-for="item in essenList" :key="item.id">
        <div class="card h-100 shadow-sm bg-light">
          <div class="card-body">
            <h5 class="card-title">{{ item.name }}</h5>
            <h6 class="card-subtitle mb-2 text-muted">
              {{ $t('pages.food_price') }}: {{ item.price }}€
            </h6>
            <p class="card-text">{{ item.beschreibung }}</p>
            <p :class="getBadgeClass(item.type)">
              {{ $t('pages.' + translateType(item.type)) }}
            </p>
          </div>
          <div class="card-footer d-flex justify-content-between" v-if="isAdmin">
            <button @click="goToEditPage(item)" class="btn btn-primary">
              {{ $t('pages.edit') }}
            </button>
            <button @click="deleteEssen(item.id)" class="btn btn-danger">
              {{ $t('pages.delete') }}
            </button>
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
  methods: { //essen wird geladen
    async loadEssen() {
      try {
        const { data } = await axios.get('http://localhost:3001/api/essen');
        this.essenList = data;
      } catch (error) {
        console.error('Fehler beim Laden der Essen:', error);
      }
    },
    async deleteEssen(id) { //Essen wird gelöscht 
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          alert('Token fehlt. Bitte melden Sie sich erneut an.');
          this.$router.push('/login');
          return;
        }

        const { data } = await axios.delete(`http://localhost:3001/api/essen/${id}`, {
          headers: { Authorization: `Bearer ${token}` },
        });

        if (data.success) {
          this.loadEssen();
        } else {
          alert(data.message);
        }
      } catch (error) {
        console.error('Fehler beim Löschen des Essens:', error);
        alert('Fehler beim Löschen des Essens.');
      }
    },
    goToEditPage(item) {
      if (this.isAdmin) {
        this.$router.push(`/essen/edit/${item.id}`);
      }
    },
    translateType(type) {
      const types = {
        'mit Fleisch': 'meat',
        'vegetarisch': 'vegetarian',
        'vegan': 'vegan',
      };
      return types[type] || type;
    },
    getBadgeClass(type) {
      if (type === 'vegan') return 'badge bg-lavender text-dark';
      if (type === 'vegetarisch') return 'badge bg-peach text-dark';
      if (type === 'mit Fleisch') return 'badge bg-lightpink text-white';
      return 'badge bg-secondary text-white';
    },
  },
  created() {
    this.loadEssen();
  },
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap');

.container {
  max-width: 1200px;
}

.card {
  background-color: #f9f9f9;
  transition: transform 0.2s ease-in-out;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-radius: 10px;
}

.card:hover {
  transform: scale(1.05);
}

.card-title, .card-subtitle, .card-text {
  font-family: 'Poppins', sans-serif;
}

.card-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: #343a40;
}

.card-subtitle {
  font-size: 1rem;
  font-weight: 400;
  color: #6c757d;
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

.bg-secondary {
  background-color: #6c757d;
  color: white;
}
</style>
