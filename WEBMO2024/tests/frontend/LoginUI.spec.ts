import { test, expect } from '@playwright/test';

test.describe('Login UI Tests', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('http://localhost:5173');
  });

  test('Alle Eingabefelder und Schaltflächen sind sichtbar', async ({ page }) => {
    await expect(page.locator('input#username')).toBeVisible();
    await expect(page.locator('input#password')).toBeVisible();
    await expect(page.locator('select#role')).toBeVisible();
    await expect(page.locator('button[type="submit"]')).toBeVisible();
  });

  test('Standardwerte der Eingabefelder', async ({ page }) => {
    await expect(page.locator('input#username')).toHaveValue('');
    await expect(page.locator('input#password')).toHaveValue('');
    await expect(page.locator('select#role')).toHaveValue('');
  });
});
