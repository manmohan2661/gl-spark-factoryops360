import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';

// FactoryOps360 frontend build configuration.
// Business logic and API wiring are intentionally left out of this starter kit.
export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  server: {
    port: 5173,
  },
});
