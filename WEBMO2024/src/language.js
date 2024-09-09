// src/language.js
import { createI18n } from 'vue-i18n';

// Definieren Sie die Übersetzungen für die unterstützten Sprachen
const messages = {
  en: {
    pages: {
      essen_list: 'Food List',
      add_food: 'Add New Food',
      create_meal_plan: 'Create Meal Plan',
      food_price: 'Price',
      food_description: 'Description',
      food_name: 'Name',
      food_type: 'Type',
      please_select: 'Please select',
      meat: 'with meat',
      vegetarian: 'vegetarian',
      vegan: 'vegan',
      edit: 'Edit',
      delete: 'Delete',
      fill_all_fields: 'Please fill in all fields correctly!',
      no_token: 'No token found. Please log in again.',
      food_added: 'Food successfully added!',
      server_error: 'Server error: ',
    },
  },
  de: {
    pages: {
      essen_list: 'Essen Liste',
      add_food: 'Neues Essen hinzufügen',
      create_meal_plan: 'Essensplan erstellen',
      food_price: 'Preis',
      food_description: 'Beschreibung',
      food_name: 'Name',
      food_type: 'Art',
      please_select: 'Bitte wählen',
      meat: 'mit Fleisch',
      vegetarian: 'vegetarisch',
      vegan: 'vegan',
      edit: 'Bearbeiten',
      delete: 'Löschen',
      fill_all_fields: 'Bitte alle Felder korrekt ausfüllen!',
      no_token: 'Kein Token vorhanden. Bitte erneut anmelden.',
      food_added: 'Essen erfolgreich hinzugefügt!',
      server_error: 'Serverfehler: ',
    },
  },
};

// Erstellen Sie eine Instanz von i18n
const i18n = createI18n({
  locale: 'en', // Standard-Sprache
  messages, // Nachrichten-Objekt
});

export default i18n; // Standard-Export der i18n-Instanz
