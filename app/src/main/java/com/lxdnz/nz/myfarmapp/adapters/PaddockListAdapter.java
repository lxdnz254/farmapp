package com.lxdnz.nz.myfarmapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lxdnz.nz.myfarmapp.R;
import com.lxdnz.nz.myfarmapp.databases.Paddock;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Created by alex on 25/07/15.
 */
public class PaddockListAdapter extends ArrayAdapter<Paddock> {

    private Context context;
    List<Paddock> paddocks;

    public PaddockListAdapter(Context context, List<Paddock> paddocks){
        super(context, R.layout.list_paddocks, paddocks);
        this.context = context;
        this.paddocks = paddocks;
    }

    private class ViewHolder {
        TextView paddockIdTxt;
        TextView paddockNameTxt;
        TextView paddockAreaTxt;
        TextView paddockCurrentCoverTxt;
        TextView paddockPreviousCoverTxt;
        TextView paddockGrowth;
    }

    @Override
    public int getCount() {
        return paddocks.size();
    }

    @Override
    public Paddock getItem(int position) {
        return paddocks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_paddocks, null);
            holder = new ViewHolder();

            holder.paddockIdTxt = (TextView) convertView
                    .findViewById(R.id.txt_paddock_id);
            holder.paddockNameTxt = (TextView) convertView
                    .findViewById(R.id.txt_paddock_name);
            holder.paddockAreaTxt = (TextView) convertView
                    .findViewById(R.id.txt_paddock_area);
            holder.paddockCurrentCoverTxt = (TextView) convertView
                    .findViewById(R.id.txt_current_cover);
            holder.paddockGrowth = (TextView) convertView
                    .findViewById(R.id.txt_growth);
            holder.paddockPreviousCoverTxt = (TextView) convertView
                    .findViewById(R.id.txt_previous_cover);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Paddock paddock = (Paddock) getItem(position);
        holder.paddockIdTxt.setText(paddock.getID() + "");
        holder.paddockNameTxt.setText(paddock.getPaddockName());
        holder.paddockAreaTxt.setText("Area :" + paddock.getArea() + "");
        holder.paddockCurrentCoverTxt.setText("Current Cover:"+paddock.getCurrentCover() + "");
        holder.paddockPreviousCoverTxt.setText("Previous Cover:"+paddock.getPreviousCover());
        if (paddock.getPreviousCover()>=paddock.getCurrentCover()){
            holder.paddockGrowth.setText("Growth is NIL");
        }else{
            int diff = paddock.getCurrentCover()-paddock.getPreviousCover();
            int dayDiff = ((int)((paddock.getCurrentCoverDate().getTime()/(24*60*60*1000))
                    -(int)(paddock.getPreviousCoverDate().getTime()/(24*60*60*1000))));
            double growth = ((double) diff/dayDiff);
            if(Double.isInfinite(growth) || Double.isNaN(growth)){growth = 0;}
            BigDecimal roundGrowth = new BigDecimal(growth).setScale(0, RoundingMode.HALF_EVEN);
            holder.paddockGrowth.setText("Growth is :"+(roundGrowth));
        }

        return convertView;
    }

    @Override
    public void add(Paddock paddock) {
        paddocks.add(paddock);
        notifyDataSetChanged();
        super.add(paddock);
    }

    @Override
    public void remove(Paddock paddock) {
        paddocks.remove(paddock);
        notifyDataSetChanged();
        super.remove(paddock);
    }
}

