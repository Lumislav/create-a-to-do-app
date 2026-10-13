// Thumbnail Studio Module for Mildy by PALIMSON Group
// Multi-layer canvas editor, touch/drag/scale gestures, pro typography, BG removal & templates

class ThumbnailStudio {
  constructor() {
    this.canvas = null;
    this.ctx = null;
    this.layers = [];
    this.selectedLayerIndex = -1;
    this.canvasPreset = 'yt'; // 'yt' (1280x720), 'shorts' (720x1280), 'square' (1080x1080)
    
    // Drag / Transform State
    this.isDragging = false;
    this.dragStartX = 0;
    this.dragStartY = 0;
    this.initialLayerX = 0;
    this.initialLayerY = 0;
  }

  init() {
    this.canvas = document.getElementById('thumbCanvas');
    if (!this.canvas) return;
    this.ctx = this.canvas.getContext('2d');
    this.setupListeners();
    this.setPreset('yt');
    this.loadTemplate('gaming');
  }

  setupListeners() {
    // Preset change
    const presetSelect = document.getElementById('thumbPresetSelect');
    if (presetSelect) {
      presetSelect.addEventListener('change', (e) => {
        this.setPreset(e.target.value);
      });
    }

    // Touch / Pointer interactions for drag & drop on canvas
    this.canvas.addEventListener('pointerdown', (e) => this.onPointerDown(e));
    window.addEventListener('pointermove', (e) => this.onPointerMove(e));
    window.addEventListener('pointerup', () => this.onPointerUp());

    // Layer action buttons
    const btnAddText = document.getElementById('btnAddTextLayer');
    if (btnAddText) btnAddText.addEventListener('click', () => this.addTextLayer());

    const btnAddImg = document.getElementById('btnAddImageLayer');
    const imgInput = document.getElementById('thumbImageLayerInput');
    if (btnAddImg && imgInput) {
      btnAddImg.addEventListener('click', () => imgInput.click());
      imgInput.addEventListener('change', (e) => {
        const file = e.target.files[0];
        if (file) {
          const reader = new FileReader();
          reader.onload = (event) => {
            const img = new Image();
            img.onload = () => this.addImageLayer(img, file.name);
            img.src = event.target.result;
          };
          reader.readAsDataURL(file);
        }
      });
    }

    const btnAddBadge = document.getElementById('btnAddBadgeLayer');
    if (btnAddBadge) btnAddBadge.addEventListener('click', () => this.addBadgeLayer());

    // Template selector
    const tmplSelect = document.getElementById('thumbTemplateSelect');
    if (tmplSelect) {
      tmplSelect.addEventListener('change', (e) => {
        this.loadTemplate(e.target.value);
      });
    }

    // Text controls
    const textValInput = document.getElementById('thumbSelectedText');
    if (textValInput) {
      textValInput.addEventListener('input', (e) => {
        const l = this.getSelectedLayer();
        if (l && l.type === 'text') {
          l.text = e.target.value;
          this.render();
        }
      });
    }

    const textSizeSlider = document.getElementById('thumbTextSizeSlider');
    if (textSizeSlider) {
      textSizeSlider.addEventListener('input', (e) => {
        const l = this.getSelectedLayer();
        if (l) {
          if (l.type === 'text') {
            l.fontSize = parseInt(e.target.value, 10);
          } else if (l.type === 'image' || l.type === 'badge') {
            l.scale = parseFloat(e.target.value) / 40;
          }
          this.render();
        }
      });
    }

    const colorPicker = document.getElementById('thumbTextColorInput');
    if (colorPicker) {
      colorPicker.addEventListener('input', (e) => {
        const l = this.getSelectedLayer();
        if (l && l.type === 'text') {
          l.color = e.target.value;
          this.render();
        }
      });
    }
  }

  setPreset(preset) {
    this.canvasPreset = preset;
    if (preset === 'yt') {
      this.canvas.width = 1280;
      this.canvas.height = 720;
    } else if (preset === 'shorts') {
      this.canvas.width = 720;
      this.canvas.height = 1280;
    } else {
      this.canvas.width = 1080;
      this.canvas.height = 1080;
    }
    this.render();
  }

  getSelectedLayer() {
    return this.layers[this.selectedLayerIndex] || null;
  }

  // Pointer event handlers for moving elements
  onPointerDown(e) {
    const rect = this.canvas.getBoundingClientRect();
    const scaleX = this.canvas.width / rect.width;
    const scaleY = this.canvas.height / rect.height;
    const x = (e.clientX - rect.left) * scaleX;
    const y = (e.clientY - rect.top) * scaleY;

    // Hit test backwards (top layer first)
    for (let i = this.layers.length - 1; i >= 0; i--) {
      const l = this.layers[i];
      if (this.isPointInsideLayer(l, x, y)) {
        this.selectedLayerIndex = i;
        this.isDragging = true;
        this.dragStartX = x;
        this.dragStartY = y;
        this.initialLayerX = l.x;
        this.initialLayerY = l.y;
        this.updateLayerPanel();
        this.render();
        return;
      }
    }
  }

  onPointerMove(e) {
    if (!this.isDragging || this.selectedLayerIndex === -1) return;
    const rect = this.canvas.getBoundingClientRect();
    const scaleX = this.canvas.width / rect.width;
    const scaleY = this.canvas.height / rect.height;
    const x = (e.clientX - rect.left) * scaleX;
    const y = (e.clientY - rect.top) * scaleY;

    const dx = x - this.dragStartX;
    const dy = y - this.dragStartY;

    const l = this.layers[this.selectedLayerIndex];
    if (l) {
      l.x = this.initialLayerX + dx;
      l.y = this.initialLayerY + dy;
      this.render();
    }
  }

  onPointerUp() {
    this.isDragging = false;
  }

  isPointInsideLayer(l, x, y) {
    if (l.type === 'text') {
      const w = l.text.length * (l.fontSize * 0.6);
      const h = l.fontSize * 1.2;
      return x >= l.x - w / 2 && x <= l.x + w / 2 && y >= l.y - h / 2 && y <= l.y + h / 2;
    } else if (l.type === 'image') {
      const w = (l.img.width || 200) * (l.scale || 1);
      const h = (l.img.height || 200) * (l.scale || 1);
      return x >= l.x - w / 2 && x <= l.x + w / 2 && y >= l.y - h / 2 && y <= l.y + h / 2;
    } else if (l.type === 'badge') {
      return x >= l.x - 140 && x <= l.x + 140 && y >= l.y - 45 && y <= l.y + 45;
    }
    return false;
  }

  addTextLayer(initialText = "ЗАГОЛОВОК ОБЛОЖКИ") {
    this.layers.push({
      id: Date.now(),
      type: 'text',
      text: initialText,
      x: this.canvas.width / 2,
      y: this.canvas.height / 2,
      fontSize: 68,
      color: '#FFFFFF',
      strokeColor: '#000000',
      strokeWidth: 8,
      shadowColor: 'rgba(0, 0, 0, 0.9)',
      shadowBlur: 14,
      rotation: 0
    });
    this.selectedLayerIndex = this.layers.length - 1;
    this.updateLayerPanel();
    this.render();
  }

  addImageLayer(img, name = 'Image') {
    this.layers.push({
      id: Date.now(),
      type: 'image',
      name: name,
      img: img,
      x: this.canvas.width * 0.7,
      y: this.canvas.height * 0.6,
      scale: 0.8,
      rotation: 0
    });
    this.selectedLayerIndex = this.layers.length - 1;
    this.updateLayerPanel();
    this.render();
    window.MildyApp.showToast(`${t('addLayerImage')}: ${name}`);
  }

  addBadgeLayer() {
    this.layers.push({
      id: Date.now(),
      type: 'badge',
      text: "TOP #1",
      x: this.canvas.width * 0.25,
      y: this.canvas.height * 0.25,
      color: '#FF2A54',
      scale: 1,
      rotation: -8
    });
    this.selectedLayerIndex = this.layers.length - 1;
    this.updateLayerPanel();
    this.render();
  }

  // Remove background of the currently selected image layer
  async removeSelectedLayerBg() {
    const l = this.getSelectedLayer();
    if (!l || l.type !== 'image') {
      window.MildyApp.showToast('Выберите слой с изображением');
      return;
    }
    window.MildyApp.showToast(t('processing'));
    try {
      const cutOut = await window.mildyAi.removeBackground(l.img);
      l.img = cutOut;
      this.render();
      window.MildyApp.showToast(t('success'));
    } catch(e) {
      console.error(e);
      window.MildyApp.showToast('Ошибка удаления фона слоя');
    }
  }

  // Layer ordering
  bringForward() {
    if (this.selectedLayerIndex < this.layers.length - 1) {
      const temp = this.layers[this.selectedLayerIndex];
      this.layers[this.selectedLayerIndex] = this.layers[this.selectedLayerIndex + 1];
      this.layers[this.selectedLayerIndex + 1] = temp;
      this.selectedLayerIndex++;
      this.updateLayerPanel();
      this.render();
    }
  }

  sendBackward() {
    if (this.selectedLayerIndex > 0) {
      const temp = this.layers[this.selectedLayerIndex];
      this.layers[this.selectedLayerIndex] = this.layers[this.selectedLayerIndex - 1];
      this.layers[this.selectedLayerIndex - 1] = temp;
      this.selectedLayerIndex--;
      this.updateLayerPanel();
      this.render();
    }
  }

  duplicateSelected() {
    const l = this.getSelectedLayer();
    if (!l) return;
    const copy = JSON.parse(JSON.stringify(l));
    copy.id = Date.now();
    copy.x += 30;
    copy.y += 30;
    if (l.type === 'image') copy.img = l.img; // keep image ref
    this.layers.push(copy);
    this.selectedLayerIndex = this.layers.length - 1;
    this.updateLayerPanel();
    this.render();
  }

  deleteSelected() {
    if (this.selectedLayerIndex >= 0) {
      this.layers.splice(this.selectedLayerIndex, 1);
      this.selectedLayerIndex = Math.max(0, this.layers.length - 1);
      this.updateLayerPanel();
      this.render();
    }
  }

  updateLayerPanel() {
    const listEl = document.getElementById('thumbLayersList');
    if (!listEl) return;
    listEl.innerHTML = '';

    this.layers.forEach((l, idx) => {
      const item = document.createElement('div');
      item.className = `layer-item ${idx === this.selectedLayerIndex ? 'active' : ''}`;
      const title = l.type === 'text' ? `Текст: "${l.text.substring(0, 14)}"` : (l.type === 'image' ? (l.name || 'Изображение') : `Бейдж: ${l.text}`);
      item.innerHTML = `
        <span><i class="fa-solid fa-layer-group"></i> ${title}</span>
        <button class="icon-btn" style="width:24px;height:24px;font-size:10px;"><i class="fa-solid fa-trash"></i></button>
      `;
      item.addEventListener('click', (e) => {
        if (!e.target.closest('button')) {
          this.selectedLayerIndex = idx;
          this.updateLayerPanel();
          this.render();
        }
      });
      item.querySelector('button').addEventListener('click', (e) => {
        e.stopPropagation();
        this.selectedLayerIndex = idx;
        this.deleteSelected();
      });
      listEl.appendChild(item);
    });

    const l = this.getSelectedLayer();
    const textValInput = document.getElementById('thumbSelectedText');
    const textSizeSlider = document.getElementById('thumbTextSizeSlider');
    if (l && l.type === 'text') {
      if (textValInput) textValInput.value = l.text;
      if (textSizeSlider) textSizeSlider.value = l.fontSize;
    }
  }

  // Ready Templates
  loadTemplate(name) {
    this.layers = [];
    const w = this.canvas.width;
    const h = this.canvas.height;

    if (name === 'gaming') {
      this.bgType = 'gaming';
      this.addTextLayer("СЕКРЕТНЫЙ БОСС!");
      const t1 = this.layers[0];
      t1.x = w * 0.45;
      t1.y = h * 0.35;
      t1.fontSize = 84;
      t1.color = '#FFE600';
      t1.strokeColor = '#000000';
      t1.strokeWidth = 10;

      this.addBadgeLayer();
      const b1 = this.layers[1];
      b1.text = "1000 FPS";
      b1.x = w * 0.2;
      b1.y = h * 0.2;

      this.addTextLayer("КАК ПОБЕДИТЬ?");
      const t2 = this.layers[2];
      t2.x = w * 0.45;
      t2.y = h * 0.6;
      t2.fontSize = 72;
      t2.color = '#FFFFFF';
    } else if (name === 'tech') {
      this.bgType = 'tech';
      this.addTextLayer("ОБЗОР ГОДА!");
      const t1 = this.layers[0];
      t1.x = w * 0.5;
      t1.y = h * 0.4;
      t1.fontSize = 78;
      t1.color = '#00F0FF';

      this.addBadgeLayer();
      const b1 = this.layers[1];
      b1.text = "NEVER BUY";
      b1.x = w * 0.8;
      b1.y = h * 0.25;
      b1.color = '#E50914';
    } else {
      this.bgType = 'vlog';
      this.addTextLayer("Я НЕ ОЖИДАЛ...");
      const t1 = this.layers[0];
      t1.x = w * 0.5;
      t1.y = h * 0.5;
      t1.fontSize = 82;
      t1.color = '#FF2A54';
    }

    this.selectedLayerIndex = 0;
    this.updateLayerPanel();
    this.render();
  }

  // Main Render Loop
  render() {
    if (!this.canvas || !this.ctx) return;
    const w = this.canvas.width;
    const h = this.canvas.height;

    // Background drawing
    const grad = this.ctx.createLinearGradient(0, 0, w, h);
    if (this.bgType === 'gaming') {
      grad.addColorStop(0, '#0F0314');
      grad.addColorStop(0.5, '#2D0A24');
      grad.addColorStop(1, '#800020');
    } else if (this.bgType === 'tech') {
      grad.addColorStop(0, '#080C14');
      grad.addColorStop(0.6, '#141E30');
      grad.addColorStop(1, '#243B55');
    } else {
      grad.addColorStop(0, '#12050B');
      grad.addColorStop(0.5, '#400A1C');
      grad.addColorStop(1, '#E50914');
    }
    this.ctx.fillStyle = grad;
    this.ctx.fillRect(0, 0, w, h);

    // Accent Vignette
    const rad = this.ctx.createRadialGradient(w / 2, h / 2, 100, w / 2, h / 2, Math.max(w, h) * 0.7);
    rad.addColorStop(0, 'rgba(255, 42, 84, 0.15)');
    rad.addColorStop(1, 'rgba(0, 0, 0, 0.7)');
    this.ctx.fillStyle = rad;
    this.ctx.fillRect(0, 0, w, h);

    // Draw Layers
    this.layers.forEach((l, idx) => {
      this.ctx.save();
      this.ctx.translate(l.x, l.y);
      if (l.rotation) this.ctx.rotate((l.rotation * Math.PI) / 180);

      if (l.type === 'text') {
        this.ctx.textAlign = 'center';
        this.ctx.textBaseline = 'middle';
        this.ctx.font = `900 ${l.fontSize}px -apple-system, Impact, Montserrat, sans-serif`;

        // Shadow
        this.ctx.shadowColor = l.shadowColor || 'rgba(0,0,0,0.9)';
        this.ctx.shadowBlur = l.shadowBlur || 14;
        this.ctx.shadowOffsetX = 4;
        this.ctx.shadowOffsetY = 6;

        // Stroke
        if (l.strokeWidth) {
          this.ctx.strokeStyle = l.strokeColor || '#000000';
          this.ctx.lineWidth = l.strokeWidth;
          this.ctx.strokeText(l.text, 0, 0);
        }

        // Fill
        this.ctx.fillStyle = l.color || '#FFFFFF';
        this.ctx.fillText(l.text, 0, 0);

      } else if (l.type === 'image') {
        const imgW = (l.img.width || 200) * (l.scale || 1);
        const imgH = (l.img.height || 200) * (l.scale || 1);
        this.ctx.drawImage(l.img, -imgW / 2, -imgH / 2, imgW, imgH);

      } else if (l.type === 'badge') {
        // High-energy badge
        this.ctx.fillStyle = l.color || '#FF2A54';
        this.ctx.beginPath();
        this.ctx.roundRect(-120, -35, 240, 70, 16);
        this.ctx.fill();

        this.ctx.strokeStyle = '#FFFFFF';
        this.ctx.lineWidth = 4;
        this.ctx.stroke();

        this.ctx.textAlign = 'center';
        this.ctx.textBaseline = 'middle';
        this.ctx.fillStyle = '#FFFFFF';
        this.ctx.font = '900 32px -apple-system, sans-serif';
        this.ctx.fillText(l.text, 0, 0);
      }

      // Highlight bounding box for selected layer
      if (idx === this.selectedLayerIndex) {
        this.ctx.shadowColor = 'transparent';
        this.ctx.strokeStyle = 'rgba(255, 42, 84, 0.8)';
        this.ctx.lineWidth = 2;
        this.ctx.setLineDash([6, 6]);
        const boxSize = 120;
        this.ctx.strokeRect(-boxSize, -boxSize / 2, boxSize * 2, boxSize);
      }

      this.ctx.restore();
    });
  }

  // Export High-Res Thumbnail (PNG / JPG)
  exportThumbnail(format = 'png') {
    if (!this.canvas) return;
    const oldIndex = this.selectedLayerIndex;
    this.selectedLayerIndex = -1; // hide selection bounding box
    this.render();

    const mime = format === 'jpg' ? 'image/jpeg' : 'image/png';
    const dataUrl = this.canvas.toDataURL(mime, 0.95);

    const a = document.createElement('a');
    a.href = dataUrl;
    a.download = `mildy_thumbnail_${this.canvasPreset}_${Date.now()}.${format}`;
    a.click();

    this.selectedLayerIndex = oldIndex;
    this.render();
    window.MildyApp.showToast(`${t('export')}: ${format.toUpperCase()}`);
  }
}

window.thumbnailStudio = new ThumbnailStudio();
