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

package com.exedio.jspm.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.jupiter.api.Test;

class WriteTest
{
	@SuppressWarnings("HardcodedLineSeparator")
	private static final String EXPECTED_CONDITION_PREFIX =
		"<html>\n" +
		"\t<head>\n" +
		"\t\t<title>234</title>\n" +
		"\t</head>";

	@SuppressWarnings("HardcodedLineSeparator")
	private static final String EXPECTED_CONDITION_BODY =
		"\n" +
		"\t<body>\n" +
		"\t\thello-zapp\n" +
		"\t</body>";

	@SuppressWarnings("HardcodedLineSeparator")
	private static final String EXPECTED_CONDITION_POSTFIX =
		"\n" +
		"</html>\n" +
		"\n";

	private static final String EXPECTED_CONDITION_TRUE =
		EXPECTED_CONDITION_PREFIX +
		EXPECTED_CONDITION_BODY +
		EXPECTED_CONDITION_POSTFIX;

	private static final String EXPECTED_CONDITION_FALSE =
		EXPECTED_CONDITION_PREFIX +
		EXPECTED_CONDITION_POSTFIX;

	@Test
	void testWrite()
	{
		{
			final StringWriter buf = new StringWriter();
			final PrintWriter print = new PrintWriter(buf);
			Test_Jspm.writeIt(print, true, "12345");
			print.flush();
			assertEquals(EXPECTED_CONDITION_TRUE, buf.getBuffer().toString());
		}
		{
			final StringWriter buf = new StringWriter();
			final PrintWriter print = new PrintWriter(buf);
			Test_Jspm.writeIt(print, false, "12345");
			print.flush();
			assertEquals(EXPECTED_CONDITION_FALSE, buf.getBuffer().toString());
		}
	}
}
