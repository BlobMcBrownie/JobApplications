pos_tag = "SPAM"
neg_tag = "OK"
from csv import writer, reader

def normalize_word(word):
    """
    subject = string
    this function gets rid of special symbols
    leaves only clean words
    """
    replace_symb = ["-", "+", "*", "/", ",", "'",'"',".", ":","!","?","(",")",\
        "[","]","{","}","|","=","#","$",">","<"]
    final = ""
    for c in word:
        if c not in replace_symb:
            final += c
    return final.lower()


    
def spec_symbols_count_to_dict(file):
    """
    find "common spam" symbols in file and puts it to dictionary
    """
    spec_symb = ['!','@','#','$','%','^','&','*','[',']','{','}','|','>','<']
    dic = {}
    with open(file, 'r', encoding="utf-8") as f:
        for char in f.read():
            if char in spec_symb:
                if char not in dic:
                    dic[char] = 1
                else:
                    dic[char] += 1
    return dic


def words_count_to_dict(file):
    """
    scans words in a file and puts them NORMALIZED in dictionary
    """
    with open(file, 'r', encoding="utf-8") as f:
        dic = {}
        for word in f.read().split():
            w = normalize_word(word)
            if w not in dic:
                dic[w] = 1
            else:
                dic[w] += 1
    return dic


def words_from_txt_to_list(file):
    """
    created just to extract words from txt to list
    """
    w_list = []
    with open(file,'r',encoding="utf-8") as f:
        for word in f.read().split():
            w_list.append(word)

    return w_list


def is_file_spam(filename, truth_dict):
    tag = truth_dict.pop(filename)
    if tag == "SPAM":
        return True
    return False


def symbols_append_to_dict(curr_list, list_of_all_dict, spam_c, ham_c):
    dic = {}
    dic["symbol"] = curr_list[0]
    dic["spam"] = spam_c
    dic["ham"] = ham_c
    list_of_all_dict.append(dic)
    return list_of_all_dict


def symbols_increment_in_dict(dic, spam_c, ham_c):
    dic["spam"] += spam_c
    dic["ham"] += ham_c
    return dic


def symbols_to_list_of_dict(curr_list_of_symb, list_of_all_dict, filename, is_spam):
    for el in curr_list_of_symb:
        symbol_found = False
        spam = 0
        ham = 0
        
        if is_spam:
            spam = el[1]   # symbol count in current file
        else:
            ham = el[1]    # symbol count in current file
        
        for dic in list_of_all_dict:
            if dic["symbol"] == el[0]:
                symbol_found = True
                dic = symbols_increment_in_dict(dic, spam, ham)

        if not symbol_found:
            list_of_all_dict = symbols_append_to_dict(el, list_of_all_dict, spam, ham)

    for dic in list_of_all_dict:
        dic["spam_perc"] = dic["spam"] / (dic["spam"] + dic["ham"])

    return list_of_all_dict


# working with CSV
def write_dicts_to_csv(list_of_dicts, csv_file):
    """
    writes list of dicts to file
    """
    headers = ["symbol", "spam", "ham", "spam_perc"]

    with open(csv_file, "w", newline='', encoding="utf-8") as f:
        csv_writer = writer(f)
        csv_writer.writerow(headers)
        for dic in list_of_dicts:
            row = [x for x in dic.values()]
            csv_writer.writerow(row)


def read_dicts_from_csv(csv_file, only_ad = False):
    list_of_dicts = []
    with open(csv_file, 'r', newline='',  encoding="utf-8") as f:
        csv_reader = reader(f)
        a,b,c,d = next(csv_reader) # headers
        for line in csv_reader:
            if only_ad:
                list_of_dicts.append({
                a : line[0], 
                d : float(line[3])
                })
            else:
                list_of_dicts.append({
                    a : line[0],
                    b : int(line[1]), 
                    c : int(line[2]), 
                    d : float(line[3])
                    })

    return list_of_dicts

if __name__=="__main__":
    lst = [
    {'symbol': 'dvd', 'spam': 84, 'ham': 10, 'spam_perc': 0.8936170212765957},
    {'symbol': 'copy', 'spam': 105, 'ham': 11, 'spam_perc': 0.9051724137931034},
    {'symbol': 'cd', 'spam': 52, 'ham': 4, 'spam_perc': 0.9285714285714286},
    {'symbol': 'burner', 'spam': 19, 'ham': 0, 'spam_perc': 1.0},
    {'symbol': 'free', 'spam': 552, 'ham': 47, 'spam_perc': 0.9215358931552587},
    {'symbol': 'copying', 'spam': 6, 'ham': 0, 'spam_perc': 1.0},
    {'symbol': 'get', 'spam': 403, 'ham': 93, 'spam_perc': 0.8125},
    {'symbol': 'any', 'spam': 280, 'ham': 63, 'spam_perc': 0.8163265306122449},
    {'symbol': 'right', 'spam': 108, 'ham': 21, 'spam_perc': 0.8372093023255814},
    {'symbol': 'only', 'spam': 312, 'ham': 51, 'spam_perc': 0.859504132231405},
    {'symbol': 'package', 'spam': 46, 'ham': 4, 'spam_perc': 0.92},
    {'symbol': 'our', 'spam': 703, 'ham': 72, 'spam_perc': 0.9070967741935484}
    ]
    path = "knowledge/words.csv"

    # write_dicts_to_csv(lst, path)
    list_of_dicts = read_dicts_from_csv(path)
    print(list_of_dicts)
