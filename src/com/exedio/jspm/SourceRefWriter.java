package com.exedio.jspm;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

class SourceRefWriter extends Writer
{
	static final int charsPerTab = 3;

	private final Writer nested;

	private int charsInLine = 0;
	private int sourceLine = 1;
	private final Config config;
	private final File sourceFile;

	SourceRefWriter(Writer nested, final File sourceFile, final Config config)
	{
		this.nested = nested;
		this.config = config;
		this.sourceFile = sourceFile;
	}

	private String sourceRef(final int sourceLineCount, final int charsInLineCount)
	{
		if (config.isAddSourceReferences())
		{
			return fillWithTabs(charsInLineCount)+"// "+sourceFile.getName()+" line "+sourceLineCount;
		}
		else
		{
			return "";
		}
	}

	private String fillWithTabs(int charsInLineCount)
	{
		final int charsPerTab = 3;
		final int targetLength = 100;
		final StringBuilder tabs = new StringBuilder();
		for ( int i=charsInLineCount; i<targetLength; i = ((i/charsPerTab)+1)*charsPerTab )
		{
			tabs.append('\t');
		}
		tabs.append('\t');
		return tabs.toString();
	}

	@Override
	public void write(final char[] cbuf, final int off, final int len) throws IOException
	{
		for ( int i=off; i<off+len; i++ )
		{
			final char c = cbuf[i];
			if ( c=='\n' )
			{
				nested.write(sourceRef(sourceLine, charsInLine));
			}
			nested.write(c);
			charsInLine = updateCharsInLine(charsInLine, c);
		}
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

	static int updateCharsInLine(final int charsInLine, final char c)
	{
		switch (c)
		{
			case '\n':
			case '\r':
				return 0;
			case '\t':
				return ((charsInLine/charsPerTab)+1)*charsPerTab;
			default:
				return charsInLine+1;
		}
	}

	void incrementSourceLineCount()
	{
		sourceLine++;
	}
}
