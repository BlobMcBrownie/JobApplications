import os


def get_file_body(path):
    with open(path, 'r', encoding='utf-8') as f:
        return f.read()


class Corpus(object):
    """docstring for Corpus"""

    def __init__(self, path_to_emails):
        self.path = path_to_emails

    def emails(self):
        """generator"""
        # path = os.getcwd()
        file_list = os.listdir(self.path)
        for filename in file_list:
            if filename[0] == '!':
                continue
            body = get_file_body(self.path + '/' + filename)
            yield filename, body


if __name__ == "__main__":
    c = Corpus('C:/Users/vojsy/RPH/HW/spam_filter/emails')
    count = 0
    # Go through all emails and print the filename and the message body
    for fname, body in c.emails():
        print(fname)
        print(body)
        print('-------------------------')
        count += 1
    print('Finished: ', count, 'files processed.')
