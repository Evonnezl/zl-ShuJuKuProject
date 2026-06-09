import { createApp } from 'vue'
import './style.css'
import App from './App.vue'

const app = createApp(App)

// 滚动揭示指令
app.directive('reveal', {
  mounted(el) {
    el.classList.add('reveal-hidden')
    const obs = new IntersectionObserver(([entry]) => {
      if (entry.isIntersecting) {
        el.classList.add('reveal-show')
        obs.unobserve(el)
      }
    }, { threshold: 0.15, rootMargin: '0px 0px -40px 0px' })
    obs.observe(el)
  }
})

app.mount('#app')
