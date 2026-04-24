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
from posts_db_handler import PostsHandler

from books_router import router as books_router
from community_router import router as community_router
from reviews_router import router as reviews_router

load_dotenv()
API_KEY = os.getenv("API_KEY")

app = FastAPI()

@app.on_event("startup")
async def startup():
    app.state.cache_db = CacheHandler("cache.db")
    app.state.reviews_db = ReviewsHandler("libook.db")
    app.state.posts_db = PostsHandler("libook.db")
    await app.state.cache_db.init()
    await app.state.reviews_db.init()
    await app.state.posts_db.init()

#app.state.books_service = GoogleBooksService(api_key=API_KEY)
app.state.books_service = OpenLibraryService()
app.include_router(books_router)
app.include_router(community_router)
app.include_router(reviews_router)

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)
