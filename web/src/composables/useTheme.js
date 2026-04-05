import { ref, watch, onMounted } from 'vue'

const THEME_KEY = 'yuweb-theme'

const isDark = ref(false)

function getSystemTheme() {
  if (typeof window !== 'undefined' && window.matchMedia) {
    return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light'
  }
  return 'light'
}

function getStoredTheme() {
  if (typeof localStorage !== 'undefined') {
    return localStorage.getItem(THEME_KEY)
  }
  return null
}

function applyTheme(dark) {
  if (typeof document !== 'undefined') {
    document.documentElement.setAttribute('data-theme', dark ? 'dark' : 'light')
  }
}

export function useTheme() {
  const initTheme = () => {
    const stored = getStoredTheme()
    if (stored) {
      isDark.value = stored === 'dark'
    } else {
      isDark.value = getSystemTheme() === 'dark'
    }
    applyTheme(isDark.value)
  }

  const toggleTheme = () => {
    isDark.value = !isDark.value
  }

  const setTheme = (dark) => {
    isDark.value = dark
  }

  watch(isDark, (newValue) => {
    applyTheme(newValue)
    if (typeof localStorage !== 'undefined') {
      localStorage.setItem(THEME_KEY, newValue ? 'dark' : 'light')
    }
  })

  onMounted(() => {
    initTheme()
    
    if (typeof window !== 'undefined' && window.matchMedia) {
      const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
      const handleChange = (e) => {
        if (!getStoredTheme()) {
          isDark.value = e.matches
        }
      }
      mediaQuery.addEventListener('change', handleChange)
    }
  })

  return {
    isDark,
    toggleTheme,
    setTheme,
    initTheme
  }
}
