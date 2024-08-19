import type { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.example.app',
  appName: 'WEBMO2024',
  webDir: 'dist',
  ios: {
    "cordovaLinkerFlags": []
  },
  server: {
    url: "http://localhost:3001",
    cleartext: true
  }
};

export default config;
