import aiosqlite

class ReviewsHandler:

    def __init__(self, db_path: str):
        self.db_path = db_path

    async def init(self):
        async with aiosqlite.connect(self.db_path) as db:
            await db.execute("""
                CREATE TABLE IF NOT EXISTS reviews (
                    review_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    book_id TEXT NOT NULL,
                    user_id TEXT NOT NULL,
                    username TEXT NOT NULL,
                    comment TEXT NOT NULL,
                    rating INTEGER CHECK(rating >= 1 AND rating <= 5),
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    UNIQUE(book_id, user_id)
                );
            """)
            await db.commit()

    async def fetch_book_reviews(self, book_id: str, user_id: str):
        async with aiosqlite.connect(self.db_path) as db:
            cursor = await db.execute(
                """SELECT review_id, username, comment, rating, created_at FROM reviews 
                WHERE book_id = ? AND user_id != ?
                ORDER BY created_at DESC LIMIT 50;""",
                (book_id, user_id)
            )
            reviews = []
            async for row in cursor:
                review_id, username, comment, rating, created_at = row
                reviews.append({"review_id": review_id,
                                "username": username,
                                "comment": comment,
                                "rating": rating,
                                "created_at": created_at})
            await cursor.close()

            return reviews

    async def fetch_user_review(self, book_id: str, user_id: str):
        async with aiosqlite.connect(self.db_path) as db:
            cursor = await db.execute(
                """SELECT review_id, username, comment, rating, created_at FROM reviews 
                WHERE book_id = ? AND user_id = ?;""",
                (book_id, user_id)
            )

            row = await cursor.fetchone()
            await cursor.close()

            if not row:
                return None  # user didn't comment

            review_id, username, comment, rating, created_at = row

            return {"review_id": review_id,
                    "username": username,
                    "comment": comment,
                    "rating": rating,
                    "created_at": created_at}

    async def fetch_rating_stats(self, book_id: str):
        async with aiosqlite.connect(self.db_path) as db:

            cursor = await db.execute(
                """SELECT 
                AVG(rating), COUNT(*), 
                SUM(rating = 5), 
                SUM(rating = 4), 
                SUM(rating = 3), 
                SUM(rating = 2), 
                SUM(rating = 1) 
                FROM reviews WHERE book_id = ?""",
                (book_id,)
            )

            row = await cursor.fetchone()
            await cursor.close()

            return {
                "avg_rating": row[0] or 0,
                "total_reviews": row[1] or 0,
                "stars_5": row[2] or 0,
                "stars_4": row[3] or 0,
                "stars_3": row[4] or 0,
                "stars_2": row[5] or 0,
                "stars_1": row[6] or 0,
            }

    async def add_comment_to_book(self, book_id: str, user_id: str,
                                  username: str, comment: str, rating: int):
        async with aiosqlite.connect(self.db_path) as db:
            await db.execute(
                """INSERT OR REPLACE INTO reviews (book_id, user_id, username, comment, rating) 
                VALUES (?, ?, ?, ?, ?)""",
                (book_id, user_id, username, comment, rating)
            )
            await db.commit()

    async def delete_review(self, book_id: str, user_id: str):
        async with aiosqlite.connect(self.db_path) as db:
            await db.execute(
                "DELETE FROM reviews WHERE book_id = ? AND user_id = ?",
                (book_id, user_id)
            )
            await db.commit()
