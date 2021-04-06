package com.example.testapp;

import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class profileAdapter extends BaseExpandableListAdapter {

    ArrayList<String> listGroup;
    HashMap<String, ArrayList<String>> listChild;

    public profileAdapter(ArrayList<String> listGroup, HashMap<String,ArrayList<String>> listChild){
        this.listGroup = listGroup;
        this.listChild = listChild;
    }

    @Override
    public int getGroupCount() {
        return listGroup.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listChild.get(listGroup.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listGroup.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listChild.get(listGroup.get(groupPosition)).get(childPosition);

    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_expandable_list_item_1,parent,false);

        TextView textView = convertView.findViewById(android.R.id.text1);

        String sgroup = String.valueOf(getGroup(groupPosition));

        textView.setText(sgroup);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setTextColor(Color.BLUE);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        convertView =LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_selectable_list_item, parent ,false);

        TextView textView = convertView.findViewById((android.R.id.text1));

        String sChild = String.valueOf(getChild(groupPosition,childPosition));

        textView.setText(sChild);

        textView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(parent.getContext(), "long clicked " + sChild,Toast.LENGTH_SHORT).show();
                String id = MainActivity.items.get(childPosition).getName();
                String groupId = listGroup.get(groupPosition);
                Log.d("Profile: onLongClick", id);
                MainActivity.items.remove(childPosition);
                listChild.get(groupId).remove(childPosition);
                notifyDataSetChanged();
                return false;

            }
        });



        textView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Toast.makeText(parent.getContext(), sChild,Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public void setListChild(HashMap<String, ArrayList<String>> listChild1){
        this.listChild = listChild1;
    }


    public HashMap<String, ArrayList<String>>  deletingItem(){
        return this.listChild;
    }




}
