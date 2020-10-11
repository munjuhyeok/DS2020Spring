import java.io.*;
import java.util.Stack;

public class CalculatorTest
{
	public static void main(String args[])
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		while (true)
		{
			try
			{
				String input = br.readLine();
				if (input.compareTo("q") == 0)
					break;

				command(input);
			}
			catch (Exception e)
			{
				System.out.println("입력이 잘못되었습니다. 오류 : " + e.toString());
			}
		}
	}
    
	/**
	 * priority of operator
	 */
    private static int priority(String op) {
    	if (op.equals("+") || op.equals("-")) {
    		return 1;
    	}else if (op.equals("*") || op.equals("/")|| op.equals("%")) {
    		return 2;
    	}else if (op.equals("~")) {
    		return 3;
    	}else if (op.equals("^")) {
    		return 4;
    	}else {
    		return 0; //for "(" or  ")"
    	}
    }
    
	/**
	 * find length of postfix array for given infix array
	 */
    private static int postfixlength(String [] infix) {
    	int count = infix.length;
    	for(String i : infix) {
    		if(i.equals("(") || i.equals(")")){
    			count -=1;
    		}
    	}
    	return count;
    }
    
	/**
	 * convert infix String array to postfix String array
	 */
    private static String[] infixtopostfix(String[] infix) throws java.util.EmptyStackException{
		String[] postfix = new String[postfixlength(infix)];
		Stack<String> stack = new Stack<String>(); //temporary stack of operators
		
		int count = 0; //current index in postfix
		
		for (String i : infix) {
			if(i.matches("\\d+")) {
				postfix[count] = i;
				count += 1;
			}else if (i.equals("(")) {
				stack.push(i);
			}else if (i.equals(")")) { 
				while(!stack.peek().equals("(")){ 
					postfix[count] = stack.pop();
					count += 1;
				}
				stack.pop(); //throw java.util.EmptyStackException for unexpected ")"
			}else if (i.equals("+")||i.equals("-")||i.equals("*")||i.equals("/")||i.equals("%")) { // when i is left-associative operator
				while(!stack.isEmpty()&&(priority(stack.peek())>=priority(i))){
					postfix[count] = stack.pop();
					count += 1;
				}
				stack.push(i);
			}else if (i.equals("^")||i.equals("~")) { // when i is right-associative operator
				while(!stack.isEmpty()&&(priority(stack.peek())>priority(i))){
					postfix[count] = stack.pop();
					count += 1;
				}
				stack.push(i);
			}
		}
		while (!stack.isEmpty()) {
			if (stack.peek().equals("(")){
				throw new java.util.EmptyStackException(); //throw java.util.EmptyStackException if # of "(" > # of ")"
			}
            postfix[count] = stack.pop();
            count += 1;
		}
		return postfix;
    }
    
	/**
	 * do binary operation
	 */
    private static long operation(long b, long a, String op) throws ArithmeticException{
    	switch(op) {
    	case "+":
    		return a+b;
    	case "-":
    		return a-b;
    	case "*":
    		return a*b;
    	case "/":
    		return a/b;
    	case "%":
    		return a%b;
    	case "^":
    		if(b<0) {
    			throw new ArithmeticException();
    		}
    		return (long)Math.pow(a, b);
    	default:
    		throw new ArithmeticException();
    	}
    }
    
    
	/**
	 * calculate result for given postfix String array
	 */
    private static long calc(String[] postfix) throws ArithmeticException, java.util.EmptyStackException {
    	Stack<Long> stack = new Stack<Long>();
    	for(String i : postfix) {
    		if (i.matches("\\d+")) {
    			stack.push(Long.parseLong(i));
    		}else if(i.matches("~")) {
    			stack.push(-stack.pop());
    		}else { // binary operation
    			stack.push(operation(stack.pop(),stack.pop(),i));
    		}
    	}
    	long result = stack.pop();
    	if (stack.isEmpty()) {
    		return result;
    	}else { // stack should be empty
    		throw new java.util.EmptyStackException();
    	}
    }
    
	private static void command(String input)
	{
		if(input.matches(".*\\d\\s+\\d.*")){
			System.out.println("ERROR"); // ERROR if there is no operator between two operands
			return;
		}
		
		input = input.replaceAll("\\s+", ""); //remove space
		String[] infix = input.split("((?<=\\D)|(?=\\D))"); // infix as an array ~1+~2 -> {~,1,+,~,2}
		
		//change unary from "-" to "~"
		if (infix[0].equals("-")) {
			infix[0] = "~";
		}
		for (int i=1;i<infix.length;i++) {
			if (infix[i-1].matches("[^)0-9]") && infix[i].equals("-")) {
				infix[i] = "~";
			}
		}
		
		try{
			String[] postfix = infixtopostfix(infix);			
			
			// postfix String to be printed
			String postfix_str = "";
			for (String i:postfix) {
				postfix_str += i + " ";
			}
			postfix_str = postfix_str.substring(0,postfix_str.length()-1); //remove last space
			
			long result = (calc(postfix));
			
			System.out.print(postfix_str+"\n");
			System.out.print(result+"\n");
		}
		catch (java.util.EmptyStackException | ArithmeticException e) {
			System.out.println("ERROR");
			return;
		}
	}
}
