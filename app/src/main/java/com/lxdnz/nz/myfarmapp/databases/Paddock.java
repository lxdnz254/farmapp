package com.lxdnz.nz.myfarmapp.databases;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by alex on 24/07/15.
 */
public class Paddock implements Parcelable{

    private int _pid;
    private String _paddockname;
    private double _area;
    private Date _currentCoverDate;
    private int _currentCover;
    private Date _previousCoverDate;
    private int _previousCover;
    private Date _lastGrazed;
    private int _grazing;
    private int _mapped;
    private String _polyPoints;


    public Paddock() {
        super();
    }


    private Paddock(Parcel in){
        super();
        this._pid = in.readInt();
        this._paddockname = in.readString();
        this._area = in.readDouble();
        this._currentCoverDate = new Date(in.readLong());
        this._currentCover = in.readInt();
        this._previousCoverDate = new Date(in.readLong());
        this._previousCover = in.readInt();
        this._lastGrazed = new Date(in.readLong());
        this._grazing = in.readInt();
        this._mapped = in.readInt();
        this._polyPoints = in.readString();

    }


    public void setID(int pid) {
        this._pid = pid;
    }

    public int getID() {
        return this._pid;
    }

    public void setPaddockName(String paddockname) {
        this._paddockname = paddockname;
    }

    public String getPaddockName() {
        return this._paddockname;
    }

    public void setArea(double area) {
        this._area = area;
    }

    public double getArea() {
        return this._area;
    }

    public void setCurrentCoverDate(Date currentCoverDate) {
        this._currentCoverDate = currentCoverDate;
    }

    public Date getCurrentCoverDate(){
        return this._currentCoverDate;
    }

    public void setCurrentCover(int currentCover) {
        this._currentCover = currentCover;
    }

    public int getCurrentCover(){
        return this._currentCover;
    }

    public void setPreviousCoverDate(Date previousCoverDate){
        this._previousCoverDate = previousCoverDate;
    }

    public Date getPreviousCoverDate() {
        return this._previousCoverDate;
    }

    public void setPreviousCover(int previousCover){
        this._previousCover = previousCover;
    }

    public int getPreviousCover() {
        return this._previousCover;
    }

    public void setLastGrazed(Date lastGrazed) {
        this._lastGrazed = lastGrazed;
    }

    public Date getLastGrazed() {
        return this._lastGrazed;
    }

    public void setGrazing(int grazing) {
        this._grazing = grazing;
    }

    public int getGrazing() {
        return this._grazing;
    }

    public void setMapped(int mapped) {this._mapped = mapped; }

    public int getMapped() { return this._mapped; }

    public void setPolyPoints(String polyPoints) {
        this._polyPoints = polyPoints;
    }

    public String getPolyPoints() {
        return this._polyPoints;
    }



    @Override
    public String toString() {
       // return "Paddock [pid=" + _pid + ", name=" + _paddockname + ", area=" + _area + "]";
        return "Paddock " + _paddockname;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + _pid;
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
        Paddock other = (Paddock) obj;
        if (_pid != other._pid)
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
        parcel.writeString(getPaddockName());
        parcel.writeDouble(getArea());
        parcel.writeLong(getCurrentCoverDate().getTime());
        parcel.writeInt(getCurrentCover());
        parcel.writeLong(getPreviousCoverDate().getTime());
        parcel.writeInt(getPreviousCover());
        parcel.writeLong(getLastGrazed().getTime());
        parcel.writeInt(getGrazing());
        parcel.writeInt(getMapped());
        parcel.writeString(getPolyPoints());
    }

    public static final Parcelable.Creator<Paddock> CREATOR = new Parcelable.Creator<Paddock>() {
        public Paddock createFromParcel(Parcel in) {
            return new Paddock(in);
        }

        public Paddock[] newArray(int size) {
            return new Paddock[size];
        }
    };

}
