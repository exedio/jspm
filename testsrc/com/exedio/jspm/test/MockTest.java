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

import java.util.ArrayList;
import org.junit.jupiter.api.Test;

class MockTest
{
	@Test
	void testMock()
	{
		final MockOut out = new MockOut();
		Mock_Jspm.writeMock(out);

		final ArrayList<Object> expected = new ArrayList<>();
		expected.add("{1}");
		expected.add("{12}");
		expected.add("{123}");
		expected.add("[zack]");
		expected.add(Integer.valueOf(55));
		//noinspection HardcodedLineSeparator
		expected.add("{hallo\nbello}");
		expected.add("{border=\"0\"}");
		expected.add("{tib\ttab}");
		expected.add("{back\\slash}");
		expected.add("{<}");
		expected.add("[in-tag]");
		expected.add("{>}");
		expected.add("{<zack><}");
		expected.add("[in-tag2]");
		expected.add("{>}");
		assertEquals(expected, out.result);
	}
}
