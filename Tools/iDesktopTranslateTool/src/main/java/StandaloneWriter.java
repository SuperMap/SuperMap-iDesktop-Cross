import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import static sun.misc.Version.println;

/**
 * Created with IntelliJ IDEA.
 * User: lixiaoyao
 * Date: 2017/11/24
 * Time: 14:00
 * Description:
 */
public class StandaloneWriter extends XMLWriter {
	public StandaloneWriter(OutputStream out, OutputFormat format)
			throws UnsupportedEncodingException {
		super(out, format);
	}

	protected void writeDeclaration() throws IOException {
		OutputFormat format = getOutputFormat();
		String encoding = format.getEncoding();
		if (!format.isSuppressDeclaration()) {
			writer.write("<?xml version=\"1.0\"");
			if (encoding.equals("UTF8")) {
				if (!format.isOmitEncoding())
					writer.write(" encoding=\"utf-8\"");
			} else {
				if (!format.isOmitEncoding())
					writer.write(" encoding=\"" + encoding + "\"");

			}
			writer.write(" standalone=\"no\"");
			writer.write("?>");
			if (format.isNewLineAfterDeclaration())
				println();
		}
	}
}
