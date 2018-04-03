# cmpe230COcompiler
Cmpe 230 Project 1 

### What is it? 

* It is a compiler from a brand new language(co language that is made up) to a86 assembly instructions. 


### What is the syntax of .co files?

* You can only have assignments and expressions.
* In assignments you can use variables whose name starts with a letter.
* Every number considered to be in hexadecimal form and starts with a number.(a is not a number but 0a is a number.)
* Every number including the results are in 32 bit integer form.
* Main operations are pow(x,y) which is x to the y, addition and multiplication.
* Parantheses are allowed in the correct form.
* Expressions are in infix notation.
* If we write only a expression to a line, we need to print this value to the console.
### How it is done?

* First we are reading the .co file and storing all the lines. 
* Then in order to convert from infix to postfix we are parsing expressions and assignments.
* While parsing we are constructing a postfix notation in a string variable. We are converting to postfix notation because this way it is easier to translate into 
a86 
* Then we are processing the postfix notation to produce a86 codes.

### What is the output?
* After everythins is done we have a out.asm file which does the operations in the .co file.
* Expression lines, which may include variables, are evaluated and printed to the console.

### Thanks for reading :)

