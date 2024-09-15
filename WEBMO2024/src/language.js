import { createI18n } from 'vue-i18n';

const messages = {
  en: {
    pages: {
      app_name: 'Food Management',
      essen_list: 'Food List',
      add_food: 'Add New Food',
      create_meal_plan: 'Create Meal Plan',
      view_meal_plans: 'Meal Plans',
      food_price: 'Price',
      food_description: 'Description',
      food_name: 'Name',
      food_type: 'Type',
      please_select: 'Please select',
      meat: 'with meat',
      vegetarian: 'vegetarian',
      vegan: 'vegan',
      edit: 'Edit',
      edit_food: 'Edit Food', // New entry for editing food
      delete: 'Delete',
      fill_all_fields: 'Please fill in all fields correctly!',
      no_token: 'No token found. Please log in again.',
      food_added: 'Food successfully added!',
      food_updated: 'Food updated successfully!',
      update_error: 'Error updating food',
      save_food: 'Save Food',
      server_error: 'Server error: ',
      loggedInAs: 'Logged in as',
      logout: 'Logout',
      view_food: 'View Food-List',
      select_week: 'Select week:',
      choose_food: 'Choose a meal',
      duplicate_error: 'Cannot select the same meal twice!',
      save_plan: 'Save Plan',
      plan_saved: 'Meal plan saved successfully!',
      plan_deleted: 'Meal plan successfully deleted',
      week: 'Week',
      total_price: 'Total Price',
      no_plan_for_week: 'No plan for week',
      invalid_plan_id: 'Invalid plan ID',
      change_language: 'Change Language',
      plan_created_at: 'Plan created on',
      days: {
        Montag: 'Monday',
        Dienstag: 'Tuesday',
        Mittwoch: 'Wednesday',
        Donnerstag: 'Thursday',
        Freitag: 'Friday',
        wochentag: 'Day of the Week'
      }
    },
  },
  de: {
    pages: {
      app_name: 'Essensverwaltung',
      essen_list: 'Essensliste',
      add_food: 'Neues Essen hinzufügen',
      create_meal_plan: 'Essensplan erstellen',
      view_meal_plans: 'Essenspläne',
      food_price: 'Preis',
      food_description: 'Beschreibung',
      food_name: 'Name',
      food_type: 'Art',
      please_select: 'Bitte wählen',
      meat: 'mit Fleisch',
      vegetarian: 'vegetarisch',
      vegan: 'vegan',
      edit: 'Bearbeiten',
      edit_food: 'Essen bearbeiten', // Neuer Eintrag für Essen bearbeiten
      delete: 'Löschen',
      fill_all_fields: 'Bitte alle Felder korrekt ausfüllen!',
      no_token: 'Kein Token vorhanden. Bitte erneut anmelden.',
      food_added: 'Essen erfolgreich hinzugefügt!',
      food_updated: 'Essen erfolgreich aktualisiert!',
      update_error: 'Fehler beim Aktualisieren des Essens',
      save_food: 'Essen speichern',
      server_error: 'Serverfehler: ',
      loggedInAs: 'Angemeldet als',
      logout: 'Abmelden',
      view_food: 'Essensliste anzeigen',
      select_week: 'Woche auswählen:',
      choose_food: 'Wählen Sie ein Essen',
      duplicate_error: 'Zwei mal das gleiche Essen ist nicht möglich!',
      save_plan: 'Plan speichern',
      plan_saved: 'Essensplan wurde erfolgreich gespeichert!',
      plan_deleted: 'Essensplan erfolgreich gelöscht',
      week: 'Woche',
      total_price: 'Gesamtpreis',
      no_plan_for_week: 'Kein Plan für Woche',
      invalid_plan_id: 'Ungültige Plan-ID',
      change_language: 'Sprache ändern',
      plan_created_at: 'Plan erstellt am',
      days: {
        Montag: 'Montag',
        Dienstag: 'Dienstag',
        Mittwoch: 'Mittwoch',
        Donnerstag: 'Donnerstag',
        Freitag: 'Freitag',
        wochentag: 'Wochentag'
      }
    },
  },
};

const i18n = createI18n({
  locale: 'de', // Default language
  fallbackLocale: 'en', // Fallback language
  messages,
});

export default i18n;
