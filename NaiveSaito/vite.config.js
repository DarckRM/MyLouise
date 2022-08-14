import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  envDir: './config',
  plugins: [vue()],
  server: {
    host: '0.0.0.0',
    port: 8098
  },
  build: {
    chunkSizeWarningLimit:1500,
    outDir: 'dist/templates/'
  }

})
