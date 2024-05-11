package com.example.freelancerapp10.model;

import com.google.firebase.database.ServerValue;

public class ContractModel {

    String contractId;
    String deliveryDate;
    String descriptionJob;
    String price;
    String proposalDescription;
    Object timestamp;
    String title;
    String userId1;
    String userId2;
    String key;

    public ContractModel() {
    }

    public ContractModel(String contractId, String deliveryDate, String descriptionJob, String price,
                         String proposalDescription, String title, String userId1, String userId2) {
        this.contractId = contractId;
        this.deliveryDate = deliveryDate;
        this.descriptionJob = descriptionJob;
        this.price = price;
        this.proposalDescription = proposalDescription;
        //this.timestamp = ServerValue.TIMESTAMP;
        this.title = title;
        this.userId1 = userId1;
        this.userId2 = userId2;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getDescriptionJob() {
        return descriptionJob;
    }

    public void setDescriptionJob(String descriptionJob) {
        this.descriptionJob = descriptionJob;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProposalDescription() {
        return proposalDescription;
    }

    public void setProposalDescription(String proposalDescription) {
        this.proposalDescription = proposalDescription;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserId1() {
        return userId1;
    }

    public void setUserId1(String userId1) {
        this.userId1 = userId1;
    }

    public String getUserId2() {
        return userId2;
    }

    public void setUserId2(String userId2) {
        this.userId2 = userId2;
    }

    @Override
    public String toString() {
        return "ContractModel{" +
                "contractId='" + contractId + '\'' +
                ", deliveryDate='" + deliveryDate + '\'' +
                ", descriptionJob='" + descriptionJob + '\'' +
                ", price='" + price + '\'' +
                ", proposalDescription='" + proposalDescription + '\'' +
                ", timestamp=" + timestamp +
                ", title='" + title + '\'' +
                ", userId1='" + userId1 + '\'' +
                ", userId2='" + userId2 + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
