<template>
  <div>
    <div class="page-title-row">
      <h2>所有房屋</h2>
      <button class="btn btn-primary" @click="showAddForm = !showAddForm">
        {{ showAddForm ? '取消' : '+ 新增房屋' }}
      </button>
    </div>

    <!-- 新增房屋表单 -->
    <div v-if="showAddForm" class="glass-card">
      <div class="card-title">新增房屋</div>
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

    <!-- 房屋卡片网格 -->
    <div class="house-grid">
      <div
        v-for="item in list"
        :key="item.id"
        class="house-card"
        :class="buildingClass(item.title)"
      >
        <!-- 顶部色条 -->
        <div class="card-top-bar" :style="{ background: buildingGradient(item.title) }"></div>

        <!-- 卡片内容 -->
        <div class="card-body">
          <div class="card-title-row">
            <span class="house-title">{{ item.title }}</span>
            <span class="tag" :class="(item.status === '空房' || item.status === 'empty') ? 'tag-success' : 'tag-warning'">
              {{ item.status === 'empty' ? '空房' : item.status }}
            </span>
          </div>

          <div class="card-stats">
            <div class="card-stat">
              <div>
                <div class="stat-label">面积</div>
                <div class="stat-num">{{ item.area }} <small>㎡</small></div>
              </div>
            </div>
            <div class="card-stat">
              <div>
                <div class="stat-label">每平米租金</div>
                <div class="stat-num">{{ item.rentPerM2 }} <small>元</small></div>
              </div>
            </div>
            <div class="card-stat">
              <div>
                <div class="stat-label">月租金</div>
                <div class="stat-num highlight">{{ (item.area * item.rentPerM2).toFixed(0) }} <small>元</small></div>
              </div>
            </div>
          </div>

          <div class="card-actions">
            <button class="btn btn-outline btn-sm" @click="startEdit(item)">编辑</button>
            <button class="btn btn-danger btn-sm" @click="del(item.id)">删除</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 编辑弹窗 -->
    <div v-if="editing" class="modal-mask" @click.self="editing=null">
      <div class="modal-card">
        <div class="card-title">编辑房屋 #{{ editing.id }}</div>
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
        .then(data => { this.list = data.sort((a, b) => a.area - b.area) })
        .catch(err => console.error('加载房屋列表失败', err))
    },

    buildingClass(title) {
      const prefix = (title || '').charAt(0).toUpperCase()
      return 'bld-' + prefix
    },

    buildingGradient(title) {
      const prefix = (title || '').charAt(0).toUpperCase()
      const colors = {
        A: 'linear-gradient(135deg, #60a5fa, #3b82f6)',
        B: 'linear-gradient(135deg, #34d399, #059669)',
        C: 'linear-gradient(135deg, #fbbf24, #d97706)',
        D: 'linear-gradient(135deg, #f472b6, #db2777)',
        E: 'linear-gradient(135deg, #a78bfa, #7c3aed)'
      }
      return colors[prefix] || 'linear-gradient(135deg, #94a3b8, #64748b)'
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
/* 卡片网格 */
.house-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

/* 房屋卡片 */
.house-card {
  background: var(--glass-bg);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius);
  overflow: hidden;
  box-shadow: var(--glass-shadow);
  transition: transform .25s ease, box-shadow .25s ease;
}

/* 顶部色条 */
.card-top-bar {
  height: 6px;
}

/* 卡片内容 */
.card-body {
  padding: 16px 18px 18px;
}

.card-title-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
}

.house-title {
  font-size: 18px;
  font-weight: 700;
  color: #000;
  letter-spacing: 1px;
}

/* 统计数据 */
.card-stats {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 14px;
}

.card-stat {
  display: flex;
  align-items: center;
  gap: 10px;
}

.stat-label {
  font-size: 11px;
  color: #000;
  line-height: 1.2;
}

.stat-num {
  font-size: 15px;
  font-weight: 600;
  color: #000;
}
.stat-num small {
  font-size: 11px;
  font-weight: 400;
}
.stat-num.highlight {
  font-size: 18px;
  color: #000;
}

/* 操作按钮 */
.card-actions {
  display: flex;
  gap: 8px;
  padding-top: 12px;
  border-top: 1px solid rgba(0,0,0,.06);
}

/* 弹窗 */
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
  box-shadow: 0 20px 60px rgba(0,0,0,.3);
}

/* 响应式 */
@media (max-width: 1100px) {
  .house-grid { grid-template-columns: repeat(3, 1fr); }
}
@media (max-width: 800px) {
  .house-grid { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 500px) {
  .house-grid { grid-template-columns: 1fr; }
}
</style>
