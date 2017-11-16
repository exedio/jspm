package com.exedio.jspm.test.examples;

import java.util.ArrayList;

final class MockOut
{
	final ArrayList<Object> result = new ArrayList<>();

	void writeStatic(final String s)
	{
		result.add('{' + s + '}');
	}

	void write(final String s)
	{
		result.add('[' + s + ']');
	}
}
