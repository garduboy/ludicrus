package com.ludicrus.core.model.interfaces;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

public interface IMatch
{
	public static final int TYPE_SCHEDULED = 0;
	public static final int TYPE_LIVE = 1;
	public static final int TYPE_STARTED = 4;
	public static final int TYPE_HALF_TIME = 3;
	public static final int TYPE_FINISHED = 2;
	
	public Integer getIdMatch();
	
	public String getDate();
	
	public String getTournamentName();

	public String getHomeTeamLogo();
	
	public String getHomeTeamName();
	
	public Integer getHomeTeamId();
	
	public String getAwayTeamLogo();
	
	public String getAwayTeamName();

	public Integer getAwayTeamId();
	
	public String getTime();
	
	public String getStartTime();
	
	public String getStadiumName();
	
	public String getLocation();
	
	public Integer getHomeScore();
	
	public Integer getAwayScore();
	
	public String getHomeScoreDetails();
	
	public String getHomeLineupGK();
	
	public String getHomeLineupDefense();
	
	public String getHomeLineupMidfield();
	
	public String getHomeLineupForward();
	
	public String getHomeFormation();
	
	public String getHomeSubs();
	
	public String getHomeYC();
	
	public String getHomeRC();
	
	public String getAwayScoreDetails();
	
	public String getAwayLineupGK();
	
	public String getAwayLineupDefense();
	
	public String getAwayLineupMidfield();
	
	public String getAwayLineupForward();
	
	public String getAwayFormation();
	
	public String getAwaySubs();
	
	public String getAwayYC();
	
	public String getAwayRC();
	
	public String getSport();
	
	public int getType();
	
	public void setHomeTeamLogo(String value);
	
	public void setAwayTeamLogo(String value);
}
