<template>
  <!-- 页签栏：置于 v-app-bar 的 extension 区。左=页签（点击切换/关闭），右=刷新与内容全屏 -->
  <div class="flex items-center gap-1 px-2 bg-surface h-full w-full">
    <v-tabs
      :model-value="route.path"
      density="compact"
      color="primary"
      class="min-w-0"
      :show-arrows="false"
      align-tabs="center"
    >
      <v-tab
        v-for="t in tabs.visited"
        :key="t.path"
        :value="t.path"
        :to="t.path"
        rounded="lg"
        slim
        active-class="text-primary"
        class="text-none group"
      >
        <v-icon v-if="t.icon" :icon="t.icon" size="15" class="mr-1" />
        <span class="text-body-2">{{ t.title }}</span>
        <v-icon
          v-if="!t.affix"
          icon="mdi-close"
          size="14"
          class="ml-1 opacity-40 group-hover:opacity-100 transition-opacity"
          @click.prevent.stop="onClose(t.path)"
        />
      </v-tab>
    </v-tabs>
    <v-spacer />
    <v-tooltip text="刷新当前页" location="bottom">
      <template #activator="{ props }">
        <v-btn v-bind="props" icon="mdi-refresh" variant="text" size="small" rounded="lg" @click="app.reload()" />
      </template>
    </v-tooltip>
    <v-tooltip :text="app.contentFullscreen ? '退出内容全屏' : '内容全屏'" location="bottom">
      <template #activator="{ props }">
        <v-btn
          v-bind="props"
          :icon="app.contentFullscreen ? 'mdi-fullscreen-exit' : 'mdi-fullscreen'"
          variant="text"
          size="small"
          rounded="lg"
          @click="app.contentFullscreen = !app.contentFullscreen"
        />
      </template>
    </v-tooltip>
  </div>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { useTabsStore } from '@/stores/tabs'

const route = useRoute()
const router = useRouter()
const app = useAppStore()
const tabs = useTabsStore()

/** 关闭页签：关闭当前页时跳转到相邻页签 */
function onClose(path: string): void {
  const next = tabs.removeTab(path, route.path)
  if (next) void router.push(next)
}
</script>
