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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
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
	private static final String METHOD_STRING_BREAK = "\" +\n\t\"";

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
		SourceRefWriter o = null;
		try
		{
			source = new FileReader(sourceFile);
			o = new SourceRefWriter(new FileWriter(targetFile), sourceFile, config);

			State state = State.HTML;
			char cback = '*';
			boolean expression = true;
			int htmlCharCount = 0;
			for(int ci = source.read(); ci>0; ci = source.read())
			{
				final char c = (char)ci;

				if (c=='\r')
				{
					// ignore \r, so the same files are generated on Linux and Windows
					continue;
				}

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
								o.write("\\n"+METHOD_STRING_BREAK);
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
				if ( c=='\n' ) o.incrementSourceLineCount();
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
}
