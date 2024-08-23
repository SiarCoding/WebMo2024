const express = require('express'); // Express-Framework für HTTP-Server
const bodyParser = require('body-parser'); // Middleware zum Parsen von Anfragen
const cors = require('cors'); // Middleware für Cross-Origin Resource Sharing
const pool = require('./db/db'); // Datenbankverbindung

const app = express(); // Initialisierung der Express-Anwendung

app.use(bodyParser.json());

// Spezifische CORS-Konfiguration
app.use(cors({
  origin: 'http://localhost:5173', // Erlaubt Anfragen nur von dieser URL
  methods: ['GET', 'POST', 'PUT', 'DELETE'], // Erlaubt diese HTTP-Methoden
  credentials: true // Erlaubt das Senden von Cookies oder Authentifizierungsdaten
}));


// Endpunkte für die API

// POST-Route für Login
app.post('/login', async (req, res) => {
  const { username, password, role } = req.body; // Rolle hinzufügen

  try {
    // Suche nach dem Benutzer in der Datenbank
    const result = await pool.query('SELECT * FROM users WHERE username = $1', [username]);
    const user = result.rows[0];

    // Prüfen, ob der Benutzer existiert, das Passwort korrekt ist und die Rolle übereinstimmt
    if (user && user.password === password && user.role === role) {
      res.json({
        success: true,
        message: 'Login erfolgreich!',
        role: user.role // Rolle des Benutzers zurückgeben
      });
    } else {
      res.status(401).json({ success: false, message: 'Ungültige Anmeldedaten oder falsche Rolle gewählt' });
    }
  } catch (error) {
    console.error('Fehler beim Login:', error);
    res.status(500).json({ success: false, message: 'Serverfehler beim Login' });
  }
});

// POST-Route zum Hinzufügen von Essen
app.post('/api/essen', async (req, res) => {
  const { name, preis, art } = req.body;

  try {
    const result = await pool.query(
      'INSERT INTO essen (name, price, type) VALUES ($1, $2, $3) RETURNING *', 
      [name, preis, art]
    );

    const neuesEssen = result.rows[0];

    res.status(201).json({ 
      success: true, 
      message: 'Essen erfolgreich hinzugefügt', 
      essen: neuesEssen 
    });

  } catch (error) {
    console.error('Fehler beim Hinzufügen des Essens:', error);
    res.status(500).json({ success: false, message: 'Serverfehler beim Hinzufügen des Essens' });
  }
});

// GET-Route zum Abrufen aller Essen
app.get('/api/essen', async (req, res) => {
  try {
    const result = await pool.query('SELECT * FROM essen');
    res.json(result.rows);
  } catch (error) {
    console.error('Fehler beim Abrufen der Essen:', error);
    res.status(500).json({ success: false, message: 'Serverfehler beim Abrufen der Essen' });
  }
});

// PUT-Route zum Aktualisieren von Essen
app.put('/api/essen/:id', async (req, res) => {
  const id = parseInt(req.params.id);
  const { name, preis, art } = req.body;

  try {
    const result = await pool.query(
      'UPDATE essen SET name = $1, price = $2, type = $3 WHERE id = $4 RETURNING *',
      [name, preis, art, id]
    );

    if (result.rows.length > 0) {
      res.json({ success: true, essen: result.rows[0] });
    } else {
      res.status(404).json({ success: false, message: 'Essen nicht gefunden' });
    }

  } catch (error) {
    console.error('Fehler beim Aktualisieren des Essens:', error);
    res.status(500).json({ success: false, message: 'Serverfehler beim Aktualisieren des Essens' });
  }
});

// GET-Route zum Abrufen eines bestimmten Essens
app.get('/api/essen/:id', async (req, res) => {
  const id = parseInt(req.params.id);

  try {
    const result = await pool.query('SELECT * FROM essen WHERE id = $1', [id]);
    const einzelnesEssen = result.rows[0];

    if (einzelnesEssen) {
      res.json(einzelnesEssen);
    } else {
      res.status(404).json({ success: false, message: 'Essen nicht gefunden' });
    }
  } catch (error) {
    console.error('Fehler beim Abrufen des Essens:', error);
    res.status(500).json({ success: false, message: 'Serverfehler beim Abrufen des Essens' });
  }
});

// DELETE-Route zum Löschen von Essen
app.delete('/api/essen/:id', async (req, res) => {
  const id = parseInt(req.params.id);

  try {
    const result = await pool.query('DELETE FROM essen WHERE id = $1 RETURNING *', [id]);
    const deletedEssen = result.rows[0];

    if (deletedEssen) {
      res.json({ success: true, message: 'Essen erfolgreich gelöscht', essen: deletedEssen });
    } else {
      res.status(404).json({ success: false, message: 'Essen nicht gefunden' });
    }
  } catch (error) {
    console.error('Fehler beim Löschen des Essens:', error);
    res.status(500).json({ success: false, message: 'Serverfehler beim Löschen des Essens' });
  }
});

// GET-Route zum Testen der Datenbankverbindung
app.get('/api/test-db', (req, res) => {
  pool.query('SELECT NOW()', (err, result) => {
    if (err) {
      console.log('Fehler bei der Verbindung zur Datenbank', err);
      res.status(500).json({success: false, message: 'Datenbankverbindung fehlgeschlagen'});
    } else {
      res.json({success: true, message: 'Datenbankverbindung hergestellt', time: result.rows[0].now});
    }
  });
});

// Server starten
app.listen(3001, () => {
  console.log('Server läuft auf Port 3001');
});
