import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

public class main {

	public static Vector<String> curPostfix=new Vector<String>();  // Inbox string for conversion from infix to postfix.
	static Vector<String> ids = new Vector<String>(); // Identifiers
	static int curLine =1; // which line we process 
	static int idEnum =0;
	static Vector<Vector<String>> actionHolder = new Vector<Vector<String>>(); // This holds the text file as array.

	public static boolean isValid(String str) {   // Checks if a string is a valid hexadecimal	{  
		boolean valid = true;
		if('0'<=str.charAt(0) && '9'>=str.charAt(0)){
		if(str.length()>9) { // A valid constant cannot be longer than 9 digits e.g. 0aaaaaaaaa
			valid = false;
		}else if(str.length() ==9 && str.charAt(0)=='0'){ // And if it is a 9 digit valid constant it should start with 0.

			for(int i=1;i<str.length();i++) {

				if(!(('a'<=str.charAt(i) && str.charAt(i)<='f')||('0'<=str.charAt(i) && '9'>=str.charAt(i)))) { 

					valid = false;
				}
			}


		}else if(str.length()<9) { // The case that it is shorter than 9 characters.

			for(int i=0;i<str.length();i++) {
				if(!(('A'<=str.charAt(i) && str.charAt(i)<='F')||('a'<=str.charAt(i) && str.charAt(i)<='f')||('0'<=str.charAt(i) && '9'>=str.charAt(i)))) { // this checks that if all the characters are one of a-f or A-F or 0-9.

					valid = false;

				}
			}

		}

		}else {
			valid = false;
		}
		return valid;
	}

	public static boolean inPar(String inp,int pos) { // This checks whether a character at given position is inside a parantheses or not
		int x=0;
		for(int i=0;i<pos;i++) {
			if(inp.charAt(i)=='(') {
				x++;

			}else if(inp.charAt(i) ==')') {
				x--;
			}
		}
		return x!=0;
	}

	public static String expr(String inp) { // This checks inp is a valid expression or not. If not throws exceptions.
		
		while((inp.charAt(0)==' ' || inp.charAt(inp.length()-1)==' ')) {
			if(inp.charAt(0)==' ') {
				inp = inp.substring(1);

			}else if(inp.charAt(inp.length()-1)==' ') {
				inp = inp.substring(0,inp.length()-1);
			}
		}
		
		boolean thereIsPlus = false;
		for(int i=inp.length()-1;i>=0;i--) {

			if(inp.charAt(i)=='+' && !inPar(inp,i) ) {
				thereIsPlus = true;
				String xpr = inp.substring(0, i);
				String trm = inp.substring(i+1);
				xpr = expr(xpr);
				trm = term(trm);
				curPostfix.addElement("+");
				break;
			}
		}
		if(!thereIsPlus) inp = term(inp);	
		return inp;
	}

	public static String term(String inp) { // This checks inp is a valid term or not. If not throws exceptions.

		while((inp.charAt(0)==' ' || inp.charAt(inp.length()-1)==' ')) { // Unnecessary blank characters are deleted.
			if(inp.charAt(0)==' ') {
				inp = inp.substring(1);

			}else if(inp.charAt(inp.length()-1)==' ') {
				inp = inp.substring(0,inp.length()-1);
			}
		}
		
		boolean thereIsMult = false;

		for(int i=inp.length()-1;i>=0;i--) {

			if(inp.charAt(i)=='*' && !inPar(inp,i) ) {
				thereIsMult = true;
				String xpr = inp.substring(0, i);
				String trm = inp.substring(i+1);
				xpr = term(xpr);
				trm = factor(trm);
				curPostfix.addElement("*");
				break;
			}
		}

		if(!thereIsMult) inp = factor(inp);

		return inp;

	}

	public static String factor(String inp) { // This checks inp is a valid factor or not. If not throws exceptions.
		while((inp.charAt(0)==' ' || inp.charAt(inp.length()-1)==' ')) { // Unnecessary blank characters are deleted.
			if(inp.charAt(0)==' ') {
				inp = inp.substring(1);

			}else if(inp.charAt(inp.length()-1)==' ') {
				inp = inp.substring(0,inp.length()-1);
			}
		}
		
		if(inp.length()!=0) {
			if(isValid(inp)&& 47<inp.charAt(0) && inp.charAt(0)<58) { // If this is a hex number
				curPostfix.addElement(inp);
			}else if(inp.startsWith("pow(") && inp.charAt(inp.length()-1)==')') { // else if it is a pow()

				boolean thereIsComma = false;
				for(int i=0;i<inp.length()-1;i++) {
					if(inp.charAt(i)==',' && !inPar(inp.substring(4,inp.length()-1),i-4) ) {
						thereIsComma = true;
						String xpr = inp.substring(4, i);
						String xpr2 = inp.substring(i+1,inp.length()-1);
						xpr = expr(xpr);
						xpr2 = expr(xpr2);
						curPostfix.addElement("^");

					}
				}
				if (!thereIsComma) {
					throw new RuntimeException("Line " + curLine + ":" + "Syntax error.");
				}

			}else if(inp.length()>0 && isValidId(inp)) { // or it is a letter
				curPostfix.addElement(inp);
			}else if(inp.length()>0 && (inp.charAt(0) == '(' && inp.charAt(inp.length()-1) == ')')) { // Or it is a expression inside the parantheses.
				inp= expr(inp.substring(1, inp.length()-1));
			}else {
				throw new RuntimeException("Line " + curLine + ":" + "Syntax error."); // Otherwise error
			}
		}else {
			throw new RuntimeException("Line " + curLine + ":" + "Syntax error.");
		}
		return inp;	
	}

	public static boolean isValidId(String inp) { // Checks if inp is a valid identifier name or not
		boolean valid = true;
		if(!((64<inp.charAt(0) && inp.charAt(0)<91) || (96<inp.charAt(0) && inp.charAt(0)<123)|| inp.charAt(0)=='$' || inp.charAt(0)=='_')) {
			valid = false;
		} //Ascii65-90 =  A-Z, Ascii97-122 = a-z, and $,_ are allowed as the first character in variable names.

		for(int i =1;i<inp.length()-1;i++) {
			if(!((inp.charAt(i)<58 && inp.charAt(i)>47 )||((64<inp.charAt(i) && inp.charAt(i)<91)) || (96<inp.charAt(i) && inp.charAt(i)<123)|| inp.charAt(i)=='$' || inp.charAt(i)=='_')) {
				valid = false;
			} //Ascii65-90 =  A-Z, Ascii97-122 = a-z, Ascii48-57 = 0-9 and $,_ are allowed as non-first characters in variable names.
		}


		return valid;
	}

	public static void parser(String in) { // This function will parse either an assignment statement or a expression statement.

		String[] split = in.split("=");
		while((split[0].charAt(0)==' ' || split[0].charAt(split[0].length()-1)==' ')) {
			if(split[0].charAt(0)==' ') {
				split[0] = split[0].substring(1);

			}else if(split[0].charAt(split[0].length()-1)==' ') {
				split[0] = split[0].substring(0,split[0].length()-1);
			}
		}
		if(split.length>1) {
			while((split[1].charAt(0)==' ' || split[1].charAt(split[1].length()-1)==' ')) {
				if(split[1].charAt(0)==' ') {
					split[1] = split[1].substring(1);

				}else if(split[1].charAt(split[1].length()-1)==' ') {
					split[1] = split[1].substring(0,split[1].length()-1);
				}
			}
		}




		curPostfix = new Vector<String>();
		if(split.length ==1) {
			expr(in);	
		}else if(split.length ==2 && split[0].length()>0 && isValidId(split[0])) {
			curPostfix.addElement("&" + split[0]);
			expr(split[1]);
			curPostfix.addElement("=");
		}else {
			throw new RuntimeException("Line " + curLine + ":" + "Syntax error.");
		}


	}

	public static void main(String[] args) {// Execution start, takes .co file as the only argument

		String file = args[0];  // The first and only argument is file path.
		BufferedReader br= null; // These two reader are for reading .co file.
		FileReader fr = null;

		try {
			fr = new FileReader(file);  // Try to create an FileReader instance 
			// If it is not, then throw an FileNotFoundException
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();		
		}

		br = new BufferedReader(fr);	 
		String sCurrentLine; 
		Vector<String> code = new Vector<String>(); // Hold code here	 

		try {
			while ((sCurrentLine = br.readLine()) != null) {  // Read a line until the file ends.
				if(!sCurrentLine.replaceAll(" ", "").isEmpty()) { // This checks for empty lines or the lines with only blank characters.
					code.add(sCurrentLine);
				}
			}
		} catch (IOException e) { 					// If something wrong happens while reading throw IOException.
			e.printStackTrace();
		}



		for(int i=0;i<code.size();i++) {
			curLine = i+1;
			parser(code.get(i));
			actionHolder.addElement(curPostfix);
			System.out.println(curPostfix);
		}

		doActions();
	}

	public static void doActions() {
		BufferedReader br= null; // This function will write to a file with .asm extension.
		try {
			PrintWriter writer = new PrintWriter("out.asm", "UTF-8");
			writer.println("code segment"); // Starts printing to out.asm

			Vector<String> numbers = new Vector<String>(); // this is for storing values of constants at the bottom of the file. 
			int numcount =1; // this is for enumeration while storing constants in the code. They will be like _x1 dw value,value,_x2...

			while(!actionHolder.isEmpty()) {
				Vector<String> action = actionHolder.remove(0);
				if(action.contains("=")) { // If the line is an assignment.

					String varName = "";
					while(!action.isEmpty()) {
						String c = action.remove(0); // Currently processed string.

						if(c.charAt(0)=='&') { // If the line is an assignment statement?
							varName = c.substring(1); // I am not going to push the address of te variable to the stack, rather I prefer holding the name the variable in a variable in java code.
							if(!ids.contains(varName)) {
								ids.addElement(varName+Integer.toString(idEnum)); // later I will use it when assigning the value to the variable.
								idEnum++;
							}
							
						}else if(isValid(c)) { // If it is a valid constant, push it to the stack.
							numbers.addElement(c);
							writer.println("push " + "_x" + numcount + " w"); // most significant value is lower than its least significant in the stack
							writer.println("push " + "_x" + numcount +"+2" + " w");
							numcount++;
						}else if(isValidId(c)) { // If it is a valid identifier, push it to the stack.
							for(String x:ids) {
								if(x.startsWith(c)) { // Convert c to indexed version.
									c = c+x.substring(c.length());
								}
							}
							writer.println("push "+ c + " w"); // most significant value is lower than its least significant in the stack
							writer.println("push "+ c +"+2 w"); 
							if(!ids.contains(c)) ids.addElement(c+Integer.toString(idEnum));
							idEnum++;
						}else if(c.equals("*")) { // Multiplication
							writer.println("pop _a+2 w");
							writer.println("pop _a w");
							writer.println("pop _b+2 w");
							writer.println("pop _b w");
							writer.println("call mult");
							writer.println("push _result w");
							writer.println("push _result+2 w");
						}else if(c.equals("+")) { // Addition
							writer.println("pop _a+2 w");
							writer.println("pop _a w");
							writer.println("pop _b+2 w");
							writer.println("pop _b w");
							writer.println("call adder");
							writer.println("push _result w");
							writer.println("push _result+2 w");
						}else if(c.equals("^")) { // Power
							writer.println("pop _bb+2 w");
							writer.println("pop _bb w");
							writer.println("pop _aa+2 w");
							writer.println("pop _aa w");
							
							writer.println("call takepower"); // We have some functions at the end of the asm file. We call it here and then return back.
							writer.println("push _resultp w");
							writer.println("push _resultp+2 w");

						}else if(c.equals("=")) { 
							for(String x:ids) {
								if(x.startsWith(varName)) { // Convert varName to indexed version.
									varName = varName+x.substring(varName.length());
								}
							}
							writer.println("pop " + varName+"+2"+ " w");
							writer.println("pop " + varName+ " w");
						}

					}

				}else { // If it is an expression

					while(!action.isEmpty()) { 
						String c = action.remove(0);
						if(isValid(c)) { // If it is a valid constant, push it to the stack.
							numbers.addElement(c);
							writer.println("push " + "_x" + numcount + " w"); // most significant value is lower than its least significant in the stack
							writer.println("push " + "_x" + numcount +"+2" + " w");
							numcount++;
						}else if(isValidId(c)) { // If it is a valid identifier, push it to the stack.
							for(String x:ids) {
								if(x.startsWith(c)) { // Convert c to indexed version.
									c = c+x.substring(c.length());
								}
							}
							writer.println("push "+ c + " w"); // most significant value is lower than its least significant in the stack
							writer.println("push "+ c +"+2 w");
							if(!ids.contains(c)) {
								ids.addElement(c+Integer.toString(idEnum));
								idEnum++;
							}
						}else if(c.equals("*")) { 
							writer.println("pop _a+2 w");
							writer.println("pop _a w");
							writer.println("pop _b+2 w");
							writer.println("pop _b w");
							writer.println("call mult");
							writer.println("push _result w");
							writer.println("push _result+2 w");
						}else if(c.equals("+")) { 
							writer.println("pop _a+2 w");
							writer.println("pop _a w");
							writer.println("pop _b+2 w");
							writer.println("pop _b w");
							writer.println("call adder");
							writer.println("push _result w");
							writer.println("push _result+2 w");
						}else if(c.equals("^")) { 
							writer.println("pop _bb+2 w");
							writer.println("pop _bb w");
							writer.println("pop _aa+2 w");
							writer.println("pop _aa w");
							writer.println("call takepower");
							writer.println("push _resultp w");
							writer.println("push _resultp+2 w");
						}						

					}
					
					writer.println("pop _result+2");
					writer.println("pop _result");
					writer.println("call printtoscreen"); // This is important we have a new function here to print to screen.
					writer.println("call printnewline");


				}	
			}

			writer.println("int 20h");
			// *************Functions, unreachable directly*************
			// This function takes _bth power of _a and writes the result to _resultp (because it uses _result to mult in it.)
			writer.println("takepower:");
			writer.println("mov _resultp+2 w,1h");
			writer.println("mov ax,_aa w"); 
			writer.println("mov _cc w,ax");
			writer.println("mov ax,_aa+2 w"); 
			writer.println("mov _cc+2 w,ax");
			writer.println("powforloop:");
			writer.println("cmp _bb w,0h");
			writer.println("je firstcheck");
			writer.println("jmp firstzero");
			writer.println("firstcheck:");
			writer.println("cmp _bb+2 w,0h");
			writer.println("je gooutoffor");
			writer.println("firstzero:");
			writer.println("mov bx,0001h");
			writer.println("and bx,_bb+2 w");
			writer.println("cmp bx,0h");
			writer.println("je jumpy");
			writer.println("mov ax, _resultp w");
			writer.println("mov _a w,ax");
			writer.println("mov ax, _resultp+2 w"); 
			writer.println("mov _a+2 w,ax");
			writer.println("mov ax,_cc w");
			writer.println("mov _b w,ax");
			writer.println("mov ax,_cc+2 w");
			writer.println("mov _b+2 w,ax");
			writer.println("call mult ;p*p2");
			writer.println("mov ax,_result w");
			writer.println("mov _resultp w,ax");
			writer.println("mov ax,_result+2 w");
			writer.println("mov _resultp+2 w,ax");
			writer.println("jumpy:");
			writer.println("mov ax,_cc w");
			writer.println("mov _a w,ax");
			writer.println("mov ax,_cc w");
			writer.println("mov _b w,ax");
			writer.println("mov ax,_cc+2 w");
			writer.println("mov _a+2 w,ax");
			writer.println("mov ax,_cc+2 w");
			writer.println("mov _b+2 w,ax");
			writer.println("call mult ;p2*p2");
			writer.println("mov ax,_result w");
			writer.println("mov _cc w,ax");
			writer.println("mov ax,_result+2 w");
			writer.println("mov _cc+2 w,ax");
			writer.println("call shright");
			writer.println("jmp powforloop");
			writer.println("gooutoffor:");
			writer.println("ret");
			writer.println("shright:");
			writer.println("mov ax,_bb w");
			writer.println("div w[10h]");
			writer.println("mov _bb w,ax");
			writer.println("mov bx,dx");
			writer.println("shl bx,3");
			writer.println("shr _bb+2 w,1");
			writer.println("add _bb+2,bx");
			writer.println("ret");
			//This functions prints the 32 bit number in _result as hexadecimal.
			writer.println("printnewline:");
			writer.println("mov dl,0ah");
			writer.println("mov ah,02h");
			writer.println("int 21h");
			writer.println("mov dl,0dh");
			writer.println("mov ah,02h");
			writer.println("int 21h");
			writer.println("ret");
			writer.println("printtoscreen:");
			writer.println("mov si,0004h");
			writer.println("mov ax,_result+2 w");
			writer.println("mov bx,10h");
			writer.println("print:");
			writer.println("xor dx,dx");
			writer.println("div bx");
			writer.println("push dx");
			writer.println("dec si");
			writer.println("jz ou");
			writer.println("jmp print");
			writer.println("ou:");
			writer.println("mov si,0004h");
			writer.println("mov ax,_result w");
			writer.println("mov bx,10h");
			writer.println("print2:");
			writer.println("xor dx,dx");
			writer.println("div bx");
			writer.println("push dx");
			writer.println("dec si");
			writer.println("jz ou2");
			writer.println("jmp print2");
			writer.println("ou2:");
			writer.println("mov si,08h");
			writer.println("yaz:");
			writer.println("pop ax");
			writer.println("cmp ax,0ah");
			writer.println("jb asagi");
			writer.println("yukari:");
			writer.println("add ax,57h");
			writer.println("jmp devam");
			writer.println("asagi:");
			writer.println("add ax,30h");
			writer.println("devam:");
			writer.println("mov dl,al");
			writer.println("mov ah,02h");
			writer.println("int 21h");
			writer.println("dec si");
			writer.println("cmp si,0h");
			writer.println("je goaway");
			writer.println("jmp yaz");
			writer.println("goaway:");
			writer.println("ret");
			// Multiplies 32 bits numbers in _a and _ b ; writes the result to _result
			writer.println("mult:");
			writer.println("mov _tohigh w,0h");
			writer.println("mov ax,_b+2 w");
			writer.println("mul _a w");
			writer.println("mov _c w,ax");
			writer.println("mov ax,_b w");
			writer.println("mul _a+2 w");
			writer.println("mov _c+2 w,ax");
			writer.println("mov ax,_b+2 w");
			writer.println("mul _a+2 w");
			writer.println("mov _d w,dx");
			writer.println("mov _d+2 w,ax");
			writer.println("mov ax,_d+2 w");
			writer.println("mov _result+2 w,ax");
			writer.println("mov ax,_c w");
			writer.println("add ax,_c+2 w");
			writer.println("add ax,_d w");
			writer.println("mov _result w,ax");
			writer.println("ret");
			// Adds 32 bits numbers in _a and _ b ; writes the result to _result
			writer.println("adder:");
			writer.println("mov _tohigh w,0h");
			writer.println("mov ax,_a+2 w");
			writer.println("add ax,_b+2 w");
			writer.println("mov _result+2,ax");
			writer.println("jnc skip");
			writer.println("inc _tohigh w");
			writer.println("skip:");
			writer.println("mov ax,_a w");
			writer.println("add ax,_b w");
			writer.println("add ax,_tohigh");
			writer.println("mov _result w,ax");
			writer.println("ret");
			// Variables that are used at the bottom of the file
			// _a, and _b are for 32 bit numbers which will be added, multiplied.
			//_c and _d are assistant variable to multiply
			//_tohigh is for the check of carry
			// _result holds the result
			writer.println("_a dw 0000h,0000h");
			writer.println("_b dw 0000h,0000h");
			writer.println("_c dw 0000h,0000h");
			writer.println("_d dw 0000h,0000h");
			writer.println("_tohigh dw 0000h");
			writer.println("_aa dw 0000h,0000h");
			writer.println("_bb dw 0000h,0000h");
			writer.println("_cc dw 0000h,0000h");
			writer.println("_resultp dw 0000h,0000h");
			writer.println("_result dw 0000h,0000h");
			for(int i =0;i<ids.size();i++) {// Add variables at the end of the file, 2 words for each variable.
				writer.println(ids.get(i) + " dw " + "0000h,0000h"); // to reach 16bit of a variable write [var], to reach last 16 bits of the variable write [var]+2
			}

			// Below we will store all the numbers written in the program. We will use this to push to stack and pop from the stack.
			for(int i =0;i<numbers.size();i++) {
				if(numbers.get(i).length()==9) {
					String mostSig = numbers.get(i).substring(1, 5);
					String leastSig = numbers.get(i).substring(5);
					if(('0'<=leastSig.charAt(0) && '9'>=leastSig.charAt(0))){
						writer.println("_x" + Integer.toString(i+1) + " dw " + mostSig+"h,"+leastSig+"h"); // If the length of it is 9 then we should discard the first 0 character and divide it into two from middle to store.
					} else {
						writer.println("_x" + Integer.toString(i+1) + " dw " + mostSig+"h,"+"0"+leastSig+"h"); // This case is for 0fffffffh example we need to put that 0.
					}
					
				}else {
					String newStr = numbers.get(i);
					while(newStr.length()!=8) {
						newStr ="0" + newStr;
					}
					// System.out.println(newStr);
					if(('0'<=newStr.substring(4).charAt(0) && '9'>=newStr.substring(4).charAt(0))){
						writer.println("_x" + Integer.toString(i+1) + " dw " + newStr.substring(0, 4)+"h,"+newStr.substring(4)+"h"); // If the length of it is 9 then we should discard the first 0 character and divide it into two from middle to store.
					} else {
						writer.println("_x" + Integer.toString(i+1) + " dw " + newStr.substring(0, 4)+"h,"+"0"+ newStr.substring(4)+"h"); // This case is for 0fffffffh example we need to put that 0.
					}
					 // Make the string 8 length then divide it into to store it.
				}
			}

			writer.print("code ends");

			writer.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}