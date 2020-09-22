package com.example.adminapp2020;

import android.os.Parcel;
import android.os.Parcelable;

public class ModelRegBus implements Parcelable {
    String busid,companyname,license,route;
    boolean assignedWithDriver;

    public ModelRegBus() {
    }

    protected ModelRegBus(Parcel in) {
        busid = in.readString();
        companyname = in.readString();
        license = in.readString();
        route = in.readString();
        assignedWithDriver = in.readByte() != 0;
    }

    public static final Creator<ModelRegBus> CREATOR = new Creator<ModelRegBus>() {
        @Override
        public ModelRegBus createFromParcel(Parcel in) {
            return new ModelRegBus(in);
        }

        @Override
        public ModelRegBus[] newArray(int size) {
            return new ModelRegBus[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(busid);
        parcel.writeString(companyname);
        parcel.writeString(license);
        parcel.writeString(route);
        parcel.writeByte((byte) (assignedWithDriver ? 1 : 0));
    }

    public String getBusid() {
        return busid;
    }

    public void setBusid(String busid) {
        this.busid = busid;
    }

    public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public boolean isAssignedWithDriver() {
        return assignedWithDriver;
    }

    public void setAssignedWithDriver(boolean assignedWithDriver) {
        this.assignedWithDriver = assignedWithDriver;
    }

    public static Creator<ModelRegBus> getCREATOR() {
        return CREATOR;
    }
}
