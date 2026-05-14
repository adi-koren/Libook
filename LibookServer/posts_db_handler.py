import aiosqlite

class PostsHandler:

    def __init__(self, db_path: str):
        self.db_path = db_path

    async def init(self):
        async with aiosqlite.connect(self.db_path) as db:
            await db.executescript("""
            CREATE TABLE IF NOT EXISTS posts (
            post_id INTEGER PRIMARY KEY AUTOINCREMENT, 
            user_id TEXT NOT NULL, 
            username TEXT NOT NULL, 
            headline TEXT NOT NULL, 
            content TEXT NOT NULL, 
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP
            );

            CREATE VIRTUAL TABLE IF NOT EXISTS posts_fts USING fts5(
            username, 
            headline, 
            content, 
            content='posts', 
            content_rowid='rowid'
            );

            CREATE TRIGGER IF NOT EXISTS posts_ai 
            AFTER INSERT ON posts 
            BEGIN 
                INSERT INTO posts_fts(rowid, username, headline, content)
                VALUES (new.rowid, new.username, new.headline, new.content);
            END;

            CREATE TRIGGER IF NOT EXISTS posts_au 
            AFTER UPDATE ON posts 
            BEGIN 
                UPDATE posts_fts
                SET username = new.username,
                headline = new.headline,
                content = new.content
                WHERE rowid = new.rowid;
            END;

            CREATE TRIGGER IF NOT EXISTS posts_ad 
            AFTER DELETE ON posts 
            BEGIN 
                INSERT INTO posts_fts(posts_fts, rowid, username, headline, content)
                VALUES('delete', old.rowid, old.username, old.headline, old.content);
            END;
            
            CREATE TRIGGER IF NOT EXISTS delete_post_reviews 
            AFTER DELETE ON posts 
            BEGIN 
                DELETE FROM reviews 
                WHERE item_id = old.post_id; 
            END;
            """)
            await db.commit()

    async def search_posts(self, q: str, startIndex: int):
        async with aiosqlite.connect(self.db_path) as db:
            if not q or not q.strip():
                cursor = await db.execute("""
                    SELECT post_id, username, headline, created_at
                    FROM posts 
                    ORDER BY created_at DESC
                    LIMIT 10 OFFSET ?;""", (startIndex,))

            else:
                q = " ".join(word + "*" for word in q.split())
                cursor = await db.execute("""
                    SELECT p.post_id, p.username, p.headline, p.created_at
                    FROM posts p
                    JOIN posts_fts fts ON p.rowid = fts.rowid
                    WHERE posts_fts MATCH ?
                    ORDER BY rank
                    LIMIT 10 OFFSET ?;""", (q, startIndex))

            posts = []
            async for row in cursor:
                post_id, username, headline, created_at = row
                posts.append({
                    "post_id": str(post_id),
                    "username": username,
                    "headline": headline,
                    "created_at": created_at
                })

            await cursor.close()
            return posts

    async def fetch_post(self, post_id: str):
        async with aiosqlite.connect(self.db_path) as db:
            cursor = await db.execute(
                """SELECT user_id, username, headline, content, created_at FROM posts 
                WHERE post_id = ?;""",
                (post_id,)
            )

            row = await cursor.fetchone()
            await cursor.close()

            if not row:
                return None  # post doesn't exist

            user_id, username, headline, content, created_at = row

            return {"post_id": post_id,
                    "user_id": user_id,
                    "username": username,
                    "headline": headline,
                    "content": content,
                    "created_at": created_at}


    async def fetch_user_posts(self, user_id: str):
        async with aiosqlite.connect(self.db_path) as db:
            cursor = await db.execute("""
                SELECT post_id, username, headline, created_at
                FROM posts 
                WHERE user_id = ?  
                ORDER BY created_at DESC;""", (user_id,))

            posts = []
            async for row in cursor:
                post_id, username, headline, created_at = row
                posts.append({
                    "post_id": str(post_id),
                    "username": username,
                    "headline": headline,
                    "created_at": created_at
                })

            await cursor.close()
            return posts

    async def publish_post(self, user_id: str, username: str,
                           headline: str, content: str):
        async with aiosqlite.connect(self.db_path) as db:
            cursor = await db.execute(
                """INSERT INTO posts (user_id, username, headline, content) 
                VALUES (?, ?, ?, ?)""",
                (user_id, username, headline, content)
            )
            await db.commit()

            return cursor.lastrowid


    async def delete_post(self, post_id: str):
        async with aiosqlite.connect(self.db_path) as db:
            await db.execute(
                "DELETE FROM posts WHERE post_id = ?;",
                (post_id,)
            )
            await db.commit()
