package com.technoHertz.web.models;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserPojo implements Serializable {

    private String firstName;
    private String lastName;
    private String company;
    private String email;
    private String title;
    private String phoneNumber;
    private String department;
    private String jobType;


    public static UserPojo convetTOUserPojo(UserData userData) {
        UserPojo userPojo = new UserPojo();
        userPojo.setFirstName(userData.getFirstName());
        userPojo.setLastName(userData.getLastName());
        userPojo.setCompany(userData.getCompany());
        userPojo.setEmail(userData.getEmail());
        userPojo.setTitle(userData.getTitle());
        userPojo.setPhoneNumber(userData.getPhoneNumber());
        userPojo.setDepartment(userData.getDepartment());
        userPojo.setJobType(userData.getJobType());
        return userPojo;
    }
}
