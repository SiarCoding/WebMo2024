<!--Das hinzufügen der Esseneinträge findet hier statt (/add-essen). Verwendet wird dabei Bootstrap für das Design. Im Templete kommt der HTML Code Abschnitt!
Den <script> nutzen wir für Syntax!!!-->
<template>
  <div class="container mt-5"> <!-- Container (Bootstrap)-->
    <h2 class="mb-4 text-center">
      🍽️ Neues Essen hinzufügen <!-- Titel mit EMOJI -->
    </h2> 
    <form @submit.prevent="hinzufuegen" class="card p-4 shadow-sm" style="background-color: #e3f2fd;"> <!-- hier kommt das formular-->
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

      <button type="submit" class="btn btn-primary w-100">Hinzufügen</button>
    </form>
    <p v-if="message" class="mt-3 text-success text-center">{{ message }}</p>
  </div>
</template>

<script>
import axios from 'axios'; // Zuerst wird die axios-Bibliothek importiert, die für HTTP-Anfragen genutzt wird um daten zu senden an den server

export default {
  data() { //wir implementieren daten für das essen die relevant sind für das hinzufügen
    return {
      essen: {
        name: '', //name des essens
        preis: 0, //preis. hier ist default 0 gesetzt.
        art: ''  //art des essens
      },
      message: '' // nachricht die gesendet wird von Server erfolgs oder fehlermeldung
    };
  },
  methods: { //Hier werden die Methoden definiert, die in der Komponente verwendet werden können. In diesem Fall gibt es eine Methode namens hinzufuegen, die ausgeführt wird, wenn das Formular abgeschickt wird.
    async hinzufuegen() { //asynchrone Funktion
      console.log('Essen hinzufügen gestartet');

      // Einfache Validierung vor dem Senden
      if (!this.essen.name || this.essen.preis <= 0 || !this.essen.art) { // wenn der Name des Essens NICHT eingegeben wurde (!), der Preis des Essens kleiner gleich 0 ist und das essen KEINE Art bekommt (!)
        this.message = 'Bitte alle Felder korrekt ausfüllen!'; // Dann sendet uns der server dann diese nachricht (Fehler catchen mithilfe von  IF-Verzweigung)
        return;
      }

      try {
        const response = await axios.post('http://localhost:3001/api/essen', this.essen);  // Port läuft auf 3001 und es sendet ein POST-Request an (api/essen)
        console.log('Serverantwort:', response); // server massage
        if (response.data.success) { //fals die daten richtig sind
          this.message = 'Essen erfolgreich hinzugefügt!'; // Taucht als Mitteilung für message
          this.$emit('essenAdded', response.data.essen);  // Neues Essen über ein Event melden
          this.essen = { name: '', preis: 0, art: '' };  // Formular zurücksetzen
        } else {
          this.message = 'Fehler beim Hinzufügen des Essens'; // Fehlernachricht
        }
      } catch (error) { //Fehler mit Message und Statuscode abfangen
        console.error('Fehler beim Hinzufügen des Essens:', error);
        this.message = 'Serverfehler: ' + error.message;
      }
    }
  }
};
</script>
