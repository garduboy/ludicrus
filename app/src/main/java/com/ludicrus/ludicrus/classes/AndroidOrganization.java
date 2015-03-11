package com.ludicrus.ludicrus.classes;

import com.ludicrus.core.classes.Organization;

import org.json.JSONException;
import org.json.JSONObject;

public class AndroidOrganization extends Organization
{
	private static final long serialVersionUID = 1L;

	public AndroidOrganization(JSONObject json) throws JSONException
	{
		this.id = (json.has("idOrganization")? json.getInt("idOrganization"):0);
		this.isFavorite = (json.has("isFavorite")? json.getBoolean("isFavorite"):false);
		this.name = (json.has("name")? json.getString("name"):"");
		this.logo = (json.has("logo")? json.getString("logo"):"");
		this.country = (json.has("country")? json.getString("country"):"");
        this.orgType = (json.has("orgType")? json.getInt("orgType"):0);
	}
}
