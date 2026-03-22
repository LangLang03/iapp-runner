<template>
  <div class="code-block">
    <div class="code-header" v-if="title || language">
      <span class="code-title" v-if="title">{{ title }}</span>
      <span class="code-language" v-if="language">{{ language }}</span>
    </div>
    <pre><code>{{ code }}</code></pre>
  </div>
</template>

<script setup>
defineProps({
  code: {
    type: String,
    required: true
  },
  title: {
    type: String,
    default: ''
  },
  language: {
    type: String,
    default: ''
  }
})
</script>

<style scoped>
.code-block {
  margin: 1rem 0;
  border-radius: 6px;
  overflow: hidden;
}

.code-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 1rem;
  background-color: #0f172a;
  border-bottom: 1px solid #334155;
}

.code-title {
  font-size: 0.8125rem;
  color: #94a3b8;
}

.code-language {
  font-size: 0.75rem;
  color: #64748b;
  text-transform: uppercase;
}

.code-block pre {
  margin: 0;
  border-radius: 0;
}
</style>
