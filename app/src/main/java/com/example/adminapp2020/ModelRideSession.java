package com.example.adminapp2020;

public class ModelRideSession {
    private String sessionId,driverId,busId;


    public ModelRideSession(String sessionId, String driverId, String busId) {
        this.sessionId = sessionId;
        this.driverId = driverId;
        this.busId = busId;
    }

    public ModelRideSession() {
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getBusId() {
        return busId;
    }

    public void setBusId(String busId) {
        this.busId = busId;
    }
}
