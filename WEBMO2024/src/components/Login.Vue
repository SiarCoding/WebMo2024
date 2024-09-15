<template>
  <div class="container mt-5">
    <div class="row justify-content-center">
      <div class="col-md-6">
        <div class="card shadow-sm">
          <div class="card-body">
            <h2 class="text-center mb-4">Login</h2>
            <form @submit.prevent="login">
              <div class="mb-3">
                <label for="username" class="form-label">Benutzername:</label>
                <input id="username" type="text" v-model="username" class="form-control" required />
              </div>

              <div class="mb-3">
                <label for="password" class="form-label">Passwort:</label>
                <input id="password" type="password" v-model="password" class="form-control" required />
              </div>

              <div class="mb-3">
                <label for="role" class="form-label">Rolle:</label>
                <select id="role" v-model="role" class="form-select" required>
                  <option value="">Rolle wählen</option>
                  <option value="admin">Admin</option>
                  <option value="user">User</option>
                </select>
              </div>

              <button type="submit" class="btn btn-primary w-100">Login</button>
            </form>
            <p v-if="errorMessage" class="text-danger text-center mt-3">{{ errorMessage }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      username: '',
      password: '',
      role: '',
      errorMessage: '',
    };
  },
  methods: {
    async login() {
      if (!this.role) {
        this.errorMessage = 'Bitte wählen Sie eine Rolle aus.';
        return;
      }

      try {
        const response = await axios.post('http://localhost:3001/login', {
          username: this.username,
          password: this.password,
          role: this.role,
        });

        if (response.data.success) {
          alert('Login erfolgreich!');
          localStorage.setItem('token', response.data.token);
          localStorage.setItem('role', response.data.role);
          this.$router.push('/essen');
        } else {
          this.errorMessage = response.data.message;
        }
      } catch (error) {
        console.error('Login-Fehler:', error);
        this.errorMessage = 'Ein Fehler ist aufgetreten';
      }
    }
  },
};
</script>

<style scoped>
.card {
  background-color: #f9f9f9;
  border-radius: 15px;
}

.btn-primary:hover {
  background-color: #0056b3;
}

.text-danger {
  font-weight: bold;
}
</style>
