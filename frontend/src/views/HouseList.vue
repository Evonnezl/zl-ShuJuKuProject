<template>
  <div>
    <div class="page-title-row">
      <h2>所有房屋</h2>
      <button class="btn btn-primary" @click="showAddForm = !showAddForm">
        {{ showAddForm ? '取消' : '+ 新增房屋' }}
      </button>
    </div>

    <!-- 新增房屋表单 -->
    <div v-if="showAddForm" class="card">
      <div class="card-header">新增房屋</div>
      <div style="display:grid; grid-template-columns:1fr 1fr; gap:12px;">
        <div class="form-group">
          <label>房屋名称</label>
          <input v-model="form.title" class="form-control" placeholder="如：A101" />
        </div>
        <div class="form-group">
          <label>面积（㎡）</label>
          <input v-model.number="form.area" type="number" class="form-control" placeholder="如：80" />
        </div>
        <div class="form-group">
          <label>每平米租金（元）</label>
          <input v-model.number="form.rentPerM2" type="number" class="form-control" placeholder="如：25" />
        </div>
        <div class="form-group">
          <label>状态</label>
          <select v-model="form.status" class="form-control">
            <option value="空房">空房</option>
            <option value="已分配">已分配</option>
          </select>
        </div>
      </div>
      <button class="btn btn-primary mt-2" @click="addHouse">提交</button>
      <span v-if="msg" class="alert alert-success" style="margin-left:12px;">{{ msg }}</span>
    </div>

    <!-- 房屋列表 -->
    <div class="card">
      <div class="table-wrapper">
        <table>
          <thead>
            <tr>
              <th style="width:60px;">ID</th>
              <th>房屋名称</th>
              <th style="width:100px;">状态</th>
              <th style="width:100px;">面积（㎡）</th>
              <th style="width:130px;">每平米租金</th>
              <th style="width:120px;">月租金</th>
              <th style="width:120px;">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in list" :key="item.id">
              <td>{{ item.id }}</td>
              <td><strong>{{ item.title }}</strong></td>
              <td>
                <span class="tag" :class="item.status === '空房' ? 'tag-success' : 'tag-warning'">
                  {{ item.status }}
                </span>
              </td>
              <td class="col-center">{{ item.area }}</td>
              <td class="col-right">{{ item.rentPerM2 }} 元</td>
              <td class="col-right">{{ (item.area * item.rentPerM2).toFixed(2) }} 元</td>
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
        <div class="card-header">编辑房屋 #{{ editing.id }}</div>
        <div class="form-group">
          <label>房屋名称</label>
          <input v-model="editing.title" class="form-control" />
        </div>
        <div class="form-group">
          <label>面积（㎡）</label>
          <input v-model.number="editing.area" type="number" class="form-control" />
        </div>
        <div class="form-group">
          <label>每平米租金（元）</label>
          <input v-model.number="editing.rentPerM2" type="number" class="form-control" />
        </div>
        <div class="form-group">
          <label>状态</label>
          <select v-model="editing.status" class="form-control">
            <option value="空房">空房</option>
            <option value="已分配">已分配</option>
          </select>
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
      form: { title: '', area: null, rentPerM2: null, status: '空房' },
      msg: ''
    }
  },

  mounted() {
    this.load()
  },

  methods: {
    load() {
      fetch('http://localhost:8080/houses')
        .then(res => res.json())
        .then(data => { this.list = data })
        .catch(err => console.error('加载房屋列表失败', err))
    },

    addHouse() {
      fetch('http://localhost:8080/houses', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(this.form)
      })
        .then(() => {
          this.msg = '新增成功'
          this.form = { title: '', area: null, rentPerM2: null, status: '空房' }
          this.load()
          setTimeout(() => { this.msg = '' }, 2000)
        })
        .catch(err => console.error(err))
    },

    startEdit(item) {
      this.editing = { ...item }
    },

    saveEdit() {
      fetch(`http://localhost:8080/houses/${this.editing.id}`, {
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
      if (!confirm('确认删除该房屋？')) return
      fetch(`http://localhost:8080/houses/${id}`, { method: 'DELETE' })
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
