// Subtitles Studio Module for Mildy by PALIMSON Group
// Word-level timestamps, animated styles, real-time sync, SRT/VTT & MP4 export

class SubtitleStudio {
  constructor() {
    this.videoEl = null;
    this.canvasEl = null;
    this.subtitles = [];
    this.currentStyle = 'viral';
    this.position = 'bottom';
    this.fontSize = 24;
    this.highlightColor = '#FFE600';
    this.textColor = '#FFFFFF';
    this.strokeColor = '#000000';
    this.currentMediaUrl = null;
    this.mediaType = 'video';
    this.animationFrameId = null;
    this.isExporting = false;
  }

  init() {
    this.videoEl = document.getElementById('subVideoPlayer');
    this.subContainer = document.getElementById('subOverlayBox');
    this.setupListeners();
    this.loadSampleVideo();
  }

  setupListeners() {
    const videoInput = document.getElementById('subVideoInput');
    if (videoInput) {
      videoInput.addEventListener('change', (e) => {
        const file = e.target.files[0];
        if (file) {
          const url = URL.createObjectURL(file);
          this.loadVideo(url, file.name);
        }
      });
    }

    if (this.videoEl) {
      this.videoEl.addEventListener('timeupdate', () => this.onTimeUpdate());
      this.videoEl.addEventListener('play', () => this.startRenderLoop());
      this.videoEl.addEventListener('pause', () => this.stopRenderLoop());
    }

    // Style selector
    const styleSel = document.getElementById('subStyleSelect');
    if (styleSel) {
      styleSel.addEventListener('change', (e) => {
        this.currentStyle = e.target.value;
        this.updateStylePresets();
        this.onTimeUpdate();
      });
    }

    // Position selector
    const posSel = document.getElementById('subPosSelect');
    if (posSel) {
      posSel.addEventListener('change', (e) => {
        this.position = e.target.value;
        this.updatePosition();
      });
    }

    // Font size slider
    const fontSlider = document.getElementById('subFontSizeSlider');
    if (fontSlider) {
      fontSlider.addEventListener('input', (e) => {
        this.fontSize = parseInt(e.target.value, 10);
        document.getElementById('subFontSizeVal').textContent = `${this.fontSize}px`;
        this.onTimeUpdate();
      });
    }

    // Color pickers
    const hlColor = document.getElementById('subHlColor');
    if (hlColor) {
      hlColor.addEventListener('input', (e) => {
        this.highlightColor = e.target.value;
        this.onTimeUpdate();
      });
    }

    const txtColor = document.getElementById('subTextColor');
    if (txtColor) {
      txtColor.addEventListener('input', (e) => {
        this.textColor = e.target.value;
        this.onTimeUpdate();
      });
    }
  }

  loadVideo(url, name = 'video.mp4') {
    this.currentMediaUrl = url;
    this.videoEl.src = url;
    this.videoEl.load();
    window.MildyApp.showToast(`${t('ready')}: ${name}`);
  }

  // Generates a lightweight, stylish animated sample creator video using Canvas
  loadSampleVideo() {
    const canvas = document.createElement('canvas');
    canvas.width = 720;
    canvas.height = 1280; // 9:16 Shorts
    const ctx = canvas.getContext('2d');
    const stream = canvas.captureStream(30);

    const mediaRecorder = new MediaRecorder(stream, { mimeType: 'video/webm' });
    const chunks = [];
    mediaRecorder.ondataavailable = e => chunks.push(e.data);
    mediaRecorder.onstop = () => {
      const blob = new Blob(chunks, { type: 'video/mp4' });
      const url = URL.createObjectURL(blob);
      this.loadVideo(url, 'demo_shorts_916.mp4');
      // Set default demo transcript
      this.subtitles = window.mildyAi.getDemoTranscripts(currentLang);
      this.renderTimeline();
    };

    mediaRecorder.start();

    let frame = 0;
    const totalFrames = 30 * 8; // 8 seconds demo

    const drawFrame = () => {
      if (frame >= totalFrames) {
        mediaRecorder.stop();
        return;
      }

      // Elegant Animated Background
      const time = frame / 30;
      const grad = ctx.createLinearGradient(0, 0, 720, 1280);
      grad.addColorStop(0, '#12070D');
      grad.addColorStop(0.5, '#2D0A18');
      grad.addColorStop(1, '#800020');
      ctx.fillStyle = grad;
      ctx.fillRect(0, 0, 720, 1280);

      // Dynamic glowing aura
      const auraY = 640 + Math.sin(time * 2) * 50;
      const radGrad = ctx.createRadialGradient(360, auraY, 40, 360, auraY, 320);
      radGrad.addColorStop(0, 'rgba(255, 42, 84, 0.45)');
      radGrad.addColorStop(1, 'rgba(255, 42, 84, 0)');
      ctx.fillStyle = radGrad;
      ctx.beginPath();
      ctx.arc(360, auraY, 320, 0, Math.PI * 2);
      ctx.fill();

      // Creator Badge
      ctx.fillStyle = '#FFFFFF';
      ctx.font = 'bold 36px -apple-system, sans-serif';
      ctx.textAlign = 'center';
      ctx.fillText('Mildy Creator Studio', 360, 220);

      ctx.fillStyle = '#FF2A54';
      ctx.font = '600 22px -apple-system, sans-serif';
      ctx.fillText('PALIMSON GROUP', 360, 260);

      // Soundwave visualization simulation
      ctx.strokeStyle = '#FFFFFF';
      ctx.lineWidth = 4;
      for (let i = 0; i < 20; i++) {
        const h = Math.abs(Math.sin(time * 5 + i)) * 70 + 15;
        const x = 180 + i * 18;
        ctx.beginPath();
        ctx.moveTo(x, 600 - h / 2);
        ctx.lineTo(x, 600 + h / 2);
        ctx.stroke();
      }

      frame++;
      setTimeout(drawFrame, 1000 / 30);
    };

    drawFrame();
  }

  async runAutoTranscribe() {
    const btn = document.getElementById('btnTranscribe');
    const origText = btn.innerHTML;
    btn.disabled = true;
    btn.innerHTML = `<i class="fa-solid fa-spinner fa-spin"></i> ${t('transcribing')}`;

    const lang = document.getElementById('subLangSelect').value;
    try {
      this.subtitles = await window.mildyAi.transcribeAudio(this.currentMediaUrl, lang, (pct, msg) => {
        window.MildyApp.showToast(`${msg} (${pct}%)`);
      });
      this.renderTimeline();
      window.MildyApp.showToast(t('success'));
    } catch (e) {
      console.error(e);
      window.MildyApp.showToast('Ошибка распознавания');
    } finally {
      btn.disabled = false;
      btn.innerHTML = origText;
    }
  }

  updatePosition() {
    if (!this.subContainer) return;
    this.subContainer.style.bottom = '';
    this.subContainer.style.top = '';
    if (this.position === 'top') {
      this.subContainer.style.top = '14%';
    } else if (this.position === 'center') {
      this.subContainer.style.top = '48%';
    } else {
      this.subContainer.style.bottom = '16%';
    }
  }

  updateStylePresets() {
    if (this.currentStyle === 'viral') {
      this.highlightColor = '#FFE600';
      this.textColor = '#FFFFFF';
      this.strokeColor = '#000000';
    } else if (this.currentStyle === 'beast') {
      this.highlightColor = '#00F0FF';
      this.textColor = '#FFFFFF';
      this.strokeColor = '#000000';
    } else if (this.currentStyle === 'clean') {
      this.highlightColor = '#FFFFFF';
      this.textColor = '#E0E0E0';
      this.strokeColor = 'transparent';
    } else if (this.currentStyle === 'neon') {
      this.highlightColor = '#FF2A54';
      this.textColor = '#FFFFFF';
      this.strokeColor = '#38000C';
    } else if (this.currentStyle === 'cyber') {
      this.highlightColor = '#FFAA00';
      this.textColor = '#00FFCC';
      this.strokeColor = '#110022';
    }
    const hlEl = document.getElementById('subHlColor');
    if (hlEl) hlEl.value = this.highlightColor;
  }

  startRenderLoop() {
    const loop = () => {
      this.onTimeUpdate();
      if (!this.videoEl.paused && !this.videoEl.ended) {
        this.animationFrameId = requestAnimationFrame(loop);
      }
    };
    this.animationFrameId = requestAnimationFrame(loop);
  }

  stopRenderLoop() {
    if (this.animationFrameId) {
      cancelAnimationFrame(this.animationFrameId);
    }
  }

  onTimeUpdate() {
    if (!this.videoEl || !this.subContainer) return;
    const currentTime = this.videoEl.currentTime;

    // Find active segment
    const activeSeg = this.subtitles.find(s => currentTime >= s.start && currentTime <= s.end);
    if (!activeSeg) {
      this.subContainer.innerHTML = '';
      return;
    }

    // Build Word-Level animated elements
    let html = '';
    const words = activeSeg.words || activeSeg.text.split(' ').map(w => ({ word: w, start: activeSeg.start, end: activeSeg.end }));

    words.forEach(w => {
      const isWordActive = currentTime >= w.start && currentTime <= w.end;
      const styleStr = this.getWordStyleString(isWordActive);
      html += `<span class="sub-karaoke-word ${isWordActive ? 'active-word' : ''}" style="${styleStr}">${w.word}</span>`;
    });

    this.subContainer.innerHTML = `<div class="sub-line-wrapper style-${this.currentStyle}">${html}</div>`;
  }

  getWordStyleString(isActive) {
    let base = `font-size: ${this.fontSize}px; text-transform: uppercase; font-family: -apple-system, Impact, Montserrat, sans-serif; font-weight: 900;`;
    if (this.currentStyle === 'viral') {
      const col = isActive ? this.highlightColor : this.textColor;
      return `${base} color: ${col}; text-shadow: -2px -2px 0 #000, 2px -2px 0 #000, -2px 2px 0 #000, 2px 2px 0 #000; transform: ${isActive ? 'scale(1.15)' : 'scale(1)'};`;
    } else if (this.currentStyle === 'beast') {
      const col = isActive ? '#00FF66' : this.textColor;
      return `${base} color: ${col}; text-shadow: -3px -3px 0 #000, 3px -3px 0 #000, -3px 3px 0 #000, 3px 3px 0 #000; letter-spacing: 0.5px;`;
    } else if (this.currentStyle === 'neon') {
      const col = isActive ? '#FF2A54' : '#FFFFFF';
      return `${base} color: ${col}; text-shadow: 0 0 10px rgba(255, 42, 84, 0.9), 0 0 20px rgba(255, 42, 84, 0.6);`;
    } else if (this.currentStyle === 'clean') {
      return `font-size: ${this.fontSize - 4}px; font-weight: 600; color: ${isActive ? '#FFFFFF' : '#CCCCCC'}; background: rgba(0,0,0,0.65); padding: 2px 6px; border-radius: 4px;`;
    } else if (this.currentStyle === 'cyber') {
      return `${base} color: ${isActive ? '#00FFA3' : '#00D1FF'}; text-shadow: 0 0 8px rgba(0,255,163,0.8);`;
    }
    return `${base} color: ${this.textColor};`;
  }

  renderTimeline() {
    const listEl = document.getElementById('subTimelineList');
    if (!listEl) return;
    listEl.innerHTML = '';

    if (this.subtitles.length === 0) {
      listEl.innerHTML = `<div class="empty-state">${t('noSubtitlesYet')}</div>`;
      return;
    }

    this.subtitles.forEach((seg, idx) => {
      const row = document.createElement('div');
      row.className = 'sub-item-row';
      row.innerHTML = `
        <span class="sub-time-tag">${seg.start.toFixed(1)}s - ${seg.end.toFixed(1)}s</span>
        <input type="text" class="sub-text-edit" value="${seg.text}">
        <button class="icon-btn" style="width:28px;height:28px;font-size:12px;color:#FF4757;" title="Delete">
          <i class="fa-solid fa-trash"></i>
        </button>
      `;

      // Text edit listener
      const input = row.querySelector('.sub-text-edit');
      input.addEventListener('input', (e) => {
        this.subtitles[idx].text = e.target.value;
        // update words array
        const words = e.target.value.split(/\s+/);
        const duration = this.subtitles[idx].end - this.subtitles[idx].start;
        this.subtitles[idx].words = words.map((w, wIdx) => ({
          word: w,
          start: this.subtitles[idx].start + (wIdx * (duration / words.length)),
          end: this.subtitles[idx].start + ((wIdx + 1) * (duration / words.length))
        }));
        this.onTimeUpdate();
      });

      // Delete line
      row.querySelector('button').addEventListener('click', () => {
        this.subtitles.splice(idx, 1);
        this.renderTimeline();
        this.onTimeUpdate();
      });

      listEl.appendChild(row);
    });
  }

  addSubtitleLine() {
    const lastSeg = this.subtitles[this.subtitles.length - 1];
    const newStart = lastSeg ? (lastSeg.end + 0.3) : 0;
    const newEnd = newStart + 2.0;
    this.subtitles.push({
      id: Date.now(),
      start: newStart,
      end: newEnd,
      text: "Новая строка субтитров",
      words: [
        { word: "Новая", start: newStart, end: newStart + 0.6 },
        { word: "строка", start: newStart + 0.6, end: newStart + 1.2 },
        { word: "субтитров", start: newStart + 1.2, end: newEnd }
      ]
    });
    this.renderTimeline();
    window.MildyApp.showToast(t('addSubtitleLine'));
  }

  // Export SRT format
  exportSRT() {
    if (this.subtitles.length === 0) {
      window.MildyApp.showToast(t('noSubtitlesYet'));
      return;
    }
    const formatTime = (seconds) => {
      const date = new Date(0);
      date.setMilliseconds(seconds * 1000);
      const iso = date.toISOString().substr(11, 12);
      return iso.replace('.', ',');
    };

    let srt = '';
    this.subtitles.forEach((seg, idx) => {
      srt += `${idx + 1}\n`;
      srt += `${formatTime(seg.start)} --> ${formatTime(seg.end)}\n`;
      srt += `${seg.text}\n\n`;
    });

    this.downloadFile(srt, 'mildy_subtitles.srt', 'text/plain');
    window.MildyApp.showToast(t('exportSrt'));
  }

  // Export VTT format
  exportVTT() {
    if (this.subtitles.length === 0) {
      window.MildyApp.showToast(t('noSubtitlesYet'));
      return;
    }
    const formatTime = (seconds) => {
      const date = new Date(0);
      date.setMilliseconds(seconds * 1000);
      return date.toISOString().substr(11, 12);
    };

    let vtt = 'WEBVTT\n\n';
    this.subtitles.forEach((seg, idx) => {
      vtt += `${idx + 1}\n`;
      vtt += `${formatTime(seg.start)} --> ${formatTime(seg.end)}\n`;
      vtt += `${seg.text}\n\n`;
    });

    this.downloadFile(vtt, 'mildy_subtitles.vtt', 'text/vtt');
    window.MildyApp.showToast(t('exportVtt'));
  }

  // Export video with burned-in animated subtitles to MP4
  async exportBurnedVideo() {
    if (!this.videoEl || this.isExporting) return;
    this.isExporting = true;
    window.MildyApp.showToast(t('exportingMp4'));

    const width = this.videoEl.videoWidth || 720;
    const height = this.videoEl.videoHeight || 1280;

    const renderCanvas = document.createElement('canvas');
    renderCanvas.width = width;
    renderCanvas.height = height;
    const ctx = renderCanvas.getContext('2d');

    const stream = renderCanvas.captureStream(30);
    const mediaRecorder = new MediaRecorder(stream, { mimeType: 'video/webm' });
    const chunks = [];
    mediaRecorder.ondataavailable = e => chunks.push(e.data);

    mediaRecorder.onstop = () => {
      const blob = new Blob(chunks, { type: 'video/mp4' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `mildy_subtitled_${Date.now()}.mp4`;
      a.click();
      this.isExporting = false;
      window.MildyApp.showToast(t('success'));
    };

    mediaRecorder.start();

    // Play from beginning
    this.videoEl.currentTime = 0;
    await this.videoEl.play();

    const drawFrame = () => {
      if (this.videoEl.paused || this.videoEl.ended) {
        mediaRecorder.stop();
        return;
      }

      ctx.drawImage(this.videoEl, 0, 0, width, height);

      // Draw active subtitle
      const curTime = this.videoEl.currentTime;
      const activeSeg = this.subtitles.find(s => curTime >= s.start && curTime <= s.end);

      if (activeSeg) {
        const words = activeSeg.words || activeSeg.text.split(' ').map(w => ({ word: w, start: activeSeg.start, end: activeSeg.end }));
        
        ctx.textAlign = 'center';
        ctx.textBaseline = 'middle';
        const fontSizePx = Math.floor(height * 0.04);
        ctx.font = `900 ${fontSizePx}px -apple-system, Impact, Montserrat, sans-serif`;

        const yPos = this.position === 'top' ? height * 0.18 : (this.position === 'center' ? height * 0.5 : height * 0.82);

        // Calculate line widths for word placement
        let fullText = words.map(w => w.word.toUpperCase()).join(' ');
        ctx.lineWidth = Math.max(4, Math.floor(fontSizePx * 0.16));
        ctx.strokeStyle = '#000000';
        ctx.strokeText(fullText, width / 2, yPos);
        ctx.fillStyle = '#FFFFFF';
        ctx.fillText(fullText, width / 2, yPos);

        // Highlight active word
        const activeWord = words.find(w => curTime >= w.start && curTime <= w.end);
        if (activeWord) {
          ctx.fillStyle = this.highlightColor;
          ctx.fillText(activeWord.word.toUpperCase(), width / 2, yPos);
        }
      }

      requestAnimationFrame(drawFrame);
    };

    drawFrame();
  }

  downloadFile(content, fileName, mimeType) {
    const blob = new Blob([content], { type: mimeType });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = fileName;
    a.click();
    URL.revokeObjectURL(url);
  }
}

window.subtitlesStudio = new SubtitleStudio();
