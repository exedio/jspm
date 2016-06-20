/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package com.exedio.jspm;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

final class Config
{
	private Charset charset = StandardCharsets.US_ASCII;

	public void setCharset(final String value)
	{
		charset = Charset.forName(value);
	}

	Charset getCharset()
	{
		return charset;
	}


	private String     staticMethod = "writeStatic";
	private String expressionMethod = "write";

	public void setMethod(final String value)
	{
		if(value==null)
			throw new NullPointerException();

		staticMethod = value;
		expressionMethod = value;
	}

	public void setStaticMethod(final String value)
	{
		if(value==null)
			throw new NullPointerException();

		staticMethod = value;
	}

	public void setExpressionMethod(final String value)
	{
		if(value==null)
			throw new NullPointerException();

		expressionMethod = value;
	}

	String getMethodStatic()
	{
		return staticMethod;
	}

	String getMethodExpression()
	{
		return expressionMethod;
	}


	private boolean addSourceReferences = true;

	void setAddSourceReferences(final boolean value)
	{
		addSourceReferences = value;
	}

	boolean isAddSourceReferences()
	{
		return addSourceReferences;
	}


	private int charsPerTab = 3;

	void setCharsPerTab(final int value)
	{
		if ( value<1 )
		{
			throw new IllegalArgumentException("charsPerTab must be > 0");
		}
		charsPerTab = value;
	}

	int getCharsPerTab()
	{
		return charsPerTab;
	}


	private int sourceRefTargetPosition = 90;

	void setSourceRefTargetPosition(final int value)
	{
		if ( value<0 )
		{
			throw new IllegalArgumentException("sourceRefTargetPosition must >= 0");
		}
		sourceRefTargetPosition = value;
	}

	int getSourceRefTargetPosition()
	{
		return sourceRefTargetPosition;
	}


	private boolean verbose = false;

	void setVerbose(final boolean value)
	{
		verbose = value;
	}

	boolean isVerbose()
	{
		return verbose;
	}
}
