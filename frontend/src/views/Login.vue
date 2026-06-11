<template>
  <div class="login-page">
    <div class="stars-bg" />
    <div class="login-card">
      <!-- 左侧品牌区 -->
      <div class="brand-panel">
        <div class="brand-content">
          <h1>房产管理系统</h1>
          <p class="brand-sub">HOUSING MANAGEMENT</p>
          <div class="brand-line"></div>
          <div class="feature-list">
            <div class="feature-item">分房管理</div>
            <div class="feature-item">调房退房</div>
            <div class="feature-item">统计查询</div>
            <div class="feature-item">住房标准</div>
            <div class="feature-item">房租计算</div>
            <div class="feature-item">分配单打印</div>
          </div>
        </div>
      </div>

      <!-- 右侧表单区 -->
      <div class="form-panel">
        <div class="tab-bar">
          <div class="tab" :class="{ active: mode === 'login' }" @click="switchMode('login')">登 录</div>
          <div class="divider"></div>
          <div class="tab" :class="{ active: mode === 'register' }" @click="switchMode('register')">注 册</div>
        </div>

        <div v-if="error" class="alert alert-error">{{ error }}</div>
        <div v-if="success" class="alert alert-success">{{ success }}</div>

        <!-- ======== 登录表单 ======== -->
        <template v-if="mode === 'login'">
          <div class="form-group">
            <label>账号</label>
            <input v-model="loginForm.account" class="form-control" placeholder="邮箱或姓名" @keyup.enter="doLogin" />
          </div>
          <div class="form-group">
            <label>密码</label>
            <input v-model="loginForm.password" type="password" class="form-control" placeholder="输入密码" @keyup.enter="doLogin" />
          </div>
          <button class="btn btn-primary login-btn" @click="doLogin" :disabled="!loginForm.account || !loginForm.password">
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
            <input v-model="regForm.code" class="form-control" placeholder="6位验证码" maxlength="6" />
          </div>
          <div class="form-group">
            <label>姓名</label>
            <input v-model="regForm.name" class="form-control" placeholder="请输入姓名" />
          </div>
          <div class="form-group">
            <label>密码</label>
            <input v-model="regForm.password" type="password" class="form-control" placeholder="至少4位" />
          </div>
          <button class="btn btn-primary login-btn" @click="doRegister" :disabled="!canRegister">
            注 册
          </button>
        </template>

        <div v-if="debugCode" class="debug-hint">
          验证码：<strong>{{ debugCode }}</strong>
        </div>
      </div>
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
      codeCountdown: 60,
      debugCode: ''
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
      this.debugCode = ''
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
          this.debugCode = data.debugCode || ''
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
  width: 100%;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  background:
    linear-gradient(135deg, rgba(6,10,26,.50) 0%, rgba(10,18,36,.40) 30%, rgba(8,16,32,.44) 60%, rgba(4,8,20,.55) 100%),
    url('/zlzl.jpg') center/cover no-repeat fixed;
}
.stars-bg {
  position: fixed; inset: 0; pointer-events: none;
  background:
    radial-gradient(1px 1px at 10% 15%, rgba(255,255,255,.9), transparent),
    radial-gradient(1px 1px at 25% 35%, rgba(255,255,255,.5), transparent),
    radial-gradient(1.5px 1.5px at 40% 10%, rgba(255,255,255,.8), transparent),
    radial-gradient(1px 1px at 55% 45%, rgba(255,255,255,.6), transparent),
    radial-gradient(1.5px 1.5px at 70% 20%, rgba(255,255,255,.7), transparent),
    radial-gradient(1px 1px at 85% 50%, rgba(255,255,255,.5), transparent),
    radial-gradient(1px 1px at 15% 60%, rgba(255,255,255,.7), transparent),
    radial-gradient(2px 2px at 20% 85%, rgba(255,255,255,.8), transparent),
    radial-gradient(2px 2px at 60% 5%, rgba(255,255,255,.9), transparent),
    radial-gradient(2px 2px at 90% 90%, rgba(255,255,255,.7), transparent);
}

/* 卡片容器 */
.login-card {
  position: relative; z-index: 1;
  display: flex;
  background: var(--glass-bg);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius);
  overflow: hidden;
  width: 760px;
  max-width: 94vw;
  min-height: 480px;
  box-shadow:
    0 8px 32px rgba(15, 20, 45, .14),
    0 0 100px rgba(59,130,246,.06);
  animation: cardIn .6s cubic-bezier(.4,0,.2,1);
}
@keyframes cardIn {
  from { opacity: 0; transform: translateY(30px) scale(.97); }
  to   { opacity: 1; transform: translateY(0) scale(1); }
}

/* 左侧品牌区 */
.brand-panel {
  flex: 0 0 340px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px 36px;
  background: rgba(59,130,246,.04);
  border-right: 1px solid rgba(0,0,0,.04);
}
.brand-content { text-align: center; }
.brand-content h1 {
  font-size: 26px;
  color: #000;
  margin: 0 0 4px;
  letter-spacing: 3px;
}
.brand-sub {
  font-size: 11px;
  color: #000;
  opacity: .3;
  letter-spacing: 4px;
  margin-bottom: 20px;
}
.brand-line {
  width: 40px; height: 3px;
  background: linear-gradient(90deg, #3b82f6, #818cf8);
  border-radius: 2px;
  margin: 0 auto 24px;
}
.feature-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: center;
}
.feature-item {
  padding: 6px 16px;
  background: rgba(255,255,255,.6);
  border: 1px solid rgba(0,0,0,.06);
  border-radius: 20px;
  font-size: 12px;
  color: #000;
  opacity: .55;
  letter-spacing: 1px;
}

/* 右侧表单区 */
.form-panel {
  flex: 1;
  padding: 48px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.tab-bar {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24px;
}
.tab {
  padding: 8px 32px;
  cursor: pointer;
  font-size: 16px;
  color: #000;
  opacity: .3;
  transition: all .25s;
  letter-spacing: 2px;
}
.tab.active {
  opacity: 1;
  color: #3b82f6;
  font-weight: 700;
}
.tab:hover { opacity: .7; }
.divider {
  width: 1px; height: 20px;
  background: rgba(0,0,0,.1);
}

.login-btn {
  width: 100%;
  padding: 12px;
  font-size: 15px;
  letter-spacing: 4px;
  margin-top: 8px;
}

.debug-hint {
  margin-top: 14px;
  padding: 7px 10px;
  background: rgba(234,179,8,.1);
  border: 1px dashed rgba(234,179,8,.3);
  border-radius: 8px;
  font-size: 12px;
  text-align: center;
  color: #000;
  opacity: .6;
}

@media (max-width: 700px) {
  .login-card { flex-direction: column; }
  .brand-panel { flex: auto; padding: 28px 24px; border-right: none; border-bottom: 1px solid rgba(0,0,0,.04); }
  .feature-item { font-size: 11px; padding: 4px 12px; }
  .form-panel { padding: 28px 24px; }
}
</style>
