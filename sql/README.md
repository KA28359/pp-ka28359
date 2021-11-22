Design a set of classes for manipulating data given in a tabular
format. You need to create a table in database, and insert everything
from the input table file to the table in database, and then execute
SQL queries in order to get the result.


### TABLE FORMAT

Here is an example of a table (in a .txt file):

```
colA colB colC
1 3.3 abc
2 4.4 def
```

The table above has 2 rows (except the header row) and 3 columns, but
it can have any number of rows and columns and the numbers do not need
to match. The content of the table can also be empty, but it is
guaranteed that the table has a header row.

If the number of columns in all rows is not the same, you should print
"NUM COLS ERROR".

Each cell of a table can be one of the following:

  an integer (INT in H2)

  a floating point number (DOUBLE in H2)

  a string (VARCHAR in H2). Each string is a sequence of characters
  that ends with a white space char; "abc" is a valid string and "a
  bc" are two strings "a" and "bc". Strings are *not* quoted in the
  input file.

In this example, we have a table with 2 rows and 3 columns. The first
column are integers (1, 2), the second column is floating point
numbers (3.3, 4.4), and the third column is strings (abc, def). A
single table can contain various types but all the cells in a column
have the same type.


### PROGRAM

A user can invoke your program with the following command:
```
./simple TABLE_FILE OUT_FILE
```
`TABLE_FILE` is the path to the input table
`OUT_FILE` is the path where you should store the result

The output of this invocation should include the column with the
maximum sum, where the sum of a column is the sum of all the cells in
the column. If two columns has the same sum, pick the column with a
smaller column index (assuming column indexes increase from left to
right).

Ignore the columns without a sum because of invalid type of any cell
in the column (i.e., strings). If there is no column in `TABLE_FILE`
that can be used for the sum or `TABLE_FILE` is empty (except the
header row), then `OUT_FILE` should be empty.

Ignore the header during computation, but print the relevant header to
`OUT_FILE` (if there exists a column).


### SAMPLE TESTS

We provide some sample tests. See "tests" for all table and output files.

./simple 1.txt 1.out


### GENERAL

You are required to use H2
(<https://www.h2database.com/html/main.html>) and JDBC API
(<https://docs.oracle.com/javase/8/docs/technotes/guides/jdbc/>).

`code/simple` script should be able to compile and run your program
with the given arguments. We will be invoking this script when
grading. Also, we provide script `s` for you to be able to run a
sample test.

If you detect any other error not specified in this README, then print
"OTHER ERROR" to stdout.

We will use Linux to test all assignments. If you do not test on
Linux, we might see different result from you.

Be creative. When something is unclear make a reasonable assumption.

Also, do not try to cover everything at once; start with small
examples and then expand from there.

In your implementation, you can use standard libraries available in H2
Java 8, but you cannot use any other third-party libraries/code.
