
package com.exedio.jspm;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;

class Compiler
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

	Compiler(final String fileName) throws IOException
	{
		final File sourceFile = new File(fileName);

		final String targetFileName;
		if(fileName.endsWith(FILE_SUFFIX))
			targetFileName = fileName.substring(0, fileName.length()-FILE_SUFFIX.length())+"_Jspm.java";
		else
			targetFileName = fileName;

		final File targetFile = new File(targetFileName);
		System.out.println("Compiling "+sourceFile+" to "+targetFileName);
		
		Reader source = null;
		Writer o = null;
		try
		{
			source = new FileReader(sourceFile);
			o = new FileWriter(targetFile);
			
			o.write(
					"package com.exedio.jspm.test;\n\n" +
					"class Test_Jspm\n" +
					"{\n" +
					"\tstatic final void writeIt(final "+PrintWriter.class.getName()+" out)\n" +
					"\t{\n");
			
			int state = STATE_HTML;
			char cback = '*';
			boolean expression = true;
			o.write(PRINT_PREFIX);
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
								o.write("\\\"");
								break;
							case '\t':
								o.write("\\t");
								break;
							case '\n':
								o.write("\\n");
								break;
							case '\r':
								break;
							default:
								o.write(c);
								break;
						}
						break;
					case STATE_HTML_LESS:
						if(c=='%')
						{
							state = STATE_JAVA_FIRST;
							expression = false;
							o.write(PRINT_SUFFIX);
						}
						else
						{
							state = STATE_HTML;
							o.write(cback);
							o.write(c);
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
							if(expression)
								o.write(PRINT_SUFFIX_EXPRESSION);
							o.write(PRINT_PREFIX);
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
			o.write(PRINT_SUFFIX);

			o.write(
					"\n" +
					"\t}\n" +
					"}\n");
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
