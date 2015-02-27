package com.ludicrus.core.model.interfaces;

import java.util.List;

public interface IUser
{
	public Integer getIdUser();
	
	public String getLastName();
	
	public String getName();
	
	public String getUserName();
	
	public Integer getIdImage();
	
	public String getEmail();
	
	public List<ISportsTeam> getFavoriteSportsTeams();
}
