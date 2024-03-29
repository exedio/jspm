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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

public final class AntTask extends Task
{
	private final ArrayList<FileSet>  fileSets  = new ArrayList<>();
	private final Config config = new Config();

	@SuppressWarnings("unused") // called by ant via reflection
	public void addFileset(final FileSet fileSet)
	{
		fileSets.add(fileSet);
	}

	@SuppressWarnings("unused") // called by ant via reflection
	public void setCharset(final String value)
	{
		config.setCharset(value);
	}

	@SuppressWarnings("unused") // called by ant via reflection
	public void setMethod(final String value)
	{
		config.setMethod(value);
	}

	@SuppressWarnings("unused") // called by ant via reflection
	public void setStaticMethod(final String value)
	{
		config.setStaticMethod(value);
	}

	@SuppressWarnings("unused") // called by ant via reflection
	public void setExpressionMethod(final String value)
	{
		config.setExpressionMethod(value);
	}

	@SuppressWarnings("unused") // called by ant via reflection
	public void setAddSourceReferences(final boolean value)
	{
		config.setAddSourceReferences(value);
	}

	@SuppressWarnings("unused") // called by ant via reflection
	public void setCharsPerTab(final int value)
	{
		config.setCharsPerTab(value);
	}

	@SuppressWarnings("unused") // called by ant via reflection
	public void setSourceReferenceTargetPosition(final int value)
	{
		config.setSourceRefTargetPosition(value);
	}

	@SuppressWarnings("unused") // called by ant via reflection
	public void setVerbose(final boolean value)
	{
		config.setVerbose(value);
	}

	@Override
	public void execute() throws BuildException
	{
		try
		{
			for(final FileSet fileSet : fileSets)
			{
				final DirectoryScanner directoryScanner = fileSet.getDirectoryScanner(getProject());
				final File dir = fileSet.getDir(getProject());
				final String[] files = directoryScanner.getIncludedFiles();
				final AtomicInteger count = new AtomicInteger();
				for(final String file : files)
					(new Compiler((new File(dir, file)).getAbsolutePath(), config)).translateIfDirty(count);
				if(count.intValue()>0)
					System.out.println("Translated " + count + " jspm files in " + fileSet.getDir());
			}
		}
		catch(final IOException e)
		{
			throw new BuildException(e);
		}
	}
}
