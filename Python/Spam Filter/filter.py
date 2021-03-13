from basefilter import BaseFilter
from normalise import get_file_headers, leave_only_body, remove_html
from file_utils import (is_file_spam,
    symbols_to_list_of_dict,
    words_count_to_dict, 
    spec_symbols_count_to_dict,
    read_dicts_from_csv, 
    write_dicts_to_csv)
from utils import read_classification_from_file, write_classification_to_file
from dict_utils import filter_dict, sort_dic_by_most_common, brutal_filter
from subject import subject_counter_to_dict
from os import listdir

# from time import time

# from quality import compute_quality_for_corpus

IS_SPAM_CONST = 0.77

class MyFilter(BaseFilter):
    """
    Vojtech Sykora and Miroslav Falcmann - SPAM FILTER

    We normalise each email extracting headers into a dict
    We work with body:
        * Count all symbols and words.
        * We filter the words we get so that we don't save all nonsense.
        * We add the obtained knowledge to our 3 csv files with columns for:
            - "symbol" = the word/symbol
            - "spam" = number of times the symbol/word was found in a SPAM 
                        email according to !truth.txt
            - "ham" = number of times the symbol/word was found in a OK 
                        email according to !truth.txt
            - "spam_perc" = spam/(spam + ham)
    We test using the information from our csv files in the knowledge folder.
    """

    def train(self, path):
        # s1 = time()
        # creates datasets which we will work with later
        self.symbol_list_of_dict = read_dicts_from_csv("knowledge/symbols.csv")
        self.word_list_of_dict = read_dicts_from_csv("knowledge/words.csv")
        self.subject_list_of_dict = read_dicts_from_csv("knowledge/subject.csv")
        self.class_dict = read_classification_from_file(path + '/!truth.txt')
        email_names = [n for n in self.class_dict.keys()]

        for email in email_names:
            file = path + '/' + email
            is_spam = is_file_spam(email, self.class_dict)

            head_dict = get_file_headers(file)
            leave_only_body(file)
            remove_html(file)

            # makes dictionary from words and symbols in body
            w = words_count_to_dict(file)
            w = filter_dict(w)
            s = spec_symbols_count_to_dict(file)

            # makes list from dictionary (needed for correct input form)
            w = sort_dic_by_most_common(w)
            s = sort_dic_by_most_common(s)

            # update overall symb and word list of dict
            self.symbol_list_of_dict = symbols_to_list_of_dict(s, self.symbol_list_of_dict, email, is_spam)
            self.word_list_of_dict = symbols_to_list_of_dict(w, self.word_list_of_dict, email, is_spam)

            # Working with the subject header
            subject = head_dict.get("Subject")
            if subject:
                subj = subject_counter_to_dict(subject)
                subj = filter_dict(subj)
                subj = sort_dic_by_most_common(subj)
                self.subject_list_of_dict = symbols_to_list_of_dict(subj, self.subject_list_of_dict, email, is_spam)

        # advanced filter of words
        self.word_list_of_dict = brutal_filter(self.word_list_of_dict)

        # write knowledge to csv
        write_dicts_to_csv(self.word_list_of_dict, "knowledge/words.csv")
        write_dicts_to_csv(self.symbol_list_of_dict, "knowledge/symbols.csv")
        write_dicts_to_csv(self.subject_list_of_dict, "knowledge/subject.csv")

        # s2 = time()
        # print(f"Train ran for {s2 - s1} sec.")


    def test(self, path):
        # import previously earned knowledge from csv files
        prediction_dict = {}
        subject_csv_dict = read_dicts_from_csv("knowledge/subject.csv", only_ad = True)
        words_csv_dict = read_dicts_from_csv("knowledge/words.csv", only_ad = True)
        symbols_csv_dict = read_dicts_from_csv("knowledge/symbols.csv", only_ad = True)

        for email in [n for n in listdir(path) if n[0] != '!' and n != 'desktop.ini']:
            file = path + '/' + email

            head_dict = get_file_headers(file)
            leave_only_body(file)
            remove_html(file)

            # makes dictionary from words and symbols in body
            w = words_count_to_dict(file)
            w = filter_dict(w)
            s = spec_symbols_count_to_dict(file)

            # Working with the subject header
            subject = head_dict.get("Subject")
            if subject:
                subj = subject_counter_to_dict(subject)
                subj = filter_dict(subj)

            # ------ INCREMENTING THE SPAM SCORE ------
            n = 0
            words_count = 0
            overall_score = 0
            
            if subject:
                for key, value in subj.items():
                    for el in subject_csv_dict:
                        if key == el['symbol']:
                            overall_score += el['spam_perc'] * value
                            words_count += (1 * value)
            
            for key, value in s.items():
                for el in symbols_csv_dict:
                    if key == el['symbol']:
                        overall_score += el['spam_perc'] * value
                        words_count += (1 * value)

            for key, value in w.items():
                for el in words_csv_dict:
                    if key == el['symbol']:
                        overall_score += el['spam_perc'] * value
                        words_count += (1 * value)
            #-------------------------------------------------------------


            # ----- DECIDE WHETHER ITS SPAM OR NOT ------
            if words_count != 0:
                n = overall_score / words_count # SPAM PROBABILITY
            if n > IS_SPAM_CONST: 
                prediction_dict[email] = 'SPAM'
            else:
                prediction_dict[email] = 'OK'

        write_classification_to_file(path + '/!prediction.txt', prediction_dict)


if __name__ == "__main__":
    f = MyFilter()
    cesta_train = "data/2copy"
    cesta_test = "data/testing/2copy"
    
    # cesta_train = "Testing_data/1copy"
    # f.train(cesta_train)

    # cesta_train = "Testing_data/2copy"
    # f.train(cesta_train)

    # print(f"IS_SPAM_CONST = {IS_SPAM_CONST}")

    # cesta_test = "Testing_data/2test"
    # print(f"\nTesting {cesta_test}")
    # f.test(cesta_test)
    # print(f"Quality of {cesta_test} is {compute_quality_for_corpus(cesta_test)}")

    # cesta_test = "Testing_data/1copy"
    # print(f"\nTesting {cesta_test}")
    # f.test(cesta_test)
    # print(f"Quality of {cesta_test} is {compute_quality_for_corpus(cesta_test)}")
    