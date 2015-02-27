package com.ludicrus.core.model.rss;

import com.ludicrus.core.model.interfaces.IResultParser;

public class ResultParser implements IResultParser
{
	protected String resultString;
	
	public ResultParser(String results)
	{
		resultString = results;
	}
	
	public void parseResult()
	{
		throw new Error();
	}

	public String getResultString() {
		return resultString;
	}

	public void setResultString(String resultString) {
		this.resultString = resultString;
	}
}
