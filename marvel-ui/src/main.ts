import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import vuetify from './plugins/vuetify'
// UnoCSS 产物（映射到 uno-* 级联层，层序见 public/layers.css）
import 'virtual:uno.css'
import 'unfonts.css'

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.use(vuetify)
app.mount('#app')
