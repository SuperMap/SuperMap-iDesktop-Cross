/**
 * Created with IntelliJ IDEA.
 * User: lixiaoyao
 * Date: 2017/11/27
 * Time: 14:04
 * Description:Punctuation conversion
 */
public class PunctuationUtilities {

	private PunctuationUtilities() {
		// ignore
	}

	/**
	 * Semiangle sign full width symbols
	 *
	 * @param input
	 * @return
	 */
	public static String ToSBC(String input) { //半角转全角：
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 32) {
				c[i] = (char) 12288;
				continue;
			}
			if (c[i] < 127) c[i] = (char) (c[i] + 65248);
		}
		return new String(c);
	}

	/**
	 * The full width half angle symbol sign
	 *
	 * @param input
	 * @return
	 */
	public static String ToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		String result=new String(c);
		result=result.replace("。",".");
		return result;
	}

}
