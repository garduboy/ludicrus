package com.ludicrus.core.classes;

import java.io.Serializable;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.ludicrus.core.model.interfaces.IMatch;

public class SoccerMatch implements IMatch, Serializable {

	private static final long serialVersionUID = 1L;
	protected Integer 	idMatch;
	protected DateTime	dateTime;
	protected String 	date;
	protected String	startTime;
	protected String 	tournamentName;
	protected Integer	homeTeamId;
	protected String	homeTeamLogo;
	protected String 	homeTeamName;
	protected Integer	awayTeamId;
	protected String	awayTeamLogo;
	protected String 	awayTeamName;
	protected String	time;
	protected String 	location;
	protected Integer	homeScore;
	protected Integer 	awayScore;
	protected String	stadiumName;
	protected String	homeScoreDetails;
	protected String	homeLineupGK;
	protected String	homeLineupDefense;
	protected String	homeLineupMidfield;
	protected String	homeLineupForward;
	protected String	homeFormation;
	protected String	homeSubs;
	protected String	homeYC;
	protected String	homeRC;
	protected String	awayScoreDetails;
	protected String	awayLineupGK;
	protected String	awayLineupDefense;
	protected String	awayLineupMidfield;
	protected String	awayLineupForward;
	protected String	awayFormation;
	protected String	awaySubs;
	protected String	awayYC;
	protected String	awayRC;
	protected int		type;
	
	public Integer getIdMatch() {
		return idMatch;
	}
	public void setIdMatch(Integer idMatch) {
		this.idMatch = idMatch;
	}
	
	public String getDate() {
		return date;
	}
	
	public DateTime dateTime()
	{
		return dateTime;
	}
	
	public void matchDateTime(String dateTime)
	{
		try
		{
			DateTimeFormatter parser = ISODateTimeFormat.dateTimeNoMillis();
	        this.dateTime = parser.parseDateTime(dateTime);
	        date = this.dateTime.toString("yyyy-MM-dd").trim();
	        startTime = this.dateTime.toString("hh:mm a z").trim();
	        
//			String timeZoneDif = "+";
//			String[] tokens = date.split("T");
//			String[] time = tokens[1].split("\\+");
//			if(time.length == 1)
//			{
//				timeZoneDif = "-";
//				time = tokens[1].split("\\-");
//			}
//			String timeZone = time[1].replace(":", "");
//	        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
//	        this.date = sdf.parse(tokens[0]+"T"+time[0]+timeZoneDif+timeZone);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void setDate(String date){
		this.date = date;
	}
	public String getTournamentName() {
		return tournamentName;
	}
	public void setTournamentName(String tournamentName) {
		this.tournamentName = tournamentName;
	}
	public Integer getHomeTeamId() {
		return homeTeamId;
	}
	public void setHomeTeamId(Integer homeTeamId) {
		this.homeTeamId = homeTeamId;
	}
	public String getHomeTeamName() {
		return homeTeamName;
	}
	public void setHomeTeamName(String homeTeamName) {
		this.homeTeamName = homeTeamName;
	}
	public Integer getAwayTeamId() {
		return awayTeamId;
	}
	public void setAwayTeamId(Integer awayTeamId) {
		this.awayTeamId = awayTeamId;
	}
	public String getAwayTeamName() {
		return awayTeamName;
	}
	public void setAwayTeamName(String awayTeamName) {
		this.awayTeamName = awayTeamName;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
		if(this.time.equals("Finished"))
			setType(IMatch.TYPE_FINISHED);
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public Integer getHomeScore() {
		return homeScore;
	}
	public void setHomeScore(Integer homeScore) {
		this.homeScore = homeScore;
	}
	public Integer getAwayScore() {
		return awayScore;
	}
	public void setAwayScore(Integer awayScore) {
		this.awayScore = awayScore;
	}
	public String getStadiumName() {
		return stadiumName;
	}
	public void setStadiumName(String stadiumName) {
		this.stadiumName = stadiumName;
	}
	public String getHomeScoreDetails() {
		return homeScoreDetails;
	}
	public void setHomeScoreDetails(String homeScoreDetails) {
		this.homeScoreDetails = homeScoreDetails;
	}
	public String getHomeLineupGK() {
		return homeLineupGK;
	}
	public void setHomeLineupGK(String homeLineupGK) {
		this.homeLineupGK = homeLineupGK;
	}
	public String getHomeLineupDefense() {
		return homeLineupDefense;
	}
	public void setHomeLineupDefense(String homeLineupDefense) {
		this.homeLineupDefense = homeLineupDefense;
	}
	public String getHomeLineupMidfield() {
		return homeLineupMidfield;
	}
	public void setHomeLineupMidfield(String homeLineupMidfield) {
		this.homeLineupMidfield = homeLineupMidfield;
	}
	public String getHomeLineupForward() {
		return homeLineupForward;
	}
	public void setHomeLineupForward(String homeLineupForward) {
		this.homeLineupForward = homeLineupForward;
	}
	public String getHomeSubs() {
		return homeSubs;
	}
	public void setHomeSubs(String homeSubs) {
		this.homeSubs = homeSubs;
	}
	public String getHomeYC() {
		return homeYC;
	}
	public void setHomeYC(String homeYC) {
		this.homeYC = homeYC;
	}
	public String getHomeRC() {
		return homeRC;
	}
	public void setHomeRC(String homeRC) {
		this.homeRC = homeRC;
	}
	public String getAwayScoreDetails() {
		return awayScoreDetails;
	}
	public void setAwayScoreDetails(String awayScoreDetails) {
		this.awayScoreDetails = awayScoreDetails;
	}
	public String getAwayLineupGK() {
		return awayLineupGK;
	}
	public void setAwayLineupGK(String awayLineupGK) {
		this.awayLineupGK = awayLineupGK;
	}
	public String getAwayLineupDefense() {
		return awayLineupDefense;
	}
	public void setAwayLineupDefense(String awayLineupDefense) {
		this.awayLineupDefense = awayLineupDefense;
	}
	public String getAwayLineupMidfield() {
		return awayLineupMidfield;
	}
	public void setAwayLineupMidfield(String awayLineupMidfield) {
		this.awayLineupMidfield = awayLineupMidfield;
	}
	public String getAwayLineupForward() {
		return awayLineupForward;
	}
	public void setAwayLineupForward(String awayLineupForward) {
		this.awayLineupForward = awayLineupForward;
	}
	public String getAwaySubs() {
		return awaySubs;
	}
	public void setAwaySubs(String awaySubs) {
		this.awaySubs = awaySubs;
	}
	public String getAwayYC() {
		return awayYC;
	}
	public void setAwayYC(String awayYC) {
		this.awayYC = awayYC;
	}
	public String getAwayRC() {
		return awayRC;
	}
	public void setAwayRC(String awayRC) {
		this.awayRC = awayRC;
	}
	public String getHomeFormation() {
		return homeFormation;
	}
	public void setHomeFormation(String homeFormation) {
		this.homeFormation = homeFormation;
	}
	public String getAwayFormation() {
		return awayFormation;
	}
	public void setAwayFormation(String awayFormation) {
		this.awayFormation = awayFormation;
	}
	public String getSport()
	{
		return "soccer";
	}
	
	public int getType()
	{
		return type;
	}
	
	public void setType(int type)
	{
		this.type =type;  
	}
	public String getHomeTeamLogo() {
		return homeTeamLogo;
	}
	public void setHomeTeamLogo(String homeTeamLogo) {
		this.homeTeamLogo = homeTeamLogo;
	}
	public String getAwayTeamLogo() {
		return awayTeamLogo;
	}
	public void setAwayTeamLogo(String awayTeamLogo) {
		this.awayTeamLogo = awayTeamLogo;
	}
}
