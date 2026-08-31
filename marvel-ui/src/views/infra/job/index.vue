<template>
  <v-card class="border-thin">
    <v-toolbar flat density="comfortable" color="transparent">
      <v-toolbar-title class="text-h6 font-bold">定时任务</v-toolbar-title>
      <v-spacer />
      <v-text-field
        v-model="query.jobName"
        label="任务名称"
        density="compact"
        hide-details
        class="mr-2"
        style="max-width: 160px"
        @keyup.enter="load"
      />
      <v-btn color="primary" prepend-icon="mdi-magnify" @click="load">搜索</v-btn>
      <v-btn
        v-if="auth.hasPerm('infra:job:add')"
        color="success"
        prepend-icon="mdi-plus"
        class="ml-2"
        @click="openAdd"
      >
        新增
      </v-btn>
    </v-toolbar>

    <v-data-table :headers="headers" :items="rows" item-value="jobId" :loading="loading" hover>
      <template #item.status="{ item }">
        <v-chip :color="item.status === '0' ? 'success' : 'grey'" size="small" label>
          {{ item.status === '0' ? '运行中' : '已暂停' }}
        </v-chip>
      </template>
      <template #item.actions="{ item }">
        <v-tooltip v-if="auth.hasPerm('infra:job:run')" text="立即执行">
          <template #activator="{ props: p }">
            <v-icon v-bind="p" icon="mdi-play" size="18" class="mr-3 text-success" @click="onRun(item)" />
          </template>
        </v-tooltip>
        <v-tooltip v-if="auth.hasPerm('infra:job:list')" text="执行日志">
          <template #activator="{ props: p }">
            <v-icon v-bind="p" icon="mdi-text-box-outline" size="18" class="mr-3 text-secondary" @click="openLogs(item)" />
          </template>
        </v-tooltip>
        <v-tooltip v-if="auth.hasPerm('infra:job:edit')" :text="item.status === '0' ? '暂停' : '恢复'">
          <template #activator="{ props: p }">
            <v-icon
              v-bind="p"
              :icon="item.status === '0' ? 'mdi-pause' : 'mdi-play-outline'"
              size="18"
              class="mr-3 text-warning"
              @click="onToggleStatus(item)"
            />
          </template>
        </v-tooltip>
        <v-tooltip v-if="auth.hasPerm('infra:job:edit')" text="编辑">
          <template #activator="{ props: p }">
            <v-icon v-bind="p" icon="mdi-pencil" size="18" class="mr-3 text-secondary" @click="openEdit(item)" />
          </template>
        </v-tooltip>
        <v-tooltip v-if="auth.hasPerm('infra:job:remove')" text="删除">
          <template #activator="{ props: p }">
            <v-icon v-bind="p" icon="mdi-delete" size="18" class="text-error" @click="onDelete(item)" />
          </template>
        </v-tooltip>
      </template>
    </v-data-table>

    <!-- 任务编辑对话框 -->
    <v-dialog v-model="dialog" width="560">
      <v-card :title="form.jobId ? '修改任务' : '新增任务'" rounded="xl">
        <v-card-text>
          <v-text-field v-model="form.jobName" label="任务名称" />
          <v-text-field v-model="form.invokeTarget" label="调用目标（如 sampleJob.run）" />
          <v-text-field v-model="form.cronExpression" label="cron 表达式（如 0 * * * * ?）" />
          <v-radio-group v-model="form.status" inline label="状态">
            <v-radio label="运行中" value="0" />
            <v-radio label="暂停" value="1" />
          </v-radio-group>
          <v-text-field v-model="form.remark" label="备注" />
          <div class="text-caption text-medium-emphasis">
            调用目标为 Spring Bean 名 + 无参方法名；内置演示任务：sampleJob.run
          </div>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn @click="dialog = false">取消</v-btn>
          <v-btn color="primary" @click="onSave">保存</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- 执行日志对话框 -->
    <v-dialog v-model="logDialog" width="720">
      <v-card title="执行日志" rounded="xl">
        <v-card-text>
          <v-data-table
            :headers="logHeaders"
            :items="logs"
            item-value="jobLogId"
            :loading="logLoading"
            hover
            :items-per-page="8"
          >
            <template #item.status="{ item }">
              <v-chip :color="item.status === '0' ? 'success' : 'error'" size="small" label>
                {{ item.status === '0' ? '成功' : '失败' }}
              </v-chip>
            </template>
            <template #item.endTime="{ item }">
              {{ item.endTime }}
              <div v-if="item.errorMsg" class="text-caption text-error">{{ item.errorMsg }}</div>
            </template>
          </v-data-table>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn @click="logDialog = false">关闭</v-btn>
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
import type { SysJobLogRow, SysJobRow } from '@/types/api'

const auth = useAuthStore()
const rows = ref<SysJobRow[]>([])
const loading = ref(false)
const dialog = ref(false)
const logDialog = ref(false)
const logLoading = ref(false)
const logs = ref<SysJobLogRow[]>([])
const query = reactive({ jobName: '' })
const form = reactive<Partial<SysJobRow>>({})
const snack = reactive({ show: false, text: '', color: 'success' })

const headers = [
  { title: 'ID', key: 'jobId', width: 70 },
  { title: '任务名称', key: 'jobName' },
  { title: '调用目标', key: 'invokeTarget' },
  { title: 'cron 表达式', key: 'cronExpression' },
  { title: '状态', key: 'status', width: 90 },
  { title: '操作', key: 'actions', width: 200, sortable: false },
]

const logHeaders = [
  { title: '日志ID', key: 'jobLogId', width: 80 },
  { title: '状态', key: 'status', width: 80 },
  { title: '开始时间', key: 'startTime', width: 180 },
  { title: '结束时间', key: 'endTime' },
]

function notify(text: string, color: 'success' | 'error' = 'success'): void {
  Object.assign(snack, { show: true, text, color })
}

async function load(): Promise<void> {
  loading.value = true
  try {
    rows.value = await http.get<SysJobRow[]>('/infra/job/list', { params: { ...query } })
  } catch (e) {
    notify(e instanceof Error ? e.message : '加载失败', 'error')
  } finally {
    loading.value = false
  }
}

function openAdd(): void {
  clearObject(form)
  Object.assign(form, { status: '1', jobGroup: 'DEFAULT' })
  dialog.value = true
}

function openEdit(item: SysJobRow): void {
  clearObject(form)
  Object.assign(form, item)
  dialog.value = true
}

async function onSave(): Promise<void> {
  try {
    if (form.jobId) {
      await http.put<null>('/infra/job', form)
    } else {
      await http.post<null>('/infra/job', form)
    }
    dialog.value = false
    notify('保存成功')
    void load()
  } catch (e) {
    notify(e instanceof Error ? e.message : '保存失败', 'error')
  }
}

async function onRun(item: SysJobRow): Promise<void> {
  try {
    const cost = await http.put<number>(`/infra/job/run/${item.jobId}`)
    notify(`执行成功，耗时 ${cost}ms`)
    void load()
  } catch (e) {
    notify(e instanceof Error ? e.message : '执行失败', 'error')
  }
}

/** 运行中→暂停，暂停→恢复 */
async function onToggleStatus(item: SysJobRow): Promise<void> {
  const next = item.status === '0' ? '1' : '0'
  try {
    await http.put<null>(`/infra/job/changeStatus?jobId=${item.jobId}&status=${next}`)
    notify(next === '0' ? '已恢复' : '已暂停')
    void load()
  } catch (e) {
    notify(e instanceof Error ? e.message : '操作失败', 'error')
  }
}

async function onDelete(item: SysJobRow): Promise<void> {
  if (!window.confirm(`确认删除任务「${item.jobName}」？`)) return
  try {
    await http.delete<null>(`/infra/job/${item.jobId}`)
    notify('删除成功')
    void load()
  } catch (e) {
    notify(e instanceof Error ? e.message : '删除失败', 'error')
  }
}

async function openLogs(item: SysJobRow): Promise<void> {
  logDialog.value = true
  logLoading.value = true
  try {
    logs.value = await http.get<SysJobLogRow[]>(`/infra/job/logs/${item.jobId}`)
  } catch (e) {
    notify(e instanceof Error ? e.message : '加载失败', 'error')
  } finally {
    logLoading.value = false
  }
}

onMounted(() => {
  void load()
})
</script>
