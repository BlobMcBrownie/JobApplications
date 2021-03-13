from collections import Counter
from utils import write_classification_to_file
from dict_utils import print_dict
from os import listdir

# ------------------------- HEADERS -------------------------------------
def get_file_headers(file):
    """
    Puts all email headers as keys and their respective values in a dict
    """
    headers_dict = {}
    last_used_h = None
    with open(file, 'r', encoding='utf-8') as f:
        for line in f:
            if line[0] == '\n':
                break

            line = line.strip().split()
            if len(line) < 2:
                continue

            if line[0][-1] == ':':
                h = line[0][:-1]  # header without :
                last_used_h = h
                line.pop(0)  # get rid of header
                pure_line = " ".join(line)
                headers_dict[h] = pure_line
            else:  # line doesnt start with a header
                new_line = " ".join(line)
                if not new_line:
                    current_line = headers_dict.get(last_used_h)
                    new_line = current_line + " " + new_line
                    headers_dict[last_used_h] = new_line

    return headers_dict


def write_headers_in_file(old_file, new_file):
    headers_dict = get_file_headers(old_file)
    write_classification_to_file(new_file, headers_dict)
# -------------------------------------------------------------------------

# ------------------------- ONLY BODY -------------------------------------
def leave_only_body(old_file, new_file=None):
    """
    deletes all headers from a file which leaves only the body
    if no new_file inputted, it overwrites the same file
    """
    if not new_file:
        new_file = old_file

    body = ""
    is_body = False
    last_char = None

    with open(old_file, 'r', encoding='utf-8') as f:  # get only body into a str
        for char in f.read():
            if not is_body:  # if it is not body
                if char == '\n' and last_char == '\n':
                    is_body = True
                last_char = char
            else:  # we are in body
                body += char

    with open(new_file, 'w', encoding='utf-8') as f:  # write body into new_file
        f.write(body)


def leave_only_body_in_each_file(path):
    for email in listdir(path):
        if email[0] != '!' and email != "desktop.ini":
            email = path + '/' + email
            leave_only_body(email, email)
# -------------------------------------------------------------------------

# ---------------------- REMOVE HTML TAGS ---------------------------------
def remove_html(old_file, new_file = None):
    """
    scans file and creates new without the HTML symbols...
    """
    if not new_file:
        new_file = old_file

    with open(old_file, "r", encoding="utf-8") as f:
        text_correct = ""
        inside_html = 0
        open_br = False
        amp_char = False

        for char in f.read():
            if not inside_html:
                if char == '<':
                    open_br = True
                    inside_html = 1
                elif char == '&':
                    amp_char = True
                    last_char = '&'
                    inside_html = 1
                elif char != '<' and char != '&':
                    text_correct += char

            else:
                if char == '>' and open_br:
                    inside_html = 0
                    open_br = False
                elif char == ';' and amp_char:
                    inside_html = 0
                    amp_char = False
                elif last_char == '&' and char == ' ':
                    inside_html = 0

            last_char = char

    with open(new_file, 'w', encoding="utf-8") as f:
        f.write(text_correct)


def remove_html_in_each_file(path):
    for email in listdir(path):
        if email[0] != '!' and email != "desktop.ini":
            email = path + '/' + email
            remove_html(email, email)
# -------------------------------------------------------------------------


if __name__=="__main__":
    file = "Format dat/1/00425.529f44cda59588d37959083c93a79764"
    x = get_file_headers(file)
    print_dict(x)
