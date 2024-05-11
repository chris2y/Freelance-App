package com.example.freelancerapp10;

public interface PaymentListener {
    void initializePayment(String amount, String email, String fullName, String phone,
                           String uid2,String itemId,String deliveryDate,String proposalDescription);
}
