import aiosqlite
import json
import time

class DatabaseHandler:
    CACHE_TTL_SECONDS = 60 * 60 * 24  # 24 hours

    def __init__(self, db_path: str):
        self.db_path = db_path

    async def init(self):
        async with aiosqlite.connect(self.db_path) as db:
            await db.execute("""
                CREATE TABLE IF NOT EXISTS books_cache (
                    book_id TEXT PRIMARY KEY,
                    data TEXT NOT NULL,
                    last_updated INTEGER NOT NULL
                );
            """)
            await db.commit()

    async def fetch_book(self, book_id: str):
        async with aiosqlite.connect(self.db_path) as db:
            cursor = await db.execute(
                "SELECT data, last_updated FROM books_cache WHERE book_id = ?",
                (book_id,)
            )
            row = await cursor.fetchone()
            await cursor.close()

            if not row:
                return None  # doesn't exist in cache

            data_json, updated_at = row

            # check if still valid
            if time.time() - updated_at > self.CACHE_TTL_SECONDS:
                return None  # cached but passed the time

            return json.loads(data_json)

    async def store_book(self, book_id: str, data: dict):
        async with aiosqlite.connect(self.db_path) as db:
            await db.execute(
                """INSERT OR REPLACE INTO books_cache (book_id, data, last_updated) 
                VALUES (?, ?, ?)""",
                (book_id, json.dumps(data), int(time.time()))
            )
            await db.commit()
