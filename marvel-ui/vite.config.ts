import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vuetify from 'vite-plugin-vuetify'
import UnoCSS from 'unocss/vite'
import ViteFonts from 'unplugin-fonts/vite'

export default defineConfig({
  resolve: {
    alias: { '@': fileURLToPath(new URL('./src', import.meta.url)) },
  },
  plugins: [
    vue(),
    // styles.configFile 指向 Vuetify SASS 设置（关闭内置 utilities，见 settings.scss）
    vuetify({ autoImport: true, styles: { configFile: 'src/styles/settings.scss' } }),
    ViteFonts({
      fontsource: {
        families: [
          {
            name: 'Roboto',
            weights: [100, 300, 400, 500, 700, 900],
            styles: ['normal', 'italic'],
          },
        ],
      },
    }),
    // UnoCSS presetWind4（Vuetify 官方集成，见 uno.config.ts 与 public/layers.css）
    UnoCSS(),
  ],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (p) => p.replace(/^\/api/, ''),
      },
      '/uploads': 'http://localhost:8080',
    },
  },
})
