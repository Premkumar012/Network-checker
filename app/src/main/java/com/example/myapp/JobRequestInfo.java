package com.example.myapp;


public class JobRequestInfo {
    String jobId;
    String lat;
    String lng;
    String message;
    String payload;

    public String getJobId() {
        return jobId;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public String getMessage() {
        return message;
    }

    public String getPayload() {
        return payload;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "JobRequestInfo [jobId=" + jobId + ", lat=" + lat + ", lng=" + lng + ", message=" + message
                + ", payload=" + payload + "]";
    }

}
