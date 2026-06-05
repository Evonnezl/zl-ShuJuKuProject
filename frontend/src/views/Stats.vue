<template>
  <div>
    <h2>统计查询</h2>

    <div class="card">
      <p>空房数量：{{ stats.emptyHouse }}</p>
      <p>已分配房屋：{{ stats.allocatedHouse }}</p>
      <p>申请总数：{{ stats.applicationCount }}</p>
      <p>阈值分数：{{ stats.thresholdScore }}</p>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      stats: {}
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
    }
  }
}
</script>

<style scoped>
.card {
  margin-top: 20px;
  padding: 20px;
  border: 1px solid #ccc;
}
</style>