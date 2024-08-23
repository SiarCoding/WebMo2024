<template>
  <div class="container mt-5">
    <div class="row justify-content-center">
      <div class="col-md-6">
        <div class="card shadow-sm">
          <div class="card-body">
            <h2 class="card-title text-center mb-4">Login</h2>
            <form @submit.prevent="login" class="needs-validation" novalidate>
              <div class="mb-3">
                <label for="username" class="form-label">Username:</label>
                <input id="username" type="text" v-model="username" class="form-control" required />
              </div>

              <div class="mb-3">
                <label for="password" class="form-label">Password:</label>
                <input id="password" type="password" v-model="password" class="form-control" required />
              </div>

              <div class="mb-3">
                <label for="role" class="form-label">Role:</label>
                <select id="role" v-model="role" class="form-select" required>
                  <option value="">Select Role</option>
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
      try {
        const response = await axios.post('http://localhost:3001/login', {
          username: this.username,
          password: this.password,
          role: this.role,
        });

        if (response.data.success) {
          alert('Login erfolgreich!');

          // Speichere die Rolle des Benutzers
          localStorage.setItem('role', response.data.role);

          if (response.data.role === 'admin') {
            // Weiterleitung zur Admin-Essen-Seite
            this.$router.push('/essen');
          } else {
            // Weiterleitung zur User-Ansicht der Essensliste
            this.$router.push('/essen-user');
          }
        } else {
          this.errorMessage = response.data.message;
        }
      } catch (error) {
        this.errorMessage = 'Ein Fehler ist aufgetreten';
        console.error('Login-Fehler:', error);
      }
    },
  },
};
</script>

<style scoped>
/* Optional: Zusätzliche Stile für eine persönliche Note */
.card {
  background-color: #f9f9f9;
  border-radius: 15px;
  border: none;
}

.card-title {
  font-weight: bold;
  color: #333;
}

.btn-primary {
  background-color: #007bff;
  border-color: #007bff;
}

.btn-primary:hover {
  background-color: #0056b3;
  border-color: #0056b3;
}

.text-danger {
  font-weight: bold;
}
</style>
