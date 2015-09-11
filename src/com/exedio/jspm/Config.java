/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

final class Config
{
	private String     staticMethod = "writeStatic";
	private String expressionMethod = "write";

	public void setMethod(final String method)
	{
		if(method==null)
			throw new NullPointerException();

		this.    staticMethod = method;
		this.expressionMethod = method;
	}

	public void setStaticMethod(final String staticMethod)
	{
		if(staticMethod==null)
			throw new NullPointerException();

		this.staticMethod = staticMethod;
	}

	public void setExpressionMethod(final String expressionMethod)
	{
		if(expressionMethod==null)
			throw new NullPointerException();

		this.expressionMethod = expressionMethod;
	}

	String getMethodStatic()
	{
		return staticMethod;
	}

	String getMethodExpression()
	{
		return expressionMethod;
	}


	private boolean verbose = false;

	void setVerbose(final boolean verbose)
	{
		this.verbose = verbose;
	}

	boolean isVerbose()
	{
		return verbose;
	}


	private boolean addSourceReferences = true;

	void setAddSourceReferences(boolean addSourceRefs)
	{
		this.addSourceReferences = addSourceRefs;
	}

	boolean isAddSourceReferences()
	{
		return addSourceReferences;
	}
}
