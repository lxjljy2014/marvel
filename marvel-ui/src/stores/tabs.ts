import { defineStore } from 'pinia'

export interface TabItem {
  /** 路由完整路径，同时作为页签唯一标识 */
  path: string
  title: string
  icon?: string
  /** 固定页签（首页），不显示关闭按钮 */
  affix?: boolean
}

/**
 * 多页签 Store：路由切换时收录页签，支持关闭并计算关闭后的落点路由。
 * 首页固定不可关闭；重复访问不重复收录。
 */
export const useTabsStore = defineStore('tabs', {
  state: () => ({
    visited: [] as TabItem[],
  }),
  actions: {
    addTab(route: { path: string; meta?: { title?: string; icon?: string } }): void {
      const title = route.meta?.title
      if (!title) return
      if (this.visited.some((t) => t.path === route.path)) return
      this.visited.push({
        path: route.path,
        title,
        icon: route.meta?.icon,
        affix: route.path === '/',
      })
    },

    /**
     * 关闭页签；若关闭的是当前页，返回应跳转的相邻页签路径（优先左侧），
     * 否则返回 null（停留原地）。
     */
    removeTab(path: string, currentPath: string): string | null {
      const idx = this.visited.findIndex((t) => t.path === path)
      if (idx < 0 || this.visited[idx].affix) return null
      this.visited.splice(idx, 1)
      if (path !== currentPath) return null
      // 当前页被关：取左侧页签，越界则取右侧
      const next = this.visited[Math.max(0, idx - 1)] ?? this.visited[0]
      return next?.path ?? '/'
    },

    reset(): void {
      this.visited = []
    },
  },
})
