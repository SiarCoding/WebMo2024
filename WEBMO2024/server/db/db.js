const { Pool } = require('pg');

const pool = new Pool({
  user: 'sa', // Benutzername
  host: 'db', // Hostname der Datenbank
  database: 'webmo2024', // Datenbankname
  password: '123', // Korrekt: Schlüsselwort ist "password"
  port: 5432, // Standardport für PostgreSQL
});

module.exports = pool;