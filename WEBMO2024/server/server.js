const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');
const { Pool } = require('pg');
const jwt = require('jsonwebtoken');
const { verifyToken, verifyAdmin } = require('./middleware'); // Importiere die Middleware-Funktionen

// Verbindung zur PostgreSQL-Datenbank herstellen
const pool = new Pool({
  user: 'sa', 
  host: 'localhost', 
  database: 'webmo2024', 
  password: '123', 
  port: 5432
});

const app = express();

// Middleware-Konfigurationen
app.use(bodyParser.json());
app.use(cors({
  origin: 'http://localhost:5173', 
  methods: ['GET', 'POST', 'PUT', 'DELETE'], 
  credentials: true 
}));

// POST-Route für Login
app.post('/login', async (req, res) => {
  const { username, password, role } = req.body;

  try {
    const result = await pool.query('SELECT * FROM users WHERE username = $1', [username]);
    const user = result.rows[0];

    if (user && user.password === password && user.role === role) {
      const token = jwt.sign(
        { id: user.id, username: user.username, role: user.role }, 
        'geheimnis', 
        { expiresIn: '1h' }
      );

      res.json({
        success: true,
        message: 'Login erfolgreich!',
        role: user.role,
        token: token,
      });
    } else {
      res.status(401).json({ success: false, message: 'Ungültige Anmeldedaten oder falsche Rolle gewählt' });
    }
  } catch (error) {
    console.error('Fehler beim Login:', error);
    res.status(500).json({ success: false, message: 'Serverfehler beim Login' });
  }
});

// GET-Route zum Abrufen aller Essen
app.get('/api/essen', async (req, res) => {
  try {
    const result = await pool.query('SELECT * FROM food');
    res.json(result.rows);
  } catch (error) {
    console.error('Fehler beim Abrufen der Essen:', error);
    res.status(500).json({ success: false, message: 'Serverfehler beim Abrufen der Essen' });
  }
});

// GET-Route zum Abrufen eines einzelnen Essens
app.get('/api/essen/:id', async (req, res) => {
  const { id } = req.params;
  try {
    const result = await pool.query('SELECT name, price, type FROM food WHERE id = $1', [id]);
    
    if (result.rows.length === 0) {
      return res.status(404).json({ success: false, message: 'Essen nicht gefunden' });
    }

    // Sende die vollständigen Daten zurück
    res.json(result.rows[0]);
  } catch (error) {
    console.error('Fehler beim Abrufen des Essens:', error);
    res.status(500).json({ success: false, message: 'Serverfehler beim Abrufen des Essens' });
  }
});


// POST-Route zum Hinzufügen von Essen (nur für Admins)
app.post('/api/essen', verifyToken, verifyAdmin, async (req, res) => {
  const { name, preis, art } = req.body;

  try {
    const countResult = await pool.query('SELECT COUNT(*) FROM food');
    const currentCount = parseInt(countResult.rows[0].count, 10);

    if (currentCount >= 10) {
      return res.status(400).json({ success: false, message: 'Darf nicht mehr als 10 sein.' });
    }

    const result = await pool.query(
      'INSERT INTO food (name, price, type) VALUES ($1, $2, $3) RETURNING *',
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

/// PUT-Route zum Bearbeiten eines Essens (nur für Admins)
app.put('/api/essen/:id', verifyToken, verifyAdmin, async (req, res) => {
  const { id } = req.params;
  const { name, preis, art } = req.body;

  // Überprüfen, ob alle Felder vorhanden sind und der Typ gültig ist
  if (!name || preis == null || !art) {
    return res.status(400).json({ success: false, message: 'Name, Preis und Art dürfen nicht leer sein.' });
  }

  const allowedTypes = ['mit Fleisch', 'vegetarisch', 'vegan']; // Erlaubte Typen

  if (!allowedTypes.includes(art)) {
    return res.status(400).json({ success: false, message: 'Ungültiger Typ. Erlaubt sind: mit Fleisch, vegetarisch, vegan.' });
  }

  try {
    // Überprüfen, ob das Essen mit der angegebenen ID existiert
    const result = await pool.query(
      'UPDATE food SET name = $1, price = $2, type = $3 WHERE id = $4 RETURNING *',
      [name, parseFloat(preis), art, id] // Preis als Zahl speichern
    );

    if (result.rows.length === 0) {
      return res.status(404).json({ success: false, message: 'Essen nicht gefunden' });
    }

    res.json({ success: true, message: 'Essen erfolgreich aktualisiert', essen: result.rows[0] });
  } catch (error) {
    console.error('Fehler beim Aktualisieren des Essens:', error);
    res.status(500).json({ success: false, message: 'Serverfehler beim Aktualisieren des Essens' });
  }
});





// DELETE-Route zum Löschen eines Essens (nur für Admins)
app.delete('/api/essen/:id', verifyToken, verifyAdmin, async (req, res) => {
  const { id } = req.params;

  try {
    const result = await pool.query('DELETE FROM food WHERE id = $1 RETURNING *', [id]);

    if (result.rows.length === 0) {
      return res.status(404).json({ success: false, message: 'Essen nicht gefunden' });
    }

    res.json({ success: true, message: 'Essen erfolgreich gelöscht' });
  } catch (error) {
    console.error('Fehler beim Löschen des Essens:', error);
    res.status(500).json({ success: false, message: 'Serverfehler beim Löschen des Essens' });
  }
});



// POST-Route zum Erstellen eines neuen Essensplans
app.post('/api/essensplan', async (req, res) => {
  const { wochennummer, plan } = req.body;

  if (!wochennummer || wochennummer < 1 || wochennummer > 8) {
    return res.status(400).json({ success: false, message: 'Ungültige Wochenzahl. Bitte eine Woche zwischen 1 und 8 wählen.' });
  }

  try {
    const checkExist = await pool.query(
      'SELECT plan_id FROM foodplan WHERE week_number = $1',
      [wochennummer]
    );

    if (checkExist.rows.length > 0) {
      return res.status(400).json({ success: false, message: `Ein Essensplan für Woche ${wochennummer} existiert bereits.` });
    }

    // Überprüfung auf doppelte Essen-IDs im Plan
    const essenIds = plan.map(item => item.essen_id);
    const duplicateEssenIds = essenIds.filter((item, index) => essenIds.indexOf(item) !== index);

    if (duplicateEssenIds.length > 0) {
      return res.status(400).json({ success: false, message: 'Doppelte Essen im Plan sind nicht erlaubt.' });
    }

    await pool.query('BEGIN');

    const newPlan = await pool.query(
      'INSERT INTO foodplan (week_number) VALUES ($1) RETURNING plan_id',
      [wochennummer]
    );

    let mealPlanId;
    if (newPlan.rows.length > 0) {
      mealPlanId = newPlan.rows[0].plan_id;
    } else {
      throw new Error('Fehler beim Erstellen des neuen Essensplans. plan_id konnte nicht abgerufen werden.');
    }

    if (!plan || !Array.isArray(plan) || plan.length === 0) {
      throw new Error('Das Plan-Array ist leer oder ungültig.');
    }

    for (const { tag, essen_id } of plan) {
      if (!tag || !essen_id) {
        throw new Error('Ungültige Einträge: Tag oder Essen-ID fehlen.');
      }

      await pool.query(
        'INSERT INTO food_in_plan (plan_id, food_id, day_of_week) VALUES ($1, $2, $3)',
        [mealPlanId, essen_id, tag]
      );
    }

    await pool.query('COMMIT');

    res.status(201).json({ success: true, message: `Essensplan für Woche ${wochennummer} erfolgreich gespeichert` });
  } catch (error) {
    await pool.query('ROLLBACK');
    console.error('Fehler beim Speichern des Essensplans:', error);
    res.status(500).json({ success: false, message: 'Serverfehler beim Speichern des Essensplans', error: error.message });
  }
});




app.get('/api/essensplan', async (req, res) => {
  try {
    const result = await pool.query(`
      SELECT p.plan_id, p.week_number, p.created_at, l.day_of_week, l.food_id, e.name AS food_name, e.price, e.type
      FROM foodplan p
      LEFT JOIN food_in_plan l ON p.plan_id = l.plan_id
      LEFT JOIN food e ON l.food_id = e.id
      ORDER BY p.week_number, l.day_of_week;
    `);

    const plans = {};

    result.rows.forEach(row => {
      if (!plans[row.week_number]) {
        plans[row.week_number] = {
          plan_id: row.plan_id,
          wochennummer: row.week_number,
          created_at: row.created_at,  // Füge die Erstellungszeit hinzu
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

      if (row.day_of_week && row.food_name) {
        plans[row.week_number].days[row.day_of_week] = {
          food_id: row.food_id,
          food_name: row.food_name,
          price: parseFloat(row.price),
          type: row.type
        };
        plans[row.week_number].total_price += parseFloat(row.price);
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
  const wochennummer = parseInt(req.params.week, 10);

  if (isNaN(wochennummer) || wochennummer < 1 || wochennummer > 8) {
    return res.status(400).json({ success: false, message: 'Ungültige Wochenzahl.' });
  }

  try {
    const result = await pool.query(`
      SELECT p.plan_id, p.week_number, p.created_at, l.day_of_week, l.food_id, e.name AS food_name, e.price
      FROM foodplan p
      LEFT JOIN food_in_plan l ON p.plan_id = l.plan_id
      LEFT JOIN food e ON l.food_id = e.id
      WHERE p.week_number = $1
      ORDER BY l.day_of_week;
    `, [wochennummer]);

    if (result.rows.length === 0) {
      return res.json({
        wochennummer,
        days: {
          Montag: null,
          Dienstag: null,
          Mittwoch: null,
          Donnerstag: null,
          Freitag: null
        },
        total_price: 0
      });
    }

    const plan = {
      wochennummer: wochennummer,
      created_at: result.rows[0].created_at,  // Füge die Erstellungszeit hinzu
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
      if (row.day_of_week && row.food_name) {
        plan.days[row.day_of_week] = {
          food_id: row.food_id,
          food_name: row.food_name,
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




// PUT-Route zum Aktualisieren eines Essensplans
app.put('/api/essensplan/:week', async (req, res) => {
  const wochennummer = parseInt(req.params.week, 10);
  const { plan } = req.body;

  if (isNaN(wochennummer) || wochennummer < 1 || wochennummer > 8) {
    return res.status(400).json({ success: false, message: 'Ungültige Wochenzahl.' });
  }

  try {
    await pool.query('BEGIN');

    const checkExist = await pool.query('SELECT plan_id FROM foodplan WHERE week_number = $1', [wochennummer]);
    let mealPlanId;

    if (checkExist.rows.length > 0) {
      mealPlanId = checkExist.rows[0].plan_id;
      await pool.query('DELETE FROM food_in_plan WHERE plan_id = $1', [mealPlanId]);
    } else {
      const newPlan = await pool.query('INSERT INTO foodplan (week_number) VALUES ($1) RETURNING plan_id', [wochennummer]);
      mealPlanId = newPlan.rows[0].plan_id;
    }

    for (const { tag, essen_id } of plan) {
      await pool.query(
        'INSERT INTO food_in_plan (plan_id, food_id, day_of_week) VALUES ($1, $2, $3)',
        [mealPlanId, essen_id, tag]
      );
    }

    await pool.query('COMMIT');
    res.status(200).json({ success: true, message: 'Essensplan erfolgreich aktualisiert' });
  } catch (error) {
    await pool.query('ROLLBACK');
    console.error('Fehler beim Aktualisieren des Essensplans:', error);
    res.status(500).json({ success: false, message: 'Fehler beim Aktualisieren des Essensplans' });
  }
});

// DELETE-Route zum Löschen eines Essensplans
app.delete('/api/essensplan/:id', async (req, res) => {
  const { id } = req.params;

  if (!id || isNaN(parseInt(id, 10))) {
    return res.status(400).json({ success: false, message: 'Ungültige Plan-ID' });
  }

  try {
    const planId = parseInt(id, 10);

    await pool.query('DELETE FROM food_in_plan WHERE plan_id = $1', [planId]);
    const deletePlanResult = await pool.query('DELETE FROM foodplan WHERE plan_id = $1 RETURNING *', [planId]);

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