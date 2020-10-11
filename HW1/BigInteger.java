import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
  
  
public class BigInteger
{
    public static final String QUIT_COMMAND = "quit";
    public static final String MSG_INVALID_INPUT = "INVALID INPUT";
    
    public static final Pattern Operator_pattern = Pattern.compile("\\d\\s*\\D");
    public static final Pattern number_pattern = Pattern.compile("\\d+");
    
    public static final int MAX_INPUT_SIZE=100;
    public static final int MAX_SIZE = 201;

    private static final byte[] get_zero()
    {
    	byte[] zero = new byte[MAX_SIZE];
    	for (int i=0;i<MAX_SIZE;i++) {
    		zero[i]=0;
    	}
    	return zero;
    }
    
    private boolean sign; //true for + sign for - sign
    private byte[] abs = new byte[MAX_SIZE]; //absolute value, 1234={4,3,2,1,0,0,...0,0}
    private int size; //# of digits, but size=0 for 0
    
    private boolean get_sign()
    {
    	return sign;
    }
    
    private byte[] get_abs()
    {
    	return abs;
    }
    
    private int get_size()
    {
    	return size;
    }
    
  
    public BigInteger(boolean b, byte[] num1)
    {
    	sign = b;
        abs = get_zero();
        for(int i=0;i<num1.length;i++) {
           abs[i] = num1[i];
        }
        
        size=0;
    	for (int i=num1.length-1;i>=0;i--) {
    		if (num1[i] != 0 ) {
    			size =i+1;
    			break;
    		}
    	}
    }
  
    public BigInteger(boolean b, String s)
    {
        sign = b;
        size = s.length();
        if(s.equals("0")) size =0;
        abs = get_zero();
        for(int i=s.length()-1;i>=0;i--) {
           abs[i] = (byte)s.charAt(s.length()-i-1);
           abs[i] -= 48;
        }
        
    }
    private boolean absbiggerthan(BigInteger big) //absolute value is bigger than
    {
    	if (size>big.size) return true;
    	if (size<big.size) return false;
    	for(int i = size-1;i>=0;i--) {
    		if (abs[i]>big.get_abs()[i]) return true;
    		if (abs[i]<big.get_abs()[i]) return false;
    	}
    	return false;
    }

    private static byte[] sum(byte[] a, byte[] b) //sum of non-negative inputs
    {
    	byte[] result = get_zero();
    	for(int i=0;i<MAX_INPUT_SIZE;i++) {
    		result[i]+=a[i]+b[i];
    		if(result[i]>=10) {
    			result[i+1]+=1;
    			result[i]-=10;
    		}
    		
    	}
    	return result;
    }
  
    private static byte[] diff(byte[] a, byte[] b)//difference between non-negative inputs
    {
    	BigInteger temp1=new BigInteger(true,a);
    	BigInteger temp2=new BigInteger(true,b);
    	
    	byte[] bigger = a;
    	byte[] smaller = b;
    	
    	if(!temp1.absbiggerthan(temp2)) {
    		bigger=b;
    		smaller=a;
    	}
    	byte[] result = get_zero();
    	for(int i=0;i<MAX_INPUT_SIZE;i++) {
    		result[i]+=bigger[i]-smaller[i];
    		if(result[i]<0) {
    			result[i+1]-=1;
    			result[i]+=10;
    		}
    		
    	}
    	return result;
    }
    
    private static byte[] product(byte[] a, byte[] b)//product of non-negative inputs
    {
    	byte[] result = get_zero();
    	for(int i=0;i<MAX_INPUT_SIZE;i++) {
    		for(int j=0;j<MAX_INPUT_SIZE;j++) {
    			result[i+j]+=a[i]*b[j];
        		result[i+j+1] += result[i+j]/10;
        		result[i+j] -= (result[i+j]/10)*10;
    		}
    		
    	}
    	return result;
    }
  

    public BigInteger add(BigInteger big)
    {
    	boolean sign1 = get_sign();
    	boolean sign2 = big.get_sign();
    	byte[] abs1 = get_abs();
    	byte[] abs2 = big.get_abs();
    	
    	
    	if(sign1 && sign2) {
    		return new BigInteger(true, sum(abs1, abs2));
    	}else if (!sign1 && sign2) {
    		return new BigInteger(big.absbiggerthan(this),diff(abs1,abs2));
    	}
    	else if (sign1 && !sign2) {
    		return new BigInteger(this.absbiggerthan(big),diff(abs1,abs2));
    	}
    	else {
    		return new BigInteger(false, sum(abs1, abs2));
    	}
    }
    public BigInteger subtract(BigInteger big)
    {
    	boolean sign1 = get_sign();
    	boolean sign2 = big.get_sign();
    	byte[] abs1 = get_abs();
    	byte[] abs2 = big.get_abs();
    	
    	if(sign1 && sign2) {
    		return new BigInteger(this.absbiggerthan(big), diff(abs1, abs2));
    	}else if (!sign1 && sign2) {
    		return new BigInteger(false,sum(abs1,abs2));
    	}
    	else if (sign1 && !sign2) {
    		return new BigInteger(true,sum(abs1,abs2));
    	}
    	else {
    		return new BigInteger(big.absbiggerthan(this), diff(abs1, abs2));
    	}
    }
  
    public BigInteger multiply(BigInteger big)
    {
    	boolean sign1 = get_sign();
    	boolean sign2 = big.get_sign();
    	byte[] abs1 = get_abs();
    	byte[] abs2 = big.get_abs();
    	
    	
    	if((sign1 && sign2)||(!sign1 && !sign2)) {
    		return new BigInteger(true, product(abs1, abs2));
    	}else{
    		return new BigInteger(false, product(abs1, abs2));
    	}
    }
  
    @Override
    public String toString()
    {
    	if (size==0) return "0";
    	String result = "";
    	if (!sign) result += "-";
    	for (int i=get_size()-1;i>=0;i--) {
    		result += String.valueOf(abs[i]);       
    		}
    	return result;
    }
  
    static BigInteger evaluate(String input) throws IllegalArgumentException
    {
    	Matcher m = Operator_pattern.matcher(input);
    	m.find();
    	int op_position = m.end()-1; //position of operator
    	char op = input.charAt(op_position); //operator
    	
    	String temp1 = input.substring(0,op_position); //String before operator
    	String temp2 = input.substring(op_position+1); //String after operator
    	
    	m = number_pattern.matcher(temp1);
    	m.find();
    	BigInteger num1 = new BigInteger(!temp1.matches(".*-.*"),m.group()); // negative if there is -
    	
    	m = number_pattern.matcher(temp2);
    	m.find();
    	BigInteger num2 = new BigInteger(!temp2.matches(".*-.*"),m.group()); // negative if there is -
    	
    	switch(op) {
    	case('+'):
    		return num1.add(num2);
    	case('-'):
    		return num1.subtract(num2);
    	case('*'):
    		return num1.multiply(num2);
    	}
    	
    	return num1;
    	
    }
  
    public static void main(String[] args) throws Exception
    {
        try (InputStreamReader isr = new InputStreamReader(System.in))
        {
            try (BufferedReader reader = new BufferedReader(isr))
            {
                boolean done = false;
                while (!done)
                {
                    String input = reader.readLine();
                    try
                    {
                        done = processInput(input);
                        
                    }
                    catch (IllegalArgumentException e)
                    {
                        System.err.println(MSG_INVALID_INPUT);
                    }
                }
            }
        }
    }
  
    static boolean processInput(String input) throws IllegalArgumentException
    {
        boolean quit = isQuitCmd(input);
  
        if (quit)
        {
            return true;
        }
        else
        {
            BigInteger result = evaluate(input);
            System.out.println(result.toString());
  
            return false;
        }
    }
  
    static boolean isQuitCmd(String input)
    {
        return input.equalsIgnoreCase(QUIT_COMMAND);
    }
}
