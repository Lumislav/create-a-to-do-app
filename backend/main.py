import logging
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from config import settings
from routers import health, subtitles, photofix, video

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(name)s: %(message)s"
)
logger = logging.getLogger("MildyAI")

app = FastAPI(
    title=settings.APP_NAME,
    version=settings.APP_VERSION,
    description="Dedicated FastAPI AI backend for Mildy Mobile Studio by PALIMSON Group"
)

# CORS configuration allowing Android emulator, local development, and Web client
app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.ALLOWED_ORIGINS,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Register routers
app.include_router(health.router)
app.include_router(subtitles.router)
app.include_router(photofix.router)
app.include_router(video.router)

@app.on_event("startup")
async def on_startup():
    logger.info(f"Starting {settings.APP_NAME} v{settings.APP_VERSION} ({settings.COMPANY})")
    logger.info("AI engines ready: Whisper (Word-Level) | RemBG (u2net) | SuperRes (Lanczos+Unsharp)")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)
