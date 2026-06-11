<template>
  <div>
    <!-- 统计概览卡片 -->
    <div class="stat-grid">
      <div class="stat-card">
        <div class="stat-value">{{ stats.emptyHouse ?? '-' }}</div>
        <div class="stat-label">空房数量</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.allocatedHouse ?? '-' }}</div>
        <div class="stat-label">已分配房屋</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.applicationCount ?? '-' }}</div>
        <div class="stat-label">申请总数</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.thresholdScore ?? '-' }}</div>
        <div class="stat-label">最低阈值分数</div>
      </div>
    </div>

    <!-- 住房汇总 -->
    <div v-if="stats.housingSummary" class="stat-grid" style="grid-template-columns: repeat(3, 1fr);">
      <div class="stat-card">
        <div class="stat-value">{{ stats.housingSummary.total_houses }}</div>
        <div class="stat-label">总房屋数</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.housingSummary.total_area }} <span style="font-size:14px;">㎡</span></div>
        <div class="stat-label">总面积</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.housingSummary.avg_rent_per_m2 }} <span style="font-size:14px;">元</span></div>
        <div class="stat-label">平均租金单价（每㎡）</div>
      </div>
    </div>

    <!-- 图表区 -->
    <div style="display:grid; grid-template-columns:1fr 1fr; gap:16px; margin-bottom:20px;">
      <div class="glass-card" style="padding:16px;">
        <div class="card-title">房屋状态分布</div>
        <div ref="pieChart" style="width:100%; height:300px;"></div>
      </div>
      <div class="glass-card" style="padding:16px;">
        <div class="card-title">各面积段房屋数量</div>
        <div ref="areaBarChart" style="width:100%; height:300px;"></div>
      </div>
    </div>

    <!-- 租金对比图 -->
    <div class="glass-card" style="padding:16px; margin-bottom:20px;">
      <div class="card-title">房屋月租金对比</div>
      <div ref="rentBarChart" style="width:100%; height:400px;"></div>
    </div>

    <!-- 查询工具 -->
    <div style="display:grid; grid-template-columns:1fr 1fr; gap:16px;">
      <!-- 按面积查询阈值 -->
      <div class="glass-card">
        <div class="card-title">按面积查询分房阈值</div>
        <div class="form-group">
          <label>输入面积（㎡）</label>
          <div style="display:flex; gap:8px;">
            <input v-model="queryArea" type="number" class="form-control" placeholder="如：80" style="flex:1;" />
            <button class="btn btn-primary" @click="queryThreshold">查询</button>
          </div>
        </div>
        <div v-if="thresholdResult !== null" class="alert alert-info mt-1">
          面积 <strong>{{ thresholdResult.area }}㎡</strong> 的最低分数要求：
          <strong style="font-size:18px; color:var(--primary);">{{ thresholdResult.minScore }} 分</strong>
        </div>
      </div>

      <!-- 按房号查询租金 -->
      <div class="glass-card">
        <div class="card-title">按房号查询单位面积租金</div>
        <div class="form-group">
          <label>输入房号</label>
          <div style="display:flex; gap:8px;">
            <input v-model="queryTitle" class="form-control" placeholder="如：A101" style="flex:1;" />
            <button class="btn btn-primary" @click="queryRent">查询</button>
          </div>
        </div>
        <div v-if="rentResult !== null" class="alert alert-info mt-1">
          房号 <strong>{{ rentResult.title }}</strong> 的每平米租金：
          <strong style="font-size:18px; color:var(--primary);">{{ rentResult.rentPerM2 }} 元</strong>
        </div>
      </div>
    </div>

    <!-- 房屋租金一览表 -->
    <div class="glass-card">
      <div class="card-title">房屋租金一览</div>
      <div class="table-wrapper">
        <table>
          <thead>
            <tr>
              <th>房号</th>
              <th>面积（㎡）</th>
              <th>每平米租金（元）</th>
              <th>月租金（元）</th>
              <th>状态</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in (stats.houseRentInfo || [])" :key="item.id">
              <td><strong>{{ item.title }}</strong></td>
              <td class="col-center">{{ item.area }}</td>
              <td class="col-right">{{ item.rent_per_m2 }} 元</td>
              <td class="col-right">{{ item.monthly_rent }} 元</td>
              <td>
                <span class="tag" :class="(item.status === '空房' || item.status === 'empty') ? 'tag-success' : 'tag-warning'">
                  {{ item.status === 'empty' ? '空房' : item.status }}
                </span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div v-if="!stats.houseRentInfo || stats.houseRentInfo.length === 0" class="empty-state">
        <div class="empty-icon">-</div>
        <p>暂无租金数据</p>
      </div>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  data() {
    return {
      stats: {},
      queryArea: null,
      thresholdResult: null,
      queryTitle: '',
      rentResult: null
    }
  },

  mounted() {
    this.load()
  },

  methods: {
    load() {
      fetch('http://localhost:8080/stats')
        .then(res => res.json())
        .then(data => {
          this.stats = data
          this.$nextTick(() => {
            this.renderPieChart()
            this.renderAreaBarChart()
            this.renderRentBarChart()
          })
        })
        .catch(err => console.error('加载统计失败', err))
    },

    renderPieChart() {
      const el = this.$refs.pieChart
      if (!el) return
      const chart = echarts.init(el)
      chart.setOption({
        tooltip: { trigger: 'item' },
        legend: { bottom: 0, textStyle: { color: '#000' } },
        series: [{
          type: 'pie',
          radius: ['45%', '75%'],
          center: ['50%', '45%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 6,
            borderColor: 'rgba(255,255,255,.8)',
            borderWidth: 3
          },
          label: { show: true, formatter: '{b}\n{d}%' },
          data: [
            { value: this.stats.emptyHouse || 0, name: '空房', itemStyle: { color: '#38bdf8' } },
            { value: this.stats.allocatedHouse || 0, name: '已分配', itemStyle: { color: '#818cf8' } }
          ]
        }]
      })
    },

    renderAreaBarChart() {
      const el = this.$refs.areaBarChart
      if (!el) return
      const houses = this.stats.houseRentInfo || []
      const ranges = [
        { label: '0~60㎡', min: 0, max: 60 },
        { label: '60~80㎡', min: 60, max: 80 },
        { label: '80~120㎡', min: 80, max: 120 },
        { label: '120~160㎡', min: 120, max: 999 }
      ]
      const counts = ranges.map(r => houses.filter(h => h.area > r.min && h.area <= r.max).length)

      const chart = echarts.init(el)
      chart.setOption({
        tooltip: { trigger: 'axis' },
        xAxis: {
          type: 'category',
          data: ranges.map(r => r.label),
          axisLabel: { color: '#000' }
        },
        yAxis: {
          type: 'value',
          name: '套数',
          axisLabel: { color: '#000' }
        },
        series: [{
          type: 'bar',
          data: counts,
          barWidth: '50%',
          itemStyle: {
            borderRadius: [8, 8, 0, 0],
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#60a5fa' },
              { offset: 1, color: '#3b82f6' }
            ])
          },
          label: { show: true, position: 'top', color: '#000', fontWeight: 'bold' }
        }]
      })
    },

    renderRentBarChart() {
      const el = this.$refs.rentBarChart
      if (!el) return
      const houses = (this.stats.houseRentInfo || []).slice().sort((a, b) => b.monthly_rent - a.monthly_rent)

      const chart = echarts.init(el)
      chart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' }
        },
        grid: { left: '3%', right: '10%', bottom: '3%', containLabel: true },
        xAxis: {
          type: 'value',
          name: '元/月',
          axisLabel: { color: '#000' }
        },
        yAxis: {
          type: 'category',
          data: houses.map(h => h.title),
          axisLabel: { color: '#000', fontWeight: 'bold' },
          inverse: true
        },
        series: [{
          type: 'bar',
          data: houses.map(h => ({
            value: h.monthly_rent,
            itemStyle: {
              color: h.status === '空房' || h.status === 'empty'
                ? new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                    { offset: 0, color: '#38bdf8' }, { offset: 1, color: '#0ea5e9' }
                  ])
                : new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                    { offset: 0, color: '#818cf8' }, { offset: 1, color: '#6366f1' }
                  ])
            }
          })),
          barWidth: '60%',
          itemStyle: {
            borderRadius: [0, 6, 6, 0]
          },
          label: { show: true, position: 'right', color: '#000', formatter: '{c} 元' }
        }]
      })
    },

    queryThreshold() {
      if (!this.queryArea) return
      fetch(`http://localhost:8080/stats/threshold?area=${this.queryArea}`)
        .then(res => res.json())
        .then(data => { this.thresholdResult = data })
        .catch(err => console.error('查询阈值失败', err))
    },

    queryRent() {
      if (!this.queryTitle) return
      fetch(`http://localhost:8080/stats/rent?title=${encodeURIComponent(this.queryTitle)}`)
        .then(res => res.json())
        .then(data => { this.rentResult = data })
        .catch(err => console.error('查询租金失败', err))
    }
  }
}
</script>
