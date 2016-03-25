package com.lxdnz.nz.myfarmapp.databases;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by alex on 24/07/15.
 */
public class Stock implements Parcelable{

    private int _sid;
    private String _stockname;
    private int _quantity;
    private double _dailyfeed;
    private int _supplement;
    private double _supKg;
    private double _grassKg;
    private double _areaUsing;

    private Paddock paddockin;

    public Stock() {
        super();
    }

    private Stock(Parcel in) {
        super();
        this._sid = in.readInt();
        this._stockname = in.readString();
        this._quantity = in.readInt();
        this._dailyfeed = in.readDouble();
        this._supplement = in.readInt();
        this._supKg = in.readDouble();
        this._grassKg = in.readDouble();
        this._areaUsing = in.readDouble();

        this.paddockin = in.readParcelable(Paddock.class.getClassLoader());
    }


    public void setID(int sid) {
        this._sid = sid;
    }

    public int getID() {
        return this._sid;
    }

    public void setStockName(String stockname) {
        this._stockname = stockname;
    }

    public String getStockName() {
        return this._stockname;
    }

    public void setQuantity(int quantity) {
        this._quantity = quantity;
    }

    public int getQuantity() {
        return this._quantity;
    }

    public void setDailyfeed(double dailyfeed) {
        this._dailyfeed = dailyfeed;
    }

    public double getDailyfeed() {
        return this._dailyfeed;
    }

    public void setSupplement(int supplement){
        this._supplement = supplement;
    }

    public int getSupplement() {
        return this._supplement;
    }

    public void setSupKg(double supKg){
        this._supKg = supKg;
    }

    public double getSupKg() {
        return this._supKg;
    }

    public void setGrassKg(){
        if(getSupplement() == 1){
            double grassKg = getDailyfeed() - getSupKg();
            this._grassKg = grassKg;
        }else{
            double grassKg = getDailyfeed();
            this._grassKg = grassKg;
        }
    }

    public double getGrassKg() {
        return this._grassKg;
    }

    public void setAreaUsing(double areaUsing) {
        this._areaUsing = areaUsing;
    }

    public double getAreaUsing() {
        return this._areaUsing;
    }

    public void setPaddock(Paddock paddockin){
        this.paddockin = paddockin;
    }

    public Paddock getPaddock() {
        return paddockin;
    }

    @Override
    public String toString() {
        return "Stock " + _stockname + ": " + _quantity + " head";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + _sid;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Stock other = (Stock) obj;
        if (_sid != other._sid)
            return false;
        return true;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(getID());
        parcel.writeString(getStockName());
        parcel.writeDouble(getQuantity());
        parcel.writeDouble(getDailyfeed());
        parcel.writeInt(getSupplement());
        parcel.writeDouble(getSupKg());
        parcel.writeDouble(getGrassKg());
        parcel.writeDouble(getAreaUsing());
        parcel.writeParcelable(getPaddock(), flags);
    }

    public static final Parcelable.Creator<Stock> CREATOR = new Parcelable.Creator<Stock>() {
        public Stock createFromParcel(Parcel in) {
            return new Stock(in);
        }

        public Stock[] newArray(int size) {
            return new Stock[size];
        }
    };
}
