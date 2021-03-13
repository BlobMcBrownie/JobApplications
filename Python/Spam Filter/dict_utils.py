from collections import Counter
from utils import is_neutral_word


def sort_dic_by_most_common(dic, show_first_n=None):
	return Counter(dic).most_common(show_first_n)


def print_dict(d):
    print("\n------ HEADERS DICT ------")
    for k,v in d.items():
        print(f"{k} = {v}")
    print("----------------------\n")


def print_list_of_dict(list_):
	print("\n------ LIST OF DICT ------")
	for el in list_:
		print(el)
	print("----------------------\n")


def print_body(words, symbols, most_common_n=None):
	print("\n------ BODY ------")
	print("----------------------\n")
	print("\n------ WORDS COUNT ------")
	print(sort_dic_by_most_common(words, most_common_n))
	print("\n------ SYMBOLS COUNT ------")
	print(sort_dic_by_most_common(symbols, most_common_n))
	print("----------------------\n")


def too_many_symbols(key):
	symb = ["-", "_", "+", "*", "/", ",", "'",'"',".", ":","!","?","(",")","[","]","{","}","|","~"]
	for s in symb:
		if key.count(s) > 2:
			return True
	return False


def only_int(key):
	try: 
		int(key)
	except:
		return False
	else:
		return True
	


def filter_dict(dic):
	"""
	filters dict from certain items and creates a new one
	---> our aim is to filter it from the words/symbols that appear both in SPAM and HAM
	right now filtering from: "neutral words", ... specific chars (_)chtelo by to
	"""
	new_dict = {}
	for key, value in dic.items():
		if len(key) > 25:
			continue
		elif not key:
			continue
		elif is_neutral_word(key):
			continue
		elif too_many_symbols(key):
			continue
		elif "http" in key.lower():
			continue
		elif only_int(key):
			continue
		else:
			new_dict[key] = value
	return new_dict

def brutal_filter(word_list_of_dict):
	MIN_OCCURENCE = 10
	MIN_SPAM = 0.4
	MAX_SPAM = 0.6

	new = []
	for dic in word_list_of_dict:
		if dic["spam"] + dic["ham"] < MIN_OCCURENCE:
			continue
		elif dic["spam_perc"] < MIN_SPAM or dic["spam_perc"] > MAX_SPAM:
			new.append(dic)
	return new
