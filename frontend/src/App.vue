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

const currentNav = computed(() => navItems.value.find(n => n.key === page.value))

function onLogin(u) {
  user.value = u
  sessionStorage.setItem('user', JSON.stringify(u))
  page.value = 'application'
}

function logout() {
  user.value = null
  page.value = null
  sessionStorage.removeItem('user')
}
</script>

<template>
  <Login v-if="!user" @login="onLogin" />

  <div v-else id="app-layout">
    <aside class="sidebar">
      <div class="sidebar-header">
        <div class="logo">房产管理系统</div>
        <div class="subtitle">HOUSING MANAGEMENT</div>
      </div>

      <nav class="sidebar-nav">
        <div
          v-for="item in navItems"
          :key="item.key"
          class="nav-item"
          :class="{ active: page === item.key }"
          @click="page = item.key"
        >
          <span>{{ item.label }}</span>
        </div>
      </nav>

      <div class="sidebar-footer">
        <span>{{ user.name }}（{{ user.role === 'admin' ? '管理员' : '住户' }}）</span>
        <span class="logout-link" @click="logout">退出登录</span>
      </div>
    </aside>

    <div class="main-area">
      <header class="page-header">
        <h1>{{ currentNav?.label }}</h1>
      </header>

      <main class="page-body">
        <HouseList v-if="page === 'house'" />
        <ApplicationList v-if="page === 'application'" />
        <UserList v-if="page === 'user'" />
        <HousingRecordList v-if="page === 'record'" />
        <HousingStandardList v-if="page === 'standard'" />
        <Stats v-if="page === 'stats'" />
      </main>
    </div>
  </div>
</template>

<style scoped>
#app-layout { display: flex; min-height: 100vh; width: 100%; }
.logout-link { color: rgba(255,255,255,.4); cursor: pointer; margin-top: 4px; display: block; }
.logout-link:hover { color: #fff; }
.sidebar-footer { display: flex; flex-direction: column; color: rgba(255,255,255,.5); font-size: 12px; }
</style>
