from fastapi import APIRouter, Request, Depends, HTTPException, status
from pydantic import BaseModel, field_validator
from typing import Optional, Dict
import asyncio

router = APIRouter()

def get_books_service(request: Request):
    return request.app.state.books_service

def get_cache_handler(request: Request):
    return request.app.state.cache_db

def get_reviews_handler(request: Request):
    return request.app.state.reviews_db

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
def search_books_endpoint(body: SearchRequest, bs=Depends(get_books_service)):
    print(body.q_inter)
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
async def book_info_endpoint(book_id: str, user_id: str, bs=Depends(get_books_service),
                             cache_db=Depends(get_cache_handler), reviews_db=Depends(get_reviews_handler)):
    cache_task = asyncio.create_task(cache_db.fetch_book(book_id))
    reviews_task = asyncio.create_task(reviews_db.fetch_item_reviews(book_id, user_id))
    user_review_task = asyncio.create_task(reviews_db.fetch_user_review(book_id, user_id))
    stats_task = asyncio.create_task(reviews_db.fetch_rating_stats(book_id))

    book_info = await cache_task

    if book_info is None:
        try:
            book_info = bs.get_book_info(book_id)

        except RuntimeError as e:
            raise HTTPException(
                status_code=status.HTTP_502_BAD_GATEWAY,
                detail=str(e)
        )

        await cache_db.store_book(book_id, book_info)

    reviews, user_review, stats = await asyncio.gather(
    reviews_task,
    user_review_task,
    stats_task
    )

    return {**book_info,
            "reviews": reviews,
            "user_review": user_review,
            "rating_stats": stats}

