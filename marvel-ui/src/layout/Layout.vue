<template>
  <v-app>
    <v-navigation-drawer>
      <div class="flex items-center gap-2 p-5">
        <v-avatar color="primary" size="34" rounded="lg">
          <v-icon icon="mdi-hexagon-multiple" size="20" />
        </v-avatar>
        <span class="text-h6 font-bold">Marvel Admin</span>
      </div>
      <v-divider class="mb-2" />
      <!-- color prop 定制激活态颜色（Vuetify 4：active-color 已弃用），无需覆盖底层样式 -->
      <v-list nav density="comfortable" class="px-2" color="primary">
        <v-list-item
          prepend-icon="mdi-view-dashboard-outline"
          title="首页"
          :to="{ name: 'dashboard' }"
          rounded="lg"
        />
        <template v-for="menu in auth.menus" :key="menu.id">
          <v-list-group :value="menu.menuName">
            <template #activator="{ props }">
              <v-list-item
                v-bind="props"
                :prepend-icon="menu.icon || 'mdi-menu'"
                :title="menu.menuName"
                rounded="lg"
              />
            </template>
            <v-list-item
              v-for="child in menu.children ?? []"
              :key="child.id"
              :title="child.menuName"
              :prepend-icon="child.icon || 'mdi-circle-small'"
              :to="{ path: `/${menu.path}/${child.path}` }"
              rounded="lg"
            />
          </v-list-group>
        </template>
      </v-list>

      <template #append>
        <div class="text-caption text-center p-4 opacity-60">v1.0.0 · Modular Monolith</div>
      </template>
    </v-navigation-drawer>

    <v-app-bar flat class="border-b border-gray-200">
      <v-spacer />
      <v-menu>
        <template #activator="{ props: menuProps }">
          <v-btn v-bind="menuProps" variant="text" rounded="lg" class="text-none mr-2">
            <v-avatar color="primary" size="28" class="mr-2">
              <span class="text-caption font-bold">{{ avatarText }}</span>
            </v-avatar>
            <span class="text-body-2">{{ auth.nickname }}</span>
            <v-icon icon="mdi-chevron-down" size="18" class="ml-1" />
          </v-btn>
        </template>
        <v-list density="compact" elevation="4">
          <v-list-item title="退出登录" prepend-icon="mdi-logout" @click="onLogout" />
        </v-list>
      </v-menu>
    </v-app-bar>

    <v-main>
      <v-container fluid class="p-5">
        <router-view />
      </v-container>
    </v-main>
  </v-app>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const router = useRouter()

/** 头像取昵称首字符 */
const avatarText = computed<string>(() => auth.nickname.slice(0, 1) || 'U')

async function onLogout(): Promise<void> {
  await auth.logout()
  router.push('/login')
}
</script>
