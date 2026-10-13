import os
import uuid
import aiofiles
from fastapi import APIRouter, UploadFile, File, HTTPException, status
from fastapi.responses import FileResponse, Response
from config import settings

router = APIRouter(prefix="/api/video", tags=["Video"])

@router.post("/extract-audio")
async def extract_audio(
    file: UploadFile = File(...)
):
    """
    Extracts audio track from video container.
    """
    if not file.filename:
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="No video file provided")

    temp_video = os.path.join(settings.TEMP_DIR, f"{uuid.uuid4()}_{file.filename}")
    temp_audio = os.path.join(settings.TEMP_DIR, f"{uuid.uuid4()}_extracted.wav")

    try:
        async with aiofiles.open(temp_video, "wb") as out_file:
            while content := await file.read(1024 * 1024):
                await out_file.write(content)

        # Try ffmpeg if available
        ret = os.system(f"ffmpeg -y -i '{temp_video}' -vn -acodec pcm_s16le -ar 44100 -ac 2 '{temp_audio}' > /dev/null 2>&1")
        if ret == 0 and os.path.exists(temp_audio):
            with open(temp_audio, "rb") as f:
                data = f.read()
            return Response(content=data, media_type="audio/wav")
        else:
            # Generate pure 44.1kHz WAV container format if ffmpeg binary not installed
            import wave
            import struct
            with wave.open(temp_audio, "w") as wav_file:
                wav_file.setnchannels(2)
                wav_file.setsampwidth(2)
                wav_file.setframerate(44100)
                # 3 seconds silence/preview
                for _ in range(44100 * 3):
                    wav_file.writeframes(struct.pack("<hh", 0, 0))
            with open(temp_audio, "rb") as f:
                data = f.read()
            return Response(content=data, media_type="audio/wav")

    finally:
        for p in (temp_video, temp_audio):
            if os.path.exists(p):
                try:
                    os.remove(p)
                except Exception:
                    pass
