<template>
  <div class="container mt-5">
    <h2 class="mb-4 text-center display-5">{{ $t('pages.essen_list') }}</h2>

    <div class="row">
      <div class="col-md-4 mb-4" v-for="item in essenList" :key="item.id">
        <div class="card h-100 shadow-sm bg-light">
          <div class="card-body">
            <h5 class="card-title">{{ item.name }}</h5>
            <h6 class="card-subtitle mb-2 text-muted">{{ $t('pages.food_price') }}: {{ item.price }}€</h6>
            <p class="card-text">{{ item.beschreibung }}</p>
            <p class="badge bg-info">{{ $t('pages.' + translateType(item.type)) }}</p>
          </div>
          <div class="card-footer d-flex justify-content-between" v-if="isAdmin">
            <button @click="goToEditPage(item)" class="btn btn-primary">{{ $t('pages.edit') }}</button>
            <button @click="deleteEssen(item.id)" class="btn btn-danger">{{ $t('pages.delete') }}</button>
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
        const response = await axios.get('http://localhost:3001/api/essen');
        this.essenList = response.data;
      } catch (error) {
        console.error('Fehler beim Laden der Essen:', error);
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
          this.loadEssen();
        } else {
          alert(response.data.message);
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
    this.loadEssen();
  },
};
</script>

<style scoped>
/* Importiere Google-Schriftart Poppins */
@import url('https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap');

.container {
  max-width: 1200px;
}

.title {
  font-family: 'Poppins', sans-serif;
  font-size: 2.5rem;
  font-weight: 600;
  color: #343a40;
  margin-bottom: 30px;
  text-transform: uppercase;
  letter-spacing: 1.5px;
  text-shadow: 2px 2px 5px rgba(0, 0, 0, 0.1);
}

.card {
  background-color: #cf93d7;
  transition: transform 0.2s ease-in-out;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-radius: 10px;
}

.card:hover {
  transform: scale(1.05);
}

h5.card-title {
  font-family: 'Poppins', sans-serif;
  font-size: 1.25rem;
  font-weight: 600;
  color: #343a40;
}

h6.card-subtitle {
  font-family: 'Poppins', sans-serif;
  font-size: 1rem;
  font-weight: 400;
  color: #6c757d;
}

p.card-text {
  font-family: 'Poppins', sans-serif;
  font-size: 1rem;
  color: #495057;
}
</style>
