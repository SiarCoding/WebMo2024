<template>
  <div id="app">
    <nav class="navbar navbar-expand-lg navbar-light bg-custom shadow-sm">
      <div class="container">
        <a class="navbar-brand fw-bold text-light" href="#">
          <i class="bi bi-house-door-fill"></i> <!-- Home Icon -->
          {{ $t('pages.app_name') }}
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
          <ul class="navbar-nav me-auto">
            <!-- Essen List -->
            <li class="nav-item">
              <router-link 
                class="nav-link text-light" 
                :class="{ 'active bg-highlight': isActive('/essen') }" 
                to="/essen"
              >
                <i class="bi bi-list"></i> <!-- List Icon -->
                {{ $t('pages.essen_list') }}
              </router-link>
            </li>
            <!-- Add Food -->
            <li class="nav-item">
              <router-link 
                class="nav-link text-light" 
                :class="{ 'active bg-highlight': isActive('/add-essen') }" 
                to="/add-essen"
              >
                <i class="bi bi-plus-circle-fill"></i> <!-- Add Icon -->
                {{ $t('pages.add_food') }}
              </router-link>
            </li>
            <!-- Create Meal Plan -->
            <li class="nav-item">
              <router-link 
                class="nav-link text-light" 
                :class="{ 'active bg-highlight': isActive('/essensplan') }" 
                to="/essensplan"
              >
                <i class="bi bi-calendar-check"></i> <!-- Calendar Icon -->
                {{ $t('pages.create_meal_plan') }}
              </router-link>
            </li>
            <!-- View Meal Plans -->
            <li class="nav-item">
              <router-link 
                class="nav-link text-light" 
                :class="{ 'active bg-highlight': isActive('/plaene') }" 
                to="/plaene"
              >
                <i class="bi bi-file-earmark-text"></i> <!-- File Icon -->
                {{ $t('pages.view_meal_plans') }}
              </router-link>
            </li>
          </ul>

          <!-- User Info -->
          <span class="navbar-text text-light me-3">
            <i class="bi bi-person-circle"></i> <!-- User Icon -->
            {{ $t('pages.loggedInAs') }}: {{ userRole || 'User' }}
          </span>

          <!-- Language Switcher -->
          <select v-model="currentLocale" @change="changeLocale" class="form-select form-select-sm w-auto me-3">
            <option value="de">Deutsch</option>
            <option value="en">English</option>
          </select>

          <!-- Logout Button -->
          <button class="btn btn-outline-light btn-sm" @click="logout">
            <i class="bi bi-box-arrow-right"></i> <!-- Logout Icon -->
            {{ $t('pages.logout') }}
          </button>
        </div>
      </div>
    </nav>

    <div class="container mt-4">
      <router-view></router-view>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      currentLocale: localStorage.getItem('locale') || this.$i18n.locale, // Load saved locale or use default
    };
  },
  computed: {
    userRole() {
      return localStorage.getItem('role'); // Display user role
    }
  },
  watch: {
    currentLocale(newLocale) {
      this.$i18n.locale = newLocale; // Change language dynamically
      localStorage.setItem('locale', newLocale); // Save selected language in localStorage
    }
  },
  methods: {
    changeLocale() {
      this.$i18n.locale = this.currentLocale; // Update the language
    },
    logout() {
      localStorage.removeItem('role'); // Remove role on logout
      this.$router.push('/login'); // Redirect to login page
    },
    isActive(route) {
      return this.$route.path === route; // Return true if the route is active
    }
  },
  mounted() {
    // Set default locale to German on login
    if (!localStorage.getItem('locale')) {
      this.currentLocale = 'de';
      this.$i18n.locale = 'de';
    }
  }
};
</script>

<style>
/* Background color for Navbar */
.navbar.bg-custom {
  background-color: #343a40; /* Dark gray */
}

/* Navbar link text color */
.nav-link {
  color: white;
}

/* Gold hover effect */
.nav-link:hover {
  color: #ffc107;
}

/* Active menu styling */
.active-menu, .active.bg-highlight {
  border-bottom: 2px solid #ffc107;
  background-color: rgba(255, 193, 7, 0.2); /* Light yellow highlight */
}

/* User info text color */
.navbar-text {
  font-size: 0.9rem;
  color: white;
}

/* Logout button style */
.btn-outline-light {
  border-radius: 20px;
  color: white;
  border-color: white;
}

.btn-outline-light:hover {
  background-color: white;
  color: #343a40;
}
</style>
