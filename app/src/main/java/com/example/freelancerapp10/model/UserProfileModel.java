package com.example.freelancerapp10.model;

public class UserProfileModel {
    private String dataEmail;
    private String phoneNumber;
    private String fullName;
    private String aboutMe;
    private String lookingTo;
    private String profession;
    private String skill;
    private String education;
    private String dataProfileImage;


    public UserProfileModel() {
    }

    public UserProfileModel(String dataEmail, String phoneNumber, String fullName,
                            String lookingTo,String aboutMe, String profession,
                            String skill, String education) {
        this.dataEmail = dataEmail;
        this.phoneNumber = phoneNumber;
        this.fullName = fullName;
        this.aboutMe = aboutMe;
        this.lookingTo = lookingTo;
        this.profession = profession;
        this.skill = skill;
        this.education = education;
    }
    public UserProfileModel(String dataEmail, String phoneNumber, String fullName, String lookingTo) {
        this.dataEmail = dataEmail;
        this.phoneNumber = phoneNumber;
        this.fullName = fullName;
        this.lookingTo = lookingTo;
    }

    public String getDataProfileImage() {
        return dataProfileImage;
    }

    public void setDataProfileImage(String dataProfileImage) {
        this.dataProfileImage = dataProfileImage;
    }

    public String getDataEmail() {
        return dataEmail;
    }

    public void setDataEmail(String dataEmail) {
        this.dataEmail = dataEmail;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public String getLookingTo() {
        return lookingTo;
    }

    public void setLookingTo(String lookingTo) {
        this.lookingTo = lookingTo;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }
}
