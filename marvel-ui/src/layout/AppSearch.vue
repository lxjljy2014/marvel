<template>
  <!-- 顶栏菜单搜索：过滤动态菜单中的可跳转项（menuType=C），选中即导航 -->
  <v-menu v-model="open" :close-on-content-click="false" location="bottom end" :width="320">
    <template #activator="{ props: menuProps }">
      <v-tooltip text="菜单搜索" location="bottom">
        <template #activator="{ props: tipProps }">
          <v-btn v-bind="mergeProps(menuProps, tipProps)" icon="mdi-magnify" variant="text" rounded="lg" />
        </template>
      </v-tooltip>
    </template>

    <v-card rounded="lg" class="mt-2">
      <v-text-field
        v-model="keyword"
        placeholder="搜索菜单..."
        prepend-inner-icon="mdi-magnify"
        density="compact"
        variant="outlined"
        single-line
        hide-details
        autofocus
        flat
        class="ma-3 mb-0"
      />
      <v-list density="compact" max-height="320" class="overflow-y-auto py-1">
        <v-list-item
          v-for="m in matched"
          :key="m.path"
          :prepend-icon="m.icon || 'mdi-circle-small'"
          :title="m.title"
          rounded="lg"
          @click="go(m.path)"
        />
        <div v-if="!matched.length" class="pa-4 text-center text-caption text-secondary">无匹配菜单</div>
      </v-list>
    </v-card>
  </v-menu>
</template>

<script setup lang="ts">
import { computed, mergeProps, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import type { MenuNode } from '@/types/api'

interface SearchItem {
  title: string
  path: string
  icon?: string
}

const router = useRouter()
const auth = useAuthStore()
const open = ref(false)
const keyword = ref('')

/** 展开菜单树取全部可跳转菜单（C 类型），父级目录名拼进标题便于区分同名项 */
const allItems = computed<SearchItem[]>(() => {
  const out: SearchItem[] = []
  const walk = (nodes: MenuNode[], parent: string): void => {
    for (const n of nodes) {
      if (n.menuType === 'C' && n.status === '0') {
        out.push({
          title: parent ? `${parent} / ${n.menuName}` : n.menuName,
          path: `/${parent ? `${parent}/` : ''}${n.path}`,
          icon: n.icon,
        })
      }
      if (n.children?.length) walk(n.children, n.path)
    }
  }
  walk(auth.menus, '')
  return out
})

const matched = computed<SearchItem[]>(() => {
  const kw = keyword.value.trim().toLowerCase()
  const list = kw ? allItems.value.filter((m) => m.title.toLowerCase().includes(kw)) : allItems.value
  return list.slice(0, 30)
})

function go(path: string): void {
  open.value = false
  keyword.value = ''
  void router.push(path)
}
</script>
