<template>
  <div>
    <h2>住房记录</h2>

    <table border="1" cellpadding="8">
      <thead>
        <tr>
          <th>ID</th>
          <th>户主</th>
          <th>部门</th>
          <th>职称</th>
          <th>家庭人口</th>
          <th>住房分数</th>
          <th>房号</th>
          <th>住房面积（㎡）</th>
          <th>月租金（元）</th>
          <th>入住日期</th>
        </tr>
      </thead>

      <tbody>
        <tr v-for="item in list" :key="item.id">
          <td>{{ item.id }}</td>
          <td>{{ item.userName }}</td>
          <td>{{ item.department }}</td>
          <td>{{ item.titleLevel }}</td>
          <td>{{ item.familySize }}</td>
          <td>{{ item.score }}</td>
          <td>{{ item.houseTitle }}</td>
          <td>{{ item.houseArea }}</td>
          <td>{{ item.rentAmount }}</td>
          <td>{{ item.moveInDate }}</td>
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
      fetch('http://localhost:8080/records')
        .then(res => res.json())
        .then(data => {
          this.list = data
        })
        .catch(err => console.error('加载住房记录失败', err))
    }
  }
}
</script>

<style scoped>
table {
  border-collapse: collapse;
  width: 95%;
  margin-top: 20px;
}

th, td {
  padding: 8px;
  text-align: center;
}
</style>