package com.example.freelancerapp10.model;

public class WorkerModel {
    String userId;
    String dataProfileImage;
    String fullName;
    long createdTimestamp;
    String aboutMe;
    String skill;
    String profession;
    String education;
    String moneyEarned;
    String deliveredJobs;

    public WorkerModel() {
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDataProfileImage() {
        return dataProfileImage;
    }

    public void setDataProfileImage(String dataProfileImage) {
        this.dataProfileImage = dataProfileImage;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getMoneyEarned() {
        return moneyEarned;
    }

    public void setMoneyEarned(String moneyEarned) {
        this.moneyEarned = moneyEarned;
    }

    public String getDeliveredJobs() {
        return deliveredJobs;
    }

    public void setDeliveredJobs(String deliveredJobs) {
        this.deliveredJobs = deliveredJobs;
    }
    @Override
    public String toString() {
        return "WorkerModel{" +
                "userId='" + userId + '\'' +
                ", dataProfileImage='" + dataProfileImage + '\'' +
                ", fullName='" + fullName + '\'' +
                ", createdTimestamp=" + createdTimestamp +
                ", aboutMe='" + aboutMe + '\'' +
                ", skill='" + skill + '\'' +
                ", profession='" + profession + '\'' +
                ", education='" + education + '\'' +
                ", moneyEarned='" + moneyEarned + '\'' +
                ", deliveredJobs='" + deliveredJobs + '\'' +
                '}';
    }
}
