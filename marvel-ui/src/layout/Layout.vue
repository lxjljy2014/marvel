<template>
  <!-- 注意：v-app 只在 App.vue 根组件出现一次；本组件作为布局层
       直接提供 v-navigation-drawer / v-app-bar / v-main，
       再嵌套 v-app 会形成双重布局坐标系，导致整页随内容滚动 -->
  <v-navigation-drawer>
    <template #prepend>
      <div class="flex items-center gap-2 p-4">
        <v-avatar color="primary" size="34" rounded="lg">
          <v-icon icon="mdi-hexagon-multiple" size="20" />
        </v-avatar>
        <span class="text-h6 font-bold">Marvel Admin</span>
      </div>
      <v-divider />
    </template>
  
      <!-- color prop 定制激活态颜色（Vuetify 4：active-color 已弃用），无需覆盖底层样式 -->
      <v-list nav density="comfortable" color="primary">
        <!-- exact 必须加：首页是 layout 的空 path 默认子路由（'' 解析后与父级 '/' 同路径），
             vue-router 对默认子路由会退而匹配父级 record，导致任何子页面下首页都被判激活 -->
        <v-list-item
          prepend-icon="mdi-view-dashboard-outline"
          title="首页"
          :to="{ name: 'dashboard' }"
          exact
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
              <span class="text-caption font-weight-bold">{{ avatarText }}</span>
            </v-avatar>
            <span class="text-body-2">{{ auth.nickname }}</span>
            <v-icon icon="mdi-chevron-down" size="18" class="ml-1" />
          </v-btn>
        </template>
        <v-list nav density="compact" elevation="4">
          <v-list-item title="退出登录" prepend-icon="mdi-logout" @click="onLogout" />
        </v-list>
      </v-menu>
    </v-app-bar>

    <!-- scrollable：v-main 绝对定位铺满可视区（自动避开 app-bar/drawer），
         内容在 main 区域内部滚动而非整页滚动；配合容器 h-full 可做满高页面 -->
    <v-main scrollable>
      <v-container fluid class="h-full flex flex-col">
        <router-view v-slot="{ Component, route }">
          <v-scroll-x-transition mode="out-in">
            <div :key="route.path" class="flex-1 min-h-0 flex flex-col">
              <component :is="Component" />
            </div>
          </v-scroll-x-transition>
        </router-view>
      </v-container>
    </v-main>
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
