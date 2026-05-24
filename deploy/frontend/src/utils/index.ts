export function maskName(name: string): string {
  if (!name) return '***'
  if (name.length <= 1) return name + '*'
  if (name.length === 2) return name[0] + '*'
  return name[0] + '***' + name[name.length - 1]
}

export const statusMap: Record<string, { text: string; color: string }> = {
  WAITING: { text: '等待中', color: '#409eff' },
  CURRENT: { text: '当前排单', color: '#67c23a' },
  PENDING_SETTLEMENT: { text: '待结算', color: '#e6a23c' },
  COMPLETED: { text: '已完结', color: '#909399' },
}

export const statusOptions = [
  { label: '等待中', value: 'WAITING' },
  { label: '当前排单', value: 'CURRENT' },
  { label: '待结算', value: 'PENDING_SETTLEMENT' },
  { label: '已完结', value: 'COMPLETED' },
]
