package com.example.adminapp2020;

import android.os.Parcel;
import android.os.Parcelable;

public class ModelDriver implements Parcelable {
    private String drivername,phonenumber,regtrationnumber;
    private boolean assignWithBus;

    public ModelDriver() {
    }

    protected ModelDriver(Parcel in) {
        drivername = in.readString();
        phonenumber = in.readString();
        regtrationnumber = in.readString();
        assignWithBus = in.readByte() != 0;
    }

    public static final Creator<ModelDriver> CREATOR = new Creator<ModelDriver>() {
        @Override
        public ModelDriver createFromParcel(Parcel in) {
            return new ModelDriver(in);
        }

        @Override
        public ModelDriver[] newArray(int size) {
            return new ModelDriver[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(drivername);
        parcel.writeString(phonenumber);
        parcel.writeString(regtrationnumber);
        parcel.writeByte((byte) (assignWithBus ? 1 : 0));
    }

    public String getDrivername() {
        return drivername;
    }

    public void setDrivername(String drivername) {
        this.drivername = drivername;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getRegtrationnumber() {
        return regtrationnumber;
    }

    public void setRegtrationnumber(String regtrationnumber) {
        this.regtrationnumber = regtrationnumber;
    }

    public boolean isAssignWithBus() {
        return assignWithBus;
    }

    public void setAssignWithBus(boolean assignWithBus) {
        this.assignWithBus = assignWithBus;
    }

    public static Creator<ModelDriver> getCREATOR() {
        return CREATOR;
    }
}