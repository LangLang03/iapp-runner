<template>
  <header class="navbar">
    <div class="container navbar-content">
      <router-link to="/" class="logo">
        <span class="logo-text">YuWeb</span>
      </router-link>
      
      <nav class="nav-links" :class="{ active: menuOpen }">
        <router-link to="/" class="nav-link" @click="closeMenu">首页</router-link>
        <router-link to="/quickstart" class="nav-link" @click="closeMenu">快速开始</router-link>
        <router-link to="/api" class="nav-link" @click="closeMenu">API 文档</router-link>
        <router-link to="/config" class="nav-link" @click="closeMenu">配置指南</router-link>
        <router-link to="/examples" class="nav-link" @click="closeMenu">示例代码</router-link>
      </nav>
      
      <button class="menu-toggle" @click="toggleMenu" aria-label="Toggle menu">
        <span class="menu-icon" :class="{ active: menuOpen }"></span>
      </button>
    </div>
  </header>
</template>

<script setup>
import { ref } from 'vue'

const menuOpen = ref(false)

const toggleMenu = () => {
  menuOpen.value = !menuOpen.value
}

const closeMenu = () => {
  menuOpen.value = false
}
</script>

<style scoped>
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: var(--header-height);
  background-color: var(--bg-primary);
  border-bottom: 1px solid var(--border);
  z-index: 100;
}

.navbar-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
}

.logo {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: 700;
  font-size: 1.25rem;
  color: var(--text-primary);
}

.logo:hover {
  color: var(--primary);
}

.logo-text {
  letter-spacing: -0.5px;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.nav-link {
  padding: 0.5rem 1rem;
  font-size: 0.9375rem;
  font-weight: 500;
  color: var(--text-secondary);
  transition: color 0.2s ease;
  border-radius: 4px;
}

.nav-link:hover {
  color: var(--primary);
  background-color: var(--bg-secondary);
}

.nav-link.router-link-active {
  color: var(--primary);
  background-color: var(--bg-secondary);
}

.menu-toggle {
  display: none;
  padding: 0.5rem;
  background: none;
  border: none;
}

.menu-icon {
  display: block;
  width: 24px;
  height: 2px;
  background-color: var(--text-primary);
  position: relative;
  transition: background-color 0.2s ease;
}

.menu-icon::before,
.menu-icon::after {
  content: '';
  position: absolute;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: var(--text-primary);
  transition: transform 0.2s ease;
}

.menu-icon::before {
  top: -7px;
}

.menu-icon::after {
  top: 7px;
}

.menu-icon.active {
  background-color: transparent;
}

.menu-icon.active::before {
  transform: translateY(7px) rotate(45deg);
}

.menu-icon.active::after {
  transform: translateY(-7px) rotate(-45deg);
}

@media (max-width: 768px) {
  .menu-toggle {
    display: block;
  }
  
  .nav-links {
    position: fixed;
    top: var(--header-height);
    left: 0;
    right: 0;
    flex-direction: column;
    background-color: var(--bg-primary);
    border-bottom: 1px solid var(--border);
    padding: 1rem;
    gap: 0.25rem;
    transform: translateY(-100%);
    opacity: 0;
    visibility: hidden;
    transition: all 0.3s ease;
  }
  
  .nav-links.active {
    transform: translateY(0);
    opacity: 1;
    visibility: visible;
  }
  
  .nav-link {
    width: 100%;
    padding: 0.75rem 1rem;
  }
}
</style>
