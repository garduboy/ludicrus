package com.ludicrus.core.classes;

import com.ludicrus.core.model.interfaces.ISportsTeam;

public class SportsTeam implements ISportsTeam {
		
	protected Integer 	idTeam;
	protected String 	countryName;
	protected String 	description;
	protected String 	homePageURL;
	protected String 	logo;
	protected String	name;
	protected String 	nickname;
	protected String 	sportName;
	protected String	stadiumName;
	protected String 	wikiLink;
	
	public Integer getIdTeam() {
		return idTeam;
	}
	public void setIdTeam(Integer idTeam) {
		this.idTeam = idTeam;
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
	public String getSportName() {
		return sportName;
	}
	public void setSportName(String sportName) {
		this.sportName = sportName;
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
}
