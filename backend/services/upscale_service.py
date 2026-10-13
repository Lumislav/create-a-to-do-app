import io
import logging

logger = logging.getLogger(__name__)

class UpscaleService:
    def upscale(self, image_bytes: bytes, scale: int = 2) -> bytes:
        """
        Enhances image resolution and micro-details using bicubic resampling and unsharp mask deconvolution.
        """
        try:
            from PIL import Image, ImageEnhance, ImageFilter

            img = Image.open(io.BytesIO(image_bytes))
            new_size = (img.width * scale, img.height * scale)
            
            # High-fidelity Lanczos/Bicubic resampling
            upscaled = img.resize(new_size, Image.Resampling.LANCZOS)

            # Detail and sharpness enhancement kernel
            unsharp = upscaled.filter(ImageFilter.UnsharpMask(radius=2, percent=160, threshold=3))
            
            # Enhance contrast subtly
            enhancer = ImageEnhance.Contrast(unsharp)
            enhanced = enhancer.enhance(1.08)

            output = io.BytesIO()
            format_name = "PNG" if img.mode == "RGBA" else "JPEG"
            enhanced.save(output, format=format_name, quality=95)
            return output.getvalue()
        except Exception as e:
            logger.error(f"Upscaling error: {e}")
            return image_bytes
