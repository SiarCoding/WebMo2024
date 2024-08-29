import { test, expect } from '@playwright/test';

test.describe('Login Validation Tests', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('http://localhost:5173');
  });

  test('Fehlermeldung bei leeren Eingabefeldern', async ({ page }) => {
    await page.click('button[type="submit"]');
    await expect(page.locator('.text-danger')).toHaveText('Bitte wählen Sie eine Rolle aus.');
  });

  test('Fehlermeldung bei nicht ausgewählter Rolle', async ({ page }) => {
    await page.fill('input#username', 'testuser');
    await page.fill('input#password', 'password123');
    await page.click('button[type="submit"]');
    await expect(page.locator('.text-danger')).toHaveText('Bitte wählen Sie eine Rolle aus.');
  });

  test('Keine Fehlermeldung bei vollständigen Eingaben für Benutzer', async ({ page }) => {
    await page.fill('input#username', 'user');
    await page.fill('input#password', 'user123');
    await page.selectOption('select#role', 'user');
    await page.click('button[type="submit"]');
    await expect(page.locator('.text-danger')).toBeHidden();
  });

  test('Keine Fehlermeldung bei vollständigen Eingaben für Admin', async ({ page }) => {
    await page.fill('input#username', 'admin');
    await page.fill('input#password', 'admin123');
    await page.selectOption('select#role', 'admin');
    await page.click('button[type="submit"]');
    await expect(page.locator('.text-danger')).toBeHidden();
  });

  test('Fehlermeldung bei falschen Anmeldedaten', async ({ page }) => {
    await page.fill('input#username', 'falscheruser');
    await page.fill('input#password', 'falsch123');
    await page.selectOption('select#role', 'user');
    await page.click('button[type="submit"]');
    await expect(page.locator('.text-danger')).toHaveText('Ein Fehler ist aufgetreten');
  });
});
