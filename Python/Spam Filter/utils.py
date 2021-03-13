from random import choice

POS_TAG = "SPAM"
NEG_TAG = "OK"


def read_classification_from_file(path):
	"""reads classification of mails from file and returns a dict"""
	spam_dict = {}
	with open(path, 'r', encoding='utf-8') as f:
		for line in f:
			mail, spam = line.strip().split(" ", 1) # add split on first space
			spam_dict[mail] = spam
	return spam_dict


def write_classification_to_file(path, spam_dict):
	"""writes classification of mails from dict to file"""
	s = ''
	for k,v in spam_dict.items():
		s += k + ' ' + v + '\n'
	with open(path, 'w', encoding='utf-8') as f:
			f.write(s)


def count_rows_and_words(path):
    n_lines = 0
    n_words = 0
    with open(path, 'r', encoding='utf-8') as f:
        for line in f:
            n_lines += 1
            l = line.strip().split()
            n_words += len(l)
    return (n_lines, n_words)


def mark_all_in_dict(dictionary, tag = None):

	if tag:
	    return {k:tag for k in dictionary.keys()}
	else:
		return {k:choice((POS_TAG, NEG_TAG)) for k in dictionary.keys()}


def is_neutral_word(word):
	"""
	checks if the given word is one of the neutral words
	"""
	neutral_words= [
		'the', 'of', 'to', 'and', 'a', 'in', 'is', 'it', 'you', 'that', 'he', 'was', 
		'for', 'on', 'are', 'with', 'as', 'I', 'his', 'they', 'be', 'at', 'one', 
		'have', 'this', 'from', 'or', 'had', 'by', 'not', 'word', 'but', 'what', 
		'some', 'we', 'can', 'out', 'other', 'were', 'all', 'there', 'when', 'up', 
		'use', 'your', 'how', 'said', 'an', 'each', 'she', 'which', 'do', 'their', 
		'time', 'if', 'will', 'way', 'about', 'many', 'then', 'them', 'write', 'would', 
		'like', 'so', 'these', 'her', 'long', 'make', 'thing', 'see', 'him', 'two', 
		'has', 'look', 'more', 'day', 'could', 'go', 'come', 'did', 'number', 'sound', 
		'no', 'most', 'people', 'my', 'over', 'know', 'water', 'than', 'call', 'first', 
		'who', 'may', 'down', 'side', 'been', 'now', 'find'
	]
	if word in neutral_words:
		return True
	else:
		return False


def print_dict(d):
    print("\n------ HEADERS DICT ------")
    for k,v in d.items():
        print(f"{k} = {v}")
    print("----------------------\n")
