package com.ludicrus.ludicrus.parcelable;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.ludicrus.core.model.interfaces.IUser;
import com.ludicrus.core.model.interfaces.ISportsTeam;

public class UserMobile implements Parcelable
{
	private Integer 	_idUser;
	private Integer		_idImage;
	private String		_email;
	private String 		_name;
	private String 		_lastName;
	private Integer		_loginType;
	private String		_userName;
	private List<ISportsTeam> _favoriteSportsTeams;
	
	public UserMobile()
	{
	}
	
	public UserMobile(Parcel in)
	{
		readFromParcel(in);
	}
	
	@Override
	public int describeContents() {
        return 0;
    }
	
	/**
	 *
	 * Called from the constructor to create this
	 * object from a parcel.
	 *
	 * @param in parcel from which to re-create object
	 */
	private void readFromParcel(Parcel in)
	{
		_idUser = in.readInt();
		_name = in.readString();
		_lastName = in.readString();
	}
	
	@Override
	public void writeToParcel(Parcel out, int flags)
	{
        out.writeInt(_idUser);
        out.writeString(_name);
        out.writeString(_lastName);
    }
	
	/**
    *
    * This field is needed for Android to be able to
    * create new objects, individually or as arrays.
    */
   public static final Parcelable.Creator<UserMobile> CREATOR =
   	new Parcelable.Creator<UserMobile>()
   	{
       public UserMobile createFromParcel(Parcel in) {
           return new UserMobile(in);
       }

       public UserMobile[] newArray(int size) {
           return new UserMobile[size];
       }
    };
       
	public Integer getIdUser() {
		return _idUser;
	}
	public void setIdUser(Integer _idUser) {
		this._idUser = _idUser;
	}

	public Integer getIdImage() {
		return _idImage;
	}
	public void setIdImage(Integer _idImage) {
		this._idImage = _idImage;
	}

	public String getEmail() {
		return _email;
	}
	public void setEmail(String _eMail) {
		this._email = _eMail;
	}

	public String getName() {
		return _name;
	}
	public void setName(String _name) {
		this._name = _name;
	}
	public String getLastName() {
		return _lastName;
	}
	public void setLastName(String _lastName) {
		this._lastName = _lastName;
	}
	public Integer getLoginType() {
		return _loginType;
	}
	public void setLoginType(Integer _loginType) {
		this._loginType = _loginType;
	}
	public String getUserName() {
		return _userName;
	}
	public void setUserName(String _userName) {
		this._userName = _userName;
	}
	public List<ISportsTeam> getFavoriteSportsTeams() {
		return _favoriteSportsTeams;
	}
}
