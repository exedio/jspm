package com.exedio.jspm;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

class SourceRefWriter extends Writer
{
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
			return tabsToFill(charsInLineCount)+"// "+sourceFile.getName()+" line "+sourceLineCount;
		}
		else
		{
			return "";
		}
	}

	/** get the tab string to fill up a line of length 'charsInLineCount'; always return at least one tab */
	String tabsToFill(int charsInLineCount)
	{
		final int charsPerTab = config.getCharsPerTab();
		final int targetLength = config.getSourceRefTargetPosition();
		final StringBuilder tabs = new StringBuilder();
		for ( int i=charsInLineCount; i<targetLength; i = ((i/charsPerTab)+1)*charsPerTab )
		{
			tabs.append('\t');
		}
		if ( tabs.length()==0 )
		{
			tabs.append('\t');
		}
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
			updateCharsInLine(c);
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

	void updateCharsInLine(final char c)
	{
		switch (c)
		{
			case '\n':
			case '\r':
				charsInLine = 0;
				break;
			case '\t':
				final int charsPerTab = config.getCharsPerTab();
				charsInLine = ((charsInLine/charsPerTab)+1)*charsPerTab;
				break;
			default:
				charsInLine++;
				break;
		}
	}

	void incrementSourceLineCount()
	{
		sourceLine++;
	}
}
