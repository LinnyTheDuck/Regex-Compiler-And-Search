/// Stefenie Pickston 1506427
/// Holly Smallwood 1505405

import java.io.*;
import java.util.*;

public class REsearch {
    public static List<state> stateArray = new ArrayList<state>(); // array of states
    public static Scanner sc = new Scanner(System.in); // reads bytes from the input river
    public static BufferedReader br; // br to read lines from file
    public static int numCurrStates = 0;
    public static int startState;    
    public static deque d; // our deque

    public static void main(String[] args) {
        // FSM COMES FROM SYSTEM.IN
        // FILE TO SEARCH COMES FROM ARGS[0]

        // FORMATTING OF SYSTEM.IN
        // STATE-NUMBER SYMBOL-MATCH-OR-BRANCH NEXT-1 NEXT-2
        // BRANCH IS "BR"
        // ANY IS "AN"
        // FINISH/SUCSESS IS "FN"

        if (args.length != 1) { // for incorrect input
            System.out.println("ERROR: No file to search");
            System.err.println("usage: java REcompile \"REGEX\" | java REsearch file.txt");
            System.err.println("usage: cat machinecode.txt | java REsearch file.txt");
        } else {
            try {
                // for debug
                // br = new BufferedReader(new FileReader("test.txt"));
                // File f = new File("machinecode.txt");
                // sc = new Scanner(f);

                br = new BufferedReader(new FileReader(args[0])); // puts file in buffered reader
                String fileLine; // for reading c line from file

                // take the start state from first line
                String firstLn = sc.nextLine();
                String[] exFirst = firstLn.split(" ");
                startState = Integer.parseInt(exFirst[2]);

                // take each line
                // spilt into 4 or 3 and put in array of states
                while (sc.hasNextLine()) { // while reading
                    String line = sc.nextLine(); // get the line
                    String[] extracted = line.split(" "); // split and put in an array

                    if (extracted.length == 3) { // if the array is length 3
                        state s = new state(Integer.parseInt(extracted[0]), extracted[1],
                                Integer.parseInt(extracted[2]));
                        stateArray.add(s);// add to array
                    } else if (extracted.length == 4) { // otherwise is length 4
                        state s;
                        if(extracted[1].equals(""))
                            s = new state(Integer.parseInt(extracted[0]), " ", Integer.parseInt(extracted[3]));
                        else
                            s = new state(Integer.parseInt(extracted[0]), extracted[1], Integer.parseInt(extracted[2]), Integer.parseInt(extracted[3]));
                        stateArray.add(s);// add to array
                    }
                }

                // for debug - make sure machine is built correctly
                // for (state s : stateArray)
                // System.out.println(s.mismatch);

                // setup the deque
                d = new deque(); // create new deque
                d.insertRear(stateArray.get(startState)); // push first state

                while ((fileLine = br.readLine()) != null) { // until end of file
                    if (checkLine(fileLine)) // check if part of pattern
                        System.out.println(fileLine); // yes - prints line with pattern
                    d.empty();
                    d.insertRear(stateArray.get(startState)); // push first state
                }

            } catch (Exception e) {
                System.err.println("Your file is invalid");
                e.printStackTrace();
            }
        }
    }

    // checks the line of text and if not found, starts the search from the next letter
    public static boolean checkLine(String line) {
        int index = 0; // where is our starting char
        boolean found = false;

        while (index < line.length()) { // iterates along the line until there is no more
            if (checkWord(line, index)) { // checks if the word exists in the current index
                found = true; // if pass return true
                break;
            }
            index++; // else increment the index
        }
        return found; // if fail return false
    }

    public static boolean checkWord(String line, int index) {
        int pointer = index; // keep track of which character we are comparing in the string
        boolean didSomething = false;

        while (!d.isEmpty()) { // while the deque is not empty
            int totalNext = d.size(); // keep track of inital amount of nexts as d.size changes

            if(pointer >= line.length() && !d.peek(0).equals("FN")){
                d.empty(); 
                d.insertRear(stateArray.get(startState)); // enqueue first state
                return false; // fail if out pointer is out of bounds
            }

            // for all next states from numCurrStates to size - 1
            for (int i = 0; i < totalNext; i++) { // start with only next states
                if(d.peek(0).equals("FN")){ // last state of the bunch
                    d.empty(); // empty the deque
                    d.insertRear(stateArray.get(startState)); // enqueue first state
                    return true; // return true is if finish is the only state possible
                } else if (d.peek(0).equals(String.valueOf(line.charAt(pointer))) || d.peek(0).equals("AN")) { // match or wildcard
                    d.insertRear(stateArray.get(stateArray.get(d.currState(0)).N1)); // insert next state 1
                    d.deleteFront(); // pop off top current state
                    didSomething = true;
                } else if(d.peek(0).equals("BR")) { // if we have a branch
                    if(didSomething)
                        d.deleteFront();
                    else{
                        d.insertRear(stateArray.get(stateArray.get(d.currState(0)).N1)); // insert next state 1
                        if (stateArray.get(d.currState(0)).N2 != null) // insert N2 if it exists
                            d.insertRear(stateArray.get(stateArray.get(d.currState(0)).N2));
                        d.deleteFront();
                    }
                } else // remove said state without adding anything as you cant
                    d.deleteFront();

                if(i == totalNext - 1 && didSomething){
                    pointer ++; // increment pointer on the last curr
                    didSomething = false;
                }
            }
        }

        d.empty(); // empty the deque to make all values null (even though it's technically already empty)
        d.insertRear(stateArray.get(startState)); // enqueue first state
        return false; // deque is empty, search failed
    }

    // -----------------CLASS-----------------//

    // state object
    public static class state {
        int SN;
        Integer N1, N2; // use class so can check for null
        String mismatch;

        // constructor for two values
        public state(int sn, String mm) {
            SN = sn;
            mismatch = mm;
        }

        // constructor for three values
        public state(int sn, String mm, int n1) {
            SN = sn;
            mismatch = mm;
            N1 = n1;
        }

        // constructor for four values
        public state(int sn, String mm, int n1, int n2) {
            SN = sn;
            mismatch = mm;
            N1 = n1;
            N2 = n2;
        }
    }

    // node for each deque
    public static class node {
        node next, prev;
        state data; // our data stored here

        // Function to get c new node
        public static node getnode(state data) {
            node newNode = new node();
            newNode.data = data;
            newNode.prev = newNode.next = null;
            return newNode;
        }
    }

    // implement deque here
    public static class deque {
        node first, last;
        int size;

        // constructor
        public deque() {
            first = last = null;
            size = 0;
        }

        // checks whether deque is empty or not
        public boolean isEmpty() {
            return (first == null);
        }

        // return the number of elements in the deque
        public int size() {
            return size;
        }

        // Function to insert an element at the first end
        public void insertFront(state data) {
            node newNode = node.getnode(data);
            // If true then new element cannot be added and it is an 'Overflow' condition
            if (newNode == null)
                System.out.println("OverFlow: node is null");
            else {
                if (first == null) // If deque is empty
                    last = first = newNode;
                else { // Inserts node at the first end
                    newNode.next = first;
                    first.prev = newNode;
                    first = newNode;
                }
                size++; // increase size
            }
        }

        // Function to insert an element at the last end
        void insertRear(state data) {
            node newNode = node.getnode(data);
            // If true then new element cannot be added and it is an 'Overflow' condition
            if (newNode == null)
                System.out.println("OverFlow: node is null");
            else {
                if (last == null) // If deque is empty
                    first = last = newNode;
                else { // Inserts node at the last end
                    newNode.prev = last;
                    last.next = newNode;
                    last = newNode;
                }
                size++;
            }
        }

        // Function to delete the element from the first end
        void deleteFront() {
            // If deque is empty then 'Underflow' condition
            if (isEmpty())
                System.out.println("UnderFlow: deque is empty");

            // Deletes the node from the first end and makes the adjustment in the links
            else {
                // node temp = first;
                first = first.next;

                if (first == null) // If only one element is present
                    last = null;
                else
                    first.prev = null;
                size--;
            }
        }

        // Function to delete all the elements from Deque
        void empty() {
            last = null;
            while (first != null) {
                // node temp = first;
                first = first.next;
            }
            size = 0;
        }

        String peek(int index) { // peeks the string item on an index
            node temp = first;
            for (int i = 1; i < index; i++)
                temp = temp.next; // iterate through until we find it
            return temp.data.mismatch; // return the mismatch 'char'
        }

        int currState(int index){ // peeks the current state on an index
            node temp = first;
            for (int i = 1; i < index; i++)
                temp = temp.next; // iterate through until we find it
            return temp.data.SN; // return the state number
        }
    }
}
