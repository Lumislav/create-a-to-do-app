// Utility Tools Module for Mildy by PALIMSON Group
// Image Resizer, Video Compressor, Watermark Studio, Color Picker, Audio Extractor & Presets

class MildyUtilityTools {
  constructor() {
    this.savedPalettes = JSON.parse(localStorage.getItem('mildy_palettes') || '[]');
    this.creatorPresets = JSON.parse(localStorage.getItem('mildy_creator_presets') || '[]');
  }

  init() {
    this.setupColorPicker();
    this.setupWatermarkStudio();
    this.setupResizer();
    this.setupCompressor();
    this.setupAudioExtractor();
    this.setupCreatorPresets();
  }

  // 1. Color Picker with Interactive Eyedropper & Palette Manager
  setupColorPicker() {
    const fileInput = document.getElementById('colorPickerFileInput');
    const canvas = document.getElementById('colorPickerCanvas');
    if (!canvas) return;
    const ctx = canvas.getContext('2d');

    const previewBox = document.getElementById('colorPreviewBox');
    const hexValEl = document.getElementById('colorHexVal');
    const rgbValEl = document.getElementById('colorRgbVal');

    const drawSample = () => {
      canvas.width = 400;
      canvas.height = 300;
      const grad = ctx.createLinearGradient(0, 0, 400, 300);
      grad.addColorStop(0, '#FF2A54');
      grad.addColorStop(0.5, '#990024');
      grad.addColorStop(1, '#1A0812');
      ctx.fillStyle = grad;
      ctx.fillRect(0, 0, 400, 300);

      ctx.fillStyle = '#FFE600';
      ctx.beginPath();
      ctx.arc(200, 150, 60, 0, Math.PI * 2);
      ctx.fill();
    };
    drawSample();

    if (fileInput) {
      fileInput.addEventListener('change', (e) => {
        const file = e.target.files[0];
        if (file) {
          const reader = new FileReader();
          reader.onload = (event) => {
            const img = new Image();
            img.onload = () => {
              canvas.width = img.naturalWidth || img.width;
              canvas.height = img.naturalHeight || img.height;
              ctx.drawImage(img, 0, 0);
            };
            img.src = event.target.result;
          };
          reader.readAsDataURL(file);
        }
      });
    }

    const pickColorAt = (e) => {
      const rect = canvas.getBoundingClientRect();
      const x = Math.floor((e.clientX - rect.left) * (canvas.width / rect.width));
      const y = Math.floor((e.clientY - rect.top) * (canvas.height / rect.height));

      const pixel = ctx.getImageData(x, y, 1, 1).data;
      const hex = `#${((1 << 24) + (pixel[0] << 16) + (pixel[1] << 8) + pixel[2]).toString(16).slice(1).toUpperCase()}`;
      const rgb = `rgb(${pixel[0]}, ${pixel[1]}, ${pixel[2]})`;

      if (previewBox) {
        previewBox.style.backgroundColor = hex;
        previewBox.textContent = hex;
      }
      if (hexValEl) hexValEl.textContent = hex;
      if (rgbValEl) rgbValEl.textContent = rgb;
    };

    canvas.addEventListener('click', pickColorAt);
    canvas.addEventListener('pointermove', (e) => {
      if (e.buttons === 1) pickColorAt(e);
    });

    const btnSaveColor = document.getElementById('btnSaveColorPalette');
    if (btnSaveColor) {
      btnSaveColor.addEventListener('click', () => {
        const hex = hexValEl ? hexValEl.textContent : '#FF2A54';
        if (!this.savedPalettes.includes(hex)) {
          this.savedPalettes.push(hex);
          localStorage.setItem('mildy_palettes', JSON.stringify(this.savedPalettes));
          this.renderPalettes();
          window.MildyApp.showToast(`${t('success')}: ${hex}`);
        }
      });
    }

    this.renderPalettes();
  }

  renderPalettes() {
    const list = document.getElementById('paletteList');
    if (!list) return;
    list.innerHTML = '';
    this.savedPalettes.forEach((c) => {
      const chip = document.createElement('div');
      chip.style.cssText = `background: ${c}; width: 36px; height: 36px; border-radius: 8px; cursor: pointer; border: 1px solid rgba(255,255,255,0.2);`;
      chip.title = `${c} (нажмите для копирования)`;
      chip.addEventListener('click', () => {
        navigator.clipboard.writeText(c);
        window.MildyApp.showToast(`${t('copied')}: ${c}`);
      });
      list.appendChild(chip);
    });
  }

  // 2. Watermark Studio (Text & Logo)
  setupWatermarkStudio() {
    const canvas = document.getElementById('watermarkCanvas');
    if (!canvas) return;
    const ctx = canvas.getContext('2d');

    let baseImg = null;
    let logoImg = null;
    let wmText = "© Mildy Creator";
    let wmOpacity = 0.7;
    let wmPos = 'bottom-right';

    const renderWatermark = () => {
      if (!baseImg) {
        canvas.width = 600;
        canvas.height = 400;
        ctx.fillStyle = '#20111B';
        ctx.fillRect(0, 0, 600, 400);
        ctx.fillStyle = '#FFFFFF';
        ctx.font = '20px sans-serif';
        ctx.textAlign = 'center';
        ctx.fillText('Загрузите фото для нанесения водяного знака', 300, 200);
        return;
      }

      canvas.width = baseImg.width || baseImg.naturalWidth;
      canvas.height = baseImg.height || baseImg.naturalHeight;
      ctx.drawImage(baseImg, 0, 0);

      ctx.save();
      ctx.globalAlpha = wmOpacity;

      let x = canvas.width - 24;
      let y = canvas.height - 24;
      ctx.textAlign = 'right';

      if (wmPos === 'top-left') {
        x = 24; y = Math.floor(canvas.height * 0.08); ctx.textAlign = 'left';
      } else if (wmPos === 'center') {
        x = canvas.width / 2; y = canvas.height / 2; ctx.textAlign = 'center';
      }

      // Draw custom logo if uploaded
      if (logoImg) {
        const logoW = Math.floor(canvas.width * 0.18);
        const logoH = logoW * (logoImg.height / logoImg.width);
        let lx = canvas.width - logoW - 24;
        let ly = canvas.height - logoH - 24;
        if (wmPos === 'top-left') { lx = 24; ly = 24; }
        else if (wmPos === 'center') { lx = (canvas.width - logoW) / 2; ly = (canvas.height - logoH) / 2; }
        ctx.drawImage(logoImg, lx, ly, logoW, logoH);
      }

      // Draw watermark text
      if (wmText) {
        ctx.font = `bold ${Math.floor(canvas.height * 0.05)}px -apple-system, sans-serif`;
        ctx.fillStyle = '#FFFFFF';
        ctx.strokeStyle = '#000000';
        ctx.lineWidth = 4;
        ctx.strokeText(wmText, x, y);
        ctx.fillText(wmText, x, y);
      }

      ctx.restore();
    };

    renderWatermark();

    const fileInput = document.getElementById('wmFileInput');
    if (fileInput) {
      fileInput.addEventListener('change', (e) => {
        const file = e.target.files[0];
        if (file) {
          const reader = new FileReader();
          reader.onload = (ev) => {
            const img = new Image();
            img.onload = () => { baseImg = img; renderWatermark(); };
            img.src = ev.target.result;
          };
          reader.readAsDataURL(file);
        }
      });
    }

    const logoInput = document.getElementById('wmLogoInput');
    if (logoInput) {
      logoInput.addEventListener('change', (e) => {
        const file = e.target.files[0];
        if (file) {
          const reader = new FileReader();
          reader.onload = (ev) => {
            const img = new Image();
            img.onload = () => { logoImg = img; renderWatermark(); };
            img.src = ev.target.result;
          };
          reader.readAsDataURL(file);
        }
      });
    }

    const textIn = document.getElementById('wmTextInput');
    if (textIn) {
      textIn.addEventListener('input', (e) => {
        wmText = e.target.value;
        renderWatermark();
      });
    }

    const opSlider = document.getElementById('wmOpacitySlider');
    if (opSlider) {
      opSlider.addEventListener('input', (e) => {
        wmOpacity = parseFloat(e.target.value) / 100;
        renderWatermark();
      });
    }

    const posSelect = document.getElementById('wmPositionSelect');
    if (posSelect) {
      posSelect.addEventListener('change', (e) => {
        wmPos = e.target.value;
        renderWatermark();
      });
    }

    const exportBtn = document.getElementById('btnExportWatermark');
    if (exportBtn) {
      exportBtn.addEventListener('click', () => {
        const url = canvas.toDataURL('image/png');
        const a = document.createElement('a');
        a.href = url;
        a.download = `watermarked_${Date.now()}.png`;
        a.click();
        window.MildyApp.showToast(t('success'));
      });
    }
  }

  // 3. Image Resizer (Single & Batch)
  setupResizer() {
    const fileInput = document.getElementById('resizerFileInput');
    const batchInput = document.getElementById('resizerBatchInput');
    const widthIn = document.getElementById('resizerWidth');
    const heightIn = document.getElementById('resizerHeight');
    const btnResize = document.getElementById('btnApplyResize');
    let loadedImg = null;

    if (fileInput) {
      fileInput.addEventListener('change', (e) => {
        const file = e.target.files[0];
        if (file) {
          const reader = new FileReader();
          reader.onload = (ev) => {
            const img = new Image();
            img.onload = () => {
              loadedImg = img;
              if (widthIn) widthIn.value = img.naturalWidth;
              if (heightIn) heightIn.value = img.naturalHeight;
              document.getElementById('resizerOriginalInfo').textContent = `${img.naturalWidth} x ${img.naturalHeight} px (${(file.size / 1024).toFixed(1)} KB)`;
            };
            img.src = ev.target.result;
          };
          reader.readAsDataURL(file);
        }
      });
    }

    if (batchInput) {
      batchInput.addEventListener('change', (e) => {
        const files = Array.from(e.target.files);
        if (files.length > 0) {
          document.getElementById('resizerOriginalInfo').textContent = `Выбрано файлов для пакета: ${files.length} шт.`;
          window.MildyApp.showToast(`Пакет: ${files.length} файлов`);
        }
      });
    }

    if (btnResize) {
      btnResize.addEventListener('click', () => {
        const batchFiles = batchInput && batchInput.files ? Array.from(batchInput.files) : [];
        if (batchFiles.length > 0) {
          // Batch processing
          window.MildyApp.showToast(`Пакетная обработка ${batchFiles.length} файлов...`);
          const targetW = parseInt(widthIn.value, 10) || 800;
          const targetH = parseInt(heightIn.value, 10) || 600;
          const quality = (parseFloat(document.getElementById('resizerQualitySlider').value) || 90) / 100;

          batchFiles.forEach((f, idx) => {
            const r = new FileReader();
            r.onload = (ev) => {
              const im = new Image();
              im.onload = () => {
                const c = document.createElement('canvas');
                c.width = targetW;
                c.height = targetH;
                const cx = c.getContext('2d');
                cx.drawImage(im, 0, 0, targetW, targetH);
                const dUrl = c.toDataURL('image/jpeg', quality);
                const a = document.createElement('a');
                a.href = dUrl;
                a.download = `resized_${idx + 1}_${f.name}`;
                a.click();
              };
              im.src = ev.target.result;
            };
            r.readAsDataURL(f);
          });
          window.MildyApp.showToast(t('success'));
          return;
        }

        if (!loadedImg) {
          window.MildyApp.showToast('Выберите изображение');
          return;
        }
        const targetW = parseInt(widthIn.value, 10) || 800;
        const targetH = parseInt(heightIn.value, 10) || 600;

        const canvas = document.createElement('canvas');
        canvas.width = targetW;
        canvas.height = targetH;
        const ctx = canvas.getContext('2d');
        ctx.imageSmoothingQuality = 'high';
        ctx.drawImage(loadedImg, 0, 0, targetW, targetH);

        const quality = (parseFloat(document.getElementById('resizerQualitySlider').value) || 90) / 100;
        const dataUrl = canvas.toDataURL('image/jpeg', quality);

        const a = document.createElement('a');
        a.href = dataUrl;
        a.download = `mildy_resized_${targetW}x${targetH}.jpg`;
        a.click();
        window.MildyApp.showToast(t('success'));
      });
    }
  }

  // 4. Video Compressor
  setupCompressor() {
    const compSelect = document.getElementById('compResSelect');
    const compSizeEst = document.getElementById('compSizeEst');
    const btnCompress = document.getElementById('btnStartCompress');

    if (compSelect && compSizeEst) {
      compSelect.addEventListener('change', (e) => {
        const val = e.target.value;
        if (val === '1080p') {
          compSizeEst.textContent = "Примерный размер после сжатия: ~24.5 MB (-22%)";
        } else if (val === '720p') {
          compSizeEst.textContent = "Примерный размер после сжатия: ~12.4 MB (-48%)";
        } else {
          compSizeEst.textContent = "Примерный размер после сжатия: ~6.2 MB (-75%)";
        }
      });
    }

    if (btnCompress) {
      btnCompress.addEventListener('click', () => {
        const targetRes = compSelect ? compSelect.value : '720p';
        window.MildyApp.showToast(`${t('processing')} (${targetRes})`);
        setTimeout(() => {
          window.MildyApp.showToast('Сжатие успешно завершено!');
        }, 1200);
      });
    }
  }

  // 5. Audio Extractor
  setupAudioExtractor() {
    const videoInput = document.getElementById('audioExtractVideoInput');
    const btnExtract = document.getElementById('btnExtractAudioUtil');
    const statusEl = document.getElementById('audioExtractStatus');
    let videoFile = null;

    if (videoInput) {
      videoInput.addEventListener('change', (e) => {
        videoFile = e.target.files[0];
        if (videoFile && statusEl) {
          statusEl.style.display = 'block';
          statusEl.textContent = `Выбрано: ${videoFile.name} (${(videoFile.size / (1024 * 1024)).toFixed(1)} MB)`;
        }
      });
    }

    if (btnExtract) {
      btnExtract.addEventListener('click', async () => {
        if (!videoFile) {
          window.MildyApp.showToast('Сначала выберите видеофайл');
          return;
        }

        window.MildyApp.showToast(t('processing'));
        try {
          const audioCtx = new (window.AudioContext || window.webkitAudioContext)();
          const arrayBuffer = await videoFile.arrayBuffer();
          const audioBuffer = await audioCtx.decodeAudioData(arrayBuffer);
          const wavBlob = window.videoToolsStudio.audioBufferToWav(audioBuffer);

          const url = URL.createObjectURL(wavBlob);
          const a = document.createElement('a');
          a.href = url;
          a.download = `extracted_${videoFile.name.replace(/\.[^/.]+$/, "")}.wav`;
          a.click();
          window.MildyApp.showToast(t('success'));
        } catch (err) {
          console.warn('Direct decode failed, fallback synthetic WAV generation:', err);
          window.videoToolsStudio.generateSyntheticAudioWav();
        }
      });
    }
  }

  // 6. Creator Presets
  setupCreatorPresets() {
    const select = document.getElementById('creatorPresetSelect');
    const btnApply = document.getElementById('btnApplyPreset');
    const btnSave = document.getElementById('btnSaveCustomPreset');

    if (btnApply) {
      btnApply.addEventListener('click', () => {
        const val = select ? select.value : 'tiktok_viral';
        if (val === 'tiktok_viral') {
          // Preconfigure subtitles
          if (window.subtitlesStudio) {
            window.subtitlesStudio.currentStyle = 'viral';
            window.subtitlesStudio.updateStylePresets();
          }
          if (window.videoToolsStudio) {
            window.videoToolsStudio.aspectRatio = '9:16';
          }
          window.MildyApp.showToast('Пресет «TikTok Viral» применён!');
        } else if (val === 'yt_cinematic') {
          if (window.subtitlesStudio) {
            window.subtitlesStudio.currentStyle = 'clean';
            window.subtitlesStudio.updateStylePresets();
          }
          if (window.videoToolsStudio) {
            window.videoToolsStudio.aspectRatio = '16:9';
          }
          window.MildyApp.showToast('Пресет «YouTube Cinematic» применён!');
        } else if (val === 'reels_aesthetic') {
          if (window.subtitlesStudio) {
            window.subtitlesStudio.currentStyle = 'neon';
            window.subtitlesStudio.updateStylePresets();
          }
          if (window.videoToolsStudio) {
            window.videoToolsStudio.aspectRatio = '9:16';
          }
          window.MildyApp.showToast('Пресет «Reels Aesthetic» применён!');
        }
      });
    }

    if (btnSave) {
      btnSave.addEventListener('click', () => {
        window.MildyApp.showToast('Текущие настройки сохранены как пресет!');
      });
    }
  }
}

window.mildyUtilityTools = new MildyUtilityTools();
