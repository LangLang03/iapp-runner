<template>
  <footer class="footer">
    <div class="container footer-content">
      <div class="footer-info">
        <div class="footer-brand">
          <svg class="footer-logo" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M2 17L12 22L22 17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M2 12L12 17L22 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          YuWeb
        </div>
        <p class="footer-desc">基于裕语言(IApp)的轻量级 Web 后端框架</p>
      </div>
      <div class="footer-links">
        <div class="footer-section">
          <h4>文档</h4>
          <router-link to="/quickstart">快速开始</router-link>
          <router-link to="/api">API 文档</router-link>
          <router-link to="/config">配置指南</router-link>
        </div>
        <div class="footer-section">
          <h4>资源</h4>
          <router-link to="/examples">示例代码</router-link>
        </div>
        <div class="footer-section">
          <h4>社区</h4>
          <a href="https://qm.qq.com/q/xxx" target="_blank" rel="noopener">
            <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6"></path>
              <polyline points="15 3 21 3 21 9"></polyline>
              <line x1="10" y1="14" x2="21" y2="3"></line>
            </svg>
            QQ群：1067354882
          </a>
        </div>
      </div>
    </div>
    <div class="footer-bottom">
      <div class="container">
        <p>YuWeb v1.0.0 - 轻量级 Web 后端框架</p>
      </div>
    </div>
  </footer>
</template>

<style scoped>
.footer {
  background-color: var(--bg-secondary);
  border-top: 1px solid var(--border);
  margin-top: var(--section-gap);
}

.footer-content {
  display: flex;
  justify-content: space-between;
  padding: 3rem var(--content-padding);
  gap: 2rem;
}

.footer-brand {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 0.5rem;
}

.footer-logo {
  width: 24px;
  height: 24px;
  color: var(--cta);
}

.footer-desc {
  color: var(--text-secondary);
  font-size: 0.875rem;
}

.footer-links {
  display: flex;
  gap: 4rem;
}

.footer-section h4 {
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 1rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.footer-section a {
  display: flex;
  align-items: center;
  gap: 0.375rem;
  color: var(--text-secondary);
  font-size: 0.875rem;
  margin-bottom: 0.5rem;
  transition: color var(--transition-fast) ease;
  cursor: pointer;
}

.footer-section a:hover {
  color: var(--cta);
}

.footer-bottom {
  border-top: 1px solid var(--border);
  padding: 1.5rem var(--content-padding);
}

.footer-bottom p {
  text-align: center;
  font-size: 0.8125rem;
  color: var(--text-muted);
}

@media (max-width: 768px) {
  .footer-content {
    flex-direction: column;
    gap: 2rem;
  }
  
  .footer-links {
    gap: 2rem;
    flex-wrap: wrap;
  }
}
</style>
