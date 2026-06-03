<template>
  <div>
    <h2>用户列表</h2>
    <ul>
      <li v-for="user in users" :key="user.id">
        {{ user.name }} - {{ user.role }} - {{ user.phone }} - {{ user.email }}
      </li>
    </ul>
  </div>
</template>

<script>
export default {
  data() {
    return {
      users: []
    }
  },
  mounted() {
    fetch('http://localhostz:8080/users')
      .then(res => res.json())
      .then(data => this.users = data)
      .catch(err => console.error(err));
  }
}
</script>