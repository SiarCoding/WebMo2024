<template>
  <div id="app">
    <nav class="navbar navbar-expand-lg navbar-light bg-custom shadow-sm">
      <div class="container">
        <a class="navbar-brand fw-bold text-light" href="#">
          <i class="bi bi-house-door-fill"></i>
          {{ $t('pages.app_name') }}
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
          <ul class="navbar-nav me-auto">
            <li class="nav-item">
              <router-link class="nav-link text-light" :class="{ 'active bg-highlight': isActive('/essen') }" to="/essen">
                <i class="bi bi-list"></i>
                {{ $t('pages.essen_list') }}
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link text-light" :class="{ 'active bg-highlight': isActive('/add-essen') }" to="/add-essen">
                <i class="bi bi-plus-circle-fill"></i>
                {{ $t('pages.add_food') }}
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link text-light" :class="{ 'active bg-highlight': isActive('/essensplan') }" to="/essensplan">
                <i class="bi bi-calendar-check"></i>
                {{ $t('pages.create_meal_plan') }}
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link text-light" :class="{ 'active bg-highlight': isActive('/plaene') }" to="/plaene">
                <i class="bi bi-file-earmark-text"></i>
                {{ $t('pages.view_meal_plans') }}
              </router-link>
            </li>
          </ul>

          <span class="navbar-text text-light me-3">
            <i class="bi bi-person-circle"></i>
            {{ $t('pages.loggedInAs') }}: {{ userRole || 'User' }}
          </span>

          <select v-model="currentLocale" @change="changeLocale" class="form-select form-select-sm w-auto me-3">
            <option value="de">Deutsch</option>
            <option value="en">English</option>
          </select>

          <button class="btn btn-outline-light btn-sm" @click="logout">
            <i class="bi bi-box-arrow-right"></i>
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
      currentLocale: localStorage.getItem('locale') || this.$i18n.locale,
    };
  },
  computed: {
    userRole() {
      return localStorage.getItem('role');
    }
  },
  watch: {
    currentLocale(newLocale) {
      this.$i18n.locale = newLocale;
      localStorage.setItem('locale', newLocale);
    }
  },
  methods: {
    changeLocale() {
      this.$i18n.locale = this.currentLocale;
    },
    logout() {
      localStorage.removeItem('role');
      this.$router.push('/login');
    },
    isActive(route) {
      return this.$route.path === route;
    }
  },
  mounted() {
    if (!localStorage.getItem('locale')) {
      this.currentLocale = 'de';
      this.$i18n.locale = 'de';
    }
  }
};
</script>

<style>
.navbar.bg-custom {
  background-color: #343a40;
}

.nav-link {
  color: white;
}

.nav-link:hover {
  color: #ffc107;
}

.active-menu, .active.bg-highlight {
  border-bottom: 2px solid #ffc107;
  background-color: rgba(255, 193, 7, 0.2);
}

.navbar-text {
  font-size: 0.9rem;
  color: white;
}

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
