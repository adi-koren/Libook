import requests

FUNC_SEARCH_BOOK = 1
FUNC_BOOK_INFO = 2

class GoogleBooksService:
    BASE_API_URL = "https://www.googleapis.com/books/v1/volumes"

    def __init__(self, api_key: str):
        self._API_KEY = api_key

    #inner: filtering (intitle, inauthor...)
    def search_books(self, q, startIndex):
        params = {"q": self.__build_search_query(q),
                  "fields": "items(id, volumeInfo(title, authors, imageLinks/thumbnail))",
                  "maxResults": 10,
                  "startIndex": startIndex,
                  "key": self._API_KEY}

        return self.__send_api_request(self.BASE_API_URL, params, FUNC_SEARCH_BOOK)

    #inner:   book_id
    def get_book_info(self, book_id):
        url = self.BASE_API_URL + '/' + book_id
        params = {
            "fields": "id, volumeInfo(title, authors, publishedDate, description, imageLinks/thumbnail, industryIdentifiers)",
            "key": self._API_KEY}

        return self.__send_api_request(url, params, FUNC_BOOK_INFO)


    #private help functions
    def __send_api_request(self, url: str, params, func: int):
        response = requests.get(url, params=params)

        if response.status_code == 200:
            return [response.status_code,
                        self.__format_search_result(response) if func == FUNC_SEARCH_BOOK
                        else self.__format_book_info_result(response)]
        else:
            return [response.status_code]


    def __format_search_result(self, response):
        book_list = list()
        try:
            data_list = response.json()["items"]
            for book in data_list:
                book_list.append({"id": book.get("id", "ERROR"),
                                  "title": book["volumeInfo"].get("title", "ERROR"),
                                  "authors": book["volumeInfo"].get("authors", "ERROR"),
                                  "image": book["volumeInfo"].get("imageLinks", {}).get("thumbnail", "ERROR")})
            return book_list

        except:
            return "ERROR: something went wrong while formatting search result"


    def __format_book_info_result(self, response):
        book_info = list()
        try:
            book = response.json()
            book_info.append({"id": book.get("id", "ERROR"),
                              "title": book["volumeInfo"].get("title", "ERROR"),
                              "authors": book["volumeInfo"].get("authors", "ERROR"),
                              "publishedDate": book["volumeInfo"].get("publishedDate", "ERROR"),
                              "description": book["volumeInfo"].get("description", "ERROR"),
                              "image": book["volumeInfo"].get("imageLinks", {}).get("thumbnail", "ERROR"),
                              "industryIdentifiers": book["volumeInfo"].get("industryIdentifiers", "ERROR")})
            return book_info

        except:
            return "ERROR: something went wrong while formatting book info result"


    def __build_search_query(self, q_dict):
        return "&".join(f"{k}:{v}" for k, v in q_dict.items())# + f"&fields={fields}"
