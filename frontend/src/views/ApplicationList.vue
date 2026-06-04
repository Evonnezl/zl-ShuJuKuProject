<template>
  <div>
    <h2>申请管理</h2>

    <table border="1" cellpadding="8">
      <thead>
        <tr>
          <th>ID</th>
          <th>用户ID</th>
          <th>类型</th>
          <th>状态</th>
          <th>部门</th>
          <th>职称</th>
          <th>家庭人数</th>
          <th>分数</th>
          <th>请求面积</th>
          <th>操作</th>
        </tr>
      </thead>

      <tbody>
        <tr v-for="item in list" :key="item.id">
          <td>{{ item.id }}</td>
          <td>{{ item.userId }}</td>
          <td>{{ item.type }}</td>
          <td>{{ item.status }}</td>
          <td>{{ item.department }}</td>
          <td>{{ item.titleLevel }}</td>
          <td>{{ item.familySize }}</td>
          <td>{{ item.score }}</td>
          <td>{{ item.requestArea }}</td>
          <td>
            <button 
              v-if="item.type === 'ALLOCATE' && item.status === 'PENDING'" 
              @click="approve(item)">
              分房
            </button>

            <button 
              v-if="item.type === 'TRANSFER' && item.status === 'PENDING'" 
              @click="transfer(item)">
              调房
            </button>

            <button 
              v-if="item.type === 'RETURN' && item.status === 'PENDING'" 
              @click="release(item)">
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
    // 获取申请列表
    load() {
      fetch('http://localhost:8080/applications')
        .then(res => res.json())
        .then(data => {
          this.list = data
        })
        .catch(err => console.error('加载申请列表失败', err))
    },

    // 分房（更新状态为 APPROVED）
    approve(item) {
      fetch(`http://localhost:8080/applications/${item.id}/approve`, {
        method: 'PUT'
      })
        .then(() => this.load())
        .catch(err => console.error(err))
    },

    // 调房（更新状态为 APPROVED）
    transfer(item) {
      fetch(`http://localhost:8080/applications/${item.id}/transfer`, {
        method: 'PUT'
      })
        .then(() => this.load())
        .catch(err => console.error(err))
    },

    // 退房（更新状态为 APPROVED）
    release(item) {
      fetch(`http://localhost:8080/applications/${item.id}/release`, {
        method: 'PUT'
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
  width: 100%;
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