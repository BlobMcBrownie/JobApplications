import csv
from file_utils import (normalize_word, symbols_to_list_of_dict,
    is_file_spam, symbols_increment_in_dict, symbols_append_to_dict)
from collections import Counter
from file_utils import normalize_word

def subject_counter_to_dict(subject):
    """
    normalizes subject and
    makes a counter of the words in subject
    """
    subject = normalize_word(subject)
    subject = subject.strip().split()
    c = Counter(subject)
    c = {k:v for k,v in c.items()}
    return c



if __name__=="__main__":
    # save_subject_to_list("Life! Insurance - Why] [ Pay ( More?", [])

    fname = "0001.bfc8d64d12b325ff385cca8d07b84288"
    path_to_truth = "Testing_data/1_copy/!truth.txt"

    tmp = []

    s = subject_counter_to_dict("Life! Insurance - Why] [ Pay ( More?")
    s2 = subject_counter_to_dict(" Why do you Pay!! Life is short :(")

    print("------- FIRST LIST ------")
    l = symbols_to_list_of_dict(s, tmp, fname, True)
    for el in l:
        print(el)
    print("------- SECOND LIST UPDATE -----")
    l2 = symbols_to_list_of_dict(s2, tmp, fname, True)
    for el in l2:
        print(el)


    # c = subject_counter_to_dict("Life! Insurance - Why] [ Pay ( More?")
    # print(c)
