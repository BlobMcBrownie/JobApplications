from confmat import *
from utils import *

MY_PATH = "emails"

def quality_score(tp, tn, fp, fn):
    return (tp + tn) / (tp + tn + 10 * fp + fn)


def compute_quality_for_corpus(corpus_dir):
    t_dict = read_classification_from_file(corpus_dir + "/" + "!truth.txt")
    p_dict = read_classification_from_file(corpus_dir + "/" + "!prediction.txt")
    # print(f"truth:{t_dict}\nprediction: {p_dict}\n")

    bcm = BinaryConfusionMatrix(pos_tag='SPAM', neg_tag='OK')
    bcm.compute_from_dicts(t_dict, p_dict)
    my_dict = bcm.as_dict()
    return quality_score(**my_dict)


if __name__ == "__main__":
    # q = compute_quality_for_corpus(MY_PATH)
    # print(q)
    from simplefilters import NaiveFilter, ParanoidFilter, RandomFilter
    for filt in (NaiveFilter(), ParanoidFilter(), RandomFilter()):
        f = filt
        print(f"{f.__class__.__name__} quality is {f.compute_quality(MY_PATH)}\n")