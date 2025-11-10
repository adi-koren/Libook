from fastapi import APIRouter, Request, Depends
from pydantic import BaseModel

router = APIRouter()

def get_google_books_service(request: Request):
    return request.app.state.google_books_service

class SearchRequest(BaseModel):
    q: dict
    startIndex: int = 0

@router.post("/books/search")
def search_endpoint(body: SearchRequest, gb_service = Depends(get_google_books_service)):
    return gb_service.search_books(body.q, body.startIndex)

@router.get("/books/{book_id}")
def book_info_endpoint(book_id: str, gb_service = Depends(get_google_books_service)):
    return gb_service.get_book_info(book_id)
