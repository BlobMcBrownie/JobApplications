class BinaryConfusionMatrix:

    def __init__(self, pos_tag='SPAM', neg_tag='OK'):
        self.pos_tag = pos_tag
        self.neg_tag = neg_tag
        self.tp = 0
        self.fp = 0
        self.tn = 0
        self.fn = 0

    def as_dict(self):
        vals = {'tp': self.tp, 'tn': self.tn, 'fp': self.fp, 'fn': self.fn}
        return vals

    def update(self, truth, pred):
        if truth == pred:
            if truth == self.pos_tag:
                self.tp += 1
            elif truth == self.neg_tag:
                self.tn += 1
        elif truth == self.neg_tag and pred == self.pos_tag:
            self.fp += 1
        elif truth == self.pos_tag and pred == self.neg_tag:
            self.fn += 1

        if truth not in (self.pos_tag, self.neg_tag) or pred not in (self.pos_tag, self.neg_tag):
            raise ValueError(f"The argument may be either {self.pos_tag} or {self.neg_tag}")

    def compute_from_dicts(self, truth_dict, pred_dict):
        for key in truth_dict:
            self.update(truth_dict[key], pred_dict[key])


if __name__ == "__main__":
    cm1 = BinaryConfusionMatrix(pos_tag=True, neg_tag=False)
    print(cm1.as_dict())
    cm1.update(True, True)
    print(cm1.as_dict())

    truth_dict = {'em1': 'SPAM', 'em2': 'SPAM', 'em3': 'OK', 'em4': 'OK'}
    pred_dict = {'em1': 'SPAM', 'em2': 'OK', 'em3': 'OK', 'em4': 'SPAM'}
    cm2 = BinaryConfusionMatrix(pos_tag='SPAM', neg_tag='OK')
    cm2.compute_from_dicts(truth_dict, pred_dict)
    cm2.as_dict()  # {'tp': 1, 'tn': 1, 'fp': 1, 'fn': 1}
