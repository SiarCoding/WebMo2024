const { Pool } = require('pg');

const pool = new Pool({
  user: 'sa', // benutzername
  host: 'localhost', //hostname der datenbamk
  database: 'webmo2024', // datenbankmame
  port: 5432, // standartport für Postgresql
});

module.exports = pool; // pool exportieren
