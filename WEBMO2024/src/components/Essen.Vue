<template>
  <div class="essen-container">
    <!-- AddEssen-Komponente wird eingebunden -->
    <AddEssen @essenAdded="addEssen" />
    <h2 class="title">Essen Liste</h2>
    <ul class="essen-list">
      <li v-for="item in essenList" :key="item.id" class="essen-item">
        <div class="essen-details">
          <span class="essen-name">{{ item.name }}</span>
          <span class="essen-price">{{ item.preis }}€</span>
          <span class="essen-description">{{ item.beschreibung }}</span>
        </div>
        <div class="button-group">
          <button @click="editEssen(item)" class="edit-button">Bearbeiten</button>
          <button @click="deleteEssen(item.id)" class="delete-button">Löschen</button>
        </div>
      </li>
    </ul>
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
        const response = await axios.get('http://localhost:3000/api/essen');
        this.essenList = response.data;
      } catch (error) {
        console.error('Fehler beim Laden der Essen:', error);
      }
    },
    editEssen(essen) {
      this.selectedEssen = { ...essen };
    },
    async deleteEssen(id) {
      try {
        await axios.delete(`http://localhost:3000/api/essen/${id}`);
        this.loadEssen(); // Nach dem Löschen die Liste neu laden
      } catch (error) {
        console.error('Fehler beim Löschen des Essens:', error);
      }
    },
    async updateEssen(essen) {
      try {
        await axios.put(`http://localhost:3000/api/essen/${essen.id}`, essen);
        this.loadEssen(); // Nach dem Update die Liste neu laden
      } catch (error) {
        console.error('Fehler beim Aktualisieren des Essens:', error);
      }
    },
    addEssen(neuesEssen) {
      console.log("Neues Essen hinzugefügt", neuesEssen);
      this.essenList.push(neuesEssen);
    }
  },
  created() {
    this.loadEssen(); // Beim Laden der Seite die Essensliste abrufen
  },
};
</script>

<style scoped>
.essen-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.title {
  font-size: 26px;
  margin-bottom: 20px;
  color: #2c3e50;
  border-bottom: 2px solid #3498db;
  padding-bottom: 10px;
}

.essen-list {
  list-style: none;
  padding: 0;
}

.essen-item {
  background: #f4f4f4;
  padding: 15px;
  margin-bottom: 20px; /* Abstand zwischen den Listenelementen */
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column; /* Elemente untereinander anordnen */
}

.essen-details {
  margin-bottom: 15px; /* Abstand zwischen den Details und den Buttons */
}

.essen-name {
  display: block;
  font-weight: bold;
  color: #34495e;
  margin-bottom: 5px; /* Abstand zwischen Name und Preis */
}

.essen-price {
  display: block;
  color: #e67e22;
  margin-bottom: 5px; /* Abstand zwischen Preis und Beschreibung */
}

.essen-description {
  display: block;
  color: #7f8c8d;
}

.button-group {
  display: flex;
  gap: 10px;
}

.edit-button,
.delete-button {
  border: none;
  padding: 10px 15px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  color: #fff;
  transition: background-color 0.3s;
}

.edit-button {
  background-color: #2ecc71;
}

.edit-button:hover {
  background-color: #27ae60;
}

.delete-button {
  background-color: #e74c3c;
}

.delete-button:hover {
  background-color: #c0392b;
}
</style>