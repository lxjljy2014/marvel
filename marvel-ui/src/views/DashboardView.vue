<template>
  <div>
    <v-row>
      <v-col v-for="card in cards" :key="card.title" cols="12" sm="6" md="3">
        <v-card class="p-5">
          <div class="flex items-center gap-4">
            <v-avatar :color="card.color" size="46" rounded="lg">
              <v-icon :icon="card.icon" size="24" />
            </v-avatar>
            <div>
              <div class="text-caption text-secondary">{{ card.title }}</div>
              <div class="text-h6 font-bold">{{ card.value }}</div>
            </div>
          </div>
        </v-card>
      </v-col>
    </v-row>

    <v-card class="mt-4 p-6">
      <div class="text-h6 font-bold mb-2">欢迎使用 Marvel 后台管理系统</div>
      <p class="text-body-2 text-secondary">
        当前登录：<span class="text-primary font-medium">{{ auth.nickname }}</span>，
        角色：{{ auth.roles.join('、') }}。
        系统采用 Spring Boot 4 模块化单体架构，按微服务边界拆分模块，可平滑演进至 Spring Cloud。
      </p>
    </v-card>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import type { MenuNode } from '@/types/api'

const auth = useAuthStore()

interface StatCard {
  title: string
  value: number | string
  icon: string
  color: string
}

/** 统计卡片数据源：角色/权限/菜单数量来自登录会话 */
const cards = computed<StatCard[]>(() => [
  { title: '角色数量', value: auth.roles.length, icon: 'mdi-account-key-outline', color: 'primary' },
  { title: '权限数量', value: auth.permissions.length, icon: 'mdi-shield-check-outline', color: 'success' },
  { title: '菜单数量', value: countMenus(auth.menus), icon: 'mdi-menu', color: 'warning' },
  { title: '系统版本', value: 'v1.0.0', icon: 'mdi-tag-outline', color: 'info' },
])

function countMenus(menus: MenuNode[]): number {
  return menus.reduce<number>((acc, m) => acc + 1 + countMenus(m.children ?? []), 0)
}
</script>
