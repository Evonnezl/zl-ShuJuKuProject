<template>
  <div>
    <!-- ========== 新建申请表单 ========== -->
    <div class="glass-card">
      <div class="card-title">
        新建申请
        <span class="text-muted" style="font-weight:400; font-size:12px; margin-left:8px;">
          选择申请人、类型并填写信息
        </span>
      </div>

      <div style="display:grid; grid-template-columns:1fr 1fr; gap:12px;">
        <!-- 申请类型 -->
        <div class="form-group">
          <label>申请类型</label>
          <select v-model="form.type" class="form-control">
            <option value="">-- 请选择类型 --</option>
            <option value="ALLOCATE">分房申请</option>
            <option value="TRANSFER">调房申请</option>
            <option value="RETURN">退房申请</option>
          </select>
        </div>

        <!-- 申请人 -->
        <div class="form-group">
          <label>申请人</label>
          <select v-model="form.userId" class="form-control" @change="onUserChange">
            <option value="">-- 请选择申请人 --</option>
            <option v-for="u in availableUsers" :key="u.id" :value="u.id">
              {{ u.name }}（{{ u.role === 'tenant' ? '住户' : '管理员' }}）
            </option>
          </select>
          <span v-if="form.type && availableUsers.length === 0" class="text-muted" style="font-size:12px;">
            {{ form.type === 'ALLOCATE' ? '所有用户均已有住房' : '没有用户拥有住房' }}
          </span>
        </div>

        <!-- 分房 & 调房 共同字段 -->
        <template v-if="form.type === 'ALLOCATE' || form.type === 'TRANSFER'">
          <div class="form-group">
            <label>部门</label>
            <select v-model="form.department" class="form-control">
              <option value="">-- 请选择部门 --</option>
              <option value="计算机学院">计算机学院</option>
              <option value="电子信息学院">电子信息学院</option>
              <option value="数学与统计学院">数学与统计学院</option>
              <option value="物理学院">物理学院</option>
              <option value="化学学院">化学学院</option>
              <option value="生命科学学院">生命科学学院</option>
              <option value="经济管理学院">经济管理学院</option>
              <option value="法学院">法学院</option>
              <option value="外国语学院">外国语学院</option>
              <option value="马克思主义学院">马克思主义学院</option>
              <option value="后勤集团">后勤集团</option>
              <option value="图书馆">图书馆</option>
              <option value="校机关">校机关</option>
            </select>
          </div>
          <div class="form-group">
            <label>职称</label>
            <select v-model="form.titleLevel" class="form-control">
              <option value="">-- 请选择职称 --</option>
              <option value="教授">教授</option>
              <option value="副教授">副教授</option>
              <option value="讲师">讲师</option>
              <option value="助教">助教</option>
              <option value="其他">其他</option>
            </select>
          </div>
          <div class="form-group">
            <label>家庭人数</label>
            <input v-model.number="form.familySize" type="number" min="1" class="form-control" placeholder="如：3" />
          </div>
          <div class="form-group">
            <label>住房分数 <span class="text-muted">（留空自动计算）</span></label>
            <input v-model.number="form.score" type="number" class="form-control" placeholder="留空则系统自动计算" />
          </div>
          <div class="form-group">
            <label>申请面积（㎡）</label>
            <input v-model.number="form.requestArea" type="number" min="10" class="form-control" placeholder="如：80" />
          </div>
        </template>

        <!-- 调房专属 -->
        <template v-if="form.type === 'TRANSFER'">
          <div class="form-group">
            <label>原房号</label>
            <select v-model="form.originalHouseId" class="form-control">
              <option value="">-- 请选择原住房 --</option>
              <option v-for="h in allocatedHouses" :key="h.id" :value="h.id">
                {{ h.title }}（{{ h.area }}㎡，{{ h.rentPerM2 }}元/㎡/月）
              </option>
            </select>
          </div>
        </template>

        <!-- 退房专属 -->
        <template v-if="form.type === 'RETURN'">
          <div class="form-group">
            <label>部门</label>
            <select v-model="form.department" class="form-control">
              <option value="">-- 请选择部门 --</option>
              <option value="计算机学院">计算机学院</option>
              <option value="电子信息学院">电子信息学院</option>
              <option value="数学与统计学院">数学与统计学院</option>
              <option value="物理学院">物理学院</option>
              <option value="化学学院">化学学院</option>
              <option value="生命科学学院">生命科学学院</option>
              <option value="经济管理学院">经济管理学院</option>
              <option value="法学院">法学院</option>
              <option value="外国语学院">外国语学院</option>
              <option value="马克思主义学院">马克思主义学院</option>
              <option value="后勤集团">后勤集团</option>
              <option value="图书馆">图书馆</option>
              <option value="校机关">校机关</option>
            </select>
          </div>
          <div class="form-group">
            <label>退房房号</label>
            <select v-model="form.houseId" class="form-control">
              <option value="">-- 请选择要退的房屋 --</option>
              <option v-for="h in allocatedHouses" :key="h.id" :value="h.id">
                {{ h.title }}（{{ h.area }}㎡）
              </option>
            </select>
          </div>
        </template>
      </div>

      <button class="btn btn-primary" @click="submit" :disabled="!canSubmit">
        提交申请
      </button>
      <span v-if="msg" :class="msg.startsWith('[OK]') ? 'alert alert-success' : 'alert alert-error'" style="margin-left:12px;">
        {{ msg }}
      </span>
    </div>

    <!-- ========== 申请列表 ========== -->
    <div class="glass-card">
      <div class="card-title">
        申请列表
        <span class="text-muted" style="font-weight:400; font-size:12px; margin-left:8px;">
          共 {{ filteredList.length }} 条
        </span>
      </div>
      <div class="table-wrapper">
        <table>
          <thead>
            <tr>
              <th style="width:50px;">ID</th>
              <th>申请人</th>
              <th style="width:80px;">类型</th>
              <th style="width:80px;">状态</th>
              <th>部门</th>
              <th>职称</th>
              <th style="width:70px;">家庭</th>
              <th style="width:70px;">分数</th>
              <th style="width:80px;">面积</th>
              <th style="width:130px;">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in filteredList" :key="item.id">
              <td>{{ item.id }}</td>
              <td><strong>{{ getUserName(item.userId) }}</strong></td>
              <td>
                <span v-if="item.type === 'ALLOCATE'" class="tag tag-info">分房</span>
                <span v-else-if="item.type === 'TRANSFER'" class="tag tag-warning">调房</span>
                <span v-else-if="item.type === 'RETURN'" class="tag tag-danger">退房</span>
                <span v-else>{{ item.type }}</span>
              </td>
              <td>
                <span v-if="item.status === 'PENDING'" class="tag tag-warning">待审批</span>
                <span v-else-if="item.status === 'APPROVED'" class="tag tag-success">已通过</span>
                <span v-else-if="item.status === 'REJECTED'" class="tag tag-danger">已拒绝</span>
                <span v-else>{{ item.status }}</span>
              </td>
              <td>{{ item.department }}</td>
              <td>{{ item.titleLevel }}</td>
              <td class="col-center">{{ item.familySize }}人</td>
              <td class="col-center">{{ item.score || '-' }}</td>
              <td class="col-center">{{ item.requestArea }}㎡</td>
              <td>
                <button
                  v-if="item.type === 'ALLOCATE' && item.status === 'PENDING'"
                  class="btn btn-success btn-sm" @click="approve(item)">
                  分房
                </button>
                <button
                  v-if="item.type === 'TRANSFER' && item.status === 'PENDING'"
                  class="btn btn-warning btn-sm" @click="transfer(item)">
                  调房
                </button>
                <button
                  v-if="item.type === 'RETURN' && item.status === 'PENDING'"
                  class="btn btn-danger btn-sm" @click="release(item)">
                  退房
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  inject: ['user'],

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
    // 住户只看自己的申请
    filteredList() {
      if (!this.user || this.user.role === 'admin') return this.list
      return this.list.filter(a => a.userId === this.user.id)
    },

    hasHouseUserIds() {
      return new Set(this.records.map(r => r.userId))
    },
    availableUsers() {
      let pool = this.users
      // 住户只能申请自己的
      if (this.user && this.user.role === 'tenant') {
        pool = pool.filter(u => u.id === this.user.id)
      }
      if (!this.form.type) return pool
      if (this.form.type === 'ALLOCATE') {
        return pool.filter(u => !this.hasHouseUserIds.has(u.id))
      }
      return pool.filter(u => this.hasHouseUserIds.has(u.id))
    },
    allocatedHouses() {
      return this.houses.filter(h => h.status === '已分配')
    },
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
      if (f.type === 'RETURN') return !!f.houseId && !!f.department
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
      if (body.type === 'RETURN') {
        body.titleLevel = null; body.familySize = null
        body.score = null; body.requestArea = null; body.originalHouseId = null
      }
      if (body.type === 'ALLOCATE') {
        body.houseId = null; body.originalHouseId = null
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
          this.msg = '[OK] 申请提交成功！'
          this.resetForm()
          this.loadApps()
          this.loadRecords()
          setTimeout(() => { this.msg = '' }, 3000)
        })
        .catch(err => {
          this.msg = '[ERR] ' + err.message
        })
    },
    resetForm() {
      this.form = {
        userId: '', type: '', department: '', titleLevel: '',
        familySize: null, score: null, requestArea: null,
        originalHouseId: null, houseId: null
      }
    },
    approve(item) {
      fetch(`http://localhost:8080/applications/${item.id}/approve`, { method: 'PUT' })
        .then(res => res.json())
        .then(data => {
          this.loadApps(); this.loadHouses(); this.loadRecords()
          this.msg = data.success ? '[OK] ' + data.message : '[提示] ' + data.message
          setTimeout(() => { this.msg = '' }, 5000)
        })
        .catch(err => console.error(err))
    },
    transfer(item) {
      fetch(`http://localhost:8080/applications/${item.id}/transfer`, { method: 'PUT' })
        .then(res => res.json())
        .then(data => {
          this.loadApps(); this.loadHouses(); this.loadRecords()
          this.msg = data.success ? '[OK] ' + data.message : '[提示] ' + data.message
          setTimeout(() => { this.msg = '' }, 5000)
        })
        .catch(err => console.error(err))
    },
    release(item) {
      fetch(`http://localhost:8080/applications/${item.id}/release`, { method: 'PUT' })
        .then(res => res.json())
        .then(data => {
          this.loadApps(); this.loadHouses(); this.loadRecords()
          this.msg = data.success ? '[OK] ' + data.message : '[提示] ' + data.message
          setTimeout(() => { this.msg = '' }, 5000)
        })
        .catch(err => console.error(err))
    }
  }
}
</script>
