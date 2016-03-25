package com.lxdnz.nz.myfarmapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.lxdnz.nz.myfarmapp.R;
import com.lxdnz.nz.myfarmapp.databases.Stock;

import java.util.List;

/**
 * Created by alex on 28/07/15.
 */
public class StockListAdapter extends ArrayAdapter<Stock> {

    private Context context;
    List<Stock> stocks;

    public StockListAdapter(Context context, List<Stock> stocks){
        super(context, R.layout.list_stock, stocks);
        this.context = context;
        this.stocks = stocks;
    }

    private class ViewHolder {
        TextView stockIdTxt;
        TextView stockNameTxt;
        TextView stockQuantityTxt;
        TextView stockDailyfeedTxt;
        CheckBox stockSupplementBox;
        TextView stockSupKgTxt;
        TextView stockGrassKgTxt;
        TextView stockPaddockInTxt;
        TextView stockAreaUsingTxt;
    }

    @Override
    public int getCount() {
        return stocks.size();
    }

    @Override
    public Stock getItem(int position) {
        return stocks.get(position);
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
            convertView = inflater.inflate(R.layout.list_stock, null);
            holder = new ViewHolder();

            holder.stockIdTxt = (TextView) convertView
                    .findViewById(R.id.txt_stock_id);
            holder.stockNameTxt = (TextView) convertView
                    .findViewById(R.id.txt_stock_name);
            holder.stockQuantityTxt = (TextView) convertView
                    .findViewById(R.id.txt_stock_quantity);
            holder.stockDailyfeedTxt = (TextView) convertView
                    .findViewById(R.id.txt_stock_dailyfeed);
            holder.stockSupplementBox = (CheckBox) convertView
                    .findViewById(R.id.checkbox_supp);
            holder.stockSupKgTxt = (TextView) convertView
                    .findViewById(R.id.txt_stock_suppkg);
            holder.stockGrassKgTxt = (TextView) convertView
                    .findViewById(R.id.txt_stock_grasskg);
            holder.stockPaddockInTxt = (TextView) convertView
                    .findViewById(R.id.txt_stock_in_paddock);
            holder.stockAreaUsingTxt = (TextView) convertView
                    .findViewById(R.id.txt_area_using);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Stock stock = (Stock) getItem(position);
        holder.stockIdTxt.setText(stock.getID() + "");
        holder.stockNameTxt.setText(stock.getStockName());
        holder.stockQuantityTxt.setText(stock.getQuantity() + "");
        holder.stockDailyfeedTxt.setText("Require " + stock.getDailyfeed() + "Kg Daily");
        if (stock.getSupplement() == 1){

            holder.stockSupplementBox.setChecked(true);
        }else{
            holder.stockSupplementBox.setChecked(false);
        }
        holder.stockSupKgTxt.setText(stock.getSupKg() + "Kg Supplement Daily");
        holder.stockGrassKgTxt.setText("Require " + stock.getGrassKg() + "Kg of Grass Daily");
        holder.stockPaddockInTxt.setText("In Paddock: " + stock.getPaddock());
        holder.stockAreaUsingTxt.setText("Using " + stock.getAreaUsing() + "ha");


        return convertView;
    }

    @Override
    public void add(Stock stock) {
        stocks.add(stock);
        notifyDataSetChanged();
        super.add(stock);
    }

    @Override
    public void remove(Stock stock) {
        stocks.remove(stock);
        notifyDataSetChanged();
        super.remove(stock);
    }
}
