<template>
  <div class="profile-page">
    <!-- 头像区 -->
    <div class="profile-header">
      <div class="avatar" :class="user.role === 'admin' ? 'avatar-admin' : 'avatar-tenant'">
        {{ (user.name || '?').charAt(0) }}
      </div>
      <div class="profile-name">{{ user.name }}</div>
      <span class="tag" :class="user.role === 'admin' ? 'tag-warning' : 'tag-success'">
        {{ user.role === 'admin' ? '管理员' : '住户' }}
      </span>
    </div>

    <!-- 信息卡片 -->
    <div class="profile-card glass-card">
      <div class="card-title">基本信息</div>

      <div class="info-row">
        <span class="info-label">姓名</span>
        <input v-if="editing" v-model="form.name" class="info-input" />
        <span v-else class="info-val">{{ form.name }}</span>
      </div>
      <div class="info-row">
        <span class="info-label">邮箱</span>
        <input v-if="editing" v-model="form.email" class="info-input" />
        <span v-else class="info-val">{{ form.email || '-' }}</span>
      </div>
      <div class="info-row">
        <span class="info-label">电话</span>
        <input v-if="editing" v-model="form.phone" class="info-input" />
        <span v-else class="info-val">{{ form.phone || '-' }}</span>
      </div>

      <div style="margin-top:16px;">
        <button v-if="!editing" class="btn btn-primary" @click="startEdit">编辑资料</button>
        <template v-else>
          <button class="btn btn-primary" @click="saveBasic" :disabled="saving">
            {{ saving ? '保存中...' : '保存' }}
          </button>
          <button class="btn btn-outline" @click="cancelEdit" style="margin-left:8px;">取消</button>
        </template>
        <span v-if="msg" class="alert" :class="msgType === 'ok' ? 'alert-success' : 'alert-error'" style="margin-left:12px;">{{ msg }}</span>
      </div>
    </div>

    <!-- 住房信息 -->
    <div class="profile-card glass-card">
      <div class="card-title">住房信息</div>
      <template v-if="myHouse">
        <div class="info-row">
          <span class="info-label">房号</span>
          <span class="info-val highlight">{{ myHouse.houseTitle }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">面积</span>
          <span class="info-val">{{ myHouse.houseArea }} ㎡</span>
        </div>
        <div class="info-row">
          <span class="info-label">月租金</span>
          <span class="info-val">{{ myHouse.rentAmount }} 元</span>
        </div>
        <div class="info-row">
          <span class="info-label">入住日期</span>
          <span class="info-val">{{ myHouse.moveInDate }}</span>
        </div>
      </template>
      <div v-else class="empty-house">
        <p>暂无住房分配</p>
      </div>
    </div>

    <!-- 密码卡片 -->
    <div class="profile-card glass-card">
      <div class="card-title">修改密码</div>
      <div class="info-row">
        <span class="info-label">旧密码</span>
        <input v-model="pwd.old" type="password" class="info-input" placeholder="输入当前密码" />
      </div>
      <div class="info-row">
        <span class="info-label">新密码</span>
        <input v-model="pwd.new1" type="password" class="info-input" placeholder="输入新密码" />
      </div>
      <div class="info-row">
        <span class="info-label">确认密码</span>
        <input v-model="pwd.new2" type="password" class="info-input" placeholder="再次输入新密码" />
      </div>
      <div style="margin-top:16px;">
        <button class="btn btn-primary" @click="savePwd" :disabled="savingPwd">
          {{ savingPwd ? '修改中...' : '修改密码' }}
        </button>
        <span v-if="pwdMsg" class="alert" :class="pwdType === 'ok' ? 'alert-success' : 'alert-error'" style="margin-left:12px;">{{ pwdMsg }}</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  inject: ['user'],
  data() {
    return {
      form: { name: '', email: '', phone: '' },
      pwd: { old: '', new1: '', new2: '' },
      myHouse: null,
      editing: false,
      saving: false,
      savingPwd: false,
      msg: '',
      msgType: 'ok',
      pwdMsg: '',
      pwdType: 'ok'
    }
  },

  mounted() {
    this.form.name = this.user.name || ''
    this.form.email = this.user.email || ''
    this.form.phone = this.user.phone || ''
    this.loadMyHouse()
  },

  methods: {
    loadMyHouse() {
      fetch('http://localhost:8080/records')
        .then(r => r.json())
        .then(data => {
          this.myHouse = data.find(r => r.userId === this.user.id) || null
        })
        .catch(() => {})
    },

    startEdit() {
      this.editing = true
      this.msg = ''
    },
    cancelEdit() {
      this.form.name = this.user.name || ''
      this.form.email = this.user.email || ''
      this.form.phone = this.user.phone || ''
      this.editing = false
      this.msg = ''
    },
    saveBasic() {
      this.saving = true
      this.msg = ''
      fetch(`http://localhost:8080/users/${this.user.id}/profile`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(this.form)
      })
        .then(r => r.json())
        .then(d => {
          this.saving = false
          this.editing = false
          this.msg = d.message
          this.msgType = 'ok'
          const u = { ...this.user, name: this.form.name, email: this.form.email, phone: this.form.phone }
          sessionStorage.setItem('user', JSON.stringify(u))
          this.user.name = u.name
          this.user.email = u.email
          this.user.phone = u.phone
        })
        .catch(() => {
          this.saving = false
          this.msg = '保存失败'
          this.msgType = 'err'
        })
    },

    savePwd() {
      if (!this.pwd.old) { this.pwdMsg = '请输入旧密码'; this.pwdType = 'err'; return }
      if (!this.pwd.new1) { this.pwdMsg = '请输入新密码'; this.pwdType = 'err'; return }
      if (this.pwd.new1 !== this.pwd.new2) { this.pwdMsg = '两次新密码不一致'; this.pwdType = 'err'; return }
      this.savingPwd = true
      this.pwdMsg = ''
      fetch(`http://localhost:8080/users/${this.user.id}/profile`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ oldPassword: this.pwd.old, newPassword: this.pwd.new1 })
      })
        .then(r => r.json().then(d => ({ ok: r.ok, data: d })))
        .then(({ ok, data }) => {
          this.savingPwd = false
          this.pwdMsg = data.message
          this.pwdType = ok ? 'ok' : 'err'
          if (ok) this.pwd = { old: '', new1: '', new2: '' }
        })
        .catch(() => {
          this.savingPwd = false
          this.pwdMsg = '修改失败'
          this.pwdType = 'err'
        })
    }
  }
}
</script>

<style scoped>
.profile-page {
  max-width: 500px;
  margin: 0 auto;
}

/* 头像区 */
.profile-header {
  text-align: center;
  margin-bottom: 24px;
}
.avatar {
  width: 88px;
  height: 88px;
  border-radius: 50%;
  margin: 0 auto 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36px;
  font-weight: 700;
  color: #fff;
  letter-spacing: 2px;
}
.avatar-admin {
  background: linear-gradient(135deg, #818cf8, #4f46e5);
}
.avatar-tenant {
  background: linear-gradient(135deg, #38bdf8, #0ea5e9);
}
.profile-name {
  font-size: 22px;
  font-weight: 700;
  color: #000;
  margin-bottom: 6px;
}

/* 信息卡片 */
.profile-card {
  margin-bottom: 16px;
}
.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid rgba(0,0,0,.05);
}
.info-row:last-of-type {
  border-bottom: none;
}
.info-label {
  font-size: 13px;
  color: #000;
  opacity: .5;
  min-width: 60px;
}
.info-val {
  font-size: 14px;
  font-weight: 600;
  color: #000;
}
.info-input {
  width: 220px;
  padding: 6px 10px;
  font-size: 13px;
  border: 1px solid rgba(0,0,0,.12);
  border-radius: 6px;
  background: rgba(255,255,255,.8);
  outline: none;
  color: #000;
  text-align: right;
}
.info-input:focus {
  border-color: var(--primary);
}
.info-val.highlight {
  color: #1d4ed8;
  font-size: 16px;
}
.empty-house {
  text-align: center;
  padding: 20px 0;
}
.empty-house p {
  color: #000;
  opacity: .35;
  font-size: 14px;
}
</style>
