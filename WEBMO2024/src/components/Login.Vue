<!--Hier kommt die LOGIN Seite (/login). Verwendet wird hier kein Bootstrap für das Design. Im Templete kommt der HTML Code Abschnitt!
Den <script> nutzen wir für Syntax!!!-->
<template>
  <div class="login-container">
    <h2>Login</h2>
    <form @submit.prevent="login" class="login-form">
      <div class="form-group">
        <label for="username">Username:</label>
        <input id="username" type="text" v-model="username" required />
      </div>

      <div class="form-group">
        <label for="password">Password:</label>
        <input id="password" type="password" v-model="password" required />
      </div>

      <button type="submit" class="login-button">Login</button>
    </form>
    <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
  </div>
</template>

<script>
import axios from 'axios'; // Zuerst wird die axios-Bibliothek importiert, die für HTTP-Anfragen genutzt wird um daten zu senden an den server

export default {
  data() { //nun können wir mit data() die daten per default definieren. In dem fall haben wir hier leere Stringkette!
    return {
      username: '',
      password: '',
      errorMessage: '',  // Für Fehlermeldungen relevant
    };
  },
  methods: { // hier werden jegliche Methoden definiert fürs Templete
    async login() { //die login funktion/methode kommt nun
      try {
        const response = await axios.post('http://localhost:3000/login', { //wir senden einen POST-Request an den Server
          username: this.username, //username = username
          password: this.password, //password = password
        });

        if (response.data.success) { //falls wir erfolgreich sind
          alert('Login erfolgreich!'); //bekommen wir diese Meldung
          // Weiterleitung oder andere Aktionen
        } else {
          this.errorMessage = 'Ungültige Anmeldedaten'; //Falls nicht dann diese Error Message
        }
      } catch (error) {
        this.errorMessage = 'Ein Fehler ist aufgetreten'; //Mitteilung für ErrorMassage
        console.error('Login-Fehler:', error);
      }
    },
  },
};
</script>


<!-- hier können wir nun bisschen css schreiben und das template designen. Andernfalls können wir auch hier Bootstrap verwenden im Templete und den Style Teil sparen!-->

<style scoped>
.login-container {
  max-width: 400px;
  margin: 50px auto;
  padding: 30px;
  background-color: #ffffff;
  border-radius: 10px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  font-family: 'Roboto', sans-serif;
}

h2 {
  text-align: center;
  color: #333;
  font-size: 24px;
  margin-bottom: 30px;
}

.login-form {
  display: flex;
  flex-direction: column;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #555;
  font-size: 14px;
}

input[type="text"],
input[type="password"] {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 16px;
  box-sizing: border-box;
  transition: border-color 0.3s;
}

input[type="text"]:focus,
input[type="password"]:focus {
  border-color: #007bff;
  outline: none;
}

.login-button {
  padding: 12px;
  background-color: #007bff;
  color: #fff;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 18px;
  font-weight: 600;
  text-align: center;
  transition: background-color 0.3s ease;
}

.login-button:hover {
  background-color: #0056b3;
}

.error-message {
  color: #ff4d4d;
  text-align: center;
  margin-top: 20px;
  font-weight: 600;
  font-size: 14px;
}
</style>