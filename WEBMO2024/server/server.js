// In dieser Datei definieren wir die API-Endpunkte unseres Backends, das auf Node.js basiert. 
// Wir verwenden dabei das Express-Framework, um HTTP-Serverfunktionen einfach und effizient zu erstellen und zu verwalten.

const express = require('express'); //backend für node.js (Express)
const bodyParser = require('body-parser'); // ein paket was ermöglicht HTTP-Anfragen zu parsen
const cors = require('cors');  // CORS wird hier definiert um Server Anfragen von anderen Domänen zu akzeptieren

const app = express();  // Die App-Variable wird hier initialisiert!

app.use(bodyParser.json());
app.use(cors());  // CORS wird hier aktiviert!

// Beispiel-Daten für Admin Zugang
const users = [
  { username: 'admin', password: 'admin123' }, //beispiel Daten für Admin
  { username: 'user', password: 'user123' } //beispiel Daten für User 
];

let nextId = 1; // counter definieren
let essen = []; // Deine Essenliste

let essensplaene = [
  // Beispiel-Daten für Essenspläne (Relevant für Essensplan) Kommt aber später
];

// Login-Route
app.post('/login', (req, res) => {
  const { username, password } = req.body; // wir definieren zunächste username und password und setzen sie gleich mit req.body
  const user = users.find(u => u.username === username && u.password === password); // initialiserung von username und password

  if (user) { //ist das der richtige nutzer mit den richtigen daten ? 
    res.json({ success: true, message: 'Login erfolgreich!' }); // Falls ja, dann erfolgreicher login
  } else {
    res.status(401).json({ success: false, message: 'Ungültige Anmeldedaten' }); //Falls nein dann ungültig
  }
});

// POST-Request Diese API-Route fügt uns Essen in die Liste hinzu
app.post('/api/essen', (req, res) => {
  const neuesEssen = { id: nextId++, ...req.body }; // jedes essen enthält eine eindeutige id 
  essen.push(neuesEssen); // hier wird das neue essen in die Array gepusht 
  res.status(201).json({ success: true, message: 'Essen erfolgreich hinzugefügt', essen: neuesEssen }); // code:201 erfolgreich
});

// GET Request: Diese Route erstellt uns die Liste für /essen. Das heißt mit dieser API-Route bekommen wir alles was als in liste gepusht wurde
app.get('/api/essen', (req, res) => {
  res.json(essen);
});

// PUT Request: Mit dieser Route können wir dafür sorgen dass wir die Daten aktualisieren. Das heißt es ist Relevant für die Änderungen der Daten basiernd auf der ID. 

app.put('/api/essen/:id', (req, res) => {
  const id = parseInt(req.params.id);  // id richtig interpretieren
  const index = essen.findIndex(e => e.id === id); //findIndex() durchsucht in dem Fall das Array und gibt den Index des ersten Elementes zurück. muss aber mit der bendingung übereinstimmen!
  if (index !== -1) { //wenn index kein passendes element findet gibt er -1 zurück aber solange diese bedinung erfüllt ist also ungleich -1 dann überschreibt er die daten.
    essen[index] = { ...essen[index], ...req.body }; //daten werden überschrieben
    res.json({ success: true, essen: essen[index] }); // aktualsierter Essenseintrag.
  } else {
    res.status(404).json({ success: false, message: 'Essen nicht gefunden' }); // falls die bendingung in if nicht erfüllt ist essen nicht gefunden also -1
  }
});

// GET-Request: Diese API-Route gibt die Details eines bestimmten Essens basierend auf der (ID) zurück. 
app.get('/api/essen/:id', (req, res) => {
  const id = parseInt(req.params.id); // hier wird id definiert
  const einzelnesEssen = essen.find(e => e.id === id); //durchsuchung der id im Array
  if (einzelnesEssen) { //ist es das Essen mit der Id, die wir suchen ? 
    res.json(einzelnesEssen); //wenn ja, dann gebe uns das essen mit der id, was wir suchen
  } else { // wenn nicht
    res.status(404).json({ success: false, message: 'Essen nicht gefunden' }); // dann wurde die id nicht gefunden!
  }
});

// DELETE-Request: hier wird DELETE angewendet um Essenseinträge löschen zu können basierend auf die Id. 
app.delete('/api/essen/:id', (req, res) => {
  const id = parseInt(req.params.id); // hier wird id definiert
  essen = essen.filter(e => e.id !== id); // id wird im array solange gesucht bis es gefunden wird 
  res.json({ success: true, message: 'Essen erfolgreich gelöscht' }); // wenn der eintrag gelöscht gibt, er uns success zurück
});

//GET Request: diese API-Route wird verwendet um das Essensplan für eine Woche abzurufen (Noch nicht implementiert)
app.get('/api/essensplan/:week', (req, res) => {
  // Essensplan für eine Woche abrufen
});

//POST-Request: hier wird der Essensplan für die woche gespeichert (Noch nicht implementiert)
app.post('/api/essensplan/:week', (req, res) => {
  // Essensplan speichern
});

//hier definieren wir unseren Server Port. in dem fall ist es die localhost:3001. 
app.listen(3001, () => {
  console.log('Server läuft auf Port 3001'); //fürs terminal wenn das backend läuft also der Server
});
