<template>
  <div>
    <h1 class="page-title">欢迎回来，{{ userName }}</h1>

    <!-- 统计卡片 -->
    <div class="stat-grid">
      <div class="stat-card">
        <div class="stat-value">{{ stats.totalHouses ?? '-' }}</div>
        <div class="stat-label">房屋总数</div>
      </div>
      <div class="stat-card">
        <div class="stat-value" style="color:#2ed573;">{{ stats.emptyHouse ?? '-' }}</div>
        <div class="stat-label">空房数量</div>
      </div>
      <div class="stat-card">
        <div class="stat-value" style="color:#ffa502;">{{ stats.allocatedHouse ?? '-' }}</div>
        <div class="stat-label">已分配</div>
      </div>
      <div class="stat-card">
        <div class="stat-value" style="color:#ff4757;">{{ pendingCount }}</div>
        <div class="stat-label">待审批申请</div>
      </div>
    </div>

    <!-- 住房总览 -->
    <div v-if="summary" class="stat-grid" style="grid-template-columns: repeat(3, 1fr);">
      <div class="stat-card">
        <div class="stat-value">{{ summary.total_area ?? '-' }} <span style="font-size:14px;">㎡</span></div>
        <div class="stat-label">总面积</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ summary.avg_rent_per_m2?.toFixed(2) ?? '-' }} <span style="font-size:14px;">元</span></div>
        <div class="stat-label">平均租金单价</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ userCount }}</div>
        <div class="stat-label">注册用户</div>
      </div>
    </div>

    <!-- 快捷入口 -->
    <div class="glass-card">
      <div class="card-title">快捷操作</div>
      <div style="display:flex; gap:12px; flex-wrap:wrap;">
        <button class="btn btn-primary" @click="$emit('nav','application')">申请管理</button>
        <button class="btn btn-outline" @click="$emit('nav','record')">住房记录</button>
        <template v-if="user.role === 'admin'">
          <button class="btn btn-outline" @click="$emit('nav','house')">房屋管理</button>
          <button class="btn btn-outline" @click="$emit('nav','standard')">住房标准</button>
          <button class="btn btn-outline" @click="$emit('nav','stats')">统计查询</button>
          <button class="btn btn-outline" @click="$emit('nav','user')">用户管理</button>
        </template>
      </div>
    </div>

    <!-- 最近分配 -->
    <div class="glass-card" v-if="recentRecords.length">
      <div class="card-title">最近入住</div>
      <div class="table-wrap">
        <table>
          <thead><tr><th>户主</th><th>房号</th><th>面积</th><th>月租金</th><th>入住日期</th></tr></thead>
          <tbody>
            <tr v-for="r in recentRecords" :key="r.id">
              <td><strong>{{ r.userName }}</strong></td>
              <td>{{ r.houseTitle }}</td>
              <td class="col-center">{{ r.houseArea }}㎡</td>
              <td class="col-right">{{ r.rentAmount }} 元</td>
              <td>{{ r.moveInDate }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  inject: ['user'],
  data() {
    return { stats: {}, summary: null, pendingCount: 0, recentRecords: [], userCount: 0 }
  },
  computed: {
    userName() { return this.user?.name || '' }
  },
  mounted() {
    this.loadStats()
    this.loadPending()
    this.loadRecords()
    this.loadUsers()
  },
  methods: {
    loadStats() {
      fetch('http://localhost:8080/stats')
        .then(r => r.json())
        .then(d => {
          this.stats = { totalHouses: d.housingSummary?.total_houses, emptyHouse: d.emptyHouse, allocatedHouse: d.allocatedHouse }
          this.summary = d.housingSummary
        }).catch(() => {})
    },
    loadPending() {
      fetch('http://localhost:8080/applications')
        .then(r => r.json())
        .then(d => { this.pendingCount = d.filter(a => a.status === 'PENDING').length })
        .catch(() => {})
    },
    loadRecords() {
      fetch('http://localhost:8080/records')
        .then(r => r.json())
        .then(d => { this.recentRecords = d.slice(-5).reverse() })
        .catch(() => {})
    },
    loadUsers() {
      fetch('http://localhost:8080/users')
        .then(r => r.json())
        .then(d => { this.userCount = d.length })
        .catch(() => {})
    }
  }
}
</script>
