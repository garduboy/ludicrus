package com.ludicrus.ludicrus.util;

import java.util.HashMap;
import java.util.List;

import com.ludicrus.core.model.interfaces.IOrganization;
import com.ludicrus.ludicrus.R;
import com.ludicrus.ludicrus.SportifiedApp;
import com.ludicrus.ludicrus.parcelable.UserMobile;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<Object>> _listDataChild;

    public ExpandableListAdapter(Context context, List<String> listDataHeader, HashMap<String, List<Object>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        Object childItem = this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosititon);
        String childText = "";
        if(childItem instanceof IOrganization)
        {
            childItem = ((IOrganization) childItem).getName();
        }
        return childItem;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        Object childItem = this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosition);
        long childId = 0;
        if(childItem instanceof IOrganization)
        {
            childId = ((IOrganization) childItem).getIdOrganization();
        }
        return childId;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.drawer_item, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.drawerItem);

        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        if(groupPosition == 0)
        {
            LayoutInflater inflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.drawer_profile, null);

            SportifiedApp sportApp = (SportifiedApp)this._context.getApplicationContext();
            UserMobile user = sportApp.getUser();
            TextView lblUserName = (TextView) convertView.findViewById(R.id.drawer_user_name);
            lblUserName.setText(user.getName());
        } else
        {
            String headerTitle = (String) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.drawer_group, null);
            }

            TextView lblListHeader = (TextView) convertView.findViewById(R.id.drawerGroup);
            lblListHeader.setText(headerTitle);
        }
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}

