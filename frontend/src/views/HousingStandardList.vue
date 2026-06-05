<template>
  <div>
    <h2>住房标准</h2>

    <table border="1" cellpadding="8">
      <thead>
        <tr>
          <th>ID</th>
          <th>面积</th>
          <th>最低分数</th>
        </tr>
      </thead>

      <tbody>
        <tr v-for="item in list" :key="item.id">
          <td>{{ item.id }}</td>
          <td>{{ item.area }}</td>
          <td>{{ item.minScore }}</td>
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
      fetch('http://localhost:8080/standards')
        .then(res => res.json())
        .then(data => {
          this.list = data
        })
        .catch(err => console.error('加载住房标准失败', err))
    }
  }
}
</script>