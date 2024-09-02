/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.exedio.jspm;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

class SourceRefWriter extends Writer
{
	private final BufferingWriter nested;

	private int charsInLine = 0;
	private int sourceLine = 1;
	private final Config config;
	private final File sourceFile;

	SourceRefWriter(final BufferingWriter nested, final File sourceFile, final Config config)
	{
		this.nested = nested;
		this.config = config;
		this.sourceFile = sourceFile;
	}

	private String sourceRef(final int sourceLineCount, final int charsInLineCount)
	{
		if (config.isAddSourceReferences())
		{
			return tabsToFill(charsInLineCount)+"// "+sourceFile.getName()+':'+sourceLineCount;
		}
		else
		{
			return "";
		}
	}

	/** get the tab string to fill up a line of length 'charsInLineCount'; always return at least one tab */
	String tabsToFill(final int charsInLineCount)
	{
		final int charsPerTab = config.getCharsPerTab();
		final int targetLength = config.getSourceRefTargetPosition();
		final StringBuilder tabs = new StringBuilder();
		for ( int i=charsInLineCount; i<targetLength; i = ((i/charsPerTab)+1)*charsPerTab )
		{
			tabs.append('\t');
		}
		if ( tabs.isEmpty() )
		{
			tabs.append('\t');
		}
		return tabs.toString();
	}

	@Override
	@SuppressWarnings("HardcodedLineSeparator")
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

	@SuppressWarnings("HardcodedLineSeparator")
	private void updateCharsInLine(final char c)
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

	void flushBuffer() throws IOException
	{
		nested.flushBuffer();
	}
}
