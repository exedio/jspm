
package com.exedio.jspm;

import java.io.IOException;


public class Main
{
	public static final void main(final String[] args)
	{
		try
		{
			for(int i = 0; i<args.length; i++)
			{
				final Compiler compiler = new Compiler(args[i]);
				if(compiler.isDirty())
					compiler.translate();
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
	
}
