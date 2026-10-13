import os
from pydantic import BaseModel

class Settings(BaseModel):
    APP_NAME: str = "Mildy AI Backend"
    APP_VERSION: str = "1.0.0"
    COMPANY: str = "PALIMSON Group"
    MAX_UPLOAD_SIZE_MB: int = 150  # 150 MB max upload limit
    TEMP_DIR: str = os.path.join(os.path.dirname(__file__), "temp_uploads")
    WHISPER_MODEL_SIZE: str = os.getenv("WHISPER_MODEL_SIZE", "base")
    DEVICE: str = os.getenv("DEVICE", "cpu")  # "cuda" if GPU is available
    ALLOWED_ORIGINS: list[str] = ["*"]

settings = Settings()

os.makedirs(settings.TEMP_DIR, exist_ok=True)
