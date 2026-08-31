<template>
  <v-card class="border-thin">
    <v-toolbar flat density="comfortable" color="transparent">
      <v-toolbar-title class="text-h6 font-bold">参数配置</v-toolbar-title>
      <v-spacer />
      <v-text-field
        v-model="query.configKey"
        label="参数键名"
        density="compact"
        hide-details
        class="mr-2"
        style="max-width: 160px"
        @keyup.enter="load"
      />
      <v-btn color="primary" prepend-icon="mdi-magnify" @click="load">搜索</v-btn>
      <v-btn
        v-if="auth.hasPerm('system:config:add')"
        color="success"
        prepend-icon="mdi-plus"
        class="ml-2"
        @click="openAdd"
      >
        新增
      </v-btn>
    </v-toolbar>

    <v-data-table :headers="headers" :items="rows" item-value="configId" :loading="loading" hover>
      <template #item.actions="{ item }">
        <v-tooltip v-if="auth.hasPerm('system:config:edit')" text="编辑">
          <template #activator="{ props: p }">
            <v-icon v-bind="p" icon="mdi-pencil" size="18" class="mr-3 text-secondary" @click="openEdit(item)" />
          </template>
        </v-tooltip>
        <v-tooltip v-if="auth.hasPerm('system:config:remove')" text="删除">
          <template #activator="{ props: p }">
            <v-icon v-bind="p" icon="mdi-delete" size="18" class="text-error" @click="onDelete(item)" />
          </template>
        </v-tooltip>
      </template>
    </v-data-table>

    <v-dialog v-model="dialog" width="520">
      <v-card :title="form.configId ? '修改参数' : '新增参数'" rounded="xl">
        <v-card-text>
          <v-text-field v-model="form.configName" label="参数名称" />
          <v-text-field v-model="form.configKey" label="参数键名" :disabled="!!form.configId" />
          <v-text-field v-model="form.configValue" label="参数键值" />
          <v-text-field v-model="form.remark" label="备注" />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn @click="dialog = false">取消</v-btn>
          <v-btn color="primary" @click="onSave">保存</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-snackbar v-model="snack.show" :color="snack.color" timeout="3000">{{ snack.text }}</v-snackbar>
  </v-card>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { http } from '@/api/request'
import { clearObject } from '@/utils/object'
import type { SysConfigRow } from '@/types/api'

const auth = useAuthStore()
const rows = ref<SysConfigRow[]>([])
const loading = ref(false)
const dialog = ref(false)
const query = reactive({ configKey: '' })
const form = reactive<Partial<SysConfigRow>>({})
const snack = reactive({ show: false, text: '', color: 'success' })

const headers = [
  { title: 'ID', key: 'configId', width: 70 },
  { title: '参数名称', key: 'configName' },
  { title: '参数键名', key: 'configKey' },
  { title: '参数键值', key: 'configValue' },
  { title: '备注', key: 'remark' },
  { title: '创建时间', key: 'createTime', width: 180 },
  { title: '操作', key: 'actions', width: 110, sortable: false },
]

function notify(text: string, color: 'success' | 'error' = 'success'): void {
  Object.assign(snack, { show: true, text, color })
}

async function load(): Promise<void> {
  loading.value = true
  try {
    rows.value = await http.get<SysConfigRow[]>('/system/config/list', { params: { ...query } })
  } catch (e) {
    notify(e instanceof Error ? e.message : '加载失败', 'error')
  } finally {
    loading.value = false
  }
}

function openAdd(): void {
  clearObject(form)
  dialog.value = true
}

function openEdit(item: SysConfigRow): void {
  clearObject(form)
  Object.assign(form, item)
  dialog.value = true
}

async function onSave(): Promise<void> {
  try {
    if (form.configId) {
      await http.put<null>('/system/config', form)
    } else {
      await http.post<null>('/system/config', form)
    }
    dialog.value = false
    notify('保存成功')
    void load()
  } catch (e) {
    notify(e instanceof Error ? e.message : '保存失败', 'error')
  }
}

async function onDelete(item: SysConfigRow): Promise<void> {
  if (!window.confirm(`确认删除参数「${item.configName}」？`)) return
  try {
    await http.delete<null>(`/system/config/${item.configId}`)
    notify('删除成功')
    void load()
  } catch (e) {
    notify(e instanceof Error ? e.message : '删除失败', 'error')
  }
}

onMounted(() => {
  void load()
})
</script>
