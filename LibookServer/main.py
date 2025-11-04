from dotenv import load_dotenv
import os
import requests
from google_books_service import GoogleBooksService

load_dotenv()
API_KEY = os.getenv("API_KEY")
API_URL = "https://www.googleapis.com/books/v1/volumes"


if __name__ == '__main__':
    api_handler = GoogleBooksService(API_KEY)
    #inner = {"intitle": "harry"}
    #response = api_handler.search_books(inner, 0)

    response = api_handler.get_book_info("GZAoAQAAIAAJ")

    print(f"response code: {response[0]}\nbook info:\n{response[1]}")
