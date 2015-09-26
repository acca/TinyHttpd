
public class add {
	public static void main( String argv[] ){
		System.out.println("Process add started");
		if ( (argv.length == 1) || (argv.length > 2) ) {
			System.out.println("This process expects, none or 2 parameters");
		}
		else if (argv.length == 2) {
			int sum = 0;
			for (int i=0; i<argv.length; i++) {
				try {
					sum += Integer.parseInt(argv[i]);					
				} catch (NumberFormatException e) {
					System.err.println("Failed trying to parse a non-numeric argument, " + argv[i]);
				}
			}
			System.out.println("result:"+String.valueOf(sum));
		}
	}	
}
