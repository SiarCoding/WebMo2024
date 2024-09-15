# Backend Dockerfile für Node.js

FROM node:18-alpine

# Setze das Arbeitsverzeichnis
WORKDIR /usr/src/app

# Kopiere package.json und package-lock.json, um nur die Abhängigkeiten zu installieren
COPY package*.json ./

# Installiere die Abhängigkeiten
RUN npm install

# Kopiere den Rest des Codes
COPY . .

# Expose den Port 3001 für das Backend
EXPOSE 3001

# Starte das Backend
CMD ["node", "server.js"]
