import os
from utils import write_classification_to_file, mark_all_in_dict


class BaseFilter():
    """docstring for BaseFilter"""

    def train(self, path):
        pass

    def test(self, path):
        mail_list = os.listdir(path)
        mail_list = [m for m in mail_list if m[0] != "!"]
        self.mail_dict = {}.fromkeys(mail_list, None)


if __name__ == "__main__":
    MY_PATH = "emails"
    f = BaseFilter()
    f.train(MY_PATH)
