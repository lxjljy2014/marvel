<template>
  <!-- 注意：v-app 只在 App.vue 根组件出现一次；本组件作为布局层
       直接提供 v-navigation-drawer / v-app-bar / v-main，
       再嵌套 v-app 会形成双重布局坐标系，导致整页随内容滚动 -->
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
              <span class="text-caption font-weight-bold">{{ avatarText }}</span>
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

    <!-- scrollable：v-main 绝对定位铺满可视区（自动避开 app-bar/drawer），
         内容在 main 区域内部滚动而非整页滚动；配合容器 h-full 可做满高页面；
         relative 供页面过渡时离场页绝对定位叠放（见 page-transition.css） -->
    <v-main scrollable>
      <v-container fluid class="p-5 h-full flex flex-col relative">
        <!-- 页面切换过渡（原生 Transition + 自定义样式，见 styles/page-transition.css）。
             不用 mode=out-in：与懒加载路由组件组合时 leave 完成后的重渲染链路
             会静默断裂（新页永不插入、整页空白且不可恢复），改为新旧同时过渡、
             离场页绝对定位叠放滑出，视觉等效且无死锁风险 -->
        <router-view v-slot="{ Component, route }">
          <Transition name="page">
            <component :is="Component" :key="route.path" />
          </Transition>
        </router-view>
      </v-container>
    </v-main>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import '@/styles/page-transition.css'

const auth = useAuthStore()
const router = useRouter()

/** 头像取昵称首字符 */
const avatarText = computed<string>(() => auth.nickname.slice(0, 1) || 'U')

async function onLogout(): Promise<void> {
  await auth.logout()
  router.push('/login')
}
</script>
