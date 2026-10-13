# Mildy AI Backend
**PALIMSON Group**

Выделенный сервер искусственного интеллекта для мобильного приложения **Mildy (GGS Creator)**.

## Возможности
1. **Автоматическая транскрибация речи (Whisper)**:
   - Поддержка казахского (`kk`), русского (`ru`), английского (`en`) языков и автоопределения (`auto`).
   - Пословные временные метки (`word-level timestamps`) для караоке-анимации.
   - Фильтрация голосовой активности (VAD).
2. **Сегментация и удаление фона (RemBG u2net)**:
   - Отделение людей и предметов от фона.
   - Возврат альфа-канала высокого качества (PNG).
3. **2x Super-Resolution & Micro-Detail Sharpening**:
   - Восстановление субпиксельных деталей и подавление артефактов сжатия.
4. **Извлечение аудио и медиа-конвертация**:
   - Извлечение дорожек без потерь качества.

## Запуск в разработке
```bash
# 1. Быстрый локальный запуск (без внешних зависимостей)
python3 backend/server_local.py

# 2. Полноценный запуск с моделями Whisper и RemBG
pip install -r backend/requirements.txt
uvicorn backend.main:app --host 0.0.0.0 --port 8000 --reload
```

## Docker
```bash
docker build -t mildy-ai-backend backend/
docker run -p 8000:8000 mildy-ai-backend
```
