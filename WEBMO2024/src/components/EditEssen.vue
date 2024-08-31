<template>
  <div class="container mt-5">
    <h2 class="mb-4 text-center">🍽️ Essen bearbeiten</h2>
    <form @submit.prevent="updateEssen" class="card p-4 shadow-sm" style="background-color: #e3f2fd;">
      <div class="mb-3">
        <label for="name" class="form-label">Name:</label>
        <input type="text" v-model="essen.name" class="form-control" id="name" required />
      </div>

      <div class="mb-3">
        <label for="preis" class="form-label">Preis:</label>
        <div class="input-group">
          <input type="number" step="0.01" v-model="essen.preis" class="form-control" id="preis" required />
          <span class="input-group-text">€</span>
        </div>
      </div>

      <div class="mb-4">
        <label for="art" class="form-label">Art:</label>
        <select v-model="essen.art" class="form-select" id="art" required>
          <option disabled value="">Bitte wählen</option>
          <option value="vegetarisch">Vegetarisch</option>
          <option value="vegan">Vegan</option>
          <option value="mit Fleisch">Mit Fleisch</option>
        </select>
      </div>

      <button type="submit" class="btn btn-primary w-100">Speichern</button>
    </form>
    <p v-if="message" class="mt-3 text-success text-center">{{ message }}</p>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
      return {
          essen: {
              name: '',
              preis: 0,
              art: ''
          },
          message: ''
      };
  },

   
async created() {
   // Überprüfen, ob der Benutzer authentifiziert ist
   if (!this.isAuthenticated()) {
       this.$router.push('/login');
       return;
   }

   const id = this.$route.params.id;
   try {
    const response = await axios.get(`http://localhost:3001/api/essen/${id}`); // Korrekte Verwendung von Backticks
    // <-- Hier wurde auch die URL korrigiert
       this.essen = response.data;
   } catch (error) {
       console.error('Fehler beim Laden des Essens:', error);
       this.message = 'Fehler beim Laden der Daten.';
   }
}, // <-- Das Komma hinzugefügt

  
  
  methods: {
      isAuthenticated() {
          // Überprüfe hier die Authentifizierung (z.B. Token im Local Storage)
          const token = localStorage.getItem('token');
          console.log('Authentifizierungstoken:', token); // Beispiel für Token im Local Storage
          return !!token; // Gibt true zurück, wenn der Token vorhanden ist
      },
      
      
      async updateEssen() {
  try {
    // Token aus dem localStorage abrufen
    const token = localStorage.getItem('token');

    // Überprüfen, ob das Token vorhanden ist
    if (!token) {
      this.$router.push('/login');
      return;
    }

    // Anfrage an den Server senden
    const response = await axios.put(`http://localhost:3001/api/essen/${this.essen.id}`, this.essen, {
      headers: {
        Authorization: `Bearer ${token}`, // Token in der Anfrage hinzufügen
      },
    });

    if (response.data.success) {
      this.message = 'Essen erfolgreich aktualisiert!';
      this.$router.push('/essen'); // Zurück zur Liste nach erfolgreichem Speichern
    } else {
      this.message = 'Fehler beim Aktualisieren des Essens';
    }
  } catch (error) {
    console.error('Fehler beim Aktualisieren des Essens:', error);
    this.message = 'Serverfehler: ' + error.message;

    // Falls ein Fehler auftritt, prüfen, ob der Benutzer authentifiziert ist
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token'); // Token löschen
      localStorage.removeItem('role'); // Rolle löschen
      this.$router.push('/login'); // Weiterleitung zur Login-Seite
    }
  }
}





  }
};

</script>