package com.lead.infosystems.schooldiary.Management;

/**
 * Created by MADHU on 12/30/2016.
 */

public class ItemDetail {
    String firstName, lastName, mobile_no, gmail_id, profil_pic, designation, qualifications, interests_field, contact_detail;
    public ItemDetail(String firstName, String lastName, String mobile_no, String gmail_id, String profil_pic, String qualifications, String designation, String interests_field, String contact_detail) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobile_no = mobile_no;
        this.gmail_id = gmail_id;
        this.profil_pic = profil_pic;
        this.qualifications = qualifications;
        this.designation = designation;
        this.interests_field = interests_field;
        this.contact_detail = contact_detail;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public String getGmail_id() {
        return gmail_id;
    }

    public String getProfil_pic() {
        return profil_pic;
    }

    public String getDesignation() {
        return designation;
    }

    public String getQualifications() {
        return qualifications;
    }

    public String getInterests_field() {
        return interests_field;
    }

    public String getContact_detail() {
        return contact_detail;
    }

}
