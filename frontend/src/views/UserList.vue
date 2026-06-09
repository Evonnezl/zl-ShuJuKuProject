<template>
  <div>
    <div class="page-title-row">
      <h2>所有用户</h2>
      <button class="btn btn-primary" @click="showAddForm = !showAddForm">
        {{ showAddForm ? '取消' : '+ 新增用户' }}
      </button>
    </div>

    <!-- 新增用户表单 -->
    <div v-if="showAddForm" class="card">
      <div class="card-header">新增用户</div>
      <div style="display:grid; grid-template-columns:1fr 1fr; gap:12px;">
        <div class="form-group">
          <label>姓名</label>
          <input v-model="form.name" class="form-control" placeholder="如：张三" />
        </div>
        <div class="form-group">
          <label>角色</label>
          <select v-model="form.role" class="form-control">
            <option value="admin">管理员</option>
            <option value="tenant">住户</option>
          </select>
        </div>
        <div class="form-group">
          <label>电话</label>
          <input v-model="form.phone" class="form-control" placeholder="如：13800138000" />
        </div>
        <div class="form-group">
          <label>邮箱</label>
          <input v-model="form.email" class="form-control" placeholder="如：zhangsan@qq.com" />
        </div>
      </div>
      <button class="btn btn-primary mt-2" @click="addUser">提交</button>
      <span v-if="msg" class="alert alert-success" style="margin-left:12px;">{{ msg }}</span>
    </div>

    <!-- 用户列表 -->
    <div class="card">
      <div class="table-wrapper">
        <table>
          <thead>
            <tr>
              <th style="width:60px;">ID</th>
              <th>姓名</th>
              <th style="width:100px;">角色</th>
              <th>电话</th>
              <th>邮箱</th>
              <th style="width:120px;">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in list" :key="item.id">
              <td>{{ item.id }}</td>
              <td><strong>{{ item.name }}</strong></td>
              <td>
                <span class="tag" :class="item.role === 'admin' ? 'tag-warning' : 'tag-info'">
                  {{ item.role === 'admin' ? '管理员' : '住户' }}
                </span>
              </td>
              <td>{{ item.phone }}</td>
              <td>{{ item.email }}</td>
              <td>
                <button class="btn btn-outline btn-sm" @click="startEdit(item)">编辑</button>
                <button class="btn btn-danger btn-sm" style="margin-left:6px;" @click="del(item.id)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 编辑弹窗 -->
    <div v-if="editing" class="modal-mask" @click.self="editing=null">
      <div class="modal-card">
        <div class="card-header">编辑用户 #{{ editing.id }}</div>
        <div class="form-group">
          <label>姓名</label>
          <input v-model="editing.name" class="form-control" />
        </div>
        <div class="form-group">
          <label>角色</label>
          <select v-model="editing.role" class="form-control">
            <option value="admin">管理员</option>
            <option value="tenant">住户</option>
          </select>
        </div>
        <div class="form-group">
          <label>电话</label>
          <input v-model="editing.phone" class="form-control" />
        </div>
        <div class="form-group">
          <label>邮箱</label>
          <input v-model="editing.email" class="form-control" />
        </div>
        <div style="display:flex; gap:8px; justify-content:flex-end;">
          <button class="btn btn-outline" @click="editing=null">取消</button>
          <button class="btn btn-primary" @click="saveEdit">保存</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      list: [],
      showAddForm: false,
      editing: null,
      form: { name: '', role: 'tenant', phone: '', email: '' },
      msg: ''
    }
  },

  mounted() {
    this.load()
  },

  methods: {
    load() {
      fetch('http://localhost:8080/users')
        .then(res => res.json())
        .then(data => { this.list = data })
        .catch(err => console.error('加载用户失败', err))
    },

    addUser() {
      fetch('http://localhost:8080/users', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(this.form)
      })
        .then(() => {
          this.msg = '新增成功'
          this.form = { name: '', role: 'tenant', phone: '', email: '' }
          this.load()
          setTimeout(() => { this.msg = '' }, 2000)
        })
        .catch(err => console.error(err))
    },

    startEdit(item) {
      this.editing = { ...item }
    },

    saveEdit() {
      fetch(`http://localhost:8080/users/${this.editing.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(this.editing)
      })
        .then(() => {
          this.editing = null
          this.load()
        })
        .catch(err => console.error(err))
    },

    del(id) {
      if (!confirm('确认删除该用户？')) return
      fetch(`http://localhost:8080/users/${id}`, { method: 'DELETE' })
        .then(() => this.load())
        .catch(err => console.error(err))
    }
  }
}
</script>

<style scoped>
.modal-mask {
  position: fixed; inset: 0;
  background: rgba(0,0,0,.35);
  display: flex; align-items: center; justify-content: center;
  z-index: 1000;
}
.modal-card {
  background: #fff;
  border-radius: var(--radius);
  padding: 24px;
  width: 440px;
  max-width: 90vw;
  box-shadow: var(--shadow-lg);
}
</style>
