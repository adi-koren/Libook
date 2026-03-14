import requests

FUNC_SEARCH_BOOK = 1
FUNC_BOOK_INFO = 2

class OpenLibraryService:
    BASE_API_URL = "https://openlibrary.org"
    BASE_IMAGE_URL_BEG = "https://covers.openlibrary.org/b/id/"
    BASE_IMAGE_URL_END = "-L.jpg"

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
        data_list = response.json().get("docs", [])
        if len(data_list) == 0:
            return []

        for book in data_list:
            cover_id = book.get("cover_i")
            if cover_id:
                image_url = self.BASE_IMAGE_URL_BEG + str(cover_id) + self.BASE_IMAGE_URL_END
            else:
                image_url = "Unknown cover"

            book_list.append({"id": book.get("key").split("/")[-1],
                              "title": book.get("title", "Unknown title"),
                              "authors": book.get("author_name", ["Unknown author"]),
                              "image": image_url})
        return book_list



    def __format_book_info_result(self, response):
        work = response.json()
        title = work.get("title", "Unknown title")

        description = work.get("description", "couldn't find description")
        if isinstance(description, dict):
            description = description.get("value", "couldn't find description")

        subject = (work.get("subjects") or ["Unknown subjects"])[0]

        author_name = None
        authors = work.get("authors", [])
        if authors:
            key = authors[0].get("author", {}).get("key", None)
            author_name = self.__get_author_name(key)
        if not author_name:
            author_name = "Unknown author"

        covers = work.get("covers")
        if covers and len(covers) > 0:
            cover_url = self.BASE_IMAGE_URL_BEG + str(covers[0]) + self.BASE_IMAGE_URL_END
        else:
            cover_url = "Unknown cover"

        return {
            "id": work.get("key", "").split("/")[-1],
            "title": title,
            "authors": [author_name],
            "image": cover_url,
            "description": description,
            "subjects": subject
        }


    def __get_author_name(self, author_key):
        try:
            url = f"{self.BASE_API_URL}{author_key}.json"
            response = requests.get(url, timeout=5)
            if response.status_code == 200:
                author_data = response.json()
                return author_data.get("name", "Unknown author")
        except requests.exceptions.RequestException:
            raise RuntimeError("Cannot reach Open Library (check server internet connection)")
        except:
            pass
        return "Unknown author"


    def __send_api_request(self, url: str, params, func: int):
        try:
            response = requests.get(url, params=params, timeout=5)
        except requests.exceptions.RequestException:
            raise RuntimeError("Cannot reach Open Library (check server internet connection)")


        if response.status_code != 200:
            raise RuntimeError(f"Open library error: {response.status_code}")

        if func == FUNC_SEARCH_BOOK:
            return self.__format_search_result(response)
        else:
            return self.__format_book_info_result(response)


    def __build_search_query(self, q_dict):
        return " ".join(f"{k}:{v}" for k, v in q_dict.items())# + f"&fields={fields}"
