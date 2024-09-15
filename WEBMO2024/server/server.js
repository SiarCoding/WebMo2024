const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');
const { Pool } = require('pg');
const jwt = require('jsonwebtoken');
const { verifyToken, verifyAdmin } = require('./middleware'); // Importiere die Middleware-Funktionen

const app = express(); //vereinfacht die verwaltung und die Erstellung von Routen
const pool = require('./db/db'); //db import

// Middleware Konfigurationen
app.use(bodyParser.json());

//Mithilfe von CORS können wir auf unser Frontend
app.use(cors({
  origin: 'http://localhost:5173', //Frontend übertragung
  methods: ['GET', 'POST', 'PUT', 'DELETE'],  //Diese HTTP-Methoden sind für die Anfragen unseres SQL Servers
  credentials: true //Cookies senden wir erlaubt
}));

// POST /login (POST-Route für die Login Funktionalität)
app.post('/login', async (req, res) => {
  const { username, password, role } = req.body; //enthält daten von Client

  try {
    const result = await pool.query('SELECT * FROM users WHERE username = $1', [username]); //sucht den Nutzer mit der richtigen user daten
    const user = result.rows[0]; //nimmt die erste zeile der abfrage 

    // Kontrollstruktur für user und token generierung
    if (user && user.password === password && user.role === role) {
      const token = jwt.sign({ id: user.id, username: user.username, role: user.role }, 'geheimnis', { expiresIn: '1h' });

      //Sind die Daten richtig dann kommt = JSON 201 Response (erfolgsmeldung)
      res.json({
        success: true,
        message: 'Login erfolgreich!',
        role: user.role,
        token, //Token wird erstellt
      });
    } else {
      res.status(401).json({ success: false, message: 'Ungültige Anmeldedaten oder falsche Rolle gewählt' }); //401 meldung wenn falsche daten
    }
  } catch (error) {
    console.error('Fehler beim Login:', error);
    res.status(500).json({ success: false, message: 'Serverfehler beim Login' }); //Wenn Server fehler auftritt 500 Statuscode wird gesendet an Client
  }
});

// GET api/essen: (GET-Route zum Abrufen aller Essen)
app.get('/api/essen', async (req, res) => {
  try {
    const result = await pool.query('SELECT * FROM food'); //Abrufen aller essen von der Tabelle food und speichern in result
    res.json(result.rows); //Json antwort zurück an client
  } catch (error) {
    console.error('Fehler beim Abrufen der Essen:', error); //401 falls die essen nicht abgerufen werden können
    res.status(500).json({ success: false, message: 'Serverfehler beim Abrufen der Essen' }); //Server fehler (500)
  }
});

// GET api/essen (GET-Route zum Abrufen eines einzelnen Essens)
app.get('/api/essen/:id', async (req, res) => {
  const { id } = req.params; //ID aus url wird genommen und in id gespeichert
  try {
    const result = await pool.query('SELECT name, price, type FROM food WHERE id = $1', [id]); //das essen aufrufen mit der entsprechend ID
    
    if (result.rows.length === 0) { //Wenn das Essen nicht in der tabelle nicht gefunden wird 
      return res.status(404).json({ success: false, message: 'Essen nicht gefunden' }); //Erhalten wir 404 Status code 
    }

    // Falls ja werden die Daten als json zurückgesenden 
    res.json(result.rows[0]);
  } catch (error) {
    console.error('Fehler beim Abrufen des Essens:', error); //fehlerbehandlung
    res.status(500).json({ success: false, message: 'Serverfehler beim Abrufen des Essens' }); //Serverfehler (500)
  }
});


// POST api/essen (POST-Route zum Hinzufügen von Essen (nur für Admins))
app.post('/api/essen', verifyToken, verifyAdmin, async (req, res) => { //nur admin können diese einträge erstellen mit der entsprechenden token aus middleware die erstellt wird 
  const { name, preis, art } = req.body; //variablen in anfragekörper speichern

  try {
    const countResult = await pool.query('SELECT COUNT(*) FROM food'); // wir zählen wie viele essenseinträge in der tabelle sind
    const currentCount = parseInt(countResult.rows[0].count, 10); //Zähler der Essenseinträge wird als zahl gespeichert

    //limitieren auf 10 Essen 
    if (currentCount >= 10) { 
      return res.status(400).json({ success: false, message: 'Darf nicht mehr als 10 sein.' });  //400 Status Code wenn mehr als 10 Essen erstellt werden
    }
    //Wenn weniger als 10 einträge vorhanden sind wird es dann mit INSERT ein neuer einträgt hinzufügt
    const result = await pool.query(
      'INSERT INTO food (name, price, type) VALUES ($1, $2, $3) RETURNING *',
      [name, preis, art]
    );

    //das neue Essen wird dann gespeichert und aks 201 Response zurückgegeben
    const neuesEssen = result.rows[0];
    res.status(201).json({
      success: true,
      message: 'Essen erfolgreich hinzugefügt',
      essen: neuesEssen
    });

    //Fehlerbehanldung
  } catch (error) {
    console.error('Fehler beim Hinzufügen des Essens:', error);
    res.status(500).json({ success: false, message: 'Serverfehler beim Hinzufügen des Essens' });
  }
});

// PUT /essen/id (PUT-Route zum Bearbeiten eines Essens (nur für Admins))
app.put('/api/essen/:id', verifyToken, verifyAdmin, async (req, res) => {
  const { id } = req.params;
  const { name, preis, art } = req.body;

  // Überprüfen ob alle Felder vorhanden sind und der Typ gültig ist
  if (!name || preis == null || !art) {
    return res.status(400).json({ success: false, message: 'Name, Preis und Art dürfen nicht leer sein.' });
  }

  const erlaubteArten = ['mit Fleisch', 'vegetarisch', 'vegan']; // nur diese Beschreibungen sind pro Essen möglich

  //Überprüfen ob der typ des essens gültig ist 
  if (!erlaubteArten.includes(art)) {
    return res.status(400).json({ success: false, message: 'Ungültiger Typ. Erlaubt sind: mit Fleisch, vegetarisch, vegan.' });
  }

  try {
    // exisitiert der user mit der entsprechend ID ?
    const result = await pool.query(
      'UPDATE food SET name = $1, price = $2, type = $3 WHERE id = $4 RETURNING *', // Bearbeitung des eintrages anhand der ID 
      [name, parseFloat(preis), art, id] // Preis als float datentypen speichern
    );

    if (result.rows.length === 0) { //Falls der eintrag nicht gefunden wurde dann erhält der client 404
      return res.status(404).json({ success: false, message: 'Essen nicht gefunden' });
    }

    res.json({ success: true, message: 'Essen erfolgreich aktualisiert', essen: result.rows[0] }); //JSON antwort für 201
  } catch (error) {
    console.error('Fehler beim Aktualisieren des Essens:', error);
    res.status(500).json({ success: false, message: 'Serverfehler beim Aktualisieren des Essens' });
  }
});





// DELETE /essen/id (DELETE-Route zum Löschen eines Essens (nur für Admins))
app.delete('/api/essen/:id', verifyToken, verifyAdmin, async (req, res) => {
  const { id } = req.params;

  try {
    const result = await pool.query('DELETE FROM food WHERE id = $1 RETURNING *', [id]); //DELETE Abfrage für den user mit der entsprechenden ID

    //checken ob das essem in der tabelle vorhanden ist
    if (result.rows.length === 0) {
      return res.status(404).json({ success: false, message: 'Essen nicht gefunden' });  //404 fehler
    }

    res.json({ success: true, message: 'Essen erfolgreich gelöscht' }); //201 Status code
  } catch (error) {
    console.error('Fehler beim Löschen des Essens:', error);
    res.status(500).json({ success: false, message: 'Serverfehler beim Löschen des Essens' }); //500 Server fehler
  }
});



// Funktion zum Überprüfen ob ein Essensplan für eine bestimmte woche existiert
async function checkIfPlanExists(wochennummer) {
  const result = await pool.query('SELECT plan_id FROM foodplan WHERE week_number = $1', [wochennummer]); //plan_id wird aus foodplan tabelle geprüft
  return result.rows.length > 0; //wenn es bereits eine zeile gibt wird sie uns gegeben
}

// Funktion zum Erstellen eines neuen Essensplans
async function createNewPlan(wochennummer) {
  const result = await pool.query('INSERT INTO foodplan (week_number) VALUES ($1) RETURNING plan_id', [wochennummer]); //
  return result.rows[0]?.plan_id;//der neue plan_id wird zurückgegeben
}

// Funktion zum Hinzufügen von Essen zum Plan
async function addFoodToPlan(mealPlanId, plan) {
  for (const { tag, essen_id } of plan) {
    if (!tag || !essen_id) { //überprüfung ob der tag und das essen vorhanden ist 
      throw new Error('Ungültige Einträge: Tag oder Essen-ID fehlen.');
    }
    await pool.query('INSERT INTO food_in_plan (plan_id, food_id, day_of_week) VALUES ($1, $2, $3)', [mealPlanId, essen_id, tag]); //Einträge werden in food_in_plan tabelle gespeichert 
  }
}

// POST-Route zum Erstellen eines neuen Essensplans (Nur für Admin Rolle)
app.post('/api/essensplan', verifyToken, verifyAdmin, async (req, res) => {
  const { wochennummer, plan } = req.body;

  // Validierung der Wochennummer -> kleiner als 1 oder mehr als 8 wochen nicht möglich
  if (!wochennummer || wochennummer < 1 || wochennummer > 8) {
    return res.status(400).json({ success: false, message: 'Ungültige Wochenzahl. Bitte eine Woche zwischen 1 und 8 wählen.' });
  }

  // Vermeidung von doppelten essen eintrages in die tabelle 
  const essenIds = plan.map(item => item.essen_id); //Eine liste der essen_id aus dem Plan wird erstellt.
  const duplicateEssenIds = essenIds.filter((item, index) => essenIds.indexOf(item) !== index); //prüft ob doppelte ID's in der liste gibt

  if (duplicateEssenIds.length > 0) { //wenn doppelte id's gefunden werden dann wird diese meldung angezeigt
    return res.status(400).json({ success: false, message: 'Doppelte Essen im Plan sind nicht erlaubt.' });
  }

  try {
    // prüfen ob für diese woche ein plan bereits exisitiert
    const planExists = await checkIfPlanExists(wochennummer);
    if (planExists) {
      return res.status(400).json({ success: false, message: `Ein Essensplan für Woche ${wochennummer} existiert bereits.` }); //fehlermledung wenn es bereits existiert
    }

    // Transaktion starten
    await pool.query('BEGIN');

    // Essensplan wird erstellt
    const mealPlanId = await createNewPlan(wochennummer); //funktion wird aufgerufen für die wochennummer und die plan id zurückgegeben
    if (!mealPlanId) {
      throw new Error('Fehler beim Erstellen des neuen Essensplans.'); //wenn kein plan erstellt werden kann dann ERROR
    }

    // Essen zum Plan hinzufügen
    await addFoodToPlan(mealPlanId, plan);

    // Transaktion abschließen
    await pool.query('COMMIT');

    res.status(201).json({ success: true, message: `Essensplan für Woche ${wochennummer} erfolgreich gespeichert` }); //201 Status code
  } catch (error) {
    // fehlerbehanldung: Transaktion rückgängig machen
    await pool.query('ROLLBACK'); //zurücksetzung der transaktion
    console.error('Fehler beim Speichern des Essensplans:', error);
    res.status(500).json({ success: false, message: 'Serverfehler beim Speichern des Essensplans', error: error.message }); //500 STATUS CODE
  }
});



// GET /api/essensplan (GET Request zum abrufen aller Essenspläne )
app.get('/api/essensplan', async (req, res) => {
  try {
    // Holt alle Essenspläne inkl. der einzelenen essensinträge in diesen Plänen
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
        // Falls der Plan noch nicht existiert, erstelle ihn
        plans[row.week_number] = {
          plan_id: row.plan_id, //die plan id
          wochennummer: row.week_number, //die wochennummer
          created_at: row.created_at,  // die zeit der planerstellung
          //defaultwerte: null
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
      // hier wird ein Essen für einen bestimmten Tag zu einem bestehenden Wochenplan hinzugefügt
      if (row.day_of_week && row.food_name) {
        plans[row.week_number].days[row.day_of_week] = {
          food_id: row.food_id,
          food_name: row.food_name,
          price: parseFloat(row.price), //typumwandlung für preis
          type: row.type
        };
        plans[row.week_number].total_price += parseFloat(row.price); //gesamtpreis des plans wird ermittelt die pro zeile sich addieren 
      }
    });

    //schickt alle plaene zurück
    res.json(Object.values(plans));
  } catch (error) {
    console.error('Fehler beim Abrufen der Essenspläne:', error);
    res.status(500).json({ success: false, message: 'Serverfehler beim Abrufen der Essenspläne' }); //500 server fehler
  }
});



// GET /api/essensplan/:week (Einen bestimmten Essensplan nach Woche abrufen)
app.get('/api/essensplan/:week', async (req, res) => {
  const wochennummer = parseInt(req.params.week, 10);

  if (isNaN(wochennummer) || wochennummer < 1 || wochennummer > 8) {
    return res.status(400).json({ success: false, message: 'Ungültige Wochenzahl.' });
  }

  try {
    // Holt den Plan für die bestimmte Woche
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
      created_at: result.rows[0].created_at, 
      days: {
        Montag: null,
        Dienstag: null,
        Mittwoch: null,
        Donnerstag: null,
        Freitag: null
      },
      total_price: 0
    };

    //hinzufügen des Essens für die wochentage in essensplan 
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

    // Schickt den gefundenen Plan als JSON zurück
    res.json(plan);
  } catch (error) {
    console.error('Fehler beim Abrufen des Essensplans:', error);
    res.status(500).json({ success: false, message: 'Serverfehler beim Abrufen des Essensplans' });
  }
});




// PUT /api/essensplan/:week (Einen bestehenden Essensplan aktualisieren)
app.put('/api/essensplan/:week', async (req, res) => {
  const wochennummer = parseInt(req.params.week, 10);
  const { plan } = req.body;

  if (isNaN(wochennummer) || wochennummer < 1 || wochennummer > 8) {
    return res.status(400).json({ success: false, message: 'Ungültige Wochenzahl.' });
  }

  try {
    await pool.query('BEGIN');

    // Überprüfen, ob ein Plan für die Woche existiert
    const checkExist = await pool.query('SELECT plan_id FROM foodplan WHERE week_number = $1', [wochennummer]);
    let mealPlanId;
    if (checkExist.rows.length > 0) {
      mealPlanId = checkExist.rows[0].plan_id;
      await pool.query('DELETE FROM food_in_plan WHERE plan_id = $1', [mealPlanId]);
    } else {
      // Falls es keinen Plan gibt, wird ein neuer erstellt
      const newPlan = await pool.query('INSERT INTO foodplan (week_number) VALUES ($1) RETURNING plan_id', [wochennummer]);
      mealPlanId = newPlan.rows[0].plan_id;
    }

    // Neuer Plan wird eingetragen
    for (const { tag, essen_id } of plan) {
      await pool.query(
        'INSERT INTO food_in_plan (plan_id, food_id, day_of_week) VALUES ($1, $2, $3)',
        [mealPlanId, essen_id, tag]
      );
    }

    await pool.query('COMMIT'); // Transaktion abschließen
    res.status(200).json({ success: true, message: 'Essensplan erfolgreich aktualisiert' });
  } catch (error) {
    await pool.query('ROLLBACK'); //Im Fehlerfall Transaktion zurückrollen
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

    // Lösche alle Einträge des Plans
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