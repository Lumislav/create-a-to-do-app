// PhotoFix - High Performance Image Studio for Mildy by PALIMSON Group
// AI Background Removal, Pro Adjustments, Crop/Rotate, 2x Super-Resolution & Multi-format Export

class PhotoFixStudio {
  constructor() {
    this.canvas = null;
    this.ctx = null;
    this.originalImg = null;
    this.processedCanvas = null;
    this.bgRemovedCanvas = null;
    
    // Adjustments
    this.brightness = 0;
    this.contrast = 0;
    this.saturation = 0;
    this.temperature = 0;
    this.sharpness = 0;
    this.blur = 0;

    // Background replacement mode
    this.bgMode = 'transparent'; // 'transparent', 'color', 'gradient', 'blur'
    this.bgColor = '#800020';

    // Transform
    this.rotation = 0;
    this.flipH = false;
    this.flipV = false;

    // Crop
    this.cropRatio = 'free'; // 'free', '1:1', '4:5', '16:9', '9:16'
  }

  init() {
    this.canvas = document.getElementById('photoCanvas');
    if (!this.canvas) return;
    this.ctx = this.canvas.getContext('2d');
    this.setupListeners();
    this.loadSamplePortrait();
  }

  setupListeners() {
    const fileInput = document.getElementById('photoFileInput');
    if (fileInput) {
      fileInput.addEventListener('change', (e) => {
        const file = e.target.files[0];
        if (file) {
          const reader = new FileReader();
          reader.onload = (event) => {
            const img = new Image();
            img.onload = () => this.setImage(img);
            img.src = event.target.result;
          };
          reader.readAsDataURL(file);
        }
      });
    }

    // Sliders
    this.bindSlider('sliderBrightness', 'valBrightness', (v) => { this.brightness = v; this.applyPipeline(); });
    this.bindSlider('sliderContrast', 'valContrast', (v) => { this.contrast = v; this.applyPipeline(); });
    this.bindSlider('sliderSaturation', 'valSaturation', (v) => { this.saturation = v; this.applyPipeline(); });
    this.bindSlider('sliderTemperature', 'valTemperature', (v) => { this.temperature = v; this.applyPipeline(); });
    this.bindSlider('sliderSharpness', 'valSharpness', (v) => { this.sharpness = v; this.applyPipeline(); });
    this.bindSlider('sliderBlur', 'valBlur', (v) => { this.blur = v; this.applyPipeline(); });

    // Background replacement buttons
    document.querySelectorAll('[data-bg-mode]').forEach(btn => {
      btn.addEventListener('click', (e) => {
        document.querySelectorAll('[data-bg-mode]').forEach(b => b.classList.remove('active'));
        btn.classList.add('active');
        this.bgMode = btn.getAttribute('data-bg-mode');
        this.applyPipeline();
      });
    });

    const colorPicker = document.getElementById('photoBgColorInput');
    if (colorPicker) {
      colorPicker.addEventListener('input', (e) => {
        this.bgColor = e.target.value;
        if (this.bgMode === 'color') this.applyPipeline();
      });
    }
  }

  bindSlider(id, valId, callback) {
    const el = document.getElementById(id);
    const valEl = document.getElementById(valId);
    if (el) {
      el.addEventListener('input', (e) => {
        const val = parseInt(e.target.value, 10);
        if (valEl) valEl.textContent = val;
        callback(val);
      });
    }
  }

  loadSamplePortrait() {
    // Generate a sleek creator studio portrait canvas as sample
    const sampleCanvas = document.createElement('canvas');
    sampleCanvas.width = 800;
    sampleCanvas.height = 1000;
    const ctx = sampleCanvas.getContext('2d');

    // Studio background gradient
    const bgGrad = ctx.createRadialGradient(400, 450, 50, 400, 450, 600);
    bgGrad.addColorStop(0, '#3A1523');
    bgGrad.addColorStop(1, '#11050A');
    ctx.fillStyle = bgGrad;
    ctx.fillRect(0, 0, 800, 1000);

    // Stylish creator silhouette & details
    ctx.fillStyle = '#E0A899';
    ctx.beginPath();
    ctx.ellipse(400, 420, 140, 180, 0, 0, Math.PI * 2);
    ctx.fill();

    // Hair / Headphone accent
    ctx.fillStyle = '#1D1217';
    ctx.beginPath();
    ctx.arc(400, 360, 150, Math.PI * 0.9, Math.PI * 2.1);
    ctx.fill();

    // Studio Headphones
    ctx.strokeStyle = '#FF2A54';
    ctx.lineWidth = 18;
    ctx.beginPath();
    ctx.arc(400, 390, 160, Math.PI, 0);
    ctx.stroke();

    // Shoulders / Outfit
    ctx.fillStyle = '#990024';
    ctx.beginPath();
    ctx.moveTo(180, 1000);
    ctx.bezierCurveTo(200, 680, 320, 600, 400, 600);
    ctx.bezierCurveTo(480, 600, 600, 680, 620, 1000);
    ctx.closePath();
    ctx.fill();

    // Studio lighting rim
    ctx.strokeStyle = 'rgba(255, 42, 84, 0.4)';
    ctx.lineWidth = 8;
    ctx.stroke();

    const img = new Image();
    img.onload = () => this.setImage(img);
    img.src = sampleCanvas.toDataURL();
  }

  setImage(img) {
    this.originalImg = img;
    this.bgRemovedCanvas = null;
    this.resetAdjustments();
    this.applyPipeline();
    window.MildyApp.showToast(t('ready'));
  }

  resetAdjustments() {
    this.brightness = 0;
    this.contrast = 0;
    this.saturation = 0;
    this.temperature = 0;
    this.sharpness = 0;
    this.blur = 0;
    this.rotation = 0;
    this.flipH = false;
    this.flipV = false;
    this.bgMode = 'transparent';

    ['sliderBrightness', 'sliderContrast', 'sliderSaturation', 'sliderTemperature', 'sliderSharpness', 'sliderBlur'].forEach(id => {
      const el = document.getElementById(id);
      if (el) el.value = 0;
    });
    ['valBrightness', 'valContrast', 'valSaturation', 'valTemperature', 'valSharpness', 'valBlur'].forEach(id => {
      const el = document.getElementById(id);
      if (el) el.textContent = '0';
    });
  }

  // AI Background Removal
  async removeBackground() {
    if (!this.originalImg) return;
    const btn = document.getElementById('btnRemoveBg');
    const origHtml = btn.innerHTML;
    btn.disabled = true;
    btn.innerHTML = `<i class="fa-solid fa-spinner fa-spin"></i> ${t('processing')}`;

    try {
      this.bgRemovedCanvas = await window.mildyAi.removeBackground(this.originalImg, (pct, msg) => {
        window.MildyApp.showToast(`${msg} (${pct}%)`);
      });
      this.applyPipeline();
      window.MildyApp.showToast(t('success'));
    } catch (err) {
      console.error(err);
      window.MildyApp.showToast('Ошибка удаления фона');
    } finally {
      btn.disabled = false;
      btn.innerHTML = origHtml;
    }
  }

  // 2x Super-Resolution & Denoise
  async upscalePhoto() {
    if (!this.canvas) return;
    const btn = document.getElementById('btnAiUpscale');
    const origHtml = btn.innerHTML;
    btn.disabled = true;
    btn.innerHTML = `<i class="fa-solid fa-spinner fa-spin"></i> ${t('processing')}`;

    try {
      const upscaled = await window.mildyAi.upscaleImage(this.canvas, 2, (pct, msg) => {
        window.MildyApp.showToast(`${msg} (${pct}%)`);
      });
      this.originalImg = upscaled;
      this.applyPipeline();
      window.MildyApp.showToast(t('aiUpscale') + ' ' + t('ready'));
    } catch (err) {
      console.error(err);
      window.MildyApp.showToast('Ошибка сверхразрешения');
    } finally {
      btn.disabled = false;
      btn.innerHTML = origHtml;
    }
  }

  // Auto Color Correction
  autoColorCorrect() {
    if (!this.canvas) return;
    this.brightness = 8;
    this.contrast = 18;
    this.saturation = 12;
    this.temperature = 4;
    this.sharpness = 22;

    const bEl = document.getElementById('sliderBrightness'); if (bEl) bEl.value = 8;
    const cEl = document.getElementById('sliderContrast'); if (cEl) cEl.value = 18;
    const sEl = document.getElementById('sliderSaturation'); if (sEl) sEl.value = 12;
    const tEl = document.getElementById('sliderTemperature'); if (tEl) tEl.value = 4;
    const shEl = document.getElementById('sliderSharpness'); if (shEl) shEl.value = 22;

    this.applyPipeline();
    window.MildyApp.showToast(t('autoColorBtn'));
  }

  rotate90() {
    this.rotation = (this.rotation + 90) % 360;
    this.applyPipeline();
  }

  toggleFlipH() {
    this.flipH = !this.flipH;
    this.applyPipeline();
  }

  toggleFlipV() {
    this.flipV = !this.flipV;
    this.applyPipeline();
  }

  setCropRatio(ratio) {
    this.cropRatio = ratio;
    this.applyPipeline();
    window.MildyApp.showToast(`${t('cropFree')}: ${ratio}`);
  }

  // Main Image Pipeline (Layers, Transforms, Adjustments, Filters)
  applyPipeline() {
    if (!this.originalImg || !this.canvas) return;

    let srcW = this.originalImg.width || this.originalImg.naturalWidth;
    let srcH = this.originalImg.height || this.originalImg.naturalHeight;

    // Apply Crop Ratio
    let cropW = srcW;
    let cropH = srcH;
    let cropX = 0;
    let cropY = 0;

    if (this.cropRatio === '1:1') {
      const size = Math.min(srcW, srcH);
      cropX = (srcW - size) / 2;
      cropY = (srcH - size) / 2;
      cropW = size;
      cropH = size;
    } else if (this.cropRatio === '4:5') {
      const targetRatio = 4 / 5;
      if (srcW / srcH > targetRatio) {
        cropW = srcH * targetRatio;
        cropX = (srcW - cropW) / 2;
      } else {
        cropH = srcW / targetRatio;
        cropY = (srcH - cropH) / 2;
      }
    } else if (this.cropRatio === '16:9') {
      const targetRatio = 16 / 9;
      if (srcW / srcH > targetRatio) {
        cropW = srcH * targetRatio;
        cropX = (srcW - cropW) / 2;
      } else {
        cropH = srcW / targetRatio;
        cropY = (srcH - cropH) / 2;
      }
    } else if (this.cropRatio === '9:16') {
      const targetRatio = 9 / 16;
      if (srcW / srcH > targetRatio) {
        cropW = srcH * targetRatio;
        cropX = (srcW - cropW) / 2;
      } else {
        cropH = srcW / targetRatio;
        cropY = (srcH - cropH) / 2;
      }
    }

    const isRotated90 = (this.rotation === 90 || this.rotation === 270);
    this.canvas.width = isRotated90 ? cropH : cropW;
    this.canvas.height = isRotated90 ? cropW : cropH;

    this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
    this.ctx.save();

    // Center and transform
    this.ctx.translate(this.canvas.width / 2, this.canvas.height / 2);
    this.ctx.rotate((this.rotation * Math.PI) / 180);
    this.ctx.scale(this.flipH ? -1 : 1, this.flipV ? -1 : 1);

    const drawW = cropW;
    const drawH = cropH;

    // Handle Background Replacement
    if (this.bgRemovedCanvas) {
      if (this.bgMode === 'color') {
        this.ctx.fillStyle = this.bgColor;
        this.ctx.fillRect(-drawW / 2, -drawH / 2, drawW, drawH);
      } else if (this.bgMode === 'gradient') {
        const g = this.ctx.createLinearGradient(-drawW / 2, -drawH / 2, drawW / 2, drawH / 2);
        g.addColorStop(0, '#FF2A54');
        g.addColorStop(1, '#800020');
        this.ctx.fillStyle = g;
        this.ctx.fillRect(-drawW / 2, -drawH / 2, drawW, drawH);
      } else if (this.bgMode === 'blur') {
        this.ctx.filter = 'blur(16px)';
        this.ctx.drawImage(this.originalImg, cropX, cropY, cropW, cropH, -drawW / 2, -drawH / 2, drawW, drawH);
        this.ctx.filter = 'none';
      }
      // Draw foreground subject
      this.ctx.drawImage(this.bgRemovedCanvas, cropX, cropY, cropW, cropH, -drawW / 2, -drawH / 2, drawW, drawH);
    } else {
      this.ctx.drawImage(this.originalImg, cropX, cropY, cropW, cropH, -drawW / 2, -drawH / 2, drawW, drawH);
    }

    this.ctx.restore();

    // Pixel Adjustments (Brightness, Contrast, Saturation, Temperature, Sharpness)
    this.applyColorAdjustments();
  }

  applyColorAdjustments() {
    if (this.brightness === 0 && this.contrast === 0 && this.saturation === 0 && this.temperature === 0 && this.sharpness === 0 && this.blur === 0) {
      return;
    }

    const w = this.canvas.width;
    const h = this.canvas.height;
    const imgData = this.ctx.getImageData(0, 0, w, h);
    const d = imgData.data;

    // Contrast factor
    const contrastFactor = (259 * (this.contrast + 255)) / (255 * (259 - this.contrast));
    const brightVal = this.brightness * 1.5;
    const tempVal = this.temperature * 1.2;

    for (let i = 0; i < d.length; i += 4) {
      let r = d[i];
      let g = d[i + 1];
      let b = d[i + 2];

      // Brightness
      r += brightVal;
      g += brightVal;
      b += brightVal;

      // Contrast
      r = contrastFactor * (r - 128) + 128;
      g = contrastFactor * (g - 128) + 128;
      b = contrastFactor * (b - 128) + 128;

      // Temperature (Warmth / Coolness)
      r += tempVal;
      b -= tempVal;

      // Saturation
      if (this.saturation !== 0) {
        const gray = 0.2989 * r + 0.5870 * g + 0.1140 * b;
        const satRatio = 1 + (this.saturation / 100);
        r = gray + satRatio * (r - gray);
        g = gray + satRatio * (g - gray);
        b = gray + satRatio * (b - gray);
      }

      d[i] = Math.min(255, Math.max(0, r));
      d[i + 1] = Math.min(255, Math.max(0, g));
      d[i + 2] = Math.min(255, Math.max(0, b));
    }

    this.ctx.putImageData(imgData, 0, 0);

    // Apply Sharpness kernel if needed
    if (this.sharpness > 0) {
      this.applySharpnessKernel(this.sharpness / 100);
    }
  }

  applySharpnessKernel(strength) {
    const w = this.canvas.width;
    const h = this.canvas.height;
    const imgData = this.ctx.getImageData(0, 0, w, h);
    const d = imgData.data;
    const copy = new Uint8ClampedArray(d);

    const k = strength * 1.5;
    const center = 1 + 4 * k;

    for (let y = 1; y < h - 1; y++) {
      for (let x = 1; x < w - 1; x++) {
        for (let c = 0; c < 3; c++) {
          const idx = (y * w + x) * 4 + c;
          const up = ((y - 1) * w + x) * 4 + c;
          const down = ((y + 1) * w + x) * 4 + c;
          const left = (y * w + (x - 1)) * 4 + c;
          const right = (y * w + (x + 1)) * 4 + c;

          const val = copy[idx] * center - (copy[up] + copy[down] + copy[left] + copy[right]) * k;
          d[idx] = Math.min(255, Math.max(0, val));
        }
      }
    }
    this.ctx.putImageData(imgData, 0, 0);
  }

  // Export File (PNG, JPG, WebP)
  exportPhoto(format = 'png') {
    if (!this.canvas) return;
    const mime = format === 'jpg' ? 'image/jpeg' : (format === 'webp' ? 'image/webp' : 'image/png');
    const quality = 0.95;
    const dataUrl = this.canvas.toDataURL(mime, quality);

    const a = document.createElement('a');
    a.href = dataUrl;
    a.download = `mildy_photo_${Date.now()}.${format}`;
    a.click();
    window.MildyApp.showToast(`${t('export')}: ${format.toUpperCase()}`);
  }
}

window.photoFixStudio = new PhotoFixStudio();
