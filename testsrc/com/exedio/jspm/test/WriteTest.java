
package com.exedio.jspm.test;

import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestCase;


public class WriteTest extends TestCase
{
	public static final String EXPECTED_CONDITION_PREFIX = 
		"<html>\n" +
		"\t<head>\n" +
		"\t\t<title>234</title>\n" +
		"\t</head>";

	public static final String EXPECTED_CONDITION_BODY = 
		"\n" +
		"\t<body>\n" +
		"\t\thello-zapp\n" +
		"\t</body>";
	
	public static final String EXPECTED_CONDITION_POSTFIX = 
		"\n" +
		"</html>\n" +
		"\n";

	public static final String EXPECTED_CONDITION_TRUE = 
		EXPECTED_CONDITION_PREFIX +
		EXPECTED_CONDITION_BODY +
		EXPECTED_CONDITION_POSTFIX;

	public static final String EXPECTED_CONDITION_FALSE = 
		EXPECTED_CONDITION_PREFIX +
		EXPECTED_CONDITION_POSTFIX;

	public void testWrite()
	{
		{
			final StringWriter buf = new StringWriter();
			final PrintWriter print = new PrintWriter(buf);
			Test_Jspm.writeIt(print, true);
			print.flush();
			assertEquals(EXPECTED_CONDITION_TRUE, buf.getBuffer().toString());
		}
		{
			final StringWriter buf = new StringWriter();
			final PrintWriter print = new PrintWriter(buf);
			Test_Jspm.writeIt(print, false);
			print.flush();
			assertEquals(EXPECTED_CONDITION_FALSE, buf.getBuffer().toString());
		}
	}

}
