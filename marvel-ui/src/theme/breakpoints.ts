import type { DisplayThresholds } from 'vuetify'

/**
 * 断点单一事实来源：Vuetify（display.thresholds / settings.scss $grid-breakpoints）
 * 与 UnoCSS（presetWind4 theme.breakpoint）共用同一组数值，避免响应式工具类
 * 与 v-row/v-col/useDisplay 行为不一致。
 */

// 与 settings.scss 中的 $grid-breakpoints 保持一致
const breakpoints: DisplayThresholds = {
  xs: 0,
  sm: 600,
  md: 960,
  lg: 1280,
  xl: 1920,
  xxl: 2560,
}

export const forVuetify = breakpoints

export const forUnoCSS = Object.entries(breakpoints).reduce(
  (o, [key, value]) => ({ ...o, [key]: `${value}px` }),
  {} as Record<keyof DisplayThresholds, string>,
)
