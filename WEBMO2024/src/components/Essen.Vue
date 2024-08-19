<!--Das Anzeigen der Esseneinträge als Liste findet hier statt (/essen). Verwendet wird hier kein Bootstrap für das Design sondern übers style. Im Templete kommt der HTML Code Abschnitt!
Den <script> nutzen wir für Syntax!!!-->
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
            <button @click="goToEditPage(item)" class="btn btn-primary">Bearbeiten</button> <!--Button fürs Bearbeiten-->
            <button @click="deleteEssen(item.id)" class="btn btn-danger">Löschen</button> <!--Button fürs Löschen-->
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'; // Zuerst wird die axios-Bibliothek importiert, die für HTTP-Anfragen genutzt wird um daten zu senden an den server


export default {//wir implementieren daten für das essen die relevant sind für die Liste
  data() {
    return {
      essenList: [], //in dem fall soll er einfach die Essensliste als Array zurückgeben von Server
    };
  },
  methods: { //methoden kommen jetzt hier 
    async loadEssen() { //Funktion für essen laden
      try {
        const response = await axios.get('http://localhost:3001/api/essen'); //GET Request senden an /api/essen 
        this.essenList = response.data; //ESSENS LISTE WIEDERGEBEN!
      } catch (error) {
        console.error('Fehler beim Laden der Essen:', error); // Error catchen
      }
    },
    async deleteEssen(id) { // Funktion fürs Löschen des Esseneintrages basierend auf die ID
      try {
        await axios.delete(`http://localhost:3001/api/essen/${id}`); // DELETE-Request senden an den jeweiligen ID
        this.loadEssen(); // Nach dem Löschen die Liste neu laden
      } catch (error) {
        console.error('Fehler beim Löschen des Essens:', error); // ERROR CATCHEN
      }
    },
    goToEditPage(item) { // hier gelangen wir direkt auf die EDIT SEITE !! Das heißt beim Klicken des Buttons gelangt man auf /edit-essen
      this.$router.push(`/essen/edit/${item.id}`);
    },
  },
  created() {
    this.loadEssen(); // Beim Laden der Seite die Essensliste abrufen
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