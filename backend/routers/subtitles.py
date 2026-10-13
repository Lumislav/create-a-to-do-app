import os
import uuid
import aiofiles
from fastapi import APIRouter, UploadFile, File, Form, HTTPException, status
from config import settings
from services.whisper_service import WhisperService

router = APIRouter(prefix="/api/subtitles", tags=["Subtitles"])
whisper_service = WhisperService(model_size=settings.WHISPER_MODEL_SIZE, device=settings.DEVICE)

@router.post("/transcribe")
async def transcribe_speech(
    file: UploadFile = File(...),
    language: str = Form("auto")
):
    """
    Accepts media file (video or audio) and transcribes speech into word-level timestamps.
    Supported languages: 'kk', 'ru', 'en', 'auto'.
    """
    if not file.filename:
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="No file selected")

    temp_filename = f"{uuid.uuid4()}_{file.filename}"
    temp_path = os.path.join(settings.TEMP_DIR, temp_filename)

    try:
        # Write file with size validation
        bytes_read = 0
        max_bytes = settings.MAX_UPLOAD_SIZE_MB * 1024 * 1024

        async with aiofiles.open(temp_path, "wb") as out_file:
            while content := await file.read(1024 * 1024):  # 1MB chunks
                bytes_read += len(content)
                if bytes_read > max_bytes:
                    raise HTTPException(
                        status_code=status.HTTP_413_REQUEST_ENTITY_TOO_LARGE,
                        detail=f"File exceeds maximum allowed size ({settings.MAX_UPLOAD_SIZE_MB}MB)"
                    )
                await out_file.write(content)

        # Transcribe with Whisper
        result = whisper_service.transcribe(temp_path, language=language)
        return result

    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Transcription failed: {str(e)}"
        )
    finally:
        # Guaranteed temporary file cleanup
        if os.path.exists(temp_path):
            try:
                os.remove(temp_path)
            except Exception:
                pass
