<script setup>
import { ref } from 'vue'
import HouseList from './views/HouseList.vue'
import ApplicationList from './views/ApplicationList.vue'
import UserList from './views/UserList.vue'
import HousingRecordList from './views/HousingRecordList.vue'
import HousingStandardList from './views/HousingStandardList.vue'
import Stats from './views/Stats.vue'

const page = ref('house') // 默认显示房屋管理
</script>

<template>
  <div>
    <h1>房产管理系统</h1>

    <!-- 顶部菜单切换 -->
    <button @click="page = 'house'">房屋管理</button>
    <button @click="page = 'application'">申请管理</button>
    <button @click="page = 'user'">用户管理</button>
    <button @click="page = 'record'">住房记录</button>
    <button @click="page = 'standard'">住房标准</button>
    <button @click="page = 'stats'">统计查询</button>

    <!-- 页面显示 -->
    <HouseList v-if="page === 'house'" />
    <ApplicationList v-if="page === 'application'" />
    <UserList v-if="page === 'user'" />
    <HousingRecordList v-if="page === 'record'" />
    <HousingStandardList v-if="page === 'standard'" />
    <Stats v-if="page === 'stats'" />
  </div>
</template>

<style scoped>
button {
  margin-right: 10px;
  padding: 6px 12px;
}
</style>