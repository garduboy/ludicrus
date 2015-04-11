package com.ludicrus.ludicrus.util;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ludicrus.ludicrus.R;
import com.ludicrus.ludicrus.helpers.ui.AssetsHelper;

import java.util.List;

/**
 * Created by jpgarduno on 4/10/15.
 */
public class CustomSpinnerAdapter extends ArrayAdapter{

    private Context context;
    private List<CharSequence> itemList;
    private LayoutInflater inflater;

    public CustomSpinnerAdapter(Context context, int textViewResourceId, List<CharSequence> itemList) {

        super(context, textViewResourceId, itemList);
        this.context=context;
        this.itemList=itemList;
        inflater = LayoutInflater.from(context);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent, boolean dropdown) {
        if(convertView == null)
            convertView = inflater.inflate(R.layout.league_spinner, parent, false);

        TextView v = (TextView) convertView.findViewById(R.id.league_name);
        Typeface myTypeFace = AssetsHelper.getTypeFace(context, EnumTypeface.QUICKSAND);
        v.setTextColor(context.getResources().getColor(R.color.text_font));
        if(dropdown) {
            v.setTextSize(14);
            v.setHeight(v.getMaxHeight());
            v.setBackgroundColor(context.getResources().getColor(R.color.background_main));
        }
        v.setTypeface(myTypeFace);
        v.setText(itemList.get(position));
        return v;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent, false);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent, true);
    }
}
