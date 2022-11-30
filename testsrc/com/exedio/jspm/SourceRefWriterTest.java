package com.exedio.jspm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import org.junit.jupiter.api.Test;

class SourceRefWriterTest
{
	@Test
	@SuppressWarnings("HardcodedLineSeparator")
	void testWrite() throws IOException
	{
		final StringWriter sw = new StringWriter();
		final Config config = new Config();
		config.setSourceRefTargetPosition(3);
		try(SourceRefWriter sourceRefWriter = new SourceRefWriter(new BufferingWriter(sw), new File("test.src"), config))
		{
			sourceRefWriter.write("1");
			sourceRefWriter.flushBuffer();
			assertEquals("1", sw.toString());

			sourceRefWriter.write("\n");
			sourceRefWriter.flushBuffer();
			assertEquals("1\t// test.src:1\n", sw.toString());
		}
	}

	@Test
	void testTabsToFill() throws IOException
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

	private static void assertTabsToFill(final int expectedTabCount, final int charsInLine, final int sourceRefTargetPosition) throws IOException
	{
		final Config config = new Config();
		config.setSourceRefTargetPosition(sourceRefTargetPosition);
		try(SourceRefWriter sourceRefWriter = new SourceRefWriter(new BufferingWriter(new StringWriter()), new File("test.src"), config))
		{
			final String tabString = sourceRefWriter.tabsToFill(charsInLine);
			for (int i = 0; i < tabString.length(); i++)
			{
				assertEquals('\t', tabString.charAt(i));
			}
			assertEquals(expectedTabCount, tabString.length());
		}
	}
}
