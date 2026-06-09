import { createApp } from 'vue'
import './style.css'
import App from './App.vue'

const app = createApp(App)

app.directive('reveal', {
  mounted(el) {
    el.classList.add('reveal-hidden')
    const observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          // 小延迟让过渡动画可见
          setTimeout(() => {
            requestAnimationFrame(() => {
              el.classList.add('reveal-show')
            })
          }, 80)
          observer.unobserve(el)
        }
      })
    }, { threshold: 0.1 })
    // 等 DOM 稳定后再观察
    setTimeout(() => observer.observe(el), 100)
  }
})

app.mount('#app')
