const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');
const { Pool } = require('pg');
const jwt = require('jsonwebtoken');
const { verifyToken, verifyAdmin } = require('./middleware');

const pool = new Pool({
  user: 'postgres',
  host: 'localhost',
  database: 'webmo2024',
  password: '123',
  port: 5432,
});

const app = express();

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
    const result = await pool.query('SELECT * FROM essen');
    res.json(result.rows);
  } catch (error) {
    console.error('Fehler beim Abrufen der Essen:', error);
    res.status(500).json({ success: false, message: 'Serverfehler beim Abrufen der Essen' });
  }
});

// POST-Route zum Hinzufügen von Essen (nur für Admins)
app.post('/api/essen', verifyToken, verifyAdmin, async (req, res) => {
  const { name, preis, art } = req.body;

  try {
    const countResult = await pool.query('SELECT COUNT(*) FROM essen');
    const currentCount = parseInt(countResult.rows[0].count, 10);

    if (currentCount >= 10) {
      return res.status(400).json({ success: false, message: 'Darf nicht mehr als 10 sein.' });
    }

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

// GET-Route zum Abrufen aller Essenspläne
app.get('/api/essensplan', async (req, res) => {
  try {
    const result = await pool.query(`
      SELECT week_number, json_agg(json_build_object('day_of_week', day_of_week, 'meal_id', meal_id)) AS days
      FROM essensplan
      GROUP BY week_number
      ORDER BY week_number;
    `);
    res.json(result.rows);
  } catch (error) {
    console.error('Fehler beim Abrufen der Essenspläne:', error);
    res.status(500).json({ success: false, message: 'Serverfehler beim Abrufen der Essenspläne' });
  }
});

// POST-Route zum Speichern des Essensplans
app.post('/api/essensplan', verifyToken, verifyAdmin, async (req, res) => {
  const { week_number, plan } = req.body;

  try {
    if (!week_number) {
      return res.status(400).json({ success: false, message: 'Wochennummer fehlt' });
    }

    for (const item of plan) {
      const { day_of_week, meal_id } = item;

      await pool.query(
        `INSERT INTO essensplan (week_number, day_of_week, meal_id)
         VALUES ($1, $2, $3)
         ON CONFLICT (week_number, day_of_week)
         DO UPDATE SET meal_id = EXCLUDED.meal_id;`,
        [week_number, day_of_week, meal_id]
      );
    }

    res.json({ success: true, message: 'Essensplan erfolgreich gespeichert' });
  } catch (error) {
    console.error('Fehler beim Speichern des Essensplans:', error);
    res.status(500).json({ success: false, message: 'Serverfehler beim Speichern des Essensplans' });
  }
});

// Starte den Server
app.listen(3001, () => {
  console.log('Server läuft auf Port 3001');
});
