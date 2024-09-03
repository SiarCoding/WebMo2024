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
const jwt = require('jsonwebtoken'); // JWT-Bibliothek

app.post('/login', async (req, res) => {
  const { username, password, role } = req.body;

  try {
    const result = await pool.query('SELECT * FROM users WHERE username = $1', [username]);
    const user = result.rows[0];

    // Überprüfen, ob Benutzer existiert und Anmeldedaten korrekt sind
    if (user && user.password === password && user.role === role) {
      // Token generieren
      const token = jwt.sign(
        { id: user.id, username: user.username, role: user.role }, // Payload
        'geheimnis', // Geheimnis zur Signierung des Tokens
        { expiresIn: '1h' } // Ablaufzeit des Tokens
      );

      res.json({
        success: true,
        message: 'Login erfolgreich!',
        role: user.role,
        token: token, // Token in der Antwort zurückgeben
      });
    } else {
      res.status(401).json({ success: false, message: 'Ungültige Anmeldedaten oder falsche Rolle gewählt' });
    }
  } catch (error) {
    console.error('Fehler beim Login:', error);
    res.status(500).json({ success: false, message: 'Serverfehler beim Login' });
  }
});

function generateToken(user) {
  // Nutzen Sie eine Bibliothek wie jsonwebtoken, um ein JWT zu generieren
  const jwt = require('jsonwebtoken');
  const token = jwt.sign({ id: user.id, username: user.username }, 'geheimnis', { expiresIn: '1h' });
  return token;
}


// POST-Route zum Hinzufügen von Essen
// POST-Route zum Hinzufügen von Essen
app.post('/api/essen', async (req, res) => {
  const { name, preis, art } = req.body;

  try {
    // Überprüfen, ob das Gericht bereits existiert (Groß-/Kleinschreibung wird ignoriert)
    const checkExist = await pool.query('SELECT * FROM essen WHERE LOWER(name) = LOWER($1)', [name]);
    
    if (checkExist.rows.length > 0) {
      return res.status(400).json({ success: false, message: 'Das Gericht existiert bereits in der Liste.' });
    }

    // Gericht hinzufügen, wenn es noch nicht existiert
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
// PUT-Route zum Aktualisieren von Essen
app.put('/api/essen/:id', async (req, res) => {
  const id = parseInt(req.params.id);
  if (isNaN(id)) {
    return res.status(400).json({ success: false, message: 'Ungültige ID' });
  }

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
    console.error('Fehler beim Aktualisieren des Essens:', error.message);
    res.status(500).json({ success: false, message: 'Serverfehler beim Aktualisieren des Essens' });
  }
});

// GET-Route zum Abrufen eines bestimmten Essens
app.get('/api/essen/:id', async (req, res) => {
  const id = parseInt(req.params.id);
  if (isNaN(id)) {
    return res.status(400).json({ success: false, message: 'Ungültige ID' });
  }

  try {
    const result = await pool.query('SELECT * FROM essen WHERE id = $1', [id]);
    const einzelnesEssen = result.rows[0];

    if (einzelnesEssen) {
      res.json(einzelnesEssen);
    } else {
      res.status(404).json({ success: false, message: 'Essen nicht gefunden' });
    }
  } catch (error) {
    console.error('Fehler beim Abrufen des Essens:', error.message);
    res.status(500).json({ success: false, message: 'Serverfehler beim Abrufen des Essens' });
  }
});

// DELETE-Route zum Löschen von Essen
app.delete('/api/essen/:id', async (req, res) => {
  const id = parseInt(req.params.id);
  if (isNaN(id)) {
    return res.status(400).json({ success: false, message: 'Ungültige ID' });
  }

  try {
    const result = await pool.query('DELETE FROM essen WHERE id = $1 RETURNING *', [id]);
    const deletedEssen = result.rows[0];

    if (deletedEssen) {
      res.json({ success: true, message: 'Essen erfolgreich gelöscht', essen: deletedEssen });
    } else {
      res.status(404).json({ success: false, message: 'Essen nicht gefunden' });
    }
  } catch (error) {
    console.error('Fehler beim Löschen des Essens:', error.message);
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


//API POST essensplan hinzufügen
app.post('/api/essensplan', async (req, res) => {
  const { week_number, plan } = req.body;

  console.log('Empfangene Daten:', { week_number, plan }); // Debugging-Ausgabe

  // Überprüfen Sie, ob die Wochenzahl gültig ist
  if (week_number < 1 || week_number > 8) {
    return res.status(400).json({ success: false, message: 'Ungültige Wochenzahl. Bitte eine Woche zwischen 1 und 8 wählen.' });
  }

  try {
    // Überprüfen Sie, ob bereits ein Essensplan für diese Woche existiert
    const checkExist = await pool.query(
      'SELECT plan_id FROM essensplan WHERE wochennummer = $1',
      [week_number]
    );

    if (checkExist.rows.length > 0) {
      // Wenn ein Plan existiert, geben wir eine Fehlermeldung zurück und blockieren das Hinzufügen
      return res.status(400).json({ success: false, message: 'Ein Essensplan für diese Woche existiert bereits.' });
    }

    // Beginnen Sie die Transaktion
    await pool.query('BEGIN');
    console.log('Transaktion gestartet'); // Debugging-Ausgabe

    // Erstellen Sie einen neuen Essensplan und erhalten Sie die neue `plan_id`
    const newPlan = await pool.query(
      'INSERT INTO essensplan (wochennummer) VALUES ($1) RETURNING plan_id',
      [week_number]
    );

    let mealPlanId;
    if (newPlan.rows.length > 0) {
      mealPlanId = newPlan.rows[0].plan_id;
      console.log('Neue plan_id erstellt:', mealPlanId); // Debugging-Ausgabe
    } else {
      throw new Error('Fehler beim Erstellen des neuen Essensplans. plan_id konnte nicht abgerufen werden.');
    }

    // Überprüfen Sie, ob das `plan`-Array vorhanden und nicht leer ist
    if (!plan || !Array.isArray(plan) || plan.length === 0) {
      throw new Error('Das Plan-Array ist leer oder ungültig.');
    }

    // Fügen Sie die neuen Einträge zur Tabelle `essen_im_plan` hinzu
    for (const { day_of_week, meal_id } of plan) {
      console.log('Füge hinzu zu essen_im_plan:', { mealPlanId, meal_id, day_of_week }); // Debugging-Ausgabe
      await pool.query(
        'INSERT INTO essen_im_plan (plan_id, essen_id, tag) VALUES ($1, $2, $3)',
        [mealPlanId, meal_id, day_of_week]
      );
    }

    console.log('Alle Einfügungen abgeschlossen'); // Debugging-Ausgabe

    // Transaktion erfolgreich beenden
    await pool.query('COMMIT');
    console.log('Transaktion abgeschlossen und bestätigt'); // Debugging-Ausgabe

    res.status(201).json({ success: true, message: 'Essensplan erfolgreich gespeichert' });
  } catch (error) {
    // Bei Fehler Transaktion zurückrollen
    await pool.query('ROLLBACK');
    console.error('Fehler beim Speichern des Essensplans:', error.message, error.stack); // Detaillierte Fehlerprotokollierung
    res.status(500).json({ success: false, message: 'Serverfehler beim Speichern des Essensplans' });
  }
});









// GET-Route zum Abrufen aller Essenspläne

app.get('/api/essensplan', async (req, res) => {
  try {
    const result = await pool.query(`
      SELECT p.plan_id, p.wochennummer, l.tag, l.essen_id, e.name AS meal_name, e.price
      FROM essensplan p
      LEFT JOIN essen_im_plan l ON p.plan_id = l.plan_id
      LEFT JOIN essen e ON l.essen_id = e.id
      ORDER BY p.wochennummer, l.tag;
    `);

    const plans = {};

    result.rows.forEach(row => {
      if (!plans[row.wochennummer]) {
        plans[row.wochennummer] = {
          plan_id: row.plan_id,
          week_number: row.wochennummer,
          days: {
            'Montag': null,
            'Dienstag': null,
            'Mittwoch': null,
            'Donnerstag': null,
            'Freitag': null
          },
          total_price: 0
        };
      }

      if (row.tag && row.meal_name) {
        plans[row.wochennummer].days[row.tag] = {
          meal_id: row.essen_id,
          meal_name: row.meal_name,
          price: parseFloat(row.price)
        };
        plans[row.wochennummer].total_price += parseFloat(row.price);
      }
    });

    res.json(Object.values(plans));
  } catch (error) {
    console.error('Fehler beim Abrufen der Essenspläne:', error);
    res.status(500).json({ success: false, message: 'Serverfehler beim Abrufen der Essenspläne' });
  }
});



// GET-Route zum Abrufen eines bestimmten Essensplans nach Woche
app.get('/api/essensplan/:week', async (req, res) => {
  const week_number = parseInt(req.params.week, 10); // Konvertiere den Routenparameter in eine Zahl

  if (isNaN(week_number) || week_number < 1 || week_number > 8) {
    return res.status(400).json({ success: false, message: 'Ungültige Wochenzahl.' });
  }

  try {
    const result = await pool.query(`
      SELECT p.plan_id, p.wochennummer, l.tag, l.essen_id, e.name AS meal_name, e.price
      FROM essensplan p
      LEFT JOIN essen_im_plan l ON p.plan_id = l.plan_id
      LEFT JOIN essen e ON l.essen_id = e.id
      WHERE p.wochennummer = $1
      ORDER BY l.tag;
    `, [week_number]);

    if (result.rows.length === 0) {
      return res.status(404).json({ success: false, message: 'Kein Essensplan für die ausgewählte Woche gefunden.' });
    }

    const plan = {
      week_number: week_number,
      days: {
        Montag: null,
        Dienstag: null,
        Mittwoch: null,
        Donnerstag: null,
        Freitag: null
      },
      total_price: 0
    };

    result.rows.forEach(row => {
      if (row.tag && row.meal_name) {
        plan.days[row.tag] = {
          meal_id: row.essen_id,
          meal_name: row.meal_name,
          price: parseFloat(row.price)
        };
        plan.total_price += parseFloat(row.price);
      }
    });

    res.json(plan);
  } catch (error) {
    console.error('Fehler beim Abrufen des Essensplans:', error);
    res.status(500).json({ success: false, message: 'Serverfehler beim Abrufen des Essensplans' });
  }
});



// PUT-Route zum Aktualisieren eines vorhandenen Essensplans
// PUT-Route zum Aktualisieren eines vorhandenen Essensplans
app.put('/api/essensplan/:week', async (req, res) => {
  const week_number = req.params.week;
  const { plan } = req.body;

  console.log('Aktualisiere Essensplan für Woche:', week_number);
  console.log('Geplanter Plan:', plan);

  try {
    // Starte eine neue Transaktion
    await pool.query('BEGIN');
    
    // Prüfe, ob ein vorhandener Plan für die Woche existiert
    const checkExist = await pool.query('SELECT plan_id FROM essensplan WHERE wochennummer = $1', [week_number]);
    let mealPlanId;

    if (checkExist.rows.length > 0) {
      mealPlanId = checkExist.rows[0].plan_id;
      console.log('Vorhandener Plan gefunden, Lösche alte Einträge für plan_id:', mealPlanId);
      
      // Lösche die alten Einträge
      await pool.query('DELETE FROM essen_im_plan WHERE plan_id = $1', [mealPlanId]);
    } else {
      console.log('Kein Plan gefunden, Erstelle neuen Plan');
      
      // Wenn kein Plan vorhanden ist, erstelle einen neuen
      const newPlan = await pool.query('INSERT INTO essensplan (wochennummer) VALUES ($1) RETURNING plan_id', [week_number]);
      mealPlanId = newPlan.rows[0].plan_id;
    }

    console.log('Füge neue Einträge hinzu für plan_id:', mealPlanId);

    // Füge neue Einträge für die aktualisierte oder neue plan_id hinzu
    for (const { day_of_week, meal_id } of plan) {
      console.log('Füge hinzu zu essen_im_plan:', { mealPlanId, meal_id, day_of_week });
      await pool.query(
        'INSERT INTO essen_im_plan (plan_id, essen_id, tag) VALUES ($1, $2, $3)',
        [mealPlanId, meal_id, day_of_week]
      );
    }

    // Beende die Transaktion
    await pool.query('COMMIT');
    console.log('Transaktion abgeschlossen und bestätigt');

    res.status(200).json({ success: true, message: 'Essensplan erfolgreich aktualisiert' });
  } catch (error) {
    // Rolle die Transaktion bei einem Fehler zurück
    await pool.query('ROLLBACK');
    console.error('Fehler beim Aktualisieren des Essensplans:', error);
    res.status(500).json({ success: false, message: 'Serverfehler beim Aktualisieren des Essensplans' });
  }
});



// DELETE-Route zum Löschen eines Essensplans
app.delete('/api/essensplan/:id', async (req, res) => {
  const { id } = req.params;
  console.log('Lösche Plan mit ID:', id);

  if (!id || isNaN(parseInt(id, 10))) {
    return res.status(400).json({ success: false, message: 'Ungültige Plan-ID' });
  }

  try {
    const planId = parseInt(id, 10);

    await pool.query('DELETE FROM essen_im_plan WHERE plan_id = $1', [planId]);
    const deletePlanResult = await pool.query('DELETE FROM essensplan WHERE plan_id = $1 RETURNING *', [planId]);

    if (deletePlanResult.rows.length > 0) {
      res.json({ success: true, message: 'Essensplan erfolgreich gelöscht' });
    } else {
      res.status(404).json({ success: false, message: 'Essensplan nicht gefunden' });
    }
  } catch (error) {
    console.error('Fehler beim Löschen des Essensplans:', error);
    res.status(500).json({ success: false, message: 'Serverfehler beim Löschen des Essensplans' });
  }
});






// Server starten
app.listen(3001, () => {
  console.log('Server läuft auf Port 3001');
});
