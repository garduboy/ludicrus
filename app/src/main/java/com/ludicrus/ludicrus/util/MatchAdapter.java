package com.ludicrus.ludicrus.util;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ludicrus.ludicrus.R;
import com.ludicrus.core.model.interfaces.IMatch;

//public class MatchAdapter extends ArrayAdapter<IMatch>
//{
//	public MatchAdapter(Context context, int resourceId, List<IMatch> list)
//	{
//		super(context, resourceId, list);
//	}
//	
//	@Override
//	public View getView(int position, View convertView, ViewGroup parent)
//	{
//		TextView text = new TextView(getContext());
//		text.setText(this.getItem(position).getTournamentName());
//		return text;
//	}
//}
public class MatchAdapter extends BaseAdapter
{
	private static final int TYPE_MAX_COUNT = 5;
	
	private ArrayList<IMatch> mData = new ArrayList<IMatch>();
    private LayoutInflater mInflater;
    private BitmapFactory.Options mOptions;
    private Typeface mTypeface;
    private Typeface mScoreTypeface;
    
	public MatchAdapter(LayoutInflater inflater)
	{
		mInflater = inflater;

        mOptions = new BitmapFactory.Options();
		mOptions.inMutable = true;
	}
	
	public void addItem(final IMatch item) {
        mData.add(item);
        notifyDataSetChanged();
    }

    public void setmTypeface(Typeface mTypeface) {
        this.mTypeface = mTypeface;
    }

    public void setmScoreTypeface(Typeface mScoreTypeface) {
        this.mScoreTypeface = mScoreTypeface;
    }

	public void clear()
	{
		mData.clear();
	}
	
    @Override
    public int getItemViewType(int position)
    {
    	IMatch match = mData.get(position);
        return match.getType();
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
    public IMatch getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
//		TextView text = new TextView(getContext());
//		text.setText(this.getItem(position).getTournamentName());
		ViewHolder holder = null;
        int type = getItemViewType(position);
        System.out.println("getView " + position + " " + convertView + " type = " + type);
        if (convertView == null) {
            holder = new ViewHolder();
            switch (type) {
                case IMatch.TYPE_SCHEDULED:
                    convertView = mInflater.inflate(R.layout.soccer_match_scheduled , null);
                    holder.homeTeam = (TextView)convertView.findViewById(R.id.homeTeam);
                    holder.visitorTeam = (TextView)convertView.findViewById(R.id.visitorTeam);
                    holder.homeTeamLogo = (ImageView)convertView.findViewById(R.id.homeTeamLogo);
                    holder.awayTeamLogo = (ImageView)convertView.findViewById(R.id.awayTeamLogo);
                    holder.matchLocation = (TextView)convertView.findViewById(R.id.matchLocation);
                    holder.matchLocation.setTypeface(mTypeface);
                    holder.matchTime = (TextView)convertView.findViewById(R.id.matchTime);
                    holder.matchTime.setTypeface(mTypeface);
                    break;
                case IMatch.TYPE_FINISHED: 
                case IMatch.TYPE_LIVE:
                case IMatch.TYPE_STARTED:
                case IMatch.TYPE_HALF_TIME:
                	convertView = mInflater.inflate(R.layout.soccer_match_finished , null);
                	holder.homeTeam = (TextView)convertView.findViewById(R.id.homeTeam);
                    holder.visitorTeam = (TextView)convertView.findViewById(R.id.visitorTeam);
                    holder.homeTeamLogo = (ImageView)convertView.findViewById(R.id.homeTeamLogo);
                    holder.awayTeamLogo = (ImageView)convertView.findViewById(R.id.awayTeamLogo);
                    holder.homeScore = (TextView)convertView.findViewById(R.id.homeScore);
                    holder.homeScore.setTypeface(mScoreTypeface);
                    holder.visitorScore = (TextView)convertView.findViewById(R.id.visitorScore);
                    holder.visitorScore.setTypeface(mScoreTypeface);
                    holder.matchInfo = (TextView)convertView.findViewById(R.id.matchFinished);
                	break;
//                case TYPE_SEPARATOR:
//                    convertView = mInflater.inflate(R.layout.item2, null);
//                    holder.textView = (TextView)convertView.findViewById(R.id.textSeparator);
//                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.homeTeam.setText(mData.get(position).getHomeTeamName());
        holder.homeTeam.setTypeface(mTypeface);
        holder.visitorTeam.setText(mData.get(position).getAwayTeamName());
        holder.visitorTeam.setTypeface(mTypeface);
        
        byte[] decodedString = Base64.decode(mData.get(position).getHomeTeamLogo(), Base64.DEFAULT);
        Bitmap teamMask = BitmapFactory.decodeResource(mInflater.getContext().getResources(), R.drawable.team_mask, mOptions);
        Bitmap teamMaskAlpha = BitmapFactory.decodeResource(mInflater.getContext().getResources(), R.drawable.team_mask_alpha, mOptions);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        Paint alphaP = new Paint();
        alphaP.setAntiAlias(true);
        alphaP.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        
        Bitmap teamMaskResized;
        Bitmap teamMaskAlphaResized;
        
        Bitmap homeLogo = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        decodedString = Base64.decode(mData.get(position).getAwayTeamLogo(), Base64.DEFAULT);
        Bitmap awayLogo = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        int width = homeLogo.getWidth();
        int height = homeLogo.getHeight();
        
        teamMaskResized = Bitmap.createScaledBitmap(teamMask, width, height, true);
        teamMaskAlphaResized = Bitmap.createScaledBitmap(teamMaskAlpha, width, height, true);
        
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
        c.drawBitmap(homeLogo, 0, 0, null);
        c.drawBitmap(teamMaskResized, 0, 0, paint);
        c.drawBitmap(alpha, 0, 0, alphaP);
        holder.homeTeamLogo.setImageBitmap(result);
        
        result = Bitmap.createBitmap(teamMaskResized.getWidth(), teamMaskResized.getHeight(), Config.ARGB_8888);
        c = new Canvas(result);
        c.drawBitmap(awayLogo, 0, 0, null);
        c.drawBitmap(teamMaskResized, 0, 0, paint);
        c.drawBitmap(alpha, 0, 0, alphaP);
        paint.setXfermode(null);
        alphaP.setXfermode(null);
        holder.awayTeamLogo.setImageBitmap(result);
        
        if(type == IMatch.TYPE_SCHEDULED)
        {
        	holder.matchLocation.setText(mData.get(position).getLocation());
        	holder.matchTime.setText(mData.get(position).getStartTime());
        }
        else
        {
            Integer homeScore = mData.get(position).getHomeScore();
            Integer awayScore = mData.get(position).getAwayScore();
        	holder.homeScore.setText(homeScore.toString());
        	holder.visitorScore.setText(awayScore.toString());
//        	if(type == IMatch.TYPE_FINISHED || type == IMatch.TYPE_LIVE || type == IMatch.TYPE_HALF_TIME || type == IMatch.TYPE_STARTED)
        	if(type == IMatch.TYPE_STARTED || type == IMatch.TYPE_HALF_TIME || type == IMatch.TYPE_LIVE) {
                holder.matchInfo.setText(mData.get(position).getTime());
            } else if (type == IMatch.TYPE_FINISHED) {
                if(homeScore > awayScore) {
                    convertView.setBackgroundResource(R.drawable.home_victory_row);
                } else if(awayScore > homeScore) {
                    convertView.setBackgroundResource(R.drawable.away_victory_row);
                }
            }
        }
        return convertView;
	}
	
	public static class ViewHolder {
        public TextView homeTeam;
        public TextView visitorTeam;
        public ImageView homeTeamLogo;
        public ImageView awayTeamLogo;
        public TextView matchTime;
        public TextView matchLocation;
        public TextView homeScore;
        public TextView visitorScore;
        public TextView matchInfo;
    }
}
