
package com.exedio.jspm;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

class Compiler
{
	private static final int BUFLEN = 20000;
	private static final String FILE_SUFFIX = ".jspm";

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
		Writer target = null;
		final char[] buf = new char[BUFLEN];
		try
		{
			source = new FileReader(sourceFile);
			target = new FileWriter(targetFile);
			
			for(int len = source.read(buf); len>0; len = source.read(buf))
				target.write(buf, 0, len);
			
			target.write("bello");
		}
		finally
		{
			if(source!=null)
				source.close();
			if(target!=null)
				target.close();
		}
	}

}
