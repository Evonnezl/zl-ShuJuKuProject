<template>
  <div>
    <h1 class="page-title">欢迎回来，{{ userName }}</h1>

    <!-- 统计卡片 -->
    <div class="stat-grid">
      <div class="stat-card">
        <div class="stat-value"><CountUp :to="stats.totalHouses ?? 0" /></div>
        <div class="stat-label">房屋总数</div>
      </div>
      <div class="stat-card">
        <div class="stat-value"><CountUp :to="stats.emptyHouse ?? 0" /></div>
        <div class="stat-label">空房数量</div>
      </div>
      <div class="stat-card">
        <div class="stat-value"><CountUp :to="stats.allocatedHouse ?? 0" /></div>
        <div class="stat-label">已分配</div>
      </div>
      <div class="stat-card">
        <div class="stat-value"><CountUp :to="pendingCount" /></div>
        <div class="stat-label">待审批申请</div>
      </div>
    </div>

    <!-- 迷你趋势图 -->
    <div style="display:grid; grid-template-columns:1fr 1fr; gap:16px; margin-bottom:20px;">
      <div class="glass-card" style="padding:16px;">
        <div class="card-title">各栋房屋数量</div>
        <div ref="buildingChart" style="width:100%; height:120px;"></div>
      </div>
      <div class="glass-card" style="padding:16px;">
        <div class="card-title">各栋平均面积</div>
        <div ref="areaChart" style="width:100%; height:120px;"></div>
      </div>
    </div>

    <!-- 入住率圆环 + 住房总览 -->
    <div style="display:grid; grid-template-columns:280px 1fr; gap:16px; margin-bottom:20px;">
      <!-- 入住率圆环 -->
      <div class="glass-card" style="display:flex; flex-direction:column; align-items:center; justify-content:center; padding:28px;">
        <div style="position:relative; width:180px; height:180px;">
          <svg viewBox="0 0 200 200" style="width:180px; height:180px; transform:rotate(-90deg);">
            <!-- 底色环 -->
            <circle cx="100" cy="100" r="86" fill="none"
              stroke="rgba(0,0,0,.06)" stroke-width="14" />
            <!-- 渐变进度环 -->
            <defs>
              <linearGradient id="ringGrad" x1="0%" y1="0%" x2="100%" y2="0%">
                <stop offset="0%" stop-color="#38bdf8" />
                <stop offset="50%" stop-color="#3b82f6" />
                <stop offset="100%" stop-color="#818cf8" />
              </linearGradient>
            </defs>
            <circle cx="100" cy="100" r="86" fill="none"
              stroke="url(#ringGrad)" stroke-width="14"
              stroke-linecap="round"
              :stroke-dasharray="circumference"
              :stroke-dashoffset="dashOffset"
              style="transition: stroke-dashoffset 1.5s cubic-bezier(.4,0,.2,1);" />
          </svg>
          <!-- 中心文字 -->
          <div style="position:absolute; inset:0; display:flex; flex-direction:column; align-items:center; justify-content:center;">
            <span style="font-size:42px; font-weight:700; color:#000; line-height:1;">{{ ratePercent }}<small style="font-size:16px;">%</small></span>
            <span style="font-size:11px; color:#000; margin-top:2px;">入住率</span>
          </div>
        </div>
        <div style="display:flex; gap:24px; margin-top:14px;">
          <div style="text-align:center;">
            <div style="font-size:12px; color:#000;">空房</div>
            <div style="font-size:16px; font-weight:700; color:#000;">{{ stats.emptyHouse ?? 0 }}</div>
          </div>
          <div style="text-align:center;">
            <div style="font-size:12px; color:#000;">已分配</div>
            <div style="font-size:16px; font-weight:700; color:#000;">{{ stats.allocatedHouse ?? 0 }}</div>
          </div>
        </div>
      </div>

      <!-- 右侧汇总卡片 -->
      <div style="display:flex; flex-direction:column; gap:16px;">
        <div class="stat-grid" style="grid-template-columns: repeat(3, 1fr); margin-bottom:0;">
          <div class="stat-card">
            <div class="stat-value"><CountUp :to="summary?.total_area ?? 0" /><span style="font-size:14px;"> ㎡</span></div>
            <div class="stat-label">总面积</div>
          </div>
          <div class="stat-card">
            <div class="stat-value"><CountUp :to="summary?.avg_rent_per_m2 ?? 0" :decimals="2" /><span style="font-size:14px;"> 元</span></div>
            <div class="stat-label">平均租金单价</div>
          </div>
          <div class="stat-card">
            <div class="stat-value"><CountUp :to="userCount" /></div>
            <div class="stat-label">注册用户</div>
          </div>
        </div>
        <!-- 快捷入口提到这里 -->
        <div class="glass-card" style="flex:1;">
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
import * as echarts from 'echarts'
import CountUp from '../components/CountUp.vue'

export default {
  components: { CountUp },
  inject: ['user'],
  data() {
    return { stats: {}, summary: null, pendingCount: 0, recentRecords: [], userCount: 0, houses: [] }
  },
  computed: {
    userName() { return this.user?.name || '' },
    circumference() { return Math.PI * 2 * 86 },
    totalHouses() { return (this.stats.emptyHouse || 0) + (this.stats.allocatedHouse || 0) },
    ratePercent() {
      if (!this.totalHouses) return 0
      return Math.round((this.stats.allocatedHouse / this.totalHouses) * 100)
    },
    dashOffset() {
      const rate = this.totalHouses ? this.stats.allocatedHouse / this.totalHouses : 0
      return this.circumference * (1 - rate)
    }
  },
  mounted() {
    this.loadStats()
    this.loadPending()
    this.loadRecords()
    this.loadUsers()
    this.loadHouses()
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
    },
    loadHouses() {
      fetch('http://localhost:8080/houses')
        .then(r => r.json())
        .then(d => {
          this.houses = d
          this.$nextTick(() => {
            this.renderBuildingChart()
            this.renderAreaChart()
          })
        })
        .catch(() => {})
    },
    renderBuildingChart() {
      const el = this.$refs.buildingChart
      if (!el) return
      const buildings = ['A', 'B', 'C', 'D', 'E']
      const counts = buildings.map(b => this.houses.filter(h => (h.title||'').charAt(0).toUpperCase() === b).length)
      const chart = echarts.init(el)
      chart.setOption({
        grid: { top: 18, right: 8, bottom: 20, left: 28 },
        xAxis: { type: 'category', data: buildings.map(b => b+'栋'), axisLabel: { color: '#000', fontSize: 10 } },
        yAxis: { type: 'value', axisLabel: { color: '#000', fontSize: 10 }, splitLine: { lineStyle: { color: 'rgba(0,0,0,.04)' } } },
        series: [{
          type: 'bar', data: counts, barWidth: 22,
          itemStyle: {
            borderRadius: [4, 4, 0, 0],
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#60a5fa' }, { offset: 1, color: '#3b82f6' }
            ])
          },
          label: { show: true, position: 'top', color: '#000', fontSize: 11, fontWeight: 'bold', distance: 4 }
        }]
      })
    },
    renderAreaChart() {
      const el = this.$refs.areaChart
      if (!el) return
      const buildings = ['A', 'B', 'C', 'D', 'E']
      const avgs = buildings.map(b => {
        const list = this.houses.filter(h => (h.title||'').charAt(0).toUpperCase() === b)
        if (!list.length) return 0
        return +(list.reduce((s, h) => s + h.area, 0) / list.length).toFixed(1)
      })
      const chart = echarts.init(el)
      chart.setOption({
        grid: { top: 18, right: 8, bottom: 20, left: 38 },
        xAxis: { type: 'category', data: buildings.map(b => b+'栋'), axisLabel: { color: '#000', fontSize: 10 } },
        yAxis: { type: 'value', axisLabel: { color: '#000', fontSize: 10, formatter: '{value}㎡' }, splitLine: { lineStyle: { color: 'rgba(0,0,0,.04)' } } },
        series: [{
          type: 'bar', data: avgs, barWidth: 22,
          itemStyle: {
            borderRadius: [4, 4, 0, 0],
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#a78bfa' }, { offset: 1, color: '#7c3aed' }
            ])
          },
          label: { show: true, position: 'top', color: '#000', fontSize: 11, fontWeight: 'bold', formatter: '{c}㎡' }
        }]
      })
    }
  }
}
</script>
