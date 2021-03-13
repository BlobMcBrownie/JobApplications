from basefilter import BaseFilter
from utils import write_classification_to_file, mark_all_in_dict


class NaiveFilter(BaseFilter):
    """all emails OK"""

    def test(self, path):
        super().test(path)
        self.mail_dict = mark_all_in_dict(self.mail_dict, "OK")
        write_classification_to_file(path + "/!prediction.txt", self.mail_dict)


class ParanoidFilter(BaseFilter):
    """all emails SPAM"""

    def test(self, path):
        super().test(path)
        self.mail_dict = mark_all_in_dict(self.mail_dict, "SPAM")
        write_classification_to_file(path + "/!prediction.txt", self.mail_dict)


class RandomFilter(BaseFilter):
    """random"""

    def test(self, path):
        super().test(path)
        self.mail_dict = mark_all_in_dict(self.mail_dict)
        write_classification_to_file(path + "/!prediction.txt", self.mail_dict)


if __name__ == '__main__':
    # nf = NaiveFilter()
    # nf.train("emails")
    # nf.test("emails")

    # pf = ParanoidFilter()
    # pf.train("emails")
    # pf.test("emails")

    rf = RandomFilter()
    rf.train("emails")
    rf.test("emails")
