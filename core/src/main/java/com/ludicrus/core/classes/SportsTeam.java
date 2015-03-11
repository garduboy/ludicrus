package com.ludicrus.core.classes;

import com.ludicrus.core.model.interfaces.IOrganization;
import com.ludicrus.core.model.interfaces.ISportsTeam;
import com.ludicrus.core.util.EnumSportItemType;

public class SportsTeam implements ISportsTeam, IOrganization {
		
	protected Integer 	idTeam;
    protected boolean 	isFavorite;
    protected Integer 	feedId;
	protected String 	countryName;
	protected String 	description;
	protected String 	homePageURL;
	protected String 	logo;
	protected String	name;
	protected String 	nickname;
	protected int 	    sportType;
	protected String	stadiumName;
	protected String 	wikiLink;

    public Integer getIdOrganization(){
        return feedId;
    }
	public Integer getIdTeam() {
		return idTeam;
	}
	public void setIdTeam(Integer idTeam) {
		this.idTeam = idTeam;
	}
    public Integer getFeedId() {
        return feedId;
    }
    public void setFeedId(Integer feedId) {
        this.feedId = feedId;
    }
    public boolean getIsFavorite(){
        return isFavorite;
    }
    public void setIsFavorite(boolean value){
        isFavorite = value;
    }
    public String getCountry(){
        return countryName;
    }
    public void setCountry(String value){
        countryName = value;
    }
	public String getCountryName() {
		return countryName;
	}
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getHomePageURL() {
		return homePageURL;
	}
	public void setHomePageURL(String homePageURL) {
		this.homePageURL = homePageURL;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickName) {
		this.nickname = nickName;
	}
    public int getSportType() {
        return sportType;
    }
    public void setSportType(int sportType) {
        this.sportType = sportType;
    }
	public String getStadiumName() {
		return stadiumName;
	}
	public void setStadiumName(String stadiumName) {
		this.stadiumName = stadiumName;
	}
	public String getWikiLink() {
		return wikiLink;
	}
	public void setWikiLink(String wikiLink) {
		this.wikiLink = wikiLink;
	}
    public int getOrgType() {
        return EnumSportItemType.TEAM;
    }
}
