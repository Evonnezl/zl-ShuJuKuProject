<template>
  <div>
    <h2>统计查询</h2>

    <!-- 住房汇总 -->
    <div class="card">
      <h3>住房汇总</h3>
      <p>空房数量：{{ stats.emptyHouse }}</p>
      <p>已分配房屋：{{ stats.allocatedHouse }}</p>
      <p>申请总数：{{ stats.applicationCount }}</p>
      <p>最低阈值分数：{{ stats.thresholdScore }}</p>
      <div v-if="stats.housingSummary">
        <p>总房屋数：{{ stats.housingSummary.total_houses }}</p>
        <p>总面积：{{ stats.housingSummary.total_area }} ㎡</p>
        <p>平均租金单价：{{ stats.housingSummary.avg_rent_per_m2 }} 元/㎡</p>
      </div>
    </div>

    <!-- 按面积查询阈值 -->
    <div class="card">
      <h3>按面积查询分房阈值</h3>
      <input v-model="queryArea" type="number" placeholder="输入面积（㎡）" />
      <button @click="queryThreshold">查询</button>
      <p v-if="thresholdResult !== null">
        面积 {{ thresholdResult.area }}㎡ 的最低分数要求：{{ thresholdResult.minScore }} 分
      </p>
    </div>

    <!-- 按房号查询租金 -->
    <div class="card">
      <h3>按房号查询单位面积租金</h3>
      <input v-model="queryTitle" placeholder="输入房号（如 A101）" />
      <button @click="queryRent">查询</button>
      <p v-if="rentResult !== null">
        房号 {{ rentResult.title }} 的每平米租金：{{ rentResult.rentPerM2 }} 元
      </p>
    </div>

    <!-- 房屋租金列表 -->
    <div class="card">
      <h3>房屋租金一览</h3>
      <table border="1" cellpadding="6" v-if="stats.houseRentInfo && stats.houseRentInfo.length">
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
          <tr v-for="item in stats.houseRentInfo" :key="item.id">
            <td>{{ item.title }}</td>
            <td>{{ item.area }}</td>
            <td>{{ item.rent_per_m2 }}</td>
            <td>{{ item.monthly_rent }}</td>
            <td>{{ item.status }}</td>
          </tr>
        </tbody>
      </table>
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
        .then(data => {
          this.stats = data
        })
        .catch(err => console.error('加载统计失败', err))
    },

    queryThreshold() {
      if (!this.queryArea) return
      fetch(`http://localhost:8080/stats/threshold?area=${this.queryArea}`)
        .then(res => res.json())
        .then(data => {
          this.thresholdResult = data
        })
        .catch(err => console.error('查询阈值失败', err))
    },

    queryRent() {
      if (!this.queryTitle) return
      fetch(`http://localhost:8080/stats/rent?title=${encodeURIComponent(this.queryTitle)}`)
        .then(res => res.json())
        .then(data => {
          this.rentResult = data
        })
        .catch(err => console.error('查询租金失败', err))
    }
  }
}
</script>

<style scoped>
.card {
  margin-top: 20px;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.card h3 {
  margin-top: 0;
}

input {
  padding: 4px 8px;
  margin-right: 10px;
}

button {
  padding: 6px 12px;
}

table {
  border-collapse: collapse;
  width: 100%;
  margin-top: 10px;
}

th, td {
  padding: 6px;
  text-align: center;
}
</style>