<template>
  <div>
    <h2>申请管理</h2>

    <!-- ========== 新建申请表单 ========== -->
    <div class="form-card">
      <h3>新建申请</h3>

      <div class="form-row">
        <label>申请人</label>
        <select v-model="form.userId" @change="onUserChange">
          <option value="">-- 请选择申请人 --</option>
          <option v-for="u in availableUsers" :key="u.id" :value="u.id">
            {{ u.name }}（{{ u.role === 'tenant' ? '住户' : '管理员' }}）
          </option>
        </select>
        <span v-if="form.type && availableUsers.length === 0" class="hint">
          {{ form.type === 'ALLOCATE' ? '（所有用户均已有住房）' : '（没有用户拥有住房）' }}
        </span>
      </div>

      <div class="form-row">
        <label>申请类型</label>
        <select v-model="form.type">
          <option value="">-- 请选择类型 --</option>
          <option value="ALLOCATE">分房申请</option>
          <option value="TRANSFER">调房申请</option>
          <option value="RETURN">退房申请</option>
        </select>
      </div>

      <!-- 分房 & 调房 共同字段 -->
      <template v-if="form.type === 'ALLOCATE' || form.type === 'TRANSFER'">
        <div class="form-row">
          <label>部门</label>
          <input v-model="form.department" placeholder="如：计算机学院" />
        </div>
        <div class="form-row">
          <label>职称</label>
          <select v-model="form.titleLevel">
            <option value="">-- 请选择职称 --</option>
            <option value="教授">教授</option>
            <option value="副教授">副教授</option>
            <option value="讲师">讲师</option>
            <option value="助教">助教</option>
            <option value="其他">其他</option>
          </select>
        </div>
        <div class="form-row">
          <label>家庭人数</label>
          <input v-model.number="form.familySize" type="number" min="1" placeholder="如：3" />
        </div>
        <div class="form-row">
          <label>住房分数（留空则系统自动计算）</label>
          <input v-model.number="form.score" type="number" placeholder="留空自动计算" />
        </div>
        <div class="form-row">
          <label>申请面积（㎡）</label>
          <input v-model.number="form.requestArea" type="number" min="10" placeholder="如：80" />
        </div>
      </template>

      <!-- 调房专属字段 -->
      <template v-if="form.type === 'TRANSFER'">
        <div class="form-row">
          <label>原房号</label>
          <select v-model="form.originalHouseId">
            <option value="">-- 请选择原住房 --</option>
            <option v-for="h in allocatedHouses" :key="h.id" :value="h.id">
              {{ h.title }}（{{ h.area }}㎡，{{ h.rentPerM2 }}元/㎡/月）
            </option>
          </select>
        </div>
      </template>

      <!-- 退房专属字段 -->
      <template v-if="form.type === 'RETURN'">
        <div class="form-row">
          <label>部门</label>
          <input v-model="form.department" placeholder="如：计算机学院" />
        </div>
        <div class="form-row">
          <label>退房房号</label>
          <select v-model="form.houseId">
            <option value="">-- 请选择要退的房屋 --</option>
            <option v-for="h in allocatedHouses" :key="h.id" :value="h.id">
              {{ h.title }}（{{ h.area }}㎡）
            </option>
          </select>
        </div>
      </template>

      <button class="submit-btn" @click="submit" :disabled="!canSubmit">提交申请</button>
      <span v-if="msg" class="msg">{{ msg }}</span>
    </div>

    <!-- ========== 申请列表 ========== -->
    <h3>申请列表</h3>
    <table border="1" cellpadding="8">
      <thead>
        <tr>
          <th>ID</th>
          <th>申请人</th>
          <th>类型</th>
          <th>状态</th>
          <th>部门</th>
          <th>职称</th>
          <th>家庭人数</th>
          <th>分数</th>
          <th>请求面积</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in list" :key="item.id">
          <td>{{ item.id }}</td>
          <td>{{ getUserName(item.userId) }}</td>
          <td>
            <span v-if="item.type === 'ALLOCATE'" class="tag alloc">分房</span>
            <span v-else-if="item.type === 'TRANSFER'" class="tag trans">调房</span>
            <span v-else-if="item.type === 'RETURN'" class="tag ret">退房</span>
            <span v-else>{{ item.type }}</span>
          </td>
          <td>
            <span v-if="item.status === 'PENDING'" class="tag pending">待审批</span>
            <span v-else-if="item.status === 'APPROVED'" class="tag approved">已通过</span>
            <span v-else-if="item.status === 'REJECTED'" class="tag rejected">已拒绝</span>
            <span v-else>{{ item.status }}</span>
          </td>
          <td>{{ item.department }}</td>
          <td>{{ item.titleLevel }}</td>
          <td>{{ item.familySize }}</td>
          <td>{{ item.score }}</td>
          <td>{{ item.requestArea }}</td>
          <td>
            <button
              v-if="item.type === 'ALLOCATE' && item.status === 'PENDING'"
              class="btn-alloc" @click="approve(item)">
              分房
            </button>
            <button
              v-if="item.type === 'TRANSFER' && item.status === 'PENDING'"
              class="btn-trans" @click="transfer(item)">
              调房
            </button>
            <button
              v-if="item.type === 'RETURN' && item.status === 'PENDING'"
              class="btn-ret" @click="release(item)">
              退房
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
export default {
  data() {
    return {
      list: [],
      users: [],
      houses: [],
      records: [],
      form: {
        userId: '',
        type: '',
        department: '',
        titleLevel: '',
        familySize: null,
        score: null,
        requestArea: null,
        originalHouseId: null,
        houseId: null
      },
      msg: ''
    }
  },

  computed: {
    // 拥有住房的用户ID集合
    hasHouseUserIds() {
      return new Set(this.records.map(r => r.userId))
    },

    // 根据申请类型过滤可选申请人
    availableUsers() {
      if (!this.form.type) return this.users
      if (this.form.type === 'ALLOCATE') {
        // 分房：只显示没有住房的人
        return this.users.filter(u => !this.hasHouseUserIds.has(u.id))
      }
      // 调房/退房：只显示有住房的人
      return this.users.filter(u => this.hasHouseUserIds.has(u.id))
    },

    // 已分配房屋
    allocatedHouses() {
      return this.houses.filter(h => h.status === '已分配')
    },

    // 当前选中用户的住房（用于调房/退房自动匹配）
    userHouses() {
      if (!this.form.userId) return []
      const userRecordIds = this.records
        .filter(r => r.userId === this.form.userId)
        .map(r => r.houseId)
      return this.allocatedHouses.filter(h => userRecordIds.includes(h.id))
    },

    canSubmit() {
      const f = this.form
      if (!f.userId || !f.type) return false
      if (f.type === 'RETURN') {
        return !!f.houseId && !!f.department
      }
      if (f.type === 'ALLOCATE' || f.type === 'TRANSFER') {
        if (!f.department || !f.titleLevel || !f.familySize || !f.requestArea) return false
        if (f.type === 'TRANSFER' && !f.originalHouseId) return false
        return true
      }
      return false
    }
  },

  mounted() {
    this.loadApps()
    this.loadUsers()
    this.loadHouses()
    this.loadRecords()
  },

  watch: {
    // 切类型时重置申请人
    'form.type'() {
      this.form.userId = ''
      this.form.houseId = null
      this.form.originalHouseId = null
    }
  },

  methods: {
    loadApps() {
      fetch('http://localhost:8080/applications')
        .then(res => res.json())
        .then(data => { this.list = data })
        .catch(err => console.error('加载申请失败', err))
    },

    loadUsers() {
      fetch('http://localhost:8080/users')
        .then(res => res.json())
        .then(data => { this.users = data })
        .catch(err => console.error('加载用户失败', err))
    },

    loadHouses() {
      fetch('http://localhost:8080/houses')
        .then(res => res.json())
        .then(data => { this.houses = data })
        .catch(err => console.error('加载房屋失败', err))
    },

    loadRecords() {
      fetch('http://localhost:8080/records')
        .then(res => res.json())
        .then(data => { this.records = data })
        .catch(err => console.error('加载住房记录失败', err))
    },

    getUserName(userId) {
      const u = this.users.find(u => u.id === userId)
      return u ? u.name : userId
    },

    // 选完申请人后，调房/退房自动匹配其住房
    onUserChange() {
      if (this.form.type === 'TRANSFER' && this.userHouses.length > 0) {
        this.form.originalHouseId = this.userHouses[0].id
      }
      if (this.form.type === 'RETURN' && this.userHouses.length > 0) {
        this.form.houseId = this.userHouses[0].id
      }
    },

    submit() {
      if (!this.canSubmit) return
      const body = { ...this.form }

      // 清理无关字段
      if (body.type === 'RETURN') {
        body.titleLevel = null
        body.familySize = null
        body.score = null
        body.requestArea = null
        body.originalHouseId = null
      }
      if (body.type === 'ALLOCATE') {
        body.houseId = null
        body.originalHouseId = null
      }
      if (body.type === 'TRANSFER') {
        body.houseId = null
      }

      fetch('http://localhost:8080/applications', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
      })
        .then(async res => {
          if (!res.ok) {
            const err = await res.json()
            throw new Error(err.message || '请求失败')
          }
          return res.json()
        })
        .then(() => {
          this.msg = '✅ 申请提交成功！'
          this.resetForm()
          this.loadApps()
          setTimeout(() => { this.msg = '' }, 3000)
        })
        .catch(err => {
          console.error('提交失败', err)
          this.msg = '❌ ' + err.message
        })
    },

    resetForm() {
      this.form = {
        userId: '',
        type: '',
        department: '',
        titleLevel: '',
        familySize: null,
        score: null,
        requestArea: null,
        originalHouseId: null,
        houseId: null
      }
    },

    approve(item) {
      fetch(`http://localhost:8080/applications/${item.id}/approve`, { method: 'PUT' })
        .then(() => { this.loadApps(); this.loadHouses() })
        .catch(err => console.error(err))
    },

    transfer(item) {
      fetch(`http://localhost:8080/applications/${item.id}/transfer`, { method: 'PUT' })
        .then(() => { this.loadApps(); this.loadHouses() })
        .catch(err => console.error(err))
    },

    release(item) {
      fetch(`http://localhost:8080/applications/${item.id}/release`, { method: 'PUT' })
        .then(() => { this.loadApps(); this.loadHouses() })
        .catch(err => console.error(err))
    }
  }
}
</script>

<style scoped>
/* 表单 */
.form-card {
  background: #f9f9f9;
  border: 1px solid #ddd;
  border-radius: 6px;
  padding: 20px;
  margin-bottom: 30px;
}
.form-card h3 {
  margin-top: 0;
}
.form-row {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}
.form-row label {
  width: 200px;
  font-weight: bold;
}
.form-row input, .form-row select {
  padding: 6px 10px;
  width: 300px;
  border: 1px solid #ccc;
  border-radius: 4px;
}
.submit-btn {
  margin-top: 12px;
  padding: 8px 24px;
  background: #1890ff;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 15px;
}
.submit-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}
.msg {
  margin-left: 16px;
  font-size: 15px;
}

/* 表格 */
table {
  border-collapse: collapse;
  width: 100%;
  margin-top: 10px;
}
th, td {
  padding: 8px;
  text-align: center;
}
button {
  margin: 0 3px;
  padding: 4px 10px;
  cursor: pointer;
}

/* 标签 */
.tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 3px;
  font-size: 12px;
}
.alloc { background: #e6f7ff; color: #1890ff; }
.trans { background: #fff7e6; color: #fa8c16; }
.ret   { background: #fff1f0; color: #f5222d; }
.pending  { background: #fffbe6; color: #d48806; }
.approved { background: #f6ffed; color: #52c41a; }
.rejected { background: #fff1f0; color: #f5222d; }

.btn-alloc { background: #1890ff; color: #fff; border: none; }
.btn-trans { background: #fa8c16; color: #fff; border: none; }
.btn-ret   { background: #f5222d; color: #fff; border: none; }
</style>