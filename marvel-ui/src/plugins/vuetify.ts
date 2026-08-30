import '@mdi/font/css/materialdesignicons.css'
// Vuetify 基础样式（core reset/theme 变量），经 settings.scss 编译。
// 级联层顺序由 index.html 最先加载的 public/layers.css 声明，uno 与 vuetify 样式互不冲突。
import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import { forVuetify } from '../theme/breakpoints'

/**
 * Vuetify 主题：靛蓝主色 + 中性灰辅助。
 * UnoCSS 侧（presetWind4 theme.colors）通过 --v-theme-* 变量映射这里的主题色，
 * 改主题色全局联动；display.thresholds 与 UnoCSS 断点共用 breakpoints.ts。
 */
export default createVuetify({
  theme: {
    defaultTheme: 'light',
    themes: {
      light: {
        dark: false,
        colors: {
          primary: '#4F46E5',
          secondary: '#475569',
          accent: '#7C3AED',
          background: '#F6F7FB',
          surface: '#FFFFFF',
          success: '#16A34A',
          warning: '#D97706',
          error: '#DC2626',
          info: '#0284C7',
        },
      },
    },
  },
  display: {
    mobileBreakpoint: 'md',
    thresholds: forVuetify,
  },
  defaults: {
    VBtn: { rounded: 'lg' },
    // VCard 保持 Vuetify 默认（elevated 变体自带浅阴影），不做额外覆盖
    VCard: {
      elevation: 2
    },
    VTextField: { variant: 'outlined', density: 'comfortable' },
    VSelect: { variant: 'outlined', density: 'comfortable' },
    VDataTable: { rounded: 'lg' },
  },
})
