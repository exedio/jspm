package com.exedio.jspm;

import java.io.IOException;
import java.io.Writer;

class BufferingWriter extends Writer
{
	private final Writer nested;

	private final StringBuffer buffer;

	BufferingWriter(final Writer nested)
	{
		this.nested = nested;
		this.buffer = new StringBuffer();
	}

	@Override
	public void write(final char[] cbuf, final int off, final int len)
	{
		buffer.append(cbuf, off, len);
	}

	@Override
	public void flush() throws IOException
	{
		nested.flush();
	}

	@Override
	public void close() throws IOException
	{
		nested.close();
	}

	void flushBuffer() throws IOException
	{
		nested.write(buffer.toString());
		buffer.setLength(0);
	}
}
