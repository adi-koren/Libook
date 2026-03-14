import os
from fastapi import FastAPI
from dotenv import load_dotenv
import asyncio
import httpx
import aiosqlite
import time
from google_books_service import GoogleBooksService
from open_library_service import OpenLibraryService
from cache_db_handler import CacheHandler
from reviews_db_handler import ReviewsHandler

from books_router import router as books_router

load_dotenv()
API_KEY = os.getenv("API_KEY")

app = FastAPI()

@app.on_event("startup")
async def startup():
    app.state.cache_db = CacheHandler("cache.db")
    app.state.reviews_db = ReviewsHandler("reviews.db")
    await app.state.cache_db.init()
    await app.state.reviews_db.init()

#app.state.books_service = GoogleBooksService(api_key=API_KEY)
app.state.books_service = OpenLibraryService()
app.include_router(books_router)

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)
