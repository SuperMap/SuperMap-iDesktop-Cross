import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lixiaoyao
 * Date: 2017/11/22
 * Time: 9:08
 * Description:Config file General operating functions class
 */
public class ConfigFileUtilities {

	public static Document readConfigFile(String filePath) throws Exception {
		SAXReader sax = new SAXReader();//创建一个SAXReader对象
		File xmlFile = new File(filePath);//根据指定的路径创建file对象
		Document document = sax.read(xmlFile);//获取document对象,如果文档无节点，则会抛出Exception提前结束
		return document;
	}

	public static void saveDocument(Document document, String filePath) throws IOException {
		Writer osWrite = new OutputStreamWriter(new FileOutputStream(new File(filePath)));//创建输出流
		OutputFormat format = OutputFormat.createPrettyPrint();  //获取输出的指定格式
		format.setEncoding("UTF-8");//设置编码 ，确保解析的xml为UTF-8格式
//		XMLWriter writer = new XMLWriter(osWrite, format);//XMLWriter 指定输出文件以及格式
		StandaloneWriter writer = new StandaloneWriter(new FileOutputStream(filePath), format);
		writer.write(document);//把document写入xmlFile指定的文件(可以为被解析的文件或者新创建的文件)
		writer.flush();
		writer.close();
	}

	/**
	 * @param node
	 * @description From the specified node, traverse all nodes below this node
	 */
	public static void getNodes(Element node) {
		try {

//			System.out.println("--------------------");
//			//当前节点的名称、文本内容和属性
//			System.out.println("当前节点名称：" + node.getName());//当前节点名称
//			System.out.println("当前节点的内容：" + node.getTextTrim());//当前节点名称
			List<Attribute> listAttr = node.attributes();//当前节点的所有属性的list
			for (Attribute attr : listAttr) {//遍历当前节点的所有属性
//				String name = attr.getName();//属性名称
//				String value = attr.getValue();//属性的值
//				System.out.println("属性名称：" + name + "属性值：" + value);
			}

			//递归遍历当前节点所有的子节点
			List<Element> listElement = node.elements();//所有一级子节点的list
			for (Element e : listElement) {//遍历所有一级子节点
				getNodes(e);//递归
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void editAttribute(Element root,String nodeName){
		Attribute attrDate=root.attribute(nodeName);//获取此节点的指定属性
		attrDate.setValue("libaba");//更改此属性值

	}
}
