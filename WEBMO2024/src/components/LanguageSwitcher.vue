<!-- src/components/LanguageSwitcher.vue. Das wird verwendent um sprachen wechseln zu können mithilfe von vue i18n-->
<template>
  <div class="d-flex justify-content-end p-3">
    <select class="form-select w-auto" v-model="currentLocale" @change="changeLocale">
      <option value="en">English</option>
      <option value="de">Deutsch</option>
    </select>
  </div>
</template>

<script>
export default {
  data() {
    return {
      currentLocale: this.$i18n.locale // Aktuelle Sprache von i18n
    };
  },
  methods: {
    changeLocale() {
      this.$i18n.locale = this.currentLocale; // Sprache in i18n ändern
    }
  }
};
</script>

<style scoped>

.form-select {
  width: auto;
}
</style>
