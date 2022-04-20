To Run the Programs:
Both files at once: 
    java REcompile "REGEX" | java REsearch file.txt
Just REcompile: 
    java REcompile "REGEX"
Just REsearch: 
    cat machinecode.txt | java REsearch file.txt

Rules and Dictionaries:
E -> T
E -> TE

T -> F
T -> F*
T -> F?
T -> F|T

F -> //F
F -> .
F -> a
F -> (E)

Refactoring has been used to simplify certain symbols:
T -> F+ ie "a+" has been simplified to "aa*"
T -> [F|T] ie "[abc]" has been simplified to "(//a|//b|//c)"

Refactoring has also been used to set alteration at a lower precedence:
"ab|cd" has been converted to "(ab)|(cd)"
Thus brackets can be used to achieve a higher precedence:
"a(b|c)d" has been converted to "(a(b)|(c)d)" and the b|c has been prioritized

The final converted/refactored regex will be displayed on the terminal

It is illegal for symbols to be adjacent with the exception of brackets, backslashes and wildcards.

 is used as a temporary symbol and it is assumed that the regex will not include ascii control characters
therefore if  is used within a regex it will be converted into a |

Have a nice day!
(ps there's a Gordon Ramsay Quote somewhere in the code!)