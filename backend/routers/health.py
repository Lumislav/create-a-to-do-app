from fastapi import APIRouter
from config import settings

router = APIRouter(tags=["Health"])

@router.get("/health")
async def health_check():
    return {
        "status": "online",
        "app": settings.APP_NAME,
        "version": settings.APP_VERSION,
        "company": settings.COMPANY,
        "models": "Whisper (kk, ru, en) + RemBG + SuperRes"
    }
