package com.example.jh.dutch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;


public class ExpandedAdapter extends BaseExpandableListAdapter{
    private Context myContext;
    private ArrayList<DutchMember> dutchMembers;
    private LayoutInflater inflater;
    String type;

    public ExpandedAdapter(Context myContext, String type, ArrayList<DutchMember> dutchMembers){
        this.myContext = myContext;
        this.dutchMembers = dutchMembers;
        this.type = type;
        inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getGroupCount() {
        return dutchMembers.size();
    }

    @Override
    public int getChildrenCount(int i) {
        //return dutchMembers.size();
        return 1;
    }

    @Override
    public Object getGroup(int i) {
        return dutchMembers.get(i);
    }

    @Override
    public ArrayList<String> getChild(int i, int i1) {
        return dutchMembers.get(i).info;
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i1) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        if(view== null){
            view = inflater.inflate(R.layout.parent_list, null);
        }

        DutchMember dutchMember = (DutchMember) getGroup(i);
        String id = dutchMember.id;

        TextView tv = (TextView) view.findViewById(R.id.tv_id);
        tv.setText(id);

        ImageView arrow = (ImageView) view.findViewById(R.id.arrow);

        if (b) {
            arrow.setImageResource(R.drawable.up_arrow);
        }
        else {
            arrow.setImageResource(R.drawable.down_arrow);
        }

        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {
        ArrayList<String> child = getChild(groupPosition, childPosition);
        if(type.equalsIgnoreCase("loan")) {
            if(view == null){
                view = inflater.inflate(R.layout.loan_child_list, null);
            }
            RatingBar rate = (RatingBar) view.findViewById(R.id.rate);
            TextView money = (TextView) view.findViewById(R.id.tv_money);
            TextView date = (TextView) view.findViewById(R.id.tv_date);

            rate.setRating(Float.parseFloat(child.get(0)));
            money.setText(child.get(1));
            date.setText(child.get(2));
            return view;
        }else if(type.equalsIgnoreCase("rent")){
            if(view == null){
                view = inflater.inflate(R.layout.rent_child_list, null);
            }
            RatingBar rate = (RatingBar) view.findViewById(R.id.rc_rate);
            TextView money = (TextView) view.findViewById(R.id.tv_money);
            TextView date = (TextView) view.findViewById(R.id.tv_date);
            TextView bank = (TextView) view.findViewById(R.id.tv_bank);
            TextView account = (TextView) view.findViewById(R.id.tv_account);

            rate.setRating(Float.parseFloat(child.get(0)));
            money.setText(child.get(1));
            date.setText(child.get(2));
            bank.setText(child.get(3));
            account.setText(child.get(4));

            return view;
        }else{
            return null;
        }
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
