package com.clinic.navjivan.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinic.navjivan.model.Appointment;
import com.clinic.navjivan.model.Patient;
import com.clinic.navjivan.repository.AppointmentRepository;
import com.clinic.navjivan.repository.PatientRepository;

@CrossOrigin(origins = {
        "http://localhost:3000",
        "https://navjivan.vercel.app"
}, maxAge = 3600)
@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    // Get all appointments
    @GetMapping
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        List<Appointment> appointments = appointmentRepository.findAll();
        System.out.println("Returning " + appointments.size() + " appointments");
        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(appointments);
    }

    // Get appointment by ID
    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));
        return ResponseEntity.ok(appointment);
    }

    // Get appointments by patient ID
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Appointment>> getAppointmentsByPatient(@PathVariable Long patientId) {
        List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
        return ResponseEntity.ok(appointments);
    }

    // Get appointments by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Appointment>> getAppointmentsByStatus(@PathVariable String status) {
        List<Appointment> appointments = appointmentRepository.findByStatus(status);
        return ResponseEntity.ok(appointments);
    }

    // Create a new appointment
    @PostMapping
    public ResponseEntity<Appointment> createAppointment(@RequestBody Appointment appointment) {
        if (appointment.getStatus() == null || appointment.getStatus().isEmpty()) {
            appointment.setStatus("REQUESTED");
        }
        Appointment createdAppointment = appointmentRepository.save(appointment);
        System.out.println("Created appointment: " + createdAppointment.getId());
        return ResponseEntity.ok(createdAppointment);
    }

    // Update an appointment
    @PutMapping("/{id}")
    public ResponseEntity<Appointment> updateAppointment(@PathVariable Long id,
            @RequestBody Appointment appointmentDetails) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));

        appointment.setPatientId(appointmentDetails.getPatientId());
        appointment.setPatientName(appointmentDetails.getPatientName());
        appointment.setPatientEmail(appointmentDetails.getPatientEmail());
        appointment.setPatientPhone(appointmentDetails.getPatientPhone());
        appointment.setAppointmentDate(appointmentDetails.getAppointmentDate());
        appointment.setAppointmentTime(appointmentDetails.getAppointmentTime());
        appointment.setReason(appointmentDetails.getReason());
        appointment.setStatus(appointmentDetails.getStatus());
        appointment.setNotes(appointmentDetails.getNotes());

        Appointment updatedAppointment = appointmentRepository.save(appointment);

        // If appointment is marked as Completed, update the patient's last completed
        // appointment details (guard against nulls to avoid NPE)
        if ("Completed".equals(appointmentDetails.getStatus())) {
            Long pid = appointmentDetails.getPatientId();
            Patient patient = null;

            if (pid != null) {
                patient = patientRepository.findById(pid).orElse(null);
            }

            // If no patient id, try to match by email or phone
            if (patient == null) {
                if (appointmentDetails.getPatientEmail() != null && !appointmentDetails.getPatientEmail().isEmpty()) {
                    patient = patientRepository.findByEmail(appointmentDetails.getPatientEmail()).orElse(null);
                }
            }

            if (patient == null) {
                if (appointmentDetails.getPatientPhone() != null && !appointmentDetails.getPatientPhone().isEmpty()) {
                    patient = patientRepository.findByContactNumber(appointmentDetails.getPatientPhone()).orElse(null);
                }
            }

            // If still not found, create a new patient record from appointment details
            if (patient == null) {
                Patient newPatient = new Patient();
                String fullName = appointmentDetails.getPatientName() != null ? appointmentDetails.getPatientName()
                        : "";
                String[] parts = fullName.trim().split(" ");
                if (parts.length > 0)
                    newPatient.setFirstName(parts[0]);
                if (parts.length > 1)
                    newPatient.setLastName(String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length)));
                newPatient.setEmail(appointmentDetails.getPatientEmail());
                newPatient.setContactNumber(appointmentDetails.getPatientPhone());
                if (appointmentDetails.getAppointmentDate() != null) {
                    newPatient.setLastCompletedAppointmentDate(appointmentDetails.getAppointmentDate().toString());
                }
                newPatient.setLastCompletedAppointmentReason(appointmentDetails.getReason());
                newPatient.setLastCompletedAppointmentNotes(appointmentDetails.getNotes());
                patient = patientRepository.save(newPatient);

                // update appointment to point to the new patient
                appointment.setPatientId(patient.getId());
                appointmentRepository.save(appointment);
            } else {
                // update existing patient last completed info
                if (appointmentDetails.getAppointmentDate() != null) {
                    patient.setLastCompletedAppointmentDate(appointmentDetails.getAppointmentDate().toString());
                }
                if (appointmentDetails.getReason() != null) {
                    patient.setLastCompletedAppointmentReason(appointmentDetails.getReason());
                }
                if (appointmentDetails.getNotes() != null) {
                    patient.setLastCompletedAppointmentNotes(appointmentDetails.getNotes());
                }
                patientRepository.save(patient);

                // ensure appointment.patientId is set
                if (appointment.getPatientId() == null && patient.getId() != null) {
                    appointment.setPatientId(patient.getId());
                    appointmentRepository.save(appointment);
                }
            }
        }

        return ResponseEntity.ok(updatedAppointment);
    }

    // Delete an appointment
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));
        appointmentRepository.delete(appointment);
        return ResponseEntity.noContent().build();
    }

    // Approve an appointment request
    @PutMapping("/{id}/approve")
    public ResponseEntity<Appointment> approveAppointment(@PathVariable Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));
        appointment.setStatus("SCHEDULED");
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        return ResponseEntity.ok(updatedAppointment);
    }

    // Reject an appointment request
    @PutMapping("/{id}/reject")
    public ResponseEntity<Appointment> rejectAppointment(@PathVariable Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));
        appointment.setStatus("REJECTED");
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        return ResponseEntity.ok(updatedAppointment);
    }
}
