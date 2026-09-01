import { defineStore } from 'pinia'
import vuetify from '@/plugins/vuetify'

const THEME_KEY = 'marvel-theme'
const PRIMARY_KEY = 'marvel-primary'
export const DEFAULT_PRIMARY = '#4F46E5'

/**
 * 应用全局 UI 状态：明暗主题、主题色、内容区全屏、当前页刷新 key。
 * 主题选择持久化于 localStorage，启动时由 vuetify.ts 读取恢复；
 * 主题色运行时修改 --v-theme-primary 变量，UnoCSS 侧颜色类自动联动。
 */
export const useAppStore = defineStore('app', {
  state: () => ({
    dark: localStorage.getItem(THEME_KEY) === 'dark',
    primary: localStorage.getItem(PRIMARY_KEY) ?? DEFAULT_PRIMARY,
    /** 主题配置抽屉开关 */
    themeDrawer: false,
    /** 内容区全屏：隐藏侧栏/顶栏/页签，仅保留 v-main 内容 */
    contentFullscreen: false,
    /** 当前页刷新 key：自增触发 router-view 内层 div 重建（页面重挂载重新拉数据） */
    reloadKey: 0,
  }),
  actions: {
    toggleTheme(): void {
      this.setDark(!this.dark)
    },

    setDark(dark: boolean): void {
      this.dark = dark
      vuetify.theme.global.name.value = dark ? 'dark' : 'light'
      localStorage.setItem(THEME_KEY, dark ? 'dark' : 'light')
    },

    /** 切换主题色：light/dark 两套主题同步生效 */
    setPrimary(color: string): void {
      this.primary = color
      const themes = vuetify.theme.themes.value
      themes.light.colors.primary = color
      themes.dark.colors.primary = color
      localStorage.setItem(PRIMARY_KEY, color)
    },

    /** 刷新当前页：页面组件整体重建 */
    reload(): void {
      this.reloadKey++
    },
  },
})
