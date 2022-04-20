/// Stefenie Pickston 1506427
/// Holly Smallwood 1505405

public class REcompile {

    public static String[] expression; // string array to hold expression
    public static String refactored; // string to store refactored expression
    public static int state = 0; // state counter
    public static int j = 0; // counter
    public static int initial; // the int to store the final start state

    public static String[] ch;
    public static int[] next1;
    public static int[] next2;

    public static void main(String[] args) {
        if (args.length < 1) { // if incorrect input
            System.out.println("ERROR");
            System.err.println("usage: java REcompile \"REGEX\"");
        } else {
            try {
                // debug
                //String str = "aa|ea";
                //expression = str.split("");

                // for each arg just combine them
                if(args.length > 1){
                    String merge = "";
                    for (String string : args)
                        merge += string;
                    expression = merge.split("");
                }
                else
                    expression = args[0].split(""); // assign args to string[] expression

                // set up arrays
                ch = new String[expression.length*2];
                next1 = new int[expression.length*2];
                next2 = new int[expression.length*2];

                // refactor the expression for simplification
                refactored = refactor(expression);
                expression = refactored.split("");

                System.err.println(refactored);

                // check if regex is valid and produce standard output
                try {
                    initial = expression();
                } catch (Exception e) {
                    error();
                }

                // if regex did not count all characters
                if (j != expression.length)
                    error();

                // set the final state
                set_state(state, "FN", 0, 0); // 0 is placeholder so search dont crash

                System.out.println("Start State: " + initial); // print out start state

                // print out the machine
                output();
            } catch (Exception e) {
                e.printStackTrace();
                error();
            }
        }
    }

    public static String refactor(String[] expression) {
        String firstly = "";
        String initial = "";
        String secondly = "";
        String thirdly = "";
        
        for (String string : expression) // make a string in initial
            firstly += string;
        
        boolean seen = false; // make adjustments for the | character

        for(int i = 0; i < firstly.length(); i++) { // if | is treated as literal, replace with a temp character
            char ch = firstly.charAt(i);
            if(ch == '[') {
                seen = true;
                initial += ch;
            } else if (ch == ']'){
                seen = false;
                initial += ch;
            } else if (ch == '|'){
                if(seen == true || firstly.charAt(i-1) == '\\')
                    initial += '';
                else
                    initial += ch;
            }
            else
                initial += ch;
        }

        String[] parts = initial.split("\\|"); // split the alternations

        secondly += "("; // bracket refactoring for | to set precedence
        for (int i = 0; i < parts.length - 1; i++) {
            secondly += parts[i];
            secondly += ")|(";
        }
        secondly += parts[parts.length - 1];
        secondly += ")";

        for (char ch: secondly.toCharArray()) { // replace temp character with |
            if(ch == '')
                thirdly += '|';
            else
                thirdly += ch;
        }

        expression = thirdly.split("");
        String output = ""; // place to store our refactored string

        for (int i = 0; i < expression.length; i++) {
            try {
                if(expression[i].equals("*"))
                    if(expression[i-1].equals(expression[i+1])){ // a*a converted into aa*
                        output += expression[i - 1] + "*"; 
                        i++;
                    } else // otherwise carry on
                        output += expression[i];
                else if (expression[i].equals("+"))
                    if (expression[i - 1].equals("\\")) // if string before is "\\"
                        output += expression[i]; // OH MY GOD LEAVE IT, LEAVE IT, LEAVE IT - Gordon Ramsay
                    else if(expression[i + 1].equals("+")) // cant have a++
                        error();
                    else // otherwise turn a+ into aa*
                        output += expression[i - 1] + "*";
                        //output += "*" + expression[i - 1];
                else if (expression[i].equals("[")) { // [abc] converts to (\\a|\\b|\\c)
                    i++; // increment
                    String s = "("; // place to store refactored brackets

                    if (expression[i].equals("]")) { // if ] is first then treat as literal
                        s += "\\]";
                        i++;
                    } else { // whatever is at front is a literal (brackets cant be empty ie [])
                        s += "\\" + expression[i];
                        i++;
                    }
                    while (!expression[i].equals("]")) { // keep adding until we reach an ] or it breaks
                        s += "|\\" + expression[i];
                        i++;
                    }
                    output += s + ")"; // add to the output
                } else
                    output += expression[i]; // just add onto the end of the expression
            } catch (Exception e) {
                //e.printStackTrace();
                error();
            }
        }

        return output;
    }

    public static boolean isVocab(String c) {
        // System.out.println("vocab " + c);
        // if c ascii value is between 32 and 126 then it is valid
        char ch = c.charAt(0);
        String specials = "*?|()";
        if(!(specials.indexOf(ch) == -1))
            return false;
        else
            return true;

    }

    public static void set_state(int s, String c, int n1, int n2) {
        ch[s] = String.valueOf(c); // set char (actually a string)
        next1[s] = n1; // set to next state
        next2[s] = n2; // second next state, 0 if no option
    }

    // covers rules
    // E -> T
    // E -> TE
    public static int expression() {
        // System.out.println("expression");
        int r = term(); // call term
        if (j < expression.length) { // if count is less than expression
            if (isVocab(expression[j]) || expression[j].equals("("))
                expression(); // call expression if we have an opening bracket or character afterwards
        }
        return r;
    }

    // covers rules (in precedence)
    // T -> F
    // T -> F*
    // T -> F?
    // T -> F|T
    public static int term() { // find factor
        // System.out.println("term");
        int f = state - 1;
        int t1 = factor();
        if(f<0){
            f = state-1;
        }
        int r = t1;

        if(j < expression.length){
            if(expression[j].equals("*")){
                // set_state(state, "BR", state+1, t1); // set branch state
                // j++; // increment the count
                // r = state; // set the state
                // state++;

                if(next1[f] == next2[f]) // affects previous previous state
                    next2[f] = state;
                next1[f] = state;

                f = state - 1; // for branch
                j++; // increment count
                r = state; // count the state
                state++;

                set_state(r,"BR", t1, state);
                if(next1[f] == next2[f])
                    next2[f] = state - 1; // to get the right loop in the state
                next1[f] = state - 1;
            } else if(expression[j].equals("?")) {
                if(next1[f] == next2[f])
                    next2[f] = state;
                next1[f] = state;

                f = state - 1; // for branch
                j++; // increment count
                r = state; // count the state
                state++;

                set_state(r,"BR", t1, state);
                if(next1[f] == next2[f])
                    next2[f] = state;
                next1[f] = state;
            } else if(expression[j].equals("|")){ // for alternation
                //System.err.println("alternation");
                if(next1[f] == next2[f]) // if nexts are same
                    next2[f] = state; // set next2
                next1[f] = state; // set next1

                f = state - 1; // for branch
                j++; // increment count
                r = state; // count the state
                state++;

                int t2 = term(); // get term 2
                set_state(r, "BR", t1, t2); // set branch state
                if(next1[f] == next2[f])
                    next2[f] = state;
                next1[f] = state;
            }
        }
        return r;
    }

    // covers rules (in precedence)
    // F -> //F
    // F -> .
    // F -> a
    // F -> (E)
    public static int factor() {
        // System.out.println("factor");
        int r = 0;

        if (expression[j].equals("\\")) { // \\ takes highest priority
            j++; // increment to count the \\
            if (j > expression.length) // if nothing left then throw
                error();
            set_state(state, expression[j], state + 1, state + 1); // else set the state
            j++; // count the next char
            r = state; // count the state
            state++;
        } else if (expression[j].equals(".")) {
            set_state(state, "AN", state + 1, state + 1); // create wildcard state
            j++; // count next char
            r = state; // count the state
            state++;
        } else if (isVocab(expression[j])) {
            set_state(state, expression[j], state + 1, state + 1); // create literal state
            j++; // count next char
            r = state; // count the state
            state++;
        } else {
            if (expression[j].equals("(")) { // if we find open bracket
                j++; // count the next char
                r = expression(); // call expression
                if(expression[j].equals(")")) // if we find closing bracket
                    j++; // count the next char
                else
                    error();
            } else
                error();
        }

        return r;
    }

    public static void error() {
        System.err.println("ERROR BAD PARSE"); // error message
        System.exit(1); // close the program
    }

    public static void output() {
        // FORMATTING OF SYSTEM.OUT
        // STATE-NUMBER SYMBOL-MATCH-OR-BRANCH NEXT-1 NEXT-2
        // Symbol to represent BRANCH will be ASCII value of 4

        for(int i = 0; i< expression.length*2; i++){
            System.out.print(i + " " + ch[i] + " " + next1[i]);
            if (next2[i] != next1[i])
                 System.out.print(" " + next2[i]);
            System.out.print("\n");
            if(ch[i].equals("FN")){
                break;
            }
        }
    }
}