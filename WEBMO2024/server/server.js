const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');  // CORS muss nach den Importen, aber vor der Nutzung der App definiert werden

const app = express();  // Die App-Variable wird hier initialisiert

app.use(bodyParser.json());
app.use(cors());  // CORS wird nach der Initialisierung von 'app' verwendet

// Beispiel-Benutzer (statisch)
const users = [
  { username: 'admin', password: 'admin123' }
];

let essen = [
  // Beispiel-Daten
];

let essensplaene = [
  // Beispiel-Daten für Essenspläne
];

// Login-Route
app.post('/login', (req, res) => {
  const { username, password } = req.body;
  const user = users.find(u => u.username === username && u.password === password);

  if (user) {
    res.json({ success: true, message: 'Login erfolgreich!' });
  } else {
    res.status(401).json({ success: false, message: 'Ungültige Anmeldedaten' });
  }
});

// Route für Essen hinzufügen
app.post('/api/essen', (req, res) => {
  const neuesEssen = req.body;
  essen.push(neuesEssen);  // Fügt das neue Essen zur Liste hinzu
  res.status(201).json({ success: true, message: 'Essen erfolgreich hinzugefügt', essen: neuesEssen });
});

app.get('/api/essen', (req, res) => {
  res.json(essen);
});

app.put('/api/essen/:id', (req, res) => {
  // Eintrag bearbeiten
});

app.delete('/api/essen/:id', (req, res) => {
  // Eintrag löschen
});

app.get('/api/essensplan/:week', (req, res) => {
  // Essensplan für eine Woche abrufen
});

app.post('/api/essensplan/:week', (req, res) => {
  // Essensplan speichern
});

app.listen(3000, () => {
  console.log('Server läuft auf Port 3000');
});
