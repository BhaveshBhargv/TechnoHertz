package com.technoHertz.web.models;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "Users")
public class UserData implements Serializable {
    private static final long serialVersionUID = -2343243243242432341L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String company;
    private String email;
    private String password;
    private String title;
    private String phoneNumber;
    private String department;
    private String jobType;
    private String fileName;
    private String type;

    @Lob
    private byte[] data;

}
