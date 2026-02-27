import requests

FUNC_SEARCH_BOOK = 1
FUNC_BOOK_INFO = 2

class OpenLibraryService:
    BASE_API_URL = "https://openlibrary.org"
    BASE_IMAGE_URL_BEG = "https://covers.openlibrary.org/b/id/"
    BASE_IMAGE_URL_END = "-M.jpg"

    def search_books(self, search_fields, q_inter, start_index):
        params = {"q": (search_fields or "") + self.__build_search_query(q_inter or {}),
                  "fields": "key,title,author_name,cover_i",
                  "offset": start_index,
                  "limit": 10}

        return self.__send_api_request(self.BASE_API_URL + "/search.json", params, FUNC_SEARCH_BOOK)


    def get_book_info(self, book_id):
        url = self.BASE_API_URL + "/works/" + book_id + ".json"

        return self.__send_api_request(url, None, FUNC_BOOK_INFO)


    def __format_search_result(self, response):
        book_list = list()
        try:
            data_list = response.json().get("docs", [])
            for book in data_list:
                cover_id = book.get("cover_i")
                if cover_id:
                    image_url = self.BASE_IMAGE_URL_BEG + str(cover_id) + self.BASE_IMAGE_URL_END
                else:
                    image_url = "ERROR"

                book_list.append({"id": book.get("key").split("/")[-1],
                                  "title": book.get("title", "ERROR"),
                                  "authors": book.get("author_name", ["Unknown author"]),
                                  "image": image_url})
            return book_list

        except:
            return "ERROR: something went wrong while formatting search result"


    def __format_book_info_result(self, response):
        try:
            book = response.json()

            description = book.get("description", "couldn't find description")
            if isinstance(description, dict):
                description = description.get("value", "couldn't find description")

            book_info = {"publishedDate": (book.get("subjects") or ["no subject"])[0],
                         "description": description}

            return book_info

        except:
            return "ERROR: something went wrong while formatting book info result"

    def __send_api_request(self, url: str, params, func: int):
        response = requests.get(url, params=params, timeout=5)

        if response.status_code != 200:
            raise RuntimeError(f"Open library error: {response.status_code}")

        if func == FUNC_SEARCH_BOOK:
            return self.__format_search_result(response)
        else:
            return self.__format_book_info_result(response)


    def __build_search_query(self, q_dict):
        return " ".join(f"{k}={v}" for k, v in q_dict.items())# + f"&fields={fields}"
