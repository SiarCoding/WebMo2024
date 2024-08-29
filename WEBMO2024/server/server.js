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
//API POST essensplan hinzufügen
app.post('/api/essensplan', async (req, res) => {
  const { week_number, plan } = req.body;

  // Überprüfe, ob die Wochenzahl gültig ist
  if (week_number < 1 || week_number > 8) {
    return res.status(400).json({ success: false, message: 'Ungültige Wochenzahl. Bitte eine Woche zwischen 1 und 8 wählen.' });
  }

  try {
    // Beginne eine Transaktion
    await pool.query('BEGIN');

    // Überprüfe, ob der Essensplan für die Woche bereits existiert
    const checkExist = await pool.query(
      'SELECT plan_id FROM essensplan WHERE wochennummer = $1',
      [week_number]
    );

    let mealPlanId;
    if (checkExist.rows.length > 0) {
      mealPlanId = checkExist.rows[0].plan_id;
      // Lösche den bestehenden Essensplan
      await pool.query('DELETE FROM essen_im_plan WHERE plan_id = $1', [mealPlanId]);
    } else {
      // Erstelle einen neuen Essensplan
      const newPlan = await pool.query(
        'INSERT INTO essensplan (wochennummer) VALUES ($1) RETURNING plan_id',
        [week_number]
      );
      mealPlanId = newPlan.rows[0].plan_id;
    }

    // Debugging-Ausgabe zur Überprüfung
    console.log('Essensplan ID:', mealPlanId);

    // Füge die neuen Einträge zur essen_im_plan hinzu
    const insertQueries = plan.map(({ day_of_week, meal_id }) => {
      console.log('Hinzufügen zu essen_im_plan:', { mealPlanId, meal_id, day_of_week }); // Debugging-Ausgabe
      return pool.query(
        'INSERT INTO essen_im_plan (plan_id, essen_id, tag) VALUES ($1, $2, $3)',
        [mealPlanId, meal_id, day_of_week]
      );
    });

    await Promise.all(insertQueries);

    // Transaktion erfolgreich beenden
    await pool.query('COMMIT');

    res.status(201).json({ success: true, message: 'Essensplan erfolgreich gespeichert' });
  } catch (error) {
    // Bei Fehler Transaktion zurückrollen
    await pool.query('ROLLBACK');
    console.error('Fehler beim Speichern des Essensplans:', error);
    res.status(500).json({ success: false, message: 'Serverfehler beim Speichern des Essensplans' });
  }
});



// API GET 
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

      // Überprüfe, ob ein Essen für den Tag vorhanden ist und füge es hinzu
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



app.put('/api/essensplan/:week', async (req, res) => {
  const week_number = req.params.week;
  const { plan } = req.body;

  try {
    await pool.query('BEGIN');
    
    const checkExist = await pool.query('SELECT plan_id FROM essensplan WHERE wochennummer = $1', [week_number]);
    let mealPlanId;

    if (checkExist.rows.length > 0) {
      mealPlanId = checkExist.rows[0].plan_id;
      await pool.query('DELETE FROM essen_im_plan WHERE plan_id = $1', [mealPlanId]);
    } else {
      const newPlan = await pool.query('INSERT INTO essensplan (wochennummer) VALUES ($1) RETURNING plan_id', [week_number]);
      mealPlanId = newPlan.rows[0].plan_id;
    }

    const insertQueries = plan.map(({ day_of_week, meal_id }) =>
      pool.query('INSERT INTO essen_im_plan (plan_id, essen_id, tag) VALUES ($1, $2, $3)', [mealPlanId, meal_id, day_of_week])
    );

    await Promise.all(insertQueries);
    await pool.query('COMMIT');

    res.status(200).json({ success: true, message: 'Essensplan erfolgreich aktualisiert' });
  } catch (error) {
    await pool.query('ROLLBACK');
    console.error('Fehler beim Aktualisieren des Essensplans:', error);
    res.status(500).json({ success: false, message: 'Serverfehler beim Aktualisieren des Essensplans' });
  }
});

app.delete('/api/essensplan/:id', async (req, res) => {
  const { id } = req.params; // Nutze 'id' statt 'week'
  
  if (!id) {
    return res.status(400).json({ success: false, message: 'Ungültige Plan-ID' });
  }

  try {
    // Konvertiere den `id`-Parameter zu einer Ganzzahl
    const planId = parseInt(id, 10);

    await pool.query('DELETE FROM essen_im_plan WHERE plan_id = $1', [planId]);
    await pool.query('DELETE FROM essensplan WHERE plan_id = $1', [planId]);

    res.json({ success: true, message: 'Essensplan erfolgreich gelöscht' });
  } catch (error) {
    console.error('Fehler beim Löschen des Essensplans:', error);
    res.status(500).json({ success: false, message: 'Serverfehler beim Löschen des Essensplans' });
  }
});




// Server starten
app.listen(3001, () => {
  console.log('Server läuft auf Port 3001');
});
