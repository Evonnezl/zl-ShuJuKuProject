<script setup>
import { ref, computed, provide } from 'vue'
import Login from './views/Login.vue'
import HouseList from './views/HouseList.vue'
import ApplicationList from './views/ApplicationList.vue'
import UserList from './views/UserList.vue'
import HousingRecordList from './views/HousingRecordList.vue'
import HousingStandardList from './views/HousingStandardList.vue'
import Stats from './views/Stats.vue'

const saved = sessionStorage.getItem('user')
const user = ref(saved ? JSON.parse(saved) : null)
provide('user', user)

const page = ref(user.value ? 'application' : null)

const allNavItems = [
  { key: 'application', label: '申请管理', adminOnly: false },
  { key: 'house',       label: '房屋管理', adminOnly: true },
  { key: 'user',        label: '用户管理', adminOnly: true },
  { key: 'record',      label: '住房记录', adminOnly: false },
  { key: 'standard',    label: '住房标准', adminOnly: true },
  { key: 'stats',       label: '统计查询', adminOnly: true },
]

const navItems = computed(() => {
  if (!user.value) return []
  if (user.value.role === 'admin') return allNavItems
  return allNavItems.filter(n => !n.adminOnly)
})

function onLogin(u) {
  user.value = u
  sessionStorage.setItem('user', JSON.stringify(u))
  page.value = 'application'
}

function logout() {
  user.value = null; page.value = null
  sessionStorage.removeItem('user')
}
</script>

<template>
  <Login v-if="!user" @login="onLogin" />

  <div v-else>
    <div class="stars-layer" />
    <nav class="top-nav">
      <span class="logo">房产管理系统</span>
      <div class="nav-links">
        <span v-for="item in navItems" :key="item.key"
          class="nav-link" :class="{ active: page === item.key }"
          @click="page = item.key">{{ item.label }}</span>
      </div>
      <div class="user-info">
        <span>{{ user.name }}</span>
        <span class="logout-btn" @click="logout">退出</span>
      </div>
    </nav>
    <div class="page-wrap" :key="page">
      <HouseList v-if="page === 'house'" />
      <ApplicationList v-if="page === 'application'" />
      <UserList v-if="page === 'user'" />
      <HousingRecordList v-if="page === 'record'" />
      <HousingStandardList v-if="page === 'standard'" />
      <Stats v-if="page === 'stats'" />
    </div>
  </div>
</template>
