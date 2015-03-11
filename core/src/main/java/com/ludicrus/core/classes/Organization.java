package com.ludicrus.core.classes;

import com.ludicrus.core.model.interfaces.IOrganization;

public class Organization implements IOrganization{
	
	protected Integer 	id;
	protected boolean 	isFavorite;
	protected String 	name;
	protected String	logo;
	protected String 	country;
    protected int		orgType;

	public Integer getIdOrganization()
	{
		return id;
	}
	
	public void setIdOrganization(Integer value)
	{
		id = value;
	}
	
	public boolean getIsFavorite()
	{
		return isFavorite;
	}
	
	public void setIsFavorite(boolean value)
	{
		isFavorite = value;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String value)
	{
		name = value;
	}
	
	public String getLogo()
	{
		return logo;
	}
	
	public void setLogo(String value)
	{
		logo = value;
	}
	
	public String getCountry()
	{
		return country;
	}
	
	public void setCountry(String value)
	{
		country = value;
	}

    public int getOrgType()
    {
        return orgType;
    }

    public void setOrgType(int value)
    {
        orgType = value;
    }
}
