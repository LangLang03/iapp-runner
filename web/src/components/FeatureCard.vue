<template>
  <div class="feature-card" :class="{ clickable: clickable }">
    <div class="feature-icon">
      <component :is="iconComponent" />
    </div>
    <h3 class="feature-title">{{ title }}</h3>
    <p class="feature-desc">{{ description }}</p>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { h } from 'vue'

const props = defineProps({
  icon: {
    type: String,
    default: 'code'
  },
  title: {
    type: String,
    required: true
  },
  description: {
    type: String,
    required: true
  },
  clickable: {
    type: Boolean,
    default: false
  }
})

const icons = {
  code: {
    render() {
      return h('svg', { 
        xmlns: 'http://www.w3.org/2000/svg', 
        width: 24, 
        height: 24, 
        viewBox: '0 0 24 24', 
        fill: 'none', 
        stroke: 'currentColor', 
        'stroke-width': 2, 
        'stroke-linecap': 'round', 
        'stroke-linejoin': 'round'
      }, [
        h('polyline', { points: '16 18 22 12 16 6' }),
        h('polyline', { points: '8 6 2 12 8 18' })
      ])
    }
  },
  database: {
    render() {
      return h('svg', { 
        xmlns: 'http://www.w3.org/2000/svg', 
        width: 24, 
        height: 24, 
        viewBox: '0 0 24 24', 
        fill: 'none', 
        stroke: 'currentColor', 
        'stroke-width': 2, 
        'stroke-linecap': 'round', 
        'stroke-linejoin': 'round'
      }, [
        h('ellipse', { cx: 12, cy: 5, rx: 9, ry: 3 }),
        h('path', { d: 'M21 12c0 1.66-4 3-9 3s-9-1.34-9-3' }),
        h('path', { d: 'M3 5v14c0 1.66 4 3 9 3s9-1.34 9-3V5' })
      ])
    }
  },
  shield: {
    render() {
      return h('svg', { 
        xmlns: 'http://www.w3.org/2000/svg', 
        width: 24, 
        height: 24, 
        viewBox: '0 0 24 24', 
        fill: 'none', 
        stroke: 'currentColor', 
        'stroke-width': 2, 
        'stroke-linecap': 'round', 
        'stroke-linejoin': 'round'
      }, [
        h('path', { d: 'M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z' })
      ])
    }
  },
  upload: {
    render() {
      return h('svg', { 
        xmlns: 'http://www.w3.org/2000/svg', 
        width: 24, 
        height: 24, 
        viewBox: '0 0 24 24', 
        fill: 'none', 
        stroke: 'currentColor', 
        'stroke-width': 2, 
        'stroke-linecap': 'round', 
        'stroke-linejoin': 'round'
      }, [
        h('path', { d: 'M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4' }),
        h('polyline', { points: '17 8 12 3 7 8' }),
        h('line', { x1: 12, y1: 3, x2: 12, y2: 15 })
      ])
    }
  },
  mail: {
    render() {
      return h('svg', { 
        xmlns: 'http://www.w3.org/2000/svg', 
        width: 24, 
        height: 24, 
        viewBox: '0 0 24 24', 
        fill: 'none', 
        stroke: 'currentColor', 
        'stroke-width': 2, 
        'stroke-linecap': 'round', 
        'stroke-linejoin': 'round'
      }, [
        h('path', { d: 'M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z' }),
        h('polyline', { points: '22,6 12,13 2,6' })
      ])
    }
  },
  zap: {
    render() {
      return h('svg', { 
        xmlns: 'http://www.w3.org/2000/svg', 
        width: 24, 
        height: 24, 
        viewBox: '0 0 24 24', 
        fill: 'none', 
        stroke: 'currentColor', 
        'stroke-width': 2, 
        'stroke-linecap': 'round', 
        'stroke-linejoin': 'round'
      }, [
        h('polygon', { points: '13 2 3 14 12 14 11 22 21 10 12 10 13 2' })
      ])
    }
  }
}

const iconComponent = computed(() => icons[props.icon] || icons.code)
</script>

<style scoped>
.feature-card {
  padding: 1.5rem;
  background-color: var(--bg-primary);
  border: 1px solid var(--border);
  border-radius: 12px;
  transition: border-color var(--transition-normal) ease,
              box-shadow var(--transition-normal) ease;
}

.feature-card.clickable {
  cursor: pointer;
}

.feature-card:hover {
  border-color: var(--cta);
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
}

.feature-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  background-color: var(--bg-tertiary);
  border-radius: 10px;
  margin-bottom: 1rem;
  color: var(--cta);
}

.feature-title {
  font-size: 1.125rem;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 0.5rem;
}

.feature-desc {
  font-size: 0.9375rem;
  color: var(--text-secondary);
  line-height: 1.6;
}
</style>
