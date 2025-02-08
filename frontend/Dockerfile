FROM node:21-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM node:21-alpine
WORKDIR /app
COPY package*.json ./
RUN npm ci --omit=dev
COPY --from=build /app/next.config.ts ./
COPY --from=build /app/.next ./.next
EXPOSE 3000
CMD ["npm", "start"]
