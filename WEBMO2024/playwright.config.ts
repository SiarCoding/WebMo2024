import { defineConfig, devices } from '@playwright/test';

/**
 * See https://playwright.dev/docs/test-configuration.
 */
export default defineConfig({
  testDir: './tests',
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 2 : 0,
  workers: process.env.CI ? 1 : undefined,
  reporter: 'html',
  use: {
    trace: 'on-first-retry',
    headless: false,
  },

  projects: [
    // Frontend-Tests Konfiguration
    {
      name: 'Frontend Tests - Chromium',
      testDir: './tests/frontend', // Verzeichnis für Frontend-Tests
      use: {
        ...devices['Desktop Chrome'],
        baseURL: 'http://localhost:5173', // URL für das Frontend
      },
    },
    {
      name: 'Frontend Tests - Firefox',
      testDir: './tests/frontend',
      use: {
        ...devices['Desktop Firefox'],
        baseURL: 'http://localhost:5173',
      },
    },
    {
      name: 'Frontend Tests - Webkit',
      testDir: './tests/frontend',
      use: {
        ...devices['Desktop Safari'],
        baseURL: 'http://localhost:5173',
      },
    },

    // API-Tests Konfiguration
    {
      name: 'API Tests',
      testDir: './tests/server', // Verzeichnis für API-Tests
      use: {
        baseURL: 'http://localhost:3001', // URL für die API
      },
    },
  ],

  // Optional: Lokaler Entwicklungsserver für Frontend-Tests starten
  webServer: {
    command: 'npm run dev', // Befehl zum Starten deines Frontend-Servers
    url: 'http://localhost:5173',
    reuseExistingServer: !process.env.CI,
  },
});
