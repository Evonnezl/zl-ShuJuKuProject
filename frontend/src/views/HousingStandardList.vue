<template>
  <div>
    <div class="page-title-row">
      <h2>住房标准管理</h2>
      <button class="btn btn-primary" @click="showAddForm = !showAddForm">
        {{ showAddForm ? '取消' : '+ 新增标准' }}
      </button>
    </div>

    <!-- 新增标准表单 -->
    <div v-if="showAddForm" class="glass-card" v-reveal>
      <div class="card-title">新增住房标准</div>
      <div style="display:grid; grid-template-columns:1fr 1fr 1fr; gap:12px;">
        <div class="form-group">
          <label>面积下限（㎡）</label>
          <input v-model.number="form.minArea" type="number" class="form-control" placeholder="如：60" />
        </div>
        <div class="form-group">
          <label>面积上限（㎡）</label>
          <input v-model.number="form.maxArea" type="number" class="form-control" placeholder="如：80" />
        </div>
        <div class="form-group">
          <label>最低分数</label>
          <input v-model.number="form.minScore" type="number" class="form-control" placeholder="如：80" />
        </div>
      </div>
      <button class="btn btn-primary mt-2" @click="addStandard">提交</button>
      <span v-if="msg" class="alert alert-success" style="margin-left:12px;">{{ msg }}</span>
    </div>

    <!-- 住房标准列表 -->
    <div class="glass-card" v-reveal>
      <div class="table-wrapper">
        <table>
          <thead>
            <tr>
              <th style="width:60px;">ID</th>
              <th>面积范围</th>
              <th>最低分数</th>
              <th style="width:120px;">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in list" :key="item.id">
              <td>{{ item.id }}</td>
              <td><strong>{{ item.minArea }} ~ {{ item.maxArea }} ㎡</strong></td>
              <td>
                <span class="tag" :class="item.minScore >= 100 ? 'tag-danger' : item.minScore >= 80 ? 'tag-warning' : 'tag-success'">
                  {{ item.minScore }} 分
                </span>
              </td>
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
        <div class="card-title">编辑住房标准 #{{ editing.id }}</div>
        <div class="form-group">
          <label>面积下限（㎡）</label>
          <input v-model.number="editing.minArea" type="number" class="form-control" />
        </div>
        <div class="form-group">
          <label>面积上限（㎡）</label>
          <input v-model.number="editing.maxArea" type="number" class="form-control" />
        </div>
        <div class="form-group">
          <label>最低分数要求</label>
          <input v-model.number="editing.minScore" type="number" class="form-control" />
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
      form: { minArea: null, maxArea: null, minScore: null },
      msg: ''
    }
  },

  mounted() {
    this.load()
  },

  methods: {
    load() {
      fetch('http://localhost:8080/standards')
        .then(res => res.json())
        .then(data => { this.list = data })
        .catch(err => console.error('加载失败', err))
    },

    addStandard() {
      fetch('http://localhost:8080/standards', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(this.form)
      })
        .then(() => {
          this.msg = '新增成功'
          this.form = { minArea: null, maxArea: null, minScore: null }
          this.load()
          setTimeout(() => { this.msg = '' }, 2000)
        })
        .catch(err => console.error(err))
    },

    startEdit(item) {
      this.editing = { ...item }
    },

    saveEdit() {
      fetch(`http://localhost:8080/standards/${this.editing.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(this.editing)
      })
        .then(() => { this.editing = null; this.load() })
        .catch(err => console.error(err))
    },

    del(id) {
      if (!confirm('确认删除？')) return
      fetch(`http://localhost:8080/standards/${id}`, { method: 'DELETE' })
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
