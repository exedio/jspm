package com.exedio.jspm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import org.junit.jupiter.api.Test;

@SuppressWarnings("HardcodedLineSeparator")
class BufferingWriterTest
{
	@Test
	void write() throws IOException
	{
		final Config config = new Config();
		config.setAddSourceReferences(false);
		final StringWriter sw = new StringWriter();
		try(final BufferingWriter bw = new BufferingWriter(sw);
			final SourceRefWriter writer = new SourceRefWriter(bw, new File("test.jspm"), config)
		)
		{
			writer.write("1");
			assertEquals("", sw.toString());

			writer.write("fdfd\ngfdg\n");
			assertEquals("", sw.toString());

			writer.flushBuffer();
			assertEquals("1fdfd\ngfdg\n", sw.toString());
		}
	}


	@Test
	void writeWithSourceRef() throws IOException
	{
		final Config config = new Config();
		config.setAddSourceReferences(true);
		config.setSourceRefTargetPosition(3);
		final StringWriter sw = new StringWriter();
		try(final BufferingWriter bw = new BufferingWriter(sw);
			final SourceRefWriter writer = new SourceRefWriter(bw, new File("test.jspm"), config)
		)
		{
			writer.write("line 1\n");
			writer.incrementSourceLineCount();
			assertEquals("", sw.toString());

			writer.write("line 2\n");
			writer.incrementSourceLineCount();

			writer.write("line 3\n");
			writer.incrementSourceLineCount();
			assertEquals("", sw.toString());

			writer.flushBuffer();
			assertEquals("line 1\t// test.jspm:1\nline 2\t// test.jspm:2\nline 3\t// test.jspm:3\n", sw.toString());
		}
	}

	@Test
	void flush() throws IOException
	{
		final Config config = new Config();
		config.setAddSourceReferences(false);
		final StringWriter sw = new StringWriter();
		try(final BufferingWriter bw = new BufferingWriter(sw);
			final SourceRefWriter writer = new SourceRefWriter(bw, new File("test.jspm"), config)
		)
		{
			writer.write("1");
			writer.write("\\n\" +\n\t\"");
			assertEquals("", sw.toString());
			writer.flush();
			assertEquals("", sw.toString());
		}
	}
}
