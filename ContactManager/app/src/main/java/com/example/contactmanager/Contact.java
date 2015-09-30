package com.example.contactmanager;

/**
 * Created by Mélanie on 18/09/2015.
 */
public class Contact {

    private String name;
    private String phoneNumber;
    private String mail;

    public Contact(String name, String phoneNumber, String mail)
    {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.mail = mail;
    }

    @Override
    public String toString() {
        return this.getName() +" \n" +this.phoneNumber + "\n" + this.mail;

    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

}
