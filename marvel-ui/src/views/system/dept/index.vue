<template>
  <v-card>
    <v-toolbar flat density="comfortable" color="transparent">
      <v-toolbar-title class="text-h6 font-bold">部门管理</v-toolbar-title>
      <v-spacer />
      <v-btn
        v-if="auth.hasPerm('system:dept:add')"
        color="success"
        prepend-icon="mdi-plus"
        @click="openAdd()"
      >
        新增
      </v-btn>
    </v-toolbar>

    <v-data-table :headers="headers" :items="rows" item-value="deptId" :loading="loading" hover>
      <template #item.status="{ item }">
        <v-chip :color="item.status === '0' ? 'success' : 'error'" size="small" label>
          {{ item.status === '0' ? '正常' : '停用' }}
        </v-chip>
      </template>
      <template #item.actions="{ item }">
        <v-tooltip v-if="auth.hasPerm('system:dept:add')" text="添加子部门">
          <template #activator="{ props: p }">
            <v-icon v-bind="p" icon="mdi-plus" size="18" class="mr-3 text-secondary" @click="openAdd(item)" />
          </template>
        </v-tooltip>
        <v-tooltip v-if="auth.hasPerm('system:dept:edit')" text="编辑">
          <template #activator="{ props: p }">
            <v-icon v-bind="p" icon="mdi-pencil" size="18" class="mr-3 text-secondary" @click="openEdit(item)" />
          </template>
        </v-tooltip>
        <v-tooltip v-if="auth.hasPerm('system:dept:remove')" text="删除">
          <template #activator="{ props: p }">
            <v-icon v-bind="p" icon="mdi-delete" size="18" class="text-error" @click="onDelete(item)" />
          </template>
        </v-tooltip>
      </template>
    </v-data-table>

    <v-dialog v-model="dialog" width="560">
      <v-card :title="form.deptId ? '修改部门' : '新增部门'" rounded="xl">
        <v-card-text>
          <v-select
            v-model="form.parentId"
            :items="parentOptions"
            item-title="deptName"
            item-value="deptId"
            label="上级部门"
          />
          <v-text-field v-model="form.deptName" label="部门名称" />
          <v-text-field v-model.number="form.orderNum" label="显示顺序" type="number" />
          <v-text-field v-model="form.leader" label="负责人" />
          <v-text-field v-model="form.phone" label="联系电话" />
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
import type { SysDeptRow } from '@/types/api'

const auth = useAuthStore()
const rows = ref<SysDeptRow[]>([])
const loading = ref(false)
const dialog = ref(false)
const parentOptions = ref<SysDeptRow[]>([])
const form = reactive<Partial<SysDeptRow>>({})
const snack = reactive({ show: false, text: '', color: 'success' })

const headers = [
  { title: 'ID', key: 'deptId', width: 70 },
  { title: '部门名称', key: 'deptName' },
  { title: '负责人', key: 'leader' },
  { title: '排序', key: 'orderNum', width: 80 },
  { title: '状态', key: 'status', width: 90 },
  { title: '创建时间', key: 'createTime', width: 180 },
  { title: '操作', key: 'actions', width: 150, sortable: false },
]

function notify(text: string, color: 'success' | 'error' = 'success'): void {
  Object.assign(snack, { show: true, text, color })
}

async function load(): Promise<void> {
  loading.value = true
  try {
    rows.value = await http.get<SysDeptRow[]>('/system/dept/list')
  } catch (e) {
    notify(e instanceof Error ? e.message : '加载失败', 'error')
  } finally {
    loading.value = false
  }
}

async function openAdd(parent?: SysDeptRow): Promise<void> {
  clearObject(form)
  const defaultParent = rows.value[0]?.deptId ?? 0
  Object.assign(form, {
    parentId: parent?.deptId ?? defaultParent,
    orderNum: 0,
    status: '0',
  })
  parentOptions.value = [...rows.value]
  dialog.value = true
}

async function openEdit(item: SysDeptRow): Promise<void> {
  clearObject(form)
  Object.assign(form, item)
  parentOptions.value = [...rows.value]
  dialog.value = true
}

async function onSave(): Promise<void> {
  try {
    if (form.deptId) {
      await http.put<null>('/system/dept', form)
    } else {
      await http.post<null>('/system/dept', form)
    }
    dialog.value = false
    notify('保存成功')
    void load()
  } catch (e) {
    notify(e instanceof Error ? e.message : '保存失败', 'error')
  }
}

async function onDelete(item: SysDeptRow): Promise<void> {
  if (!window.confirm(`确认删除部门「${item.deptName}」？`)) return
  try {
    await http.delete<null>(`/system/dept/${item.deptId}`)
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
