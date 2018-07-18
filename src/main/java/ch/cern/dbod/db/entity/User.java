/*
 * Copyright (C) 2015, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */

package ch.cern.dbod.db.entity;

import com.google.gson.annotations.SerializedName;
import java.util.Objects;

/**
 * Represents an user in the system.
 * @author Jose Andres Cordero Benitez
 */
public class User implements Comparable, Cloneable{
    /**
     * First name.
     */
    @SerializedName("first_name")
    private String firstName;

    /**
     * Last name.
     */
    @SerializedName("last_name")
    private String lastName;

    /**
     * Login of the user (max. 8)
     */
    @SerializedName("login")
    private String login;

    /**
     * Email.
     */
    @SerializedName("email")
    private String email;

    /**
     * Phone 1.
     */
    @SerializedName("phone1")
    private Integer phone1;

    /**
     * Phone 2.
     */
    @SerializedName("phone2")
    private Integer phone2;

    /**
     * Portable phone.
     */
    @SerializedName("portable")
    private Integer portable;

    /**
     * Department.
     */
    @SerializedName("department")
    private String department;
    
    /**
     * Group.
     */
    @SerializedName("group")
    private String group;
    
    /**
     * Section.
     */
    @SerializedName("section")
    private String section;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getPhone1() {
        return phone1;
    }

    public void setPhone1(Integer phone1) {
        this.phone1 = phone1;
    }

    public Integer getPhone2() {
        return phone2;
    }

    public void setPhone2(Integer phone2) {
        this.phone2 = phone2;
    }

    public Integer getPortable() {
        return portable;
    }

    public void setPortable(Integer portable) {
        this.portable = portable;
    }

    public String getDepartment()
    {
        return department;
    }

    public void setDepartment(String department)
    {
        this.department = department;
    }

    public String getGroup()
    {
        return group;
    }

    public void setGroup(String group)
    {
        this.group = group;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    @Override
    public int compareTo(Object object) {
        User user = (User) object;
        return this.getLogin().compareTo(user.getLogin());
    }

    @Override
    public User clone() {
        User clone = new User();
        clone.setFirstName(firstName);
        clone.setLastName(lastName);
        clone.setEmail(email);
        clone.setLogin(login);
        clone.setPhone1(phone1);
        clone.setPhone2(phone2);
        clone.setPortable(portable);
        clone.setSection(section);
        return clone;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof User) {
            User user = (User) object;
            return this.getLogin().equals(user.getLogin());
        }
        else{
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.login);
        hash = 29 * hash + Objects.hashCode(this.firstName);
        hash = 29 * hash + Objects.hashCode(this.lastName);
        hash = 29 * hash + Objects.hashCode(this.email);
        hash = 29 * hash + this.phone1;
        hash = 29 * hash + this.phone2;
        hash = 29 * hash + this.portable;
        hash = 29 * hash + Objects.hashCode(this.section);
        return hash;
    }

    @Override
    public String toString()
    {
        return "User{" + "firstName=" + firstName + ", lastName=" + lastName + ", login=" + login + ", email=" + email + ", phone1=" + phone1 + ", phone2=" + phone2 + ", portable=" + portable + ", section=" + section + '}';
    }
}
