import os
from fastapi import FastAPI
from dotenv import load_dotenv
from google_books_service import GoogleBooksService

from books_router import router as books_router

load_dotenv()
API_KEY = os.getenv("API_KEY")

app = FastAPI()
app.state.google_books_service = GoogleBooksService(api_key=API_KEY)
app.include_router(books_router)

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="127.0.0.1", port=8000, reload=True)
