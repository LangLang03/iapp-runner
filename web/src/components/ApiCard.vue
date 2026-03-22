<template>
  <div class="api-card" :id="id">
    <div class="api-header">
      <h3 class="api-name">{{ name }}()</h3>
      <span class="api-return" v-if="returnType">返回: {{ returnType }}</span>
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
              <td>{{ param.required ? '是' : '否' }}</td>
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
  align-items: baseline;
  gap: 1rem;
  margin-bottom: 0.5rem;
}

.api-name {
  font-size: 1.125rem;
  font-weight: 600;
  color: var(--primary);
  font-family: var(--font-mono);
}

.api-return {
  font-size: 0.8125rem;
  color: var(--text-muted);
}

.api-desc {
  color: var(--text-secondary);
  margin-bottom: 1rem;
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
  margin-bottom: 1rem;
}

.api-example {
  margin-top: 1rem;
}
</style>
