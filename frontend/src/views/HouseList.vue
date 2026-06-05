<template>
  <div>
    <h2>房屋管理</h2>

    <table border="1" cellpadding="8">
      <thead>
        <tr>
          <th>ID</th>
          <th>房屋名称</th>
          <th>状态</th>
          <th>面积（㎡）</th>
          <th>每平米租金（元）</th>
          <th>月租金（元）</th>
        </tr>
      </thead>

      <tbody>
        <tr v-for="item in list" :key="item.id">
          <td>{{ item.id }}</td>
          <td>{{ item.title }}</td>
          <td>{{ item.status }}</td>
          <td>{{ item.area }}</td>
          <td>{{ item.rentPerM2 }}</td>
          <td>{{ (item.area * item.rentPerM2).toFixed(2) }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
export default {
  data() {
    return {
      list: []
    }
  },

  mounted() {
    this.load()
  },

  methods: {
    load() {
      fetch('http://localhost:8080/houses')
        .then(res => res.json())
        .then(data => {
          this.list = data
        })
        .catch(err => console.error('加载房屋列表失败', err))
    }
  }
}
</script>

<style scoped>
table {
  border-collapse: collapse;
  width: 90%;
  margin-top: 20px;
}

th, td {
  padding: 8px;
  text-align: center;
}
</style>