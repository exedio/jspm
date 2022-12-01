package com.exedio.jspm.test.examples;

// class is just needed for compiler
final class MockOut
{
	void writeStatic(final String s)
	{
		throw new RuntimeException(s);
	}

	void write(final String s)
	{
		throw new RuntimeException(s);
	}

	private MockOut()
	{
		// prevent instantiation
	}
}
