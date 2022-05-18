# Regex-Compiler-And-Search

A REGEX compiler which will produce a state machine, and a matching searcher which looks for the REGEX inside strings

### REGEX Rules

- any symbol that does not have a special meaning (as given below)
is a literal that matches itself
- . is a <i> wildcard</i> symbol that matches any literal
- adjacent regexps are concatenated to form a single regexp (ie. multiple arguments)
- \* indicates closure (zero or more occurrences) on the preceding
regexp
- \+ indicates that the preceding regexp can occur one or more times
- ? indicates that the preceding regexp can occur zero or one time
- | is an infix alternation operator such that if <i> r</i> and
<i>e</i> are regexps, then <i> r|e</i> is a regexp that matches one of
either <i>r</i> or <i> e</i>
- ( and ) may enclose a regexp to raise its precedence in the
usual manner; such that if <i>e</i> is a regexp, then <i> (e)</i> is a
regexp and is equivalent to <i> e</i>. <i> e</i> cannot be empty.
- \ is an escape character that matches nothing but indicates the
symbol immediately following the backslash loses any special meaning
and is to be interpretted as a literal symbol
- square brackets "[" and "]" enclose a list of symbols of which one and only
one must match (i.e. a shorthand for multi-symbol alternation); all special
symbols lose their special meaning within the brackets,
and if the closing square bracket is to be a literal then it must be first
in the enclosed list; and the list cannot be empty.

### Operator Precedence (from high to low)

- escaped characters (i.e. symbols preceded by \\)
- parentheses (i.e. the most deeply nested regexps have
the highest precedence)
- repetition/option operators (i.e. *, + and ?)
- concatenation
- alternation (i.e. | and [ ])

### Usage

To Compile: <br>
> javac *.java

Both files at once: <br>
> java REcompile "REGEX" | java REsearch file.txt

Just REcompile: <br>
> java REcompile "REGEX"

Just REsearch: <br>
> cat machinecode.txt | java REsearch file.txt

### Known Bugs
- There seems to be an edge case with closure aka * that will produce a strange regex. Need to find the exact case and debug.

Maybe I'll get round to fixing these one day...

Written in 2021, 15/15
