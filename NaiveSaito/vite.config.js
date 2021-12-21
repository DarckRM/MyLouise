import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 8098
  },
  build: {
    chunkSizeWarningLimit:1500,
    outDir: 'dist/templates/'
  }

})
