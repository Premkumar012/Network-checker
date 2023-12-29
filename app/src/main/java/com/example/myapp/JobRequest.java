package com.example.myapp;


public class JobRequest {

    String subject;
    String wrokerId;
    JobRequestInfo jobRequestInfo;
    public String getSubject() {
        return subject;
    }
    public String getWrokerId() {
        return wrokerId;
    }
    public JobRequestInfo getJobRequestInfo() {
        return jobRequestInfo;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public void setWrokerId(String wrokerId) {
        this.wrokerId = wrokerId;
    }
    public void setJobRequestInfo(JobRequestInfo jobRequestInfo) {
        this.jobRequestInfo = jobRequestInfo;
    }
    @Override
    public String toString() {
        return "JobRequest [subject=" + subject + ", wrokerId=" + wrokerId + ", jobRequestInfo=" + jobRequestInfo + "]";
    }



}