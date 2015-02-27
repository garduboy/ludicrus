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
	      
        byte[] decodedString = Base64.decode(mData.get(position).getLogo(), Base64.DEFAULT);
        Bitmap orgLogo = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        int width = orgLogo.getWidth();
        int height = orgLogo.getHeight();
        
        Bitmap teamMask = BitmapFactory.decodeResource(mInflater.getContext().getResources(), R.drawable.team_mask);
        Bitmap teamMaskAlpha = BitmapFactory.decodeResource(mInflater.getContext().getResources(), R.drawable.team_mask_alpha);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        Bitmap teamMaskResized = Bitmap.createScaledBitmap(teamMask, width, height, true);
        Bitmap teamMaskAlphaResized = Bitmap.createScaledBitmap(teamMaskAlpha, width, height, true);
        
        //Create alpha bitmap
        Bitmap alpha = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        int[] alphaPix = new int[width * height];
        teamMaskAlphaResized.getPixels(alphaPix, 0, width, 0, 0, width, height);
        int count = width * height;
        for (int i = 0; i < count; ++i)
        {
            alphaPix[i] = alphaPix[i] << 8;
        }
        alpha.setPixels(alphaPix, 0, width, 0, 0, width, height);
        
        Bitmap result = Bitmap.createBitmap(teamMaskResized.getWidth(), teamMaskResized.getHeight(), Config.ARGB_8888);
        Canvas c = new Canvas(result);
        c.drawBitmap(orgLogo, 0, 0, null);
        c.drawBitmap(teamMaskResized, 0, 0, paint);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        c.drawBitmap(alpha, 0, 0, paint);
        paint.setXfermode(null);
        
        holder.organizationLogo.setImageBitmap(result);
        
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
