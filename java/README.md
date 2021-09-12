
Design a library for manipulating data given in a tabular format.  As
part of the assignment, you will also develop a main *program* that
will leverage the library.


### INPUT(s)

One of the key inputs to your program is going to be a txt file that
contains a table.  Keep in mind that the input might not be well
formatted.  Counting rows and columns in the table starts with 0.


### TABLE FORMAT

Here is an example of a table (in a txt file):

```
1 3 5
2 4 7
22 30 50
```

The table above has 3 rows and 3 columns. but it can have any number
of rows and columns and the numbers do not need to match.  The table
can also be empty.

If the number of columns in all rows is not the same, you should print
"NUM COLS ERROR".

The input table can also have a header like in the example below:

size group age
1 3 5
2 4 7
22 30 50

Each cell of a table can be one of the following:

  an integer (as defined by the Java programming language)

  a floating point number (as defined by the Java programming language)

  an *action* (defined later in this document)

  a string (anything that does not fit into other types).  Each string
  is a sequence of characters that ends with a white space char; "abc"
  is a valid string and "a bc" are two strings "a" and "bc".  Strings
  are *not* quoted in the input file.

A single table can contain various types (and there is no requirement
that all values in one row or column need to have the same type).

Below is an example of a table with various types and actions.

1 3 abz
2 4 __sum__([0,0],[1,2])

In this example, we have a table with 2 rows and 3 columns.  Most of
the values are integers (1, 2, 3, 4), but one cell is a string (abz),
and one cell is an action (__sum__).  __sum__ action represents a
function that takes the cells given as arguments (the number of
arguments depends on the action) and performs computation (as
described later in ACTIONS).


### PROGRAM

A user can invoke your program with the following options:

  (a) ./simple [-header] -print COLS TABLE_FILE OUT_FILE
      -header is optional and says that the input table has a header
      COLS (int) is a comma separated list of columns to print (at least one)
      TABLE_FILE is the path to the input table
      OUT_FILE is the path where you should store the result

      The output of this invocation should include the specified
      columns of the original table (preserving the order of both
      columns and rows).  If a user provides an invalid value for
      COLS, print "COL INDEX ERROR" to stdout.

      Ignore header during computation, but print the relevant headers
      to the resulting file.

  (b) ./simple [-header] -sum COL TABLE_FILE OUT_FILE
      -header is optional and says that the input table has a header
      COL (int) is a column index
      TABLE_FILE is the path to the input table
      OUT_FILE is the path where you should store the result

      The result in this case is the sum of all the cells in the given column.

      If types of any cell in the column cannot be used for the sum
      function (i.e., strings and actions), print "TYPE ERROR" to stdout.

      If COL has an invalid value, then print "COL INDEX ERROR" to stdout.

      Ignore header if present.

   (c) ./simple [-header] -action [COLS] TABLE_FILE OUT_FILE
      -header is optional and says that the input table has a header
      COLS (which is optional) a comma separated list of columns to process (default is all columns)
      TABLE_FILE is the path to the input table
      OUT_FILE is the path where you should store the result

      The result is a table (only given columns) where actions are
      executed.  Note that actions can depend on cells that are also
      actions, so you will have to build a dependency graph, check
      that it is a DAG, and only then execute.  If this is not
      satisfied, print "DAG ERROR" to stdout.

      If COLS have illegal value, print "COL INDEX ERROR" to stdout.

      Ignore header during computation, but print the relevant headers 
      to the resulting file.

   (d) ./simple [-header] -when COND TABLE_FILE OUT_FILE
      -header is optional and says that the input table has a header
      COND is a condition to specify what rows to include in the resulting file (given as string without spaces)
      TABLE_FILE is the path to the input table
      OUT_FILE is the path where you should store the result

      The result is a table (only rows that satisfy the given
      condition) written to the OUT_FILE.

      If COND is not well formed, print "COND ERROR".
      If when cannot be evaluated because of type, then default to "false"

      Format for COND:
          COND -> REL_COND
          REL_COND -> "(" EXP ")" [ REL_OP REL_COND ]
          EXP -> OPERAND OP OPERAND
          REL_OP -> "&&" | "||"
          OPERAND -> $COL_NUM | $COL_NAME | CONSTANT
          OP -> "<" | ">" | "<>" | "=="

      Examples:
          $1 == "some_string" # every row where column 1 is equal to "some_string"
          $header_name <> 5 # every row where column with the given header is not equal 5

   (e) ./simple [-header] -update ROW,COL,VAL TABLE_FILE OUT_FILE
      -header is optional and says that the input table has a header
      ROW(int) and COL(int) specify the cell to update to the given value (VAL)
      TABLE_FILE is the path to the input table
      OUT_FILE is the path where you should store the result

      The result is the table with updated value; other cells should
      remain the same.

      If COL or ROW are incorrect, print "INDEX ERROR" to stdout.

   (f) ./simple OP_FILE TABLE_FILE OUT_FILE
      OP_FILE is a file that contains a sequence (one per line) of commands (a-e), such that each command is applied in sequence
      TABLE_FILE is the path to the input table
      OUT_FILE is the path where you should store the result

      Example OP_FILE;
      -action 1 # execute actions in column 1
      -when $1>55 # only include rows when the first column is greater than 55


### ACTIONS

This section includes the list of actions (ROW and COL are integers):

  __sum__([ROW,COL]+) - computes the sum of the cell(s).
  Each argument cell's type should be integer, floating point number,
  or an action that results in the aforementioned types.  If one
  argument cell is an action, then that action will be executed before
  the value is used.

  __avg__([ROW,COL]+) - computes the average of the cell(s).
  Each argument cell's type should be integer, floating point number,
  or an action that results in the aforementioned types.  If one
  argument cell is an action, then that action will be executed before
  the value is used.

  __to_upper__([ROW,COL]) - returns the upper case version of the
  string in the argument cell (i.e., using String.toUpperCase).
  The argument cell's type should be string.
  
  __to_lower__([ROW,COL]) - returns the lower case version of the
  string in the argument cell (i.e., using String.toLowerCase).
  The argument cell's type should be string.

For all actions, if there are invalid arguments, print the following to stdout:

  Print "TYPE ERROR" if any argument cell has incorrect type
  Print "INDEX ERROR" if any ROW or COL index is not valid

All actions will be written as strings without spaces, which start
with "__" and end with "__".


### GENERAL

If you detect any other error not specified in this README, then print
"OTHER ERROR" to stdout.

We will use Linux to test all assignments.  If you do not test on
Linux, we might see different result from you.

Be creative.  When something is unclear make a reasonable assumption.

Also, do not try to cover everything at once; start with small
examples and then expand from there.

In your implementation, you can use standard libraries available in
Java 8 (java.*), but you cannot use any third-party libraries.
