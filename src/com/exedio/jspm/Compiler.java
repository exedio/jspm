/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

final class Compiler
{
	private static final String FILE_SUFFIX = ".jspm";
	
	private static final int STATE_HTML = 0;
	private static final int STATE_HTML_LESS = 1;
	private static final int STATE_JAVA = 2;
	private static final int STATE_JAVA_FIRST = 3;
	private static final int STATE_JAVA_PERCENT = 4;
	
	private static final String PRINT_PREFIX = "out.print(\"";
	private static final String PRINT_SUFFIX = "\");\n";
	private static final String PRINT_PREFIX_EXPRESSION = "out.print(";
	private static final String PRINT_SUFFIX_EXPRESSION = ");\n";
	private static final String PRINT_STRING_BREAK = "\" +\n\t\"";

	final File sourceFile;
	final File targetFile;

	Compiler(final String fileName)
	{
		this.sourceFile = new File(fileName);

		final String targetFileName;
		if(fileName.endsWith(FILE_SUFFIX))
			targetFileName = fileName.substring(0, fileName.length()-FILE_SUFFIX.length())+"_Jspm.java";
		else
			targetFileName = fileName;

		this.targetFile = new File(targetFileName);
	}
	
	void translateIfDirty() throws IOException
	{
		if(isDirty())
			translate();
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
	
	void translate() throws IOException
	{
		System.out.println("Translating "+sourceFile);
		Reader source = null;
		Writer o = null;
		try
		{
			source = new FileReader(sourceFile);
			o = new FileWriter(targetFile);
			
			int state = STATE_HTML;
			char cback = '*';
			boolean expression = true;
			int htmlCharCount = 0;
			for(int ci = source.read(); ci>0; ci = source.read())
			{
				final char c = (char)ci;
				
				switch(state)
				{
					case STATE_HTML:
						switch(c)
						{
							case '<':
								state = STATE_HTML_LESS;
								cback = c;
								break;
							case '"':
								if((htmlCharCount++)==0)
									o.write(PRINT_PREFIX);
								o.write("\\\"");
								break;
							case '\t':
								if((htmlCharCount++)==0)
									o.write(PRINT_PREFIX);
								o.write("\\t");
								break;
							case '\n':
								if((htmlCharCount++)==0)
									o.write(PRINT_PREFIX);
								o.write("\\n"+PRINT_STRING_BREAK);
								break;
							case '\\':
								if((htmlCharCount++)==0)
									o.write(PRINT_PREFIX);
								o.write("\\\\");
								break;
							case '\r':
								break;
							default:
								if((htmlCharCount++)==0)
									o.write(PRINT_PREFIX);
								o.write(c);
								break;
						}
						break;
					case STATE_HTML_LESS:
						switch(c)
						{
							case '%':
								state = STATE_JAVA_FIRST;
								expression = false;
								if(htmlCharCount>0)
									o.write(PRINT_SUFFIX);
								htmlCharCount = 0;
								break;
							case '<':
								if((htmlCharCount++)==0)
									o.write(PRINT_PREFIX);
								o.write(cback);
								break;
							default:
								state = STATE_HTML;
								if((htmlCharCount++)==0)
									o.write(PRINT_PREFIX);
								o.write(cback);
								o.write(c);
								break;
						}
						break;
					case STATE_JAVA_FIRST:
						switch(c)
						{
							case '%':
								state = STATE_JAVA_PERCENT;
								cback = c;
								break;
							case '=':
								state = STATE_JAVA;
								expression = true;
								o.write(PRINT_PREFIX_EXPRESSION);
								break;
							default:
								state = STATE_JAVA;
								o.write(c);
								break;
						}
						break;
					case STATE_JAVA:
						switch(c)
						{
							case '%':
								state = STATE_JAVA_PERCENT;
								cback = c;
								break;
							default:
								o.write(c);
								break;
						}
						break;
					case STATE_JAVA_PERCENT:
						if(c=='>')
						{
							state = STATE_HTML;
							htmlCharCount = 0;
							if(expression)
								o.write(PRINT_SUFFIX_EXPRESSION);
						}
						else
						{
							state = STATE_JAVA;
							o.write(cback);
							o.write(c);
						}
						break;
					default:
						throw new RuntimeException(String.valueOf(state));
				}
			}
			if(htmlCharCount>0)
				o.write(PRINT_SUFFIX);
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
