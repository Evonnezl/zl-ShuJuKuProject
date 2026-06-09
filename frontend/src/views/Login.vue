<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-header">
        <h1>房产管理系统</h1>
        <p class="login-sub">HOUSING MANAGEMENT SYSTEM</p>
      </div>

      <!-- 登录/注册切换 -->
      <div class="tab-bar">
        <div class="tab" :class="{ active: mode === 'login' }" @click="switchMode('login')">登录</div>
        <div class="tab" :class="{ active: mode === 'register' }" @click="switchMode('register')">注册</div>
      </div>

      <div v-if="error" class="alert alert-error">{{ error }}</div>
      <div v-if="success" class="alert alert-success">{{ success }}</div>

      <!-- ======== 登录表单 ======== -->
      <template v-if="mode === 'login'">
        <div class="form-group">
          <label>账号（邮箱或姓名）</label>
          <input v-model="loginForm.account" class="form-control" placeholder="请输入邮箱或姓名" @keyup.enter="doLogin" />
        </div>
        <div class="form-group">
          <label>密码</label>
          <input v-model="loginForm.password" type="password" class="form-control" placeholder="请输入密码" @keyup.enter="doLogin" />
        </div>
        <button class="btn btn-primary" style="width:100%;" @click="doLogin" :disabled="!loginForm.account || !loginForm.password">
          登 录
        </button>
      </template>

      <!-- ======== 注册表单 ======== -->
      <template v-if="mode === 'register'">
        <div class="form-group">
          <label>邮箱</label>
          <div style="display:flex; gap:8px;">
            <input v-model="regForm.email" class="form-control" placeholder="请输入邮箱" style="flex:1;" />
            <button class="btn btn-outline btn-sm" @click="sendCode" :disabled="!regForm.email || sending">
              {{ sending ? '发送中...' : codeSent ? codeCountdown + 's' : '发送验证码' }}
            </button>
          </div>
        </div>
        <div class="form-group">
          <label>验证码</label>
          <input v-model="regForm.code" class="form-control" placeholder="请输入6位验证码" maxlength="6" />
        </div>
        <div class="form-group">
          <label>姓名</label>
          <input v-model="regForm.name" class="form-control" placeholder="请输入姓名" />
        </div>
        <div class="form-group">
          <label>密码</label>
          <input v-model="regForm.password" type="password" class="form-control" placeholder="至少4位" />
        </div>
        <button class="btn btn-primary" style="width:100%;" @click="doRegister" :disabled="!canRegister">
          注 册
        </button>
      </template>

    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      mode: 'login',
      loginForm: { account: '', password: '' },
      regForm: { email: '', code: '', name: '', password: '' },
      error: '',
      success: '',
      sending: false,
      codeSent: false,
      codeCountdown: 60
    }
  },

  computed: {
    canRegister() {
      const f = this.regForm
      return f.email && f.code && f.name && f.password && f.password.length >= 4
    }
  },

  methods: {
    switchMode(m) {
      this.mode = m
      this.error = ''
      this.success = ''
    },

    doLogin() {
      if (!this.loginForm.account || !this.loginForm.password) return
      this.error = ''
      fetch('http://localhost:8080/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(this.loginForm)
      })
        .then(async res => {
          if (!res.ok) { const err = await res.json(); throw new Error(err.message) }
          return res.json()
        })
        .then(user => {
          sessionStorage.setItem('user', JSON.stringify(user))
          this.$emit('login', user)
        })
        .catch(err => { this.error = err.message })
    },

    sendCode() {
      if (!this.regForm.email || this.sending) return
      this.error = ''
      this.sending = true
      fetch('http://localhost:8080/auth/send-code', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: this.regForm.email })
      })
        .then(async res => {
          if (!res.ok) { const err = await res.json(); throw new Error(err.message) }
          return res.json()
        })
        .then(data => {
          this.success = data.message
          this.codeSent = true
          this.sending = false
          this.codeCountdown = 60
          const timer = setInterval(() => {
            this.codeCountdown--
            if (this.codeCountdown <= 0) { clearInterval(timer); this.codeSent = false }
          }, 1000)
        })
        .catch(err => { this.error = err.message; this.sending = false })
    },

    doRegister() {
      if (!this.canRegister) return
      this.error = ''
      fetch('http://localhost:8080/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(this.regForm)
      })
        .then(async res => {
          if (!res.ok) { const err = await res.json(); throw new Error(err.message) }
          return res.json()
        })
        .then(data => {
          this.success = '注册成功！请切换到登录页登录'
          this.regForm = { email: '', code: '', name: '', password: '' }
          setTimeout(() => { this.switchMode('login') }, 2000)
        })
        .catch(err => { this.error = err.message })
    }
  }
}
</script>

<style scoped>
.login-page {
  width: 100%; min-height: 100vh;
  display: flex; align-items: center; justify-content: center;
  position: relative; z-index: 1;
}
.login-page::before {
  content: ''; position: fixed; inset: 0; z-index: -1;
  background: url('/zlzl.jpg') center / cover no-repeat;
}
.login-page::after {
  content: ''; position: fixed; inset: 0; z-index: -1;
  background: rgba(0,0,0,.25);
}
.login-card {
  background: rgba(255,255,255,.78);
  backdrop-filter: blur(20px) saturate(150%);
  -webkit-backdrop-filter: blur(20px) saturate(150%);
  border: 1px solid rgba(255,255,255,.25);
  border-radius: 20px; padding: 36px 40px;
  width: 400px; max-width: 90vw;
  box-shadow: 0 8px 32px rgba(30,10,60,.15);
}
.login-header { text-align: center; margin-bottom: 24px; }
.login-header h1 {
  font-size: 24px;
  background: linear-gradient(135deg, #60a5fa, #3b82f6);
  -webkit-background-clip: text; -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0 0 4px; letter-spacing: 2px;
}
.login-sub { font-size: 12px; color: #94a3b8; letter-spacing: 2px; }
.tab-bar { display: flex; margin-bottom: 20px; border-bottom: 2px solid rgba(0,0,0,.06); }
.tab {
  flex: 1; text-align: center; padding: 10px; cursor: pointer;
  font-size: 15px; color: #94a3b8; transition: all .2s;
  border-bottom: 2px solid transparent; margin-bottom: -2px;
}
.tab.active { color: #3b82f6; font-weight: 600; border-bottom-color: #3b82f6; }
.tab:hover { color: #3b82f6; }
</style>
