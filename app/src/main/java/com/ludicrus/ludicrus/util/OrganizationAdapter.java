package com.ludicrus.ludicrus.util;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Bitmap.Config;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ludicrus.core.util.EnumSportItemType;
import com.ludicrus.ludicrus.R;
import com.ludicrus.ludicrus.SportifiedApp;
import com.ludicrus.core.model.interfaces.IOrganization;
import com.ludicrus.ludicrus.util.MatchAdapter.ViewHolder;

public class OrganizationAdapter extends BaseAdapter
{
	private static final int TYPE_MAX_COUNT = 1;
	
	private ArrayList<IOrganization> mData = new ArrayList<IOrganization>();
    private LayoutInflater mInflater;
    private int mOrgType;
    private Context mContext;
	    
	public OrganizationAdapter(LayoutInflater inflater, int orgType, Context context)
	{
		mInflater = inflater;
		mOrgType = orgType;
		mContext = context;
	}
	
	public void addItem(final IOrganization item) {
        mData.add(item);
        notifyDataSetChanged();
    }

	public void clear()
	{
		mData.clear();
	}

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }
    
	@Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public IOrganization getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
	    
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		OrgHolder holder = null;
        System.out.println("getView " + position + " " + convertView);
        if (convertView == null) {
            holder = new OrgHolder();
            convertView = mInflater.inflate(R.layout.organization_item , null);
            holder.organization = (TextView)convertView.findViewById(R.id.organization);
            holder.organizationLogo = (ImageView)convertView.findViewById(R.id.organizationLogo);
            
            convertView.setTag(holder);
        } else {
            holder = (OrgHolder)convertView.getTag();
        }
        holder.organizationId = mData.get(position).getIdOrganization().toString();
        if(mOrgType == EnumSportItemType.FEDERATION) {
        	holder.organization.setText(mData.get(position).getCountry());
        } else {
        	holder.organization.setText(mData.get(position).getName());
        }
        
        if(mOrgType == EnumSportItemType.TEAM) {
        	holder.isFavorite = mData.get(position).getIsFavorite();
        	holder.favoriteOrg = (ImageView)convertView.findViewById(R.id.favTeam);
       	 	holder.favoriteOrg.setVisibility(View.VISIBLE);
        	if(holder.isFavorite)
        		holder.favoriteOrg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.star_full));
        	else
        		holder.favoriteOrg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.star_empty));
        }

        Bitmap orgLogo = OrganizationLogoRenderer.getMaskedOrganizationLogo(mData.get(position).getLogo(), mInflater.getContext());

        holder.organizationLogo.setImageBitmap(orgLogo);
        
        return convertView;
	}
		
	public static class OrgHolder {
		public String organizationId;
        public TextView organization;
        public ImageView organizationLogo;
        public ImageView favoriteOrg;
        public boolean isFavorite;
    }

}
