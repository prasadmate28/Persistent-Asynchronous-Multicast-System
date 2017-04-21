

public class TestClass {

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub

		long a = System.currentTimeMillis();
		System.out.println("Systems current time stamp :: "+ a + " secs");
		Thread.sleep(60000);
		System.out.println("time now is "+ (a - System.currentTimeMillis())/1000);
		}

}
