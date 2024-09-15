const { Pool } = require('pg');

const pool = new Pool({
    user: 'sa', // Benutzername
    host: 'db', // Hostname Lokal:localhost, docker:db
    database: 'webmo2024', // Datenbankname
    password: '123', // passwort
    port: 5432, // port
});

module.exports = pool;