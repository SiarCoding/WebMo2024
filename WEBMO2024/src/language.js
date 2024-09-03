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
      edit: 'Edit',
      delete: 'Delete',
      meat: 'with meat',
      vegetarian: 'vegetarian',
      vegan: 'vegan',
    },
  },
  de: {
    pages: {
      essen_list: 'Essen Liste',
      add_food: 'Neues Essen hinzufügen',
      create_meal_plan: 'Essensplan erstellen',
      food_price: 'Preis',
      food_description: 'Beschreibung',
      edit: 'Bearbeiten',
      delete: 'Löschen',
      meat: 'mit Fleisch',
      vegetarian: 'vegetarisch',
      vegan: 'vegan',
    },
  },
};

// Erstellen Sie eine Instanz von i18n
const i18n = createI18n({
  locale: 'en', // Standard-Sprache
  messages, // Nachrichten-Objekt
});

export default i18n; // Standard-Export der i18n-Instanz
