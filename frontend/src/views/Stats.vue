<template>
  <div>
    <!-- 统计概览卡片 -->
    <div class="stat-grid">
      <div class="stat-card" v-reveal>
        <div class="stat-value">{{ stats.emptyHouse ?? '-' }}</div>
        <div class="stat-label">空房数量</div>
      </div>
      <div class="stat-card" v-reveal>
        <div class="stat-value">{{ stats.allocatedHouse ?? '-' }}</div>
        <div class="stat-label">已分配房屋</div>
      </div>
      <div class="stat-card" v-reveal>
        <div class="stat-value">{{ stats.applicationCount ?? '-' }}</div>
        <div class="stat-label">申请总数</div>
      </div>
      <div class="stat-card" v-reveal>
        <div class="stat-value">{{ stats.thresholdScore ?? '-' }}</div>
        <div class="stat-label">最低阈值分数</div>
      </div>
    </div>

    <!-- 住房汇总 -->
    <div v-if="stats.housingSummary" class="stat-grid" style="grid-template-columns: repeat(3, 1fr);">
      <div class="stat-card" v-reveal>
        <div class="stat-value">{{ stats.housingSummary.total_houses }}</div>
        <div class="stat-label">总房屋数</div>
      </div>
      <div class="stat-card" v-reveal>
        <div class="stat-value">{{ stats.housingSummary.total_area }} <span style="font-size:14px;">㎡</span></div>
        <div class="stat-label">总面积</div>
      </div>
      <div class="stat-card" v-reveal>
        <div class="stat-value">{{ stats.housingSummary.avg_rent_per_m2 }} <span style="font-size:14px;">元</span></div>
        <div class="stat-label">平均租金单价（每㎡）</div>
      </div>
    </div>

    <!-- 查询工具 -->
    <div style="display:grid; grid-template-columns:1fr 1fr; gap:16px;">
      <!-- 按面积查询阈值 -->
      <div class="glass-card" v-reveal>
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
      <div class="glass-card" v-reveal>
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
    <div class="glass-card" v-reveal>
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
                <span class="tag" :class="item.status === '空房' ? 'tag-success' : 'tag-warning'">
                  {{ item.status }}
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
        .then(data => { this.stats = data })
        .catch(err => console.error('加载统计失败', err))
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
