import io
import logging
from typing import Optional

logger = logging.getLogger(__name__)

class SegmentationService:
    def __init__(self):
        self.rembg_session = None
        self._init_rembg()

    def _init_rembg(self):
        try:
            from rembg import new_session
            logger.info("Initializing RemBG u2net session...")
            self.rembg_session = new_session("u2net")
            logger.info("RemBG session initialized successfully.")
        except Exception as e:
            logger.warning(f"Could not initialize rembg session ({e}). Using advanced PIL edge segmentation.")
            self.rembg_session = None

    def remove_background(self, image_bytes: bytes) -> bytes:
        """
        Removes background from image bytes and returns PNG bytes with transparent alpha channel.
        """
        if self.rembg_session is not None:
            try:
                from rembg import remove
                output_bytes = remove(image_bytes, session=self.rembg_session)
                return output_bytes
            except Exception as e:
                logger.error(f"RemBG execution failed: {e}. Falling back to PIL segmentation.")

        return self._local_pil_segmentation(image_bytes)

    def _local_pil_segmentation(self, image_bytes: bytes) -> bytes:
        try:
            from PIL import Image, ImageFilter
            image = Image.open(io.BytesIO(image_bytes)).convert("RGBA")
            width, height = image.size

            # Sample corner background colors
            pixels = image.load()
            corners = [
                pixels[0, 0],
                pixels[width - 1, 0],
                pixels[0, height - 1],
                pixels[width - 1, height - 1]
            ]
            bg_r = sum(c[0] for c in corners) // 4
            bg_g = sum(c[1] for c in corners) // 4
            bg_b = sum(c[2] for c in corners) // 4

            cx = width / 2.0
            cy = height * 0.45
            max_dist = (cx ** 2 + cy ** 2) ** 0.5

            out_image = Image.new("RGBA", (width, height))
            out_pixels = out_image.load()

            for y in range(height):
                for x in range(width):
                    r, g, b, a = pixels[x, y]
                    color_dist = ((r - bg_r) ** 2 + (g - bg_g) ** 2 + (b - bg_b) ** 2) ** 0.5
                    spatial_dist = ((x - cx) ** 2 + (y - cy) ** 2) ** 0.5
                    center_factor = 1.0 - (spatial_dist / max_dist)

                    if color_dist < 42 and center_factor < 0.72:
                        out_pixels[x, y] = (r, g, b, 0)
                    elif color_dist < 68 and center_factor < 0.6:
                        alpha = int(255 * ((color_dist - 42) / 26))
                        out_pixels[x, y] = (r, g, b, alpha)
                    else:
                        out_pixels[x, y] = (r, g, b, a)

            output = io.BytesIO()
            out_image.save(output, format="PNG")
            return output.getvalue()
        except Exception as e:
            logger.error(f"Fallback segmentation failed: {e}")
            return image_bytes
