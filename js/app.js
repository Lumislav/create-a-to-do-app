// Mildy by PALIMSON Group - Master Application Coordinator
// Clean Architecture, Reactive Navigation, Project Persistence & Theme/Localization Engine

class MildyAppManager {
  constructor() {
    this.currentScreen = 'home';
    this.projects = JSON.parse(localStorage.getItem('mildy_projects') || '[]');
    this.theme = localStorage.getItem('mildy_theme') || 'dark';
  }

  init() {
    this.applyTheme(this.theme);
    initI18n();
    this.setupNavigation();
    this.setupSettings();
    this.renderRecentProjects();
    this.renderProjectsScreen();

    // Default sample project if empty
    if (this.projects.length === 0) {
      this.createDemoProject();
    }
  }

  createDemoProject() {
    this.projects.push({
      id: 'proj_demo_1',
      name: 'TikTok Viral Short',
      tool: 'subtitles',
      date: new Date().toLocaleDateString(),
      preview: 'assets/logo.svg'
    });
    this.projects.push({
      id: 'proj_demo_2',
      name: 'YouTube Cover #1',
      tool: 'thumbnail',
      date: new Date().toLocaleDateString(),
      preview: 'assets/logo.svg'
    });
    localStorage.setItem('mildy_projects', JSON.stringify(this.projects));
    this.renderRecentProjects();
    this.renderProjectsScreen();
  }

  setupNavigation() {
    // Bottom Nav Tabs
    document.querySelectorAll('.nav-item').forEach(item => {
      item.addEventListener('click', () => {
        const target = item.getAttribute('data-screen');
        if (target) {
          this.navigateTo(target);
        }
      });
    });

    // Quick Tool Cards on Home Screen
    document.querySelectorAll('[data-open-tool]').forEach(card => {
      card.addEventListener('click', () => {
        const tool = card.getAttribute('data-open-tool');
        this.navigateTo(tool);
      });
    });

    // Back Buttons
    document.querySelectorAll('[data-action="back"]').forEach(btn => {
      btn.addEventListener('click', () => {
        this.navigateTo('home');
      });
    });

    // Brand click returns to home
    const brand = document.querySelector('.brand-wrapper');
    if (brand) {
      brand.addEventListener('click', () => this.navigateTo('home'));
    }

    // New Project Button
    const newProjBtn = document.getElementById('btnNewProject');
    if (newProjBtn) {
      newProjBtn.addEventListener('click', () => {
        this.navigateTo('tools');
      });
    }

    // Open Settings Button & Badge
    document.querySelectorAll('[data-action="open-settings"]').forEach(btn => {
      btn.addEventListener('click', () => {
        this.navigateTo('settings');
      });
    });

    const backendBadge = document.getElementById('backendStatusBadge');
    if (backendBadge) {
      backendBadge.style.cursor = 'pointer';
      backendBadge.addEventListener('click', () => {
        this.navigateTo('settings');
      });
    }
  }

  navigateTo(screenId) {
    document.querySelectorAll('.screen').forEach(s => s.classList.remove('active'));
    document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));

    const targetEl = document.getElementById(`screen-${screenId}`);
    if (targetEl) {
      targetEl.classList.add('active');
      this.currentScreen = screenId;
      window.scrollTo(0, 0);

      // Highlight matching bottom nav item if it's one of the 4 tabs
      const navItem = document.querySelector(`.nav-item[data-screen="${screenId}"]`);
      if (navItem) {
        navItem.classList.add('active');
      }

      // Initialize targeted studio on screen load
      if (screenId === 'subtitles' && window.subtitlesStudio) {
        window.subtitlesStudio.init();
      } else if (screenId === 'photofix' && window.photoFixStudio) {
        window.photoFixStudio.init();
      } else if (screenId === 'videotools' && window.videoToolsStudio) {
        window.videoToolsStudio.init();
      } else if (screenId === 'thumbnail' && window.thumbnailStudio) {
        window.thumbnailStudio.init();
      } else if (screenId === 'utility' && window.mildyUtilityTools) {
        window.mildyUtilityTools.init();
      } else if (screenId === 'projects') {
        this.renderProjectsScreen();
      }
    }
  }

  setupSettings() {
    // Theme Select
    const themeSelect = document.getElementById('settingThemeSelect');
    if (themeSelect) {
      themeSelect.value = this.theme;
      themeSelect.addEventListener('change', (e) => {
        this.applyTheme(e.target.value);
      });
    }

    // Language Select
    const langSelect = document.getElementById('settingLangSelect');
    if (langSelect) {
      langSelect.value = currentLang;
      langSelect.addEventListener('change', (e) => {
        setLanguage(e.target.value);
        this.showToast(t('success'));
      });
    }

    // AI Engine Mode & Backend URL
    const aiModeSelect = document.getElementById('settingAiModeSelect');
    const backendInput = document.getElementById('settingBackendUrl');
    const btnTestConn = document.getElementById('btnTestBackend');

    if (aiModeSelect) {
      aiModeSelect.value = window.mildyAi.mode;
      aiModeSelect.addEventListener('change', (e) => {
        window.mildyAi.setMode(e.target.value);
      });
    }

    if (backendInput) {
      backendInput.value = window.mildyAi.serverUrl;
      backendInput.addEventListener('change', (e) => {
        window.mildyAi.setServerUrl(e.target.value);
      });
    }

    if (btnTestConn) {
      btnTestConn.addEventListener('click', async () => {
        this.showToast(t('processing'));
        const online = await window.mildyAi.checkServerHealth();
        if (online) {
          this.showToast(t('serverStatusOnline'));
        } else {
          this.showToast(t('serverStatusOffline'));
        }
      });
    }
  }

  applyTheme(theme) {
    this.theme = theme;
    localStorage.setItem('mildy_theme', theme);
    document.documentElement.setAttribute('data-theme', theme);
  }

  showToast(message) {
    const toast = document.getElementById('toast');
    if (!toast) return;
    toast.innerHTML = `<i class="fa-solid fa-circle-check" style="color:var(--accent-bright);"></i> <span>${message}</span>`;
    toast.classList.add('show');
    clearTimeout(this.toastTimer);
    this.toastTimer = setTimeout(() => {
      toast.classList.remove('show');
    }, 2400);
  }

  // Save Project
  saveProject(name, toolType, previewDataUrl) {
    const newProj = {
      id: `proj_${Date.now()}`,
      name: name || `Проект ${this.projects.length + 1}`,
      tool: toolType,
      date: new Date().toLocaleDateString(),
      preview: previewDataUrl || 'assets/logo.svg'
    };
    this.projects.unshift(newProj);
    localStorage.setItem('mildy_projects', JSON.stringify(this.projects));
    this.renderRecentProjects();
    this.showToast(t('success'));
  }

  deleteProject(id) {
    this.projects = this.projects.filter(p => p.id !== id);
    localStorage.setItem('mildy_projects', JSON.stringify(this.projects));
    this.renderRecentProjects();
    this.renderProjectsScreen();
    this.showToast(t('delete'));
  }

  renderRecentProjects() {
    const container = document.getElementById('recentProjectsContainer');
    if (!container) return;
    container.innerHTML = '';

    if (this.projects.length === 0) {
      container.innerHTML = `<div class="empty-state">${t('noRecentProjects')}</div>`;
      return;
    }

    this.projects.slice(0, 5).forEach(p => {
      const el = document.createElement('div');
      el.className = 'recent-item';
      el.innerHTML = `
        <div class="recent-thumb">
          <i class="fa-solid fa-${this.getToolIcon(p.tool)}"></i>
        </div>
        <div class="recent-details">
          <div class="recent-name">${p.name}</div>
          <div class="recent-date">${p.date}</div>
        </div>
      `;
      el.addEventListener('click', () => {
        this.navigateTo(p.tool);
      });
      container.appendChild(el);
    });
  }

  renderProjectsScreen() {
    const list = document.getElementById('fullProjectsList');
    if (!list) return;
    list.innerHTML = '';

    if (this.projects.length === 0) {
      list.innerHTML = `<div class="empty-state">${t('noRecentProjects')}</div>`;
      return;
    }

    this.projects.forEach(p => {
      const card = document.createElement('div');
      card.className = 'tool-row-card';
      card.innerHTML = `
        <div class="tool-row-icon icon-photo">
          <i class="fa-solid fa-${this.getToolIcon(p.tool)}"></i>
        </div>
        <div class="tool-row-content">
          <h3>${p.name}</h3>
          <p>${p.tool.toUpperCase()} • ${p.date}</p>
        </div>
        <button class="icon-btn" style="width:32px;height:32px;color:#FF4757;">
          <i class="fa-solid fa-trash"></i>
        </button>
      `;
      card.addEventListener('click', (e) => {
        if (!e.target.closest('button')) {
          this.navigateTo(p.tool);
        }
      });
      card.querySelector('button').addEventListener('click', (e) => {
        e.stopPropagation();
        this.deleteProject(p.id);
      });
      list.appendChild(card);
    });
  }

  getToolIcon(tool) {
    if (tool === 'subtitles') return 'closed-captioning';
    if (tool === 'photofix') return 'wand-magic-sparkles';
    if (tool === 'videotools') return 'video';
    if (tool === 'thumbnail') return 'image';
    return 'layer-group';
  }
}

window.MildyApp = new MildyAppManager();

document.addEventListener('DOMContentLoaded', () => {
  window.MildyApp.init();
});
