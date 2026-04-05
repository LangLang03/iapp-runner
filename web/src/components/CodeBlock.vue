<template>
  <div class="code-block">
    <div class="code-header" v-if="title || language">
      <div class="code-header-left">
        <span class="code-title" v-if="title">{{ title }}</span>
        <span class="code-language" v-if="language">{{ language }}</span>
      </div>
      <button 
        class="copy-btn" 
        @click="copyCode" 
        :title="copySuccess ? '已复制' : '复制代码'"
        :aria-label="copySuccess ? '已复制' : '复制代码'"
      >
        <svg v-if="!copySuccess" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <rect x="9" y="9" width="13" height="13" rx="2" ry="2"></rect>
          <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path>
        </svg>
        <svg v-else xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <polyline points="20 6 9 17 4 12"></polyline>
        </svg>
        <span class="copy-text">{{ copySuccess ? '已复制' : '复制' }}</span>
      </button>
    </div>
    <div class="code-wrapper">
      <pre><code ref="codeRef" :class="languageClass">{{ code }}</code></pre>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import hljs from 'highlight.js/lib/core'
import iappHighlight from '../utils/iapp-highlight.js'

hljs.registerLanguage('iapp', iappHighlight)

const props = defineProps({
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

const codeRef = ref(null)
const copySuccess = ref(false)

const languageClass = computed(() => {
  if (props.language === 'iapp') {
    return 'language-iapp hljs'
  }
  return props.language ? `language-${props.language} hljs` : 'hljs'
})

const highlightCode = () => {
  if (codeRef.value) {
    if (props.language === 'iapp') {
      hljs.highlightElement(codeRef.value)
    } else if (props.language) {
      hljs.highlightElement(codeRef.value)
    }
  }
}

const copyCode = async () => {
  try {
    await navigator.clipboard.writeText(props.code)
    copySuccess.value = true
    setTimeout(() => {
      copySuccess.value = false
    }, 2000)
  } catch (err) {
    console.error('复制失败:', err)
  }
}

onMounted(() => {
  highlightCode()
})

watch(() => props.code, () => {
  setTimeout(highlightCode, 0)
})
</script>

<style scoped>
.code-block {
  margin: 1rem 0;
  border-radius: 12px;
  overflow: hidden;
  background-color: var(--code-bg);
  border: 1px solid var(--border);
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
}

.code-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem 1rem;
  background-color: var(--code-header-bg);
  border-bottom: 1px solid var(--border);
}

.code-header-left {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.code-title {
  font-size: 0.8125rem;
  color: var(--code-text);
  font-weight: 500;
  opacity: 0.9;
}

.code-language {
  font-size: 0.6875rem;
  color: var(--code-text);
  text-transform: uppercase;
  background-color: var(--cta);
  padding: 0.125rem 0.5rem;
  border-radius: 4px;
  font-weight: 600;
  opacity: 0.9;
}

.copy-btn {
  display: flex;
  align-items: center;
  gap: 0.375rem;
  padding: 0.375rem 0.75rem;
  background-color: transparent;
  border: 1px solid var(--border);
  border-radius: 6px;
  color: var(--code-text);
  font-size: 0.75rem;
  cursor: pointer;
  transition: all var(--transition-fast) ease;
  opacity: 0.7;
}

.copy-btn:hover {
  background-color: var(--bg-tertiary);
  opacity: 1;
}

.copy-btn svg {
  flex-shrink: 0;
}

.copy-text {
  font-family: var(--font-sans);
}

.code-wrapper {
  overflow-x: auto;
}

.code-block pre {
  margin: 0;
  padding: 1rem 1.25rem;
  background-color: var(--code-bg);
  border-radius: 0;
  min-height: 60px;
  font-size: 0.875rem;
  line-height: 1.7;
}

.code-block pre code {
  background: none;
  padding: 0;
  color: var(--code-text);
  font-family: var(--font-mono);
}

.code-block pre code.hljs {
  background: none;
}

.code-block :deep(.hljs-comment) {
  color: #5c6370;
  font-style: italic;
}

.code-block :deep(.hljs-keyword) {
  color: #c678dd;
}

.code-block :deep(.hljs-literal) {
  color: #56b6c2;
}

.code-block :deep(.hljs-string) {
  color: #98c379;
}

.code-block :deep(.hljs-number) {
  color: #d19a66;
}

.code-block :deep(.hljs-built_in) {
  color: #61afef;
}

.code-block :deep(.hljs-variable) {
  color: #e06c75;
}

.code-block :deep(.hljs-function) {
  color: #61afef;
}
</style>
