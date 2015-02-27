package com.ludicrus.core.model.rss;

import java.util.StringTokenizer;

public class SoccerResultParser extends ResultParser
{
	private String 	description;
	private String 	homeTeam;
	private String 	visitorTeam;
	private Integer homeScore;
	private Integer visitorScore;
	private String 	country;
	private String 	league;
	public SoccerResultParser(String results)
	{
		super(results);
		description = "";
		homeTeam = "";
		visitorTeam = "";
		country = "";
		league = "";
	}
	
	public Integer parseScore(String scoreS)
	{
		try
		{
			int score = Integer.parseInt(scoreS.trim());
			return new Integer(score);
		}
		catch (NumberFormatException nfe) 
		{
			return null;
		}
	}
	
	@Override
	public void parseResult()
	{
		StringTokenizer tokens;
		String token;
		try
		{
			tokens = new StringTokenizer(resultString, "-");
			
			StringTokenizer leftPart = new StringTokenizer(tokens.nextToken(), " ");
			description = leftPart.nextToken();
			
			while(leftPart.hasMoreTokens())
			{
				homeTeam += leftPart.nextToken() + " ";
			}
			homeTeam = homeTeam.trim();
			
			StringTokenizer middlePartL = new StringTokenizer(tokens.nextToken(), " ");
			while(middlePartL.hasMoreTokens())
			{
				token = middlePartL.nextToken();
				Integer score = parseScore(token);
				if(score == null)
					visitorTeam += token + " ";
				else
					homeScore = score;
			}
			visitorTeam = visitorTeam.trim();
			
			StringTokenizer middlePartR = new StringTokenizer(tokens.nextToken(), " ");
			String middleTemp = middlePartR.nextToken("(");
			if(parseScore(middleTemp) == null)
			{
				StringTokenizer middlePartR2 = new StringTokenizer(middleTemp, " ");
				visitorTeam += middlePartR2.nextToken() + " ";
				Integer score = parseScore(middlePartR2.nextToken()); 
				homeScore = score;
				middlePartR = new StringTokenizer(tokens.nextToken(), " ");
				middleTemp = middlePartR.nextToken("(");
			}
			
			visitorScore = parseScore(middleTemp);
			country = middlePartR.nextToken();
			
			StringTokenizer rightPart = new StringTokenizer(tokens.nextToken(), " ");
			while(rightPart.hasMoreTokens())
			{
				league += rightPart.nextToken(")") + " ";
			}
			league = league.trim();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public String toString() {
		return "Result: " + description + " HT: " + homeTeam + ":" + homeScore + " VT: " + visitorTeam + ":" + visitorScore +
			   " Country: " + country + " League: " + league;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getHomeTeam() {
		return homeTeam;
	}

	public void setHomeTeam(String homeTeam) {
		this.homeTeam = homeTeam;
	}

	public String getVisitorTeam() {
		return visitorTeam;
	}

	public void setVisitorTeam(String visitorTeam) {
		this.visitorTeam = visitorTeam;
	}

	public Integer getHomeScore() {
		return homeScore;
	}

	public void setHomeScore(Integer homeScore) {
		this.homeScore = homeScore;
	}

	public Integer getVisitorScore() {
		return visitorScore;
	}

	public void setVisitorScore(Integer visitorScore) {
		this.visitorScore = visitorScore;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getLeague() {
		return league;
	}

	public void setLeague(String league) {
		this.league = league;
	}
}
