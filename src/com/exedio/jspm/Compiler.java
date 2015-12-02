/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.concurrent.atomic.AtomicInteger;

final class Compiler
{
	private static final String FILE_SUFFIX = ".jspm";

	static enum State
	{
		HTML,
		HTML_LESS,
		JAVA,
		JAVA_FIRST,
		JAVA_PERCENT;
	}

	private static final String METHOD_SUFFIX = "\");\n";
	private static final String METHOD_SUFFIX_EXPRESSION = ");\n";

	final File sourceFile;
	final File targetFile;
	private final Config config;

	Compiler(final String fileName, final Config config)
	{
		this.sourceFile = new File(fileName);

		final String targetFileName;
		if(fileName.endsWith(FILE_SUFFIX))
			targetFileName = fileName.substring(0, fileName.length()-FILE_SUFFIX.length())+"_Jspm.java";
		else
			targetFileName = fileName;

		this.targetFile = new File(targetFileName);

		if(config==null)
			throw new NullPointerException("config");
		this.config = config;
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

	private String methodStringBreak(final int sourceLineCount, final int charsInLineCount)
	{
		return "\" +"+sourceRef(sourceLineCount, charsInLineCount)+"\n\t\"";
	}

	void translateIfDirty(final AtomicInteger count) throws IOException
	{
		if(isDirty())
			translate(count);
	}

	boolean isDirty()
	{
		final long target = targetFile.lastModified();
		if(target==0l)
			return true;

		final long source = sourceFile.lastModified();
		if(source==0l)
			return true;

		return target<source;
	}

	void translate(final AtomicInteger count) throws IOException
	{
		if(config.isVerbose())
			System.out.println("Translating " + sourceFile);
		count.incrementAndGet();

		final String prefixStatic     = "out." + config.getMethodStatic()     + "(\"";
		final String prefixExpression = "out." + config.getMethodExpression() + '(';
		Reader source = null;
		Writer o = null;
		try
		{
			source = new FileReader(sourceFile);
			o = new FileWriter(targetFile);

			State state = State.HTML;
			char cback = '*';
			boolean expression = true;
			int htmlCharCount = 0;
			int sourceLineCount = 0;
			int charsInLineCount = 0;
			for(int ci = source.read(); ci>0; ci = source.read())
			{
				final char c = (char)ci;
				charsInLineCount = updateCharsInLine(c, charsInLineCount);

				if (c=='\r')
				{
					// ignore \r, so the same files are generated on Linux and Windows
					continue;
				}
				if ( c=='\n' ) sourceLineCount++;

				switch(state)
				{
					case HTML:
						switch(c)
						{
							case '<':
								state = State.HTML_LESS;
								cback = c;
								break;
							case '"':
								if((htmlCharCount++)==0)
									o.write(prefixStatic);
								o.write("\\\"");
								break;
							case '\t':
								if((htmlCharCount++)==0)
									o.write(prefixStatic);
								o.write("\\t");
								break;
							case '\n':
								if((htmlCharCount++)==0)
									o.write(prefixStatic);
								o.write("\\n"+methodStringBreak(sourceLineCount, charsInLineCount));
								charsInLineCount = 0;
								break;
							case '\\':
								if((htmlCharCount++)==0)
									o.write(prefixStatic);
								o.write("\\\\");
								break;
							default:
								if((htmlCharCount++)==0)
									o.write(prefixStatic);
								o.write(c);
								break;
						}
						break;
					case HTML_LESS:
						switch(c)
						{
							case '%':
								state = State.JAVA_FIRST;
								expression = false;
								if(htmlCharCount>0)
									o.write(METHOD_SUFFIX);
								htmlCharCount = 0;
								break;
							case '<':
								if((htmlCharCount++)==0)
									o.write(prefixStatic);
								o.write(cback);
								break;
							default:
								state = State.HTML;
								if((htmlCharCount++)==0)
									o.write(prefixStatic);
								o.write(cback);
								o.write(c);
								break;
						}
						break;
					case JAVA_FIRST:
						switch(c)
						{
							case '%':
								state = State.JAVA_PERCENT;
								cback = c;
								break;
							case '=':
								state = State.JAVA;
								expression = true;
								o.write(prefixExpression);
								break;
							default:
								state = State.JAVA;
								o.write(c);
								break;
						}
						break;
					case JAVA:
						switch(c)
						{
							case '%':
								state = State.JAVA_PERCENT;
								cback = c;
								break;
							case '\n':
								o.write(sourceRef(sourceLineCount, charsInLineCount));
								charsInLineCount = 0;
								// fall-through
							default:
								o.write(c);
								break;
						}
						break;
					case JAVA_PERCENT:
						if(c=='>')
						{
							state = State.HTML;
							htmlCharCount = 0;
							if(expression)
								o.write(METHOD_SUFFIX_EXPRESSION);
						}
						else
						{
							state = State.JAVA;
							o.write(cback);
							o.write(c);
						}
						break;
					default:
						throw new RuntimeException(String.valueOf(state));
				}
			}
			if(htmlCharCount>0)
				o.write(METHOD_SUFFIX);
		}
		finally
		{
			if(source!=null)
				source.close();
			if(o!=null)
				o.close();
		}
	}

	private int updateCharsInLine(final char c, final int oldCharsInLineCount)
	{
		if ( c=='\r'||c=='\n' )
		{
			/*skip*/
			return oldCharsInLineCount;
		}
		else if ( c=='\t' )
		{
			return ((oldCharsInLineCount/3)+1)*3;
		}
		else
		{
			return oldCharsInLineCount+1;
		}
	}
}
