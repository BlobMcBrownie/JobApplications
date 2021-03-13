# ZPR HW04
import sys
class Stack:
    def __init__(self, content =[]):
        if isinstance(content, list):
            self.content = content
        else:
            self.content = []
            print("Warning: Creating an empty stack.")
    
    def is_empty(self):
        return (len(self.content) == 0)
    
    def push(self, value):
        self.content.append(value)

    def pop(self):
        if not self.is_empty():
            return self.content.pop(-1)
        else:
            print("Stack is empty.")
            return None


# Defining variables
s = Stack()
letters = Stack([])
filename = sys.argv[1]
letter = False
non_pair = False
pair = False
valid = False
exception = False
closed = False
tags = 0
chars = ['<','>','/']
letters_count = 0
letters_count_list = []

with open(filename, 'r', encoding="utf-8") as f:
    file_text = f.read()

for character in file_text:
    if character not in chars:
        letter = True
        if character != ' ' and not closed:
            letters_count += 1
            letters.push(character)

    if character == '<':
        closed = False
        letter = False
        s.push(character)
        continue

    elif character == '>':
        closed = True
        s.push(character)
        letters_count_list.append(letters_count)
        letters_count = 0
        if non_pair:
            tags += 1
            for let in range(letters_count_list[-1]):
                letters.pop()
            for task in range(2):
                s.pop()
            del letters_count_list[-1]
        elif pair:
            tags += 1
            letters_list = []
            for i in range(letters_count_list[-1]):
                letters_list.append(letters.pop())
            for j in range(letters_count_list[-1]):
                if letters.pop() != letters_list[j]:
                    exception = True
                    break
            for task in range(4):
                if s.is_empty():
                    exception = True
                    break
                s.pop()
            del letters_count_list[-1]
            del letters_count_list[-1]
        non_pair = False
        pair = False

    elif character == '/':
        if letter: 
            non_pair = True
        else:
            pair = True

if s.is_empty():
    valid = True

if (valid and not exception):
    print("pocet tagu: " + str(tags))
    print("text validni")
else: 
    print("text nevalidni")
