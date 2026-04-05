<template>
  <div class="api-card" :id="id">
    <div class="api-header">
      <h3 class="api-name">{{ name }}()</h3>
      <span class="api-return" v-if="returnType">
        <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <polyline points="22 12 18 12 18 12 18"></polyline>
        </svg>
        {{ returnType }}
      </span>
    </div>
    <p class="api-desc">{{ description }}</p>
    
    <div class="api-params" v-if="params && params.length > 0">
      <h4>参数</h4>
      <div class="table-container">
        <table>
          <thead>
            <tr>
              <th>参数名</th>
              <th>类型</th>
              <th>必填</th>
              <th>说明</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="param in params" :key="param.name">
              <td><code>{{ param.name }}</code></td>
              <td>{{ param.type }}</td>
              <td>
                <span class="badge" :class="param.required ? 'required' : 'optional'">
                  {{ param.required ? '是' : '否' }}
                </span>
              </td>
              <td>{{ param.desc }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
    
    <div class="api-example" v-if="example">
      <h4>示例</h4>
      <CodeBlock :code="example" language="iapp" />
    </div>
  </div>
</template>

<script setup>
import CodeBlock from './CodeBlock.vue'

defineProps({
  id: {
    type: String,
    default: ''
  },
  name: {
    type: String,
    required: true
  },
  description: {
    type: String,
    default: ''
  },
  returnType: {
    type: String,
    default: ''
  },
  params: {
    type: Array,
    default: () => []
  },
  example: {
    type: String,
    default: ''
  }
})
</script>

<style scoped>
.api-card {
  padding: 1.5rem 0;
  border-bottom: 1px solid var(--border);
}

.api-card:last-child {
  border-bottom: none;
}

.api-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 0.75rem;
}

.api-name {
  font-size: 1.125rem;
  font-weight: 600;
  color: var(--cta);
  font-family: var(--font-mono);
}

.api-return {
  display: inline-flex;
  align-items: center;
  gap: 0.375rem;
  font-size: 0.8125rem;
  color: var(--text-muted);
  background-color: var(--bg-tertiary);
  padding: 0.25rem 0.625rem;
  border-radius: 4px;
}

.api-desc {
  color: var(--text-secondary);
  margin-bottom: 1.25rem;
  line-height: 1.6;
}

.api-params h4,
.api-example h4 {
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 0.75rem;
}

.api-params {
  margin-bottom: 1.25rem;
}

.api-example {
  margin-top: 1rem;
}

.badge {
  display: inline-block;
  padding: 0.125rem 0.5rem;
  font-size: 0.75rem;
  font-weight: 500;
  border-radius: 4px;
}

.badge.required {
  background-color: rgba(34, 197, 94, 0.15);
  color: var(--success);
}

.badge.optional {
  background-color: var(--bg-tertiary);
  color: var(--text-muted);
}
</style>
