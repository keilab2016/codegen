package util;
/**
 * 
 */

/**
 * @author member
 *
 */
public class GenerateRandom {

	public static String randomText(String text, int length) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			sb.append(text.charAt((int)(Math.random() * text.length())));
		}
		return sb.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String text = "abcdefghijklmnopqrstuvwxyz";
		System.out.println(randomText(text, 32));
		System.out.println(randomText("0123456789", 32));
	}

}
