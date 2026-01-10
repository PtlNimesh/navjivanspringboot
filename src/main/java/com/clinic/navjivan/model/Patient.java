
package com.clinic.navjivan.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String dob;
    private String contactNumber;
    private String email;
    private String disease;
    private String lastCompletedAppointmentDate;
    private String lastCompletedAppointmentReason;
    private String lastCompletedAppointmentNotes;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getLastCompletedAppointmentDate() {
        return lastCompletedAppointmentDate;
    }

    public void setLastCompletedAppointmentDate(String lastCompletedAppointmentDate) {
        this.lastCompletedAppointmentDate = lastCompletedAppointmentDate;
    }

    public String getLastCompletedAppointmentReason() {
        return lastCompletedAppointmentReason;
    }

    public void setLastCompletedAppointmentReason(String lastCompletedAppointmentReason) {
        this.lastCompletedAppointmentReason = lastCompletedAppointmentReason;
    }

    public String getLastCompletedAppointmentNotes() {
        return lastCompletedAppointmentNotes;
    }

    public void setLastCompletedAppointmentNotes(String lastCompletedAppointmentNotes) {
        this.lastCompletedAppointmentNotes = lastCompletedAppointmentNotes;
    }
}
