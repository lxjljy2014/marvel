<template>
  <v-card>
    <v-toolbar flat density="comfortable" color="transparent">
      <v-toolbar-title class="text-h6 font-bold">用户管理</v-toolbar-title>
      <v-spacer />
      <v-text-field
        v-model="query.username"
        label="用户名"
        density="compact"
        hide-details
        class="mr-2" style="max-width: 190px"
        @keyup.enter="load"
      />
      <v-btn color="primary" prepend-icon="mdi-magnify" @click="load">搜索</v-btn>
      <v-btn
        v-if="auth.hasPerm('system:user:add')"
        color="success"
        prepend-icon="mdi-plus"
        class="ml-2"
        @click="openAdd"
      >
        新增
      </v-btn>
    </v-toolbar>

    <v-data-table-server
      :headers="headers"
      :items="rows"
      :items-length="total"
      :items-per-page="query.pageSize"
      :page="query.pageNum"
      :loading="loading"
      item-value="userId"
      hover
      @update:options="onOptions"
    >
      <template #item.status="{ item }">
        <v-chip :color="item.status === '0' ? 'success' : 'error'" size="small" label>
          {{ item.status === '0' ? '正常' : '停用' }}
        </v-chip>
      </template>
      <template #item.actions="{ item }">
        <v-tooltip v-if="auth.hasPerm('system:user:edit')" text="编辑">
          <template #activator="{ props: p }">
            <v-icon v-bind="p" icon="mdi-pencil" size="18" class="mr-3 text-secondary" @click="openEdit(item)" />
          </template>
        </v-tooltip>
        <v-tooltip v-if="auth.hasPerm('system:user:remove')" text="删除">
          <template #activator="{ props: p }">
            <v-icon v-bind="p" icon="mdi-delete" size="18" class="text-error" @click="onDelete(item)" />
          </template>
        </v-tooltip>
      </template>
    </v-data-table-server>

    <v-dialog v-model="dialog" width="560">
      <v-card :title="form.userId ? '修改用户' : '新增用户'" rounded="xl">
        <v-card-text>
          <v-text-field v-model="form.username" label="用户名" :disabled="!!form.userId" />
          <v-text-field v-model="form.nickname" label="昵称" />
          <v-text-field v-if="!form.userId" v-model="form.password" label="初始密码" type="password" />
          <v-text-field v-model="form.phone" label="手机号" />
          <v-select
            v-model="form.deptId"
            :items="deptOptions"
            item-title="deptName"
            item-value="deptId"
            label="部门"
          />
          <v-select
            v-model="roleIds"
            :items="roleOptions"
            item-title="roleName"
            item-value="roleId"
            label="角色"
            multiple
            chips
          />
          <v-radio-group v-model="form.status" inline label="状态">
            <v-radio label="正常" value="0" />
            <v-radio label="停用" value="1" />
          </v-radio-group>
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
import type {
  PageResult,
  SysDeptRow,
  SysRoleOption,
  SysUserRow,
  UserDetailResult,
} from '@/types/api'

const auth = useAuthStore()
const rows = ref<SysUserRow[]>([])
const total = ref(0)
const loading = ref(false)
const dialog = ref(false)
const roleIds = ref<number[]>([])
const roleOptions = ref<SysRoleOption[]>([])
const deptOptions = ref<SysDeptRow[]>([])

interface UserQuery {
  pageNum: number
  pageSize: number
  username: string
}
const query = reactive<UserQuery>({ pageNum: 1, pageSize: 10, username: '' })

/** 新增/编辑表单：userId 为空表示新增 */
const form = reactive<Partial<SysUserRow> & { password?: string }>({})

const snack = reactive({ show: false, text: '', color: 'success' })

const headers = [
  { title: 'ID', key: 'userId', width: 70 },
  { title: '用户名', key: 'username' },
  { title: '昵称', key: 'nickname' },
  { title: '手机号', key: 'phone' },
  { title: '状态', key: 'status', width: 90 },
  { title: '创建时间', key: 'createTime', width: 180 },
  { title: '操作', key: 'actions', width: 110, sortable: false },
]

function notify(text: string, color: 'success' | 'error' = 'success'): void {
  Object.assign(snack, { show: true, text, color })
}

async function load(): Promise<void> {
  loading.value = true
  try {
    const page = await http.get<PageResult<SysUserRow>>('/system/user/page', { params: { ...query } })
    rows.value = page.records
    total.value = page.total
  } catch (e) {
    notify(e instanceof Error ? e.message : '加载失败', 'error')
  } finally {
    loading.value = false
  }
}

function onOptions(opts: { page: number; itemsPerPage: number }): void {
  query.pageNum = opts.page
  query.pageSize = opts.itemsPerPage
  void load()
}

async function openAdd(): Promise<void> {
  clearObject(form)
  Object.assign(form, { status: '0' })
  roleIds.value = []
  dialog.value = true
  await loadOptions()
}

async function openEdit(item: SysUserRow): Promise<void> {
  const detail = await http.get<UserDetailResult>(`/system/user/${item.userId}`)
  clearObject(form)
  Object.assign(form, detail.user)
  roleIds.value = detail.roleIds
  dialog.value = true
  await loadOptions()
}

async function loadOptions(): Promise<void> {
  const [roles, depts] = await Promise.all([
    http.get<SysRoleOption[]>('/system/user/options/roles'),
    http.get<SysDeptRow[]>('/system/dept/list'),
  ])
  roleOptions.value = roles
  deptOptions.value = depts
}

async function onSave(): Promise<void> {
  try {
    const params = new URLSearchParams(roleIds.value.map((id) => ['roleIds', String(id)]))
    if (form.userId) {
      await http.put<null>(`/system/user?${params.toString()}`, form)
    } else {
      await http.post<null>(`/system/user?${params.toString()}`, form)
    }
    dialog.value = false
    notify('保存成功')
    void load()
  } catch (e) {
    notify(e instanceof Error ? e.message : '保存失败', 'error')
  }
}

async function onDelete(item: SysUserRow): Promise<void> {
  if (!window.confirm(`确认删除用户「${item.username}」？`)) return
  try {
    await http.delete<null>(`/system/user/${item.userId}`)
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
