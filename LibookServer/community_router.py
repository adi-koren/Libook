from fastapi import APIRouter, Request, Depends, HTTPException, status
from pydantic import BaseModel, field_validator
from typing import Optional, Dict
import asyncio

router = APIRouter()

def get_posts_handler(request: Request):
    return request.app.state.posts_db

def get_reviews_handler(request: Request):
    return request.app.state.reviews_db

class PublishPostRequest(BaseModel):
    user_id: str
    username: str
    headline: str
    content: str


@router.get("/community/search")
async def search_community_posts_endpoint(q: str, startIndex: int, posts_db=Depends(get_posts_handler)):
    posts = await posts_db.search_posts(q, startIndex)

    if not posts:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="No posts found"
        )

    return posts


@router.get("/community/{post_id}")
async def post_info_endpoint(post_id: str, user_id: str,
                             posts_db=Depends(get_posts_handler),
                             reviews_db=Depends(get_reviews_handler)):
    post_task = asyncio.create_task(posts_db.fetch_post(post_id))
    reviews_task = asyncio.create_task(reviews_db.fetch_item_reviews(post_id, user_id))
    user_review_task = asyncio.create_task(reviews_db.fetch_user_review(post_id, user_id))
    stats_task = asyncio.create_task(reviews_db.fetch_rating_stats(post_id))

    post_info, reviews, user_review, stats = await asyncio.gather(
    post_task,
    reviews_task,
    user_review_task,
    stats_task
    )

    return {**post_info,
            "reviews": reviews,
            "user_review": user_review,
            "rating_stats": stats}


@router.get("/community/user/{user_id}")
async def get_user_posts_endpoint(user_id: str, posts_db=Depends(get_posts_handler)):
    posts = await posts_db.fetch_user_posts(user_id)

    return posts


@router.post("/community/publish")
async def publish_post_endpoint(body: PublishPostRequest, posts_db=Depends(get_posts_handler)):
    post_id = await posts_db.publish_post(body.user_id, body.username, body.headline, body.content)
    return post_id

@router.delete("/community/{post_id}")
async def delete_post_endpoint(post_id: str, posts_db=Depends(get_posts_handler)):
    await posts_db.delete_post(post_id)
