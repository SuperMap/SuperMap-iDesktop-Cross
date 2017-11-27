import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: lixiaoyao
 * Date: 2017/11/23
 * Time: 12:53
 * Description:
 */
public class TextSimilarity {

//	public static double getSimilarity(String doc1, String doc2) {
//		if (doc1 != null && doc1.trim().length() > 0 && doc2 != null && doc2.trim().length() > 0) {
//			Map<Integer, int[]> AlgorithmMap = new HashMap<Integer, int[]>();
//			//将两个字符串中的中文字符以及出现的总数封装到，AlgorithmMap中
//			for (int i = 0; i < doc1.length(); i++) {
//				char d1 = doc1.charAt(i);
//				if (isHanZi(d1)) {//标点和数字不处理
//					int charIndex = getGB2312Id(d1);//保存字符对应的GB2312编码
//					if (charIndex != -1) {
//						int[] fq = AlgorithmMap.get(charIndex);
//						if (fq != null && fq.length == 2) {
//							fq[0]++;//已有该字符，加1
//						} else {
//							fq = new int[2];
//							fq[0] = 1;
//							fq[1] = 0;
//							AlgorithmMap.put(charIndex, fq);//新增字符入map
//						}
//					}
//				}
//			}
//
//			for (int i = 0; i < doc2.length(); i++) {
//				char d2 = doc2.charAt(i);
//				if (isHanZi(d2)) {
//					int charIndex = getGB2312Id(d2);
//					if (charIndex != -1) {
//						int[] fq = AlgorithmMap.get(charIndex);
//						if (fq != null && fq.length == 2) {
//							fq[1]++;
//						} else {
//							fq = new int[2];
//							fq[0] = 0;
//							fq[1] = 1;
//							AlgorithmMap.put(charIndex, fq);
//						}
//					}
//				}
//			}
//
//			Iterator<Integer> iterator = AlgorithmMap.keySet().iterator();
//			double sqdoc1 = 0;
//			double sqdoc2 = 0;
//			double denominator = 0;
//			while (iterator.hasNext()) {
//				int[] c = AlgorithmMap.get(iterator.next());
//				denominator += c[0] * c[1];
//				sqdoc1 += c[0] * c[0];
//				sqdoc2 += c[1] * c[1];
//			}
//
//			return denominator / Math.sqrt(sqdoc1 * sqdoc2);//余弦计算
//		} else {
//			throw new NullPointerException(" the Document is null or have not cahrs!!");
//		}
//	}
//
//	public static boolean isHanZi(char ch) {
//		// 判断是否汉字
//		return (ch >= 0x4E00 && ch <= 0x9FA5);
//	    /*if (ch >= 0x4E00 && ch <= 0x9FA5) {//汉字
//            return true;
//        }else{
//            String str = "" + ch;
//            boolean isNum = str.matches("[0-9]+");
//            return isNum;
//        }*/
//        /*if(Character.isLetterOrDigit(ch)){
//            String str = "" + ch;
//            if (str.matches("[0-9a-zA-Z\\u4e00-\\u9fa5]+")){//非乱码
//                return true;
//            }else return false;
//        }else return false;*/
//	}
//
//	/**
//	 * 根据输入的Unicode字符，获取它的GB2312编码或者ascii编码，
//	 *
//	 * @param ch 输入的GB2312中文字符或者ASCII字符(128个)
//	 * @return ch在GB2312中的位置，-1表示该字符不认识
//	 */
//	public static short getGB2312Id(char ch) {
//		try {
//			byte[] buffer = Character.toString(ch).getBytes("GB2312");
//			if (buffer.length != 2) {
//				// 正常情况下buffer应该是两个字节，否则说明ch不属于GB2312编码，故返回'?'，此时说明不认识该字符
//				return -1;
//			}
//			int b0 = (int) (buffer[0] & 0x0FF) - 161; // 编码从A1开始，因此减去0xA1=161
//			int b1 = (int) (buffer[1] & 0x0FF) - 161;
//			return (short) (b0 * 94 + b1);// 第一个字符和最后一个字符没有汉字，因此每个区只收16*6-2=94个汉字
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		return -1;
//	}

	private static int min(int one, int two, int three) {
		int min = one;
		if (two < min) {
			min = two;
		}
		if (three < min) {
			min = three;
		}
		return min;
	}

	public static int ld(String str1, String str2) {
		int d[][]; // 矩阵
		int n = str1.length();
		int m = str2.length();
		int i; // 遍历str1的
		int j; // 遍历str2的
		char ch1; // str1的
		char ch2; // str2的
		int temp; // 记录相同字符,在某个矩阵位置值的增量,不是0就是1
		if (n == 0) {
			return m;
		}
		if (m == 0) {
			return n;
		}
		d = new int[n + 1][m + 1];
		for (i = 0; i <= n; i++) { // 初始化第一列
			d[i][0] = i;
		}
		for (j = 0; j <= m; j++) { // 初始化第一行
			d[0][j] = j;
		}
		for (i = 1; i <= n; i++) { // 遍历str1
			ch1 = str1.charAt(i - 1);
			// 去匹配str2
			for (j = 1; j <= m; j++) {
				ch2 = str2.charAt(j - 1);
				if (ch1 == ch2) {
					temp = 0;
				} else {
					temp = 1;
				}
				// 左边+1,上边+1, 左上角+temp取最小
				d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1]+ temp);
			}
		}
		return d[n][m];
	}
	public static double sim(String str1, String str2) {
		try {
			double ld = (double)ld(str1, str2);
			return (1-ld/(double)Math.max(str1.length(), str2.length()));
		} catch (Exception e) {
			return 0.1;
		}
	}

}
