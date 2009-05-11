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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.FileSet;

public final class AntTask extends Task
{
	private final ArrayList<FileSet>  fileSets  = new ArrayList<FileSet>();
	private final ArrayList<FileList> fileLists = new ArrayList<FileList>();
	private String staticMethod = null;
	private String expressionMethod = null;

	public void addFileset(final FileSet fileSet)
	{
		fileSets.add(fileSet);
	}

	public void addFilelist(final FileList fileList)
	{
		fileLists.add(fileList);
	}
	
	public void setMethod(final String method)
	{
		this.    staticMethod = method;
		this.expressionMethod = method;
	}
	
	public void setStaticMethod(final String staticMethod)
	{
		this.staticMethod = staticMethod;
	}
	
	public void setExpressionMethod(final String expressionMethod)
	{
		this.expressionMethod = expressionMethod;
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
				final String files[] = directoryScanner.getIncludedFiles();
				for(String file : files)
					(new Compiler((new File(dir, file)).getAbsolutePath(), staticMethod, expressionMethod)).translateIfDirty();
			}
			for(final FileList fileList : fileLists)
			{
				final File dir = fileList.getDir(getProject());
				final String files[] = fileList.getFiles(getProject());
				for(String file : files)
					(new Compiler((new File(dir, file)).getAbsolutePath(), staticMethod, expressionMethod)).translateIfDirty();
			}
		}
		catch(IOException e)
		{
			throw new BuildException(e);
		}
	}
}
