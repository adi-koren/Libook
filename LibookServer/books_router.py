from fastapi import APIRouter, Request, Depends, HTTPException, status
from pydantic import BaseModel, field_validator
from typing import Optional, Dict

router = APIRouter()

def get_books_service(request: Request):
    return request.app.state.books_service

class SearchRequest(BaseModel):
    q: Optional[str] = None
    q_inter: Optional[Dict[str, str]] = None
    startIndex: int = 0

    @field_validator("q_inter", mode="before")
    @classmethod
    def at_least_one_q(cls, v, info):
        data = info.data
        if not data.get("q") and not v:
            raise ValueError("Either 'q' or 'q_inter' must be provided")
        return v

@router.post("/books/search")
def search_books(body: SearchRequest, bs=Depends(get_books_service)):
    try:
        books = bs.search_books(body.q, body.q_inter, body.startIndex)
    except RuntimeError as e:
        raise HTTPException(
            status_code=status.HTTP_502_BAD_GATEWAY,
            detail=str(e)
        )

    if not books:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="No books found"
        )

    return books


@router.get("/books/{book_id}")
def book_info_endpoint(book_id: str, bs = Depends(get_books_service)):
    try:
        book_info = bs.get_book_info(book_id)
    except RuntimeError as e:
        raise HTTPException(
            status_code=status.HTTP_502_BAD_GATEWAY,
            detail=str(e)
        )

    if not book_info:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="book not found"
        )

    return book_info
