from fastapi import APIRouter, Request, Depends, HTTPException, status
from pydantic import BaseModel, field_validator
from typing import Optional, Dict
import asyncio

router = APIRouter()

def get_reviews_handler(request: Request):
    return request.app.state.reviews_db

class ReviewRequest(BaseModel):
    user_id: str
    username: str
    comment: str
    rating: int

@router.post("/reviews/{item_id}")
async def add_review_endpoint(item_id: str, body: ReviewRequest, reviews_db=Depends(get_reviews_handler)):
    try:
        await reviews_db.add_comment_to_item(item_id, body.user_id,
                                             body.username, body.comment, body.rating)
        stats = await reviews_db.fetch_rating_stats(item_id)

    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=str(e)
        )

    return stats


@router.delete("/reviews/{item_id}")
async def delete_review_endpoint(item_id: str, user_id: str,
                                 reviews_db=Depends(get_reviews_handler)):
    try:
        await reviews_db.delete_review(item_id, user_id)
        stats = await reviews_db.fetch_rating_stats(item_id)

    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=str(e)
        )

    return stats
