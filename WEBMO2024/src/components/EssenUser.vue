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
  },
  created() {
    this.loadEssen();
  },
};
</script>




<!-- hier können wir nun bisschen css schreiben und das template designen. Andernfalls können wir auch hier Bootstrap verwenden im Templete und den Style Teil sparen!-->
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