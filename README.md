# Compilers
A repository to hold the compiler code I will build for my Compilers CMPT 432 class.

Project 1 is seperated from Main via the 'Project 1' branch

Project 2 is seperated from Main via the 'Project 2' branch

Project 3 currently resides in the main branch.

to run the Compiler, access this main directory ( DO NOT GO INTO SOURCE ) and run:

javac source/*.java

java source/Compiler.java -input-here-

:IMPORTANT NOTE:

Whatever file path you enter in place of -input-here-, please use / slashes, instead of \ slashes.
Example:
java source/Compiler.java Progs/testCases.txt

: PLEASE READ THE FOLLOWING :

Project 3 is currently being worked on. At the moment, a semi-normal, hopefully correct AST is produced. A Symbol Table is made, mostly correct. Scope checking finished minus one issue ( regarding showing the difference between the first scope 1, and a second scope 1a.) Once resolved, type checking can be done correctly.

Using Pro2cases.txt to test Scope/Type checking

CANNOT ADD TO ITSELF. BREAKS