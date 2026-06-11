<template>
  <span>{{ display }}</span>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  to: { type: Number, default: 0 },
  decimals: { type: Number, default: 0 },
  duration: { type: Number, default: 1200 }
})

const display = ref(0)

function animate(from, to) {
  const start = performance.now()
  function step(now) {
    const elapsed = now - start
    const progress = Math.min(elapsed / props.duration, 1)
    const eased = 1 - (1 - progress) * (1 - progress)
    const current = from + (to - from) * eased
    display.value = props.decimals > 0 ? +current.toFixed(props.decimals) : Math.floor(current)
    if (progress < 1) {
      requestAnimationFrame(step)
    } else {
      display.value = to
    }
  }
  requestAnimationFrame(step)
}

watch(() => props.to, (val) => {
  if (val != null && val !== display.value) {
    animate(display.value, Number(val))
  }
}, { immediate: true })
</script>
