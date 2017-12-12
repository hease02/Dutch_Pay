package com.example.jh.dutch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;
import java.util.List;

class RentMemberAdapter extends ArrayAdapter<Member> {
    RentMemberAdapter(Context context, int resource, List<Member> rentmembers) {
        super(context, resource, rentmembers);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        ViewHolder holder;

        if( convertView == null ){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            v = inflater.inflate(R.layout.rent_member_form, null);

            holder = new ViewHolder();
            holder.id = (TextView)v.findViewById(R.id.tv_rent_id);
            holder.ratingBar = (RatingBar) v.findViewById(R.id.ratingBar);
            holder.money = (TextView)v.findViewById(R.id.tv_rent_money);

            v.setTag(holder);
        }else{
            v = convertView;
            holder = (ViewHolder)v.getTag();
        }

        Member rm = getItem(position);

        if(rm != null) {
            holder.id.setText(rm.id);
            holder.ratingBar.setRating(Float.parseFloat(rm.rate));
            holder.money.setText(rm.money);
        }

        return v;
    }
}
 class ViewHolder{
     TextView id;
     RatingBar ratingBar;
     TextView money;
 }