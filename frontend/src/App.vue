<script setup>
import { ref } from 'vue'
import HouseList from './views/HouseList.vue'
import ApplicationList from './views/ApplicationList.vue'

const page = ref('house') // 默认显示房屋管理
</script>

<template>
  <div>
    <h1>房产管理系统</h1>

    <!-- 顶部菜单切换 -->
    <button @click="page = 'house'">房屋管理</button>
    <button @click="page = 'application'">申请管理</button>

    <!-- 页面显示 -->
    <HouseList v-if="page === 'house'" />
    <ApplicationList v-if="page === 'application'" />
  </div>
</template>

<style scoped>
button {
  margin-right: 10px;
  padding: 6px 12px;
}
</style>