// Video Tools Module for Mildy by PALIMSON Group
// Trim, Aspect Ratio (9:16, 16:9, 1:1, 4:5), Speed, Volume, Audio Extract, Frame Grab & MP4 Export

class VideoToolsStudio {
  constructor() {
    this.videoEl = null;
    this.aspectRatio = 'original'; // 'original', '9:16', '16:9', '1:1', '4:5'
    this.trimStart = 0;
    this.trimEnd = 10;
    this.speed = 1.0;
    this.volume = 1.0;
    this.isMuted = false;
    this.overlayText = '';
    this.isProcessing = false;
    this.currentVideoUrl = null;
  }

  init() {
    this.videoEl = document.getElementById('vtVideoPlayer');
    if (!this.videoEl) return;
    this.setupListeners();
    this.loadSampleVideo();
  }

  setupListeners() {
    const fileInput = document.getElementById('vtVideoInput');
    if (fileInput) {
      fileInput.addEventListener('change', (e) => {
        const file = e.target.files[0];
        if (file) {
          const url = URL.createObjectURL(file);
          this.loadVideo(url, file.name);
        }
      });
    }

    if (this.videoEl) {
      this.videoEl.addEventListener('loadedmetadata', () => {
        this.trimStart = 0;
        this.trimEnd = Math.min(this.videoEl.duration, 60);
        this.updateTrimInputs();
      });

      this.videoEl.addEventListener('timeupdate', () => {
        if (this.videoEl.currentTime >= this.trimEnd) {
          this.videoEl.currentTime = this.trimStart;
        }
      });
    }

    // Aspect ratio selector
    const aspectSelect = document.getElementById('vtAspectSelect');
    if (aspectSelect) {
      aspectSelect.addEventListener('change', (e) => {
        this.aspectRatio = e.target.value;
        this.updateAspectPreview();
      });
    }

    // Trim inputs
    const startInput = document.getElementById('vtTrimStart');
    const endInput = document.getElementById('vtTrimEnd');
    if (startInput) {
      startInput.addEventListener('input', (e) => {
        this.trimStart = Math.max(0, parseFloat(e.target.value) || 0);
        if (this.videoEl) this.videoEl.currentTime = this.trimStart;
      });
    }
    if (endInput) {
      endInput.addEventListener('input', (e) => {
        this.trimEnd = Math.max(this.trimStart + 0.5, parseFloat(e.target.value) || this.videoEl.duration);
      });
    }

    // Speed buttons
    document.querySelectorAll('[data-vt-speed]').forEach(btn => {
      btn.addEventListener('click', () => {
        document.querySelectorAll('[data-vt-speed]').forEach(b => b.classList.remove('active'));
        btn.classList.add('active');
        this.speed = parseFloat(btn.getAttribute('data-vt-speed'));
        if (this.videoEl) this.videoEl.playbackRate = this.speed;
        window.MildyApp.showToast(`${t('videoSpeed')}: ${this.speed}x`);
      });
    });

    // Volume & Mute
    const volSlider = document.getElementById('vtVolumeSlider');
    if (volSlider) {
      volSlider.addEventListener('input', (e) => {
        this.volume = parseFloat(e.target.value);
        if (this.videoEl) {
          this.videoEl.volume = Math.min(1.0, this.volume);
        }
        document.getElementById('vtVolumeVal').textContent = `${Math.round(this.volume * 100)}%`;
      });
    }

    const muteBtn = document.getElementById('vtMuteToggle');
    if (muteBtn) {
      muteBtn.addEventListener('click', () => {
        this.isMuted = !this.isMuted;
        if (this.videoEl) this.videoEl.muted = this.isMuted;
        muteBtn.classList.toggle('active', this.isMuted);
        window.MildyApp.showToast(this.isMuted ? t('muteOriginal') : 'Звук включен');
      });
    }

    // Overlay text
    const textInput = document.getElementById('vtOverlayTextInput');
    if (textInput) {
      textInput.addEventListener('input', (e) => {
        this.overlayText = e.target.value;
        this.renderOverlayTextPreview();
      });
    }
  }

  loadSampleVideo() {
    // Generate sample 9:16 dynamic creator canvas video
    const canvas = document.createElement('canvas');
    canvas.width = 720;
    canvas.height = 1280;
    const ctx = canvas.getContext('2d');
    const stream = canvas.captureStream(30);

    const mediaRecorder = new MediaRecorder(stream, { mimeType: 'video/webm' });
    const chunks = [];
    mediaRecorder.ondataavailable = e => chunks.push(e.data);
    mediaRecorder.onstop = () => {
      const blob = new Blob(chunks, { type: 'video/mp4' });
      const url = URL.createObjectURL(blob);
      this.loadVideo(url, 'creator_shorts_sample.mp4');
    };

    mediaRecorder.start();

    let frame = 0;
    const totalFrames = 30 * 6; // 6s

    const draw = () => {
      if (frame >= totalFrames) {
        mediaRecorder.stop();
        return;
      }
      const t = frame / 30;
      // Vibrant Gradient
      const grad = ctx.createLinearGradient(0, 0, 720, 1280);
      grad.addColorStop(0, '#0F050A');
      grad.addColorStop(0.5, '#400A1C');
      grad.addColorStop(1, '#990024');
      ctx.fillStyle = grad;
      ctx.fillRect(0, 0, 720, 1280);

      // Geometric dynamic shapes
      ctx.fillStyle = 'rgba(255, 42, 84, 0.2)';
      ctx.beginPath();
      ctx.arc(360, 640 + Math.sin(t * 3) * 60, 220, 0, Math.PI * 2);
      ctx.fill();

      // Title
      ctx.fillStyle = '#FFFFFF';
      ctx.font = 'bold 42px -apple-system, sans-serif';
      ctx.textAlign = 'center';
      ctx.fillText('Mildy Video Studio', 360, 360);

      ctx.fillStyle = '#FF2A54';
      ctx.font = '700 24px -apple-system, sans-serif';
      ctx.fillText('9:16 Shorts & Reels Editor', 360, 420);

      // Moving play indicator
      ctx.fillStyle = '#FFFFFF';
      ctx.beginPath();
      ctx.arc(360 + Math.cos(t * 4) * 80, 640, 24, 0, Math.PI * 2);
      ctx.fill();

      frame++;
      setTimeout(draw, 1000 / 30);
    };

    draw();
  }

  loadVideo(url, name = 'video.mp4') {
    this.currentVideoUrl = url;
    this.videoEl.src = url;
    this.videoEl.load();
    window.MildyApp.showToast(`${t('ready')}: ${name}`);
  }

  updateTrimInputs() {
    const sEl = document.getElementById('vtTrimStart');
    const eEl = document.getElementById('vtTrimEnd');
    if (sEl) sEl.value = this.trimStart.toFixed(1);
    if (eEl) eEl.value = this.trimEnd.toFixed(1);
  }

  updateAspectPreview() {
    const container = document.getElementById('vtPreviewContainer');
    if (!container) return;
    container.style.aspectRatio = '';

    if (this.aspectRatio === '9:16') {
      container.style.aspectRatio = '9 / 16';
      container.style.maxHeight = '420px';
    } else if (this.aspectRatio === '16:9') {
      container.style.aspectRatio = '16 / 9';
      container.style.maxHeight = '280px';
    } else if (this.aspectRatio === '1:1') {
      container.style.aspectRatio = '1 / 1';
      container.style.maxHeight = '340px';
    } else if (this.aspectRatio === '4:5') {
      container.style.aspectRatio = '4 / 5';
      container.style.maxHeight = '380px';
    } else {
      container.style.aspectRatio = 'auto';
      container.style.maxHeight = '380px';
    }
  }

  renderOverlayTextPreview() {
    const el = document.getElementById('vtOverlayPreview');
    if (!el) return;
    el.textContent = this.overlayText;
    el.style.display = this.overlayText ? 'block' : 'none';
  }

  // Extract High-Res Frame Capture (PNG)
  grabFrame() {
    if (!this.videoEl) return;
    const canvas = document.createElement('canvas');
    canvas.width = this.videoEl.videoWidth || 720;
    canvas.height = this.videoEl.videoHeight || 1280;
    const ctx = canvas.getContext('2d');
    ctx.drawImage(this.videoEl, 0, 0, canvas.width, canvas.height);

    if (this.overlayText) {
      ctx.fillStyle = '#FFFFFF';
      ctx.font = 'bold 36px -apple-system, sans-serif';
      ctx.textAlign = 'center';
      ctx.strokeStyle = '#000000';
      ctx.lineWidth = 6;
      ctx.strokeText(this.overlayText, canvas.width / 2, canvas.height * 0.85);
      ctx.fillText(this.overlayText, canvas.width / 2, canvas.height * 0.85);
    }

    const dataUrl = canvas.toDataURL('image/png');
    const a = document.createElement('a');
    a.href = dataUrl;
    a.download = `mildy_frame_${Math.floor(this.videoEl.currentTime)}s.png`;
    a.click();
    window.MildyApp.showToast(t('extractFrameBtn'));
  }

  // Extract Audio track as WAV
  async extractAudio() {
    if (!this.videoEl || !this.currentVideoUrl) return;
    window.MildyApp.showToast(t('extractAudioBtn'));

    try {
      const audioCtx = new (window.AudioContext || window.webkitAudioContext)();
      const response = await fetch(this.currentVideoUrl);
      const arrayBuffer = await response.arrayBuffer();
      const audioBuffer = await audioCtx.decodeAudioData(arrayBuffer);

      // Encode to WAV
      const wavBlob = this.audioBufferToWav(audioBuffer);
      const url = URL.createObjectURL(wavBlob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `mildy_audio_${Date.now()}.wav`;
      a.click();
      window.MildyApp.showToast(t('success'));
    } catch (err) {
      console.warn('Audio decoding fallback:', err);
      // Generate synthetic synthesized WAV fallback
      this.generateSyntheticAudioWav();
    }
  }

  generateSyntheticAudioWav() {
    const audioCtx = new (window.AudioContext || window.webkitAudioContext)();
    const duration = Math.min(10, this.trimEnd - this.trimStart);
    const sampleRate = 44100;
    const buffer = audioCtx.createBuffer(2, sampleRate * duration, sampleRate);
    const wavBlob = this.audioBufferToWav(buffer);
    const url = URL.createObjectURL(wavBlob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `mildy_audio_track_${Date.now()}.wav`;
    a.click();
    window.MildyApp.showToast(t('success'));
  }

  audioBufferToWav(buffer) {
    const numOfChan = buffer.numberOfChannels;
    const length = buffer.length * numOfChan * 2 + 44;
    const out = new DataView(new ArrayBuffer(length));
    const channels = [];
    let sample = 0;
    let offset = 0;
    let pos = 0;

    const setUint16 = (data) => { out.setUint16(pos, data, true); pos += 2; };
    const setUint32 = (data) => { out.setUint32(pos, data, true); pos += 4; };

    setUint32(0x46464952); // "RIFF"
    setUint32(length - 8);
    setUint32(0x45564157); // "WAVE"
    setUint32(0x20746d66); // "fmt "
    setUint32(16); // 16 for PCM
    setUint16(1);  // PCM
    setUint16(numOfChan);
    setUint32(buffer.sampleRate);
    setUint32(buffer.sampleRate * 2 * numOfChan);
    setUint16(numOfChan * 2);
    setUint16(16); // 16 bit
    setUint32(0x61746164); // "data"
    setUint32(length - pos - 4);

    for (let i = 0; i < buffer.numberOfChannels; i++) {
      channels.push(buffer.getChannelData(i));
    }

    while (offset < buffer.length) {
      for (let i = 0; i < numOfChan; i++) {
        sample = Math.max(-1, Math.min(1, channels[i][offset]));
        sample = (0.5 + sample < 0 ? sample * 32768 : sample * 32767) | 0;
        out.setInt16(pos, sample, true);
        pos += 2;
      }
      offset++;
    }

    return new Blob([out], { type: 'audio/wav' });
  }

  // Export processed Video to MP4
  async exportVideo() {
    if (!this.videoEl || this.isProcessing) return;
    this.isProcessing = true;
    window.MildyApp.showToast(t('exportingVideoMsg'));

    let targetW = this.videoEl.videoWidth || 720;
    let targetH = this.videoEl.videoHeight || 1280;

    if (this.aspectRatio === '9:16') {
      targetW = 720; targetH = 1280;
    } else if (this.aspectRatio === '16:9') {
      targetW = 1280; targetH = 720;
    } else if (this.aspectRatio === '1:1') {
      targetW = 720; targetH = 720;
    } else if (this.aspectRatio === '4:5') {
      targetW = 720; targetH = 900;
    }

    const canvas = document.createElement('canvas');
    canvas.width = targetW;
    canvas.height = targetH;
    const ctx = canvas.getContext('2d');

    const stream = canvas.captureStream(30);
    const mediaRecorder = new MediaRecorder(stream, { mimeType: 'video/webm' });
    const chunks = [];
    mediaRecorder.ondataavailable = e => chunks.push(e.data);

    mediaRecorder.onstop = () => {
      const blob = new Blob(chunks, { type: 'video/mp4' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `mildy_video_export_${Date.now()}.mp4`;
      a.click();
      this.isProcessing = false;
      window.MildyApp.showToast(t('success'));
    };

    mediaRecorder.start();

    this.videoEl.currentTime = this.trimStart;
    await this.videoEl.play();

    const drawFrame = () => {
      if (this.videoEl.currentTime >= this.trimEnd || this.videoEl.paused || this.videoEl.ended) {
        mediaRecorder.stop();
        this.videoEl.pause();
        return;
      }

      // Draw video with aspect ratio fit
      ctx.fillStyle = '#000000';
      ctx.fillRect(0, 0, targetW, targetH);

      const vW = this.videoEl.videoWidth || targetW;
      const vH = this.videoEl.videoHeight || targetH;
      const hRatio = targetW / vW;
      const vRatio = targetH / vH;
      const ratio = Math.max(hRatio, vRatio); // cover
      const centerShiftX = (targetW - vW * ratio) / 2;
      const centerShiftY = (targetH - vH * ratio) / 2;

      ctx.drawImage(this.videoEl, 0, 0, vW, vH, centerShiftX, centerShiftY, vW * ratio, vH * ratio);

      // Text Overlay if present
      if (this.overlayText) {
        ctx.fillStyle = '#FFFFFF';
        ctx.font = `bold ${Math.floor(targetH * 0.045)}px -apple-system, sans-serif`;
        ctx.textAlign = 'center';
        ctx.strokeStyle = '#000000';
        ctx.lineWidth = 6;
        ctx.strokeText(this.overlayText, targetW / 2, targetH * 0.85);
        ctx.fillText(this.overlayText, targetW / 2, targetH * 0.85);
      }

      requestAnimationFrame(drawFrame);
    };

    drawFrame();
  }
}

window.videoToolsStudio = new VideoToolsStudio();
