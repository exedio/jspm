package com.exedio.jspm;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import junit.framework.TestCase;

public class SourceRefWriterTest extends TestCase
{
	public void testWrite() throws IOException
	{
		final StringWriter sw = new StringWriter();
		final Config config = new Config();
		config.setSourceRefTargetPosition(3);
		final SourceRefWriter sourceRefWriter = new SourceRefWriter(sw, new File("test.src"), config);
		sourceRefWriter.write("1");
		assertEquals("1", sw.toString());

		sourceRefWriter.write("\n");
		assertEquals("1\t// test.src line 1\n", sw.toString());
	}

	public void testTabsToFill()
	{
		assertTabsToFill(2, 0, 6);
		assertTabsToFill(2, 1, 6);
		assertTabsToFill(2, 2, 6);
		assertTabsToFill(1, 3, 6);
		assertTabsToFill(1, 4, 6);
		assertTabsToFill(1, 5, 6);
		assertTabsToFill(1, 6, 6);
		assertTabsToFill(1, 7, 6);
		assertTabsToFill(1, Integer.MAX_VALUE, 6);

		assertTabsToFill(2, 0, 4);
		assertTabsToFill(2, 0, 5);
	}

	private void assertTabsToFill(int expectedTabCount, int charsInLine, int sourceRefTargetPosition)
	{
		final Config config = new Config();
		config.setSourceRefTargetPosition(sourceRefTargetPosition);
		final SourceRefWriter sourceRefWriter = new SourceRefWriter(new StringWriter(), new File("test.src"), config);
		final String tabString = sourceRefWriter.tabsToFill(charsInLine);
		for (int i = 0; i < tabString.length(); i++)
		{
			assertEquals('\t', tabString.charAt(i));
		}
		assertEquals(expectedTabCount, tabString.length());
	}
}
