<template>
  <!-- 列表页搜索卡：标题 + 展开/收起 + 折叠动画的字段区。
       默认插槽放 v-col 搜索字段（外层已提供 v-row），按钮区固定在底部 -->
  <v-card rounded="lg">
    <v-toolbar flat density="comfortable" color="transparent">
      <v-toolbar-title class="text-subtitle-1 font-weight-bold">搜索条件</v-toolbar-title>
      <v-spacer />
      <v-btn
        variant="text"
        rounded="lg"
        :append-icon="expanded ? 'mdi-chevron-up' : 'mdi-chevron-down'"
        @click="expanded = !expanded"
      >
        {{ expanded ? '收起' : '展开' }}
      </v-btn>
    </v-toolbar>

    <!-- v-expand-transition 配 v-show 实现平滑折叠/展开 -->
    <v-expand-transition>
      <div v-show="expanded">
        <v-divider />
        <v-card-text>
          <v-row>
            <slot />
            <v-col cols="12" class="text-right">
              <v-btn color="primary" prepend-icon="mdi-magnify" @click="emit('search')">搜索</v-btn>
              <v-btn class="ml-3" prepend-icon="mdi-refresh" @click="emit('reset')">重置</v-btn>
            </v-col>
          </v-row>
        </v-card-text>
      </div>
    </v-expand-transition>
  </v-card>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const emit = defineEmits<{ search: []; reset: [] }>()

/** 默认展开；纯 UI 状态，无需页面控制 */
const expanded = ref(true)
</script>
