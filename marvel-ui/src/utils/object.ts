/** 清空对象全部字段（用于表单重置） */
export function clearObject<T extends object>(obj: T): void {
  for (const key of Object.keys(obj) as (keyof T)[]) {
    delete obj[key]
  }
}
