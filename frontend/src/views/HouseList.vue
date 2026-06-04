<template>
  <div>
    <h2>房屋管理系统</h2>

    <table border="1" cellpadding="8">
      <thead>
        <tr>
          <th>ID</th>
          <th>房屋名称</th>
          <th>状态</th>
          <th>操作</th>
        </tr>
      </thead>

      <tbody>
        <tr v-for="item in list" :key="item.id">
          <td>{{ item.id }}</td>
          <td>{{ item.title }}</td>
          <td>{{ item.status }}</td>
          <td>
            <button
              @click="assign(item)"
              :disabled="item.status === '已分配'"
            >
              分房
            </button>

            <button
              @click="release(item)"
              :disabled="item.status === '空闲'"
            >
              退房
            </button>
          </td>
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

    // 获取房屋列表
    load() {
      fetch('http://localhost:8080/houses')
        .then(res => res.json())
        .then(data => {
          this.list = data
        })
        .catch(err => console.error('加载房屋列表失败', err))
    },

    // 分房（改状态为 已分配）
    assign(item) {
      fetch('http://localhost:8080/houses', {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          id: item.id,
          status: '已分配'
        })
      })
        .then(() => this.load())
        .catch(err => console.error(err))
    },

    // 退房（改状态为空闲）
    release(item) {
      fetch('http://localhost:8080/houses', {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          id: item.id,
          status: '空闲'
        })
      })
        .then(() => this.load())
        .catch(err => console.error(err))
    }
  }
}
</script>

<style scoped>
table {
  border-collapse: collapse;
  width: 80%;
  margin-top: 20px;
}

th, td {
  padding: 8px;
  text-align: center;
}

button {
  margin: 0 5px;
  padding: 4px 8px;
}
</style>