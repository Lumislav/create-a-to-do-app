import io
from fastapi import APIRouter, UploadFile, File, Form, HTTPException, status
from fastapi.responses import Response
from config import settings
from services.segmentation_service import SegmentationService
from services.upscale_service import UpscaleService

router = APIRouter(prefix="/api", tags=["PhotoFix"])
segmentation_service = SegmentationService()
upscale_service = UpscaleService()

@router.post("/background/remove")
async def remove_background(
    file: UploadFile = File(...)
):
    """
    Removes background from uploaded image and returns transparent PNG.
    """
    if not file.filename:
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="No image provided")

    contents = await file.read()
    if len(contents) > settings.MAX_UPLOAD_SIZE_MB * 1024 * 1024:
        raise HTTPException(
            status_code=status.HTTP_413_REQUEST_ENTITY_TOO_LARGE,
            detail="Image size exceeds upload limit"
        )

    try:
        result_png_bytes = segmentation_service.remove_background(contents)
        return Response(content=result_png_bytes, media_type="image/png")
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Background removal failed: {str(e)}"
        )

@router.post("/upscale/process")
async def upscale_image(
    file: UploadFile = File(...),
    scale: int = Form(2)
):
    """
    Applies 2x Super-Resolution and micro-detail restoration.
    """
    if not file.filename:
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="No image provided")

    contents = await file.read()
    try:
        result_bytes = upscale_service.upscale(contents, scale=scale)
        return Response(content=result_bytes, media_type="image/png")
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Upscaling failed: {str(e)}"
        )
