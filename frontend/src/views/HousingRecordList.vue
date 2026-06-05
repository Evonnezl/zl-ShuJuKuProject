<template>
  <div>
    <h2>住房记录</h2>

    <table border="1" cellpadding="8">
      <thead>
        <tr>
          <th>ID</th>
          <th>用户ID</th>
          <th>房屋ID</th>
          <th>分数</th>
          <th>入住日期</th>
        </tr>
      </thead>

      <tbody>
        <tr v-for="item in list" :key="item.id">
          <td>{{ item.id }}</td>
          <td>{{ item.userId }}</td>
          <td>{{ item.houseId }}</td>
          <td>{{ item.score }}</td>
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