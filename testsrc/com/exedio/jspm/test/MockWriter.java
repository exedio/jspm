
package com.exedio.jspm.test;

import java.util.ArrayList;


final class MockWriter
{
	final ArrayList result = new ArrayList();
	
	void print(final String s)
	{
		result.add(s);
	}

	void print(final int i)
	{
		result.add(new Integer(i));
	}

}
