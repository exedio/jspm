
package com.exedio.jspm.test;

import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestCase;


public class WriteTest extends TestCase
{
	public static final String EXPECTED = 
		"<html>\n" +
		"\t<head>\n" +
		"\t\t<title>234</title>\n" +
		"\t</head>zapp\n" +
		"\t<body>\n" +
		"\t\thello\n" +
		"\t</body>\n" +
		"</html>\n" +
		"\n";

	public void testWrite()
	{
		final StringWriter buf = new StringWriter();
		final PrintWriter print = new PrintWriter(buf);
		Test_Jspm.writeIt(print);
		print.flush();
		assertEquals(EXPECTED, buf.getBuffer().toString());
	}

}
