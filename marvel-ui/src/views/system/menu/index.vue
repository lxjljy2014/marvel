<template>
  <v-card>
    <v-toolbar flat density="comfortable" color="transparent">
      <v-toolbar-title class="text-h6 font-bold">菜单管理</v-toolbar-title>
      <v-spacer />
      <v-btn
        v-if="auth.hasPerm('system:menu:add')"
        color="success"
        prepend-icon="mdi-plus"
        @click="openAdd()"
      >
        新增
      </v-btn>
    </v-toolbar>

    <v-data-table :headers="headers" :items="rows" item-value="menuId" :loading="loading" hover>
      <template #item.menuType="{ item }">
        <v-chip
          size="small"
          label
          :color="item.menuType === 'M' ? 'info' : item.menuType === 'C' ? 'primary' : 'default'"
        >
          {{ MENU_TYPE_TEXT[item.menuType] }}
        </v-chip>
      </template>
      <template #item.status="{ item }">
        <v-chip :color="item.status === '0' ? 'success' : 'error'" size="small" label>
          {{ item.status === '0' ? '正常' : '停用' }}
        </v-chip>
      </template>
      <template #item.actions="{ item }">
        <v-tooltip v-if="auth.hasPerm('system:menu:add')" text="添加子菜单">
          <template #activator="{ props: p }">
            <v-icon v-bind="p" icon="mdi-plus" size="18" class="mr-3 text-secondary" @click="openAdd(item)" />
          </template>
        </v-tooltip>
        <v-tooltip v-if="auth.hasPerm('system:menu:edit')" text="编辑">
          <template #activator="{ props: p }">
            <v-icon v-bind="p" icon="mdi-pencil" size="18" class="mr-3 text-secondary" @click="openEdit(item)" />
          </template>
        </v-tooltip>
        <v-tooltip v-if="auth.hasPerm('system:menu:remove')" text="删除">
          <template #activator="{ props: p }">
            <v-icon v-bind="p" icon="mdi-delete" size="18" class="text-error" @click="onDelete(item)" />
          </template>
        </v-tooltip>
      </template>
    </v-data-table>

    <v-dialog v-model="dialog" width="560">
      <v-card :title="form.menuId ? '修改菜单' : '新增菜单'" rounded="xl">
        <v-card-text>
          <v-select
            v-model="form.parentId"
            :items="parentOptions"
            item-title="menuName"
            item-value="menuId"
            label="上级菜单"
          />
          <v-text-field v-model="form.menuName" label="菜单名称" />
          <v-radio-group v-model="form.menuType" inline label="类型">
            <v-radio label="目录" value="M" />
            <v-radio label="菜单" value="C" />
            <v-radio label="按钮" value="F" />
          </v-radio-group>
          <v-text-field v-model="form.path" label="路由地址" />
          <v-text-field
            v-if="form.menuType === 'C'"
            v-model="form.component"
            label="组件路径（如 system/user/index）"
          />
          <v-text-field v-model="form.perms" label="权限标识" />
          <v-text-field v-model.number="form.orderNum" label="显示顺序" type="number" />
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
import type { SysMenuRow } from '@/types/api'

const MENU_TYPE_TEXT: Record<SysMenuRow['menuType'], string> = {
  M: '目录',
  C: '菜单',
  F: '按钮',
}

const auth = useAuthStore()
const rows = ref<SysMenuRow[]>([])
const loading = ref(false)
const dialog = ref(false)
const parentOptions = ref<SysMenuRow[]>([])
const form = reactive<Partial<SysMenuRow>>({})
const snack = reactive({ show: false, text: '', color: 'success' })

const headers = [
  { title: 'ID', key: 'menuId', width: 70 },
  { title: '菜单名称', key: 'menuName' },
  { title: '类型', key: 'menuType', width: 90 },
  { title: '路由地址', key: 'path' },
  { title: '权限标识', key: 'perms' },
  { title: '排序', key: 'orderNum', width: 80 },
  { title: '状态', key: 'status', width: 90 },
  { title: '操作', key: 'actions', width: 140, sortable: false },
]

function notify(text: string, color: 'success' | 'error' = 'success'): void {
  Object.assign(snack, { show: true, text, color })
}

async function load(): Promise<void> {
  loading.value = true
  try {
    rows.value = await http.get<SysMenuRow[]>('/system/menu/list')
  } catch (e) {
    notify(e instanceof Error ? e.message : '加载失败', 'error')
  } finally {
    loading.value = false
  }
}

/** parent 为空表示新增根节点 */
async function openAdd(parent?: SysMenuRow): Promise<void> {
  clearObject(form)
  Object.assign(form, {
    parentId: parent?.menuId ?? 0,
    menuType: 'C',
    orderNum: 0,
    status: '0',
  })
  parentOptions.value = [{ menuId: 0, menuName: '根目录' } as SysMenuRow, ...rows.value]
  dialog.value = true
}

async function openEdit(item: SysMenuRow): Promise<void> {
  clearObject(form)
  Object.assign(form, item)
  parentOptions.value = [{ menuId: 0, menuName: '根目录' } as SysMenuRow, ...rows.value]
  dialog.value = true
}

async function onSave(): Promise<void> {
  try {
    if (form.menuId) {
      await http.put<null>('/system/menu', form)
    } else {
      await http.post<null>('/system/menu', form)
    }
    dialog.value = false
    notify('保存成功')
    void load()
  } catch (e) {
    notify(e instanceof Error ? e.message : '保存失败', 'error')
  }
}

async function onDelete(item: SysMenuRow): Promise<void> {
  if (!window.confirm(`确认删除菜单「${item.menuName}」？`)) return
  try {
    await http.delete<null>(`/system/menu/${item.menuId}`)
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
