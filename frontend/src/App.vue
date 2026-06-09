<script setup>
import { ref, computed, provide, shallowRef } from 'vue'
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

const components = { HouseList, ApplicationList, UserList, HousingRecordList, HousingStandardList, Stats }
const page = ref(user.value ? 'ApplicationList' : null)

const allNavItems = [
  { key: 'ApplicationList', label: '申请管理', adminOnly: false },
  { key: 'HouseList',       label: '房屋管理', adminOnly: true },
  { key: 'UserList',        label: '用户管理', adminOnly: true },
  { key: 'HousingRecordList', label: '住房记录', adminOnly: false },
  { key: 'HousingStandardList', label: '住房标准', adminOnly: true },
  { key: 'Stats',           label: '统计查询', adminOnly: true },
]

const navItems = computed(() => {
  if (!user.value) return []
  if (user.value.role === 'admin') return allNavItems
  return allNavItems.filter(n => !n.adminOnly)
})

function onLogin(u) {
  user.value = u
  sessionStorage.setItem('user', JSON.stringify(u))
  page.value = 'ApplicationList'
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

    <Transition name="fade" mode="out-in">
      <component :is="components[page]" :key="page" />
    </Transition>
  </div>
</template>

<style>
.fade-enter-active { transition: opacity .2s ease; }
.fade-leave-active { transition: opacity .15s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>
