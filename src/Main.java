// TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import entidades.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("Hospital Management System process initiated.");

            // 1. Environment Setup: Hospital, Departments, and Rooms
            Hospital hospital = new Hospital("Central City Hospital", "123 Health Ave.", "555-0100");

            Departamento cardiologyDept = new Departamento("Cardiology", EspecialidadMedica.CARDIOLOGIA);
            cardiologyDept.crearSala("CARD-101", "Cardiology Office");
            cardiologyDept.crearSala("CARD-102", "Operating Room");

            Departamento pediatricsDept = new Departamento("Pediatrics", EspecialidadMedica.PEDIATRIA);
            pediatricsDept.crearSala("PED-201", "Pediatric Office");

            Departamento traumatologyDept = new Departamento("Traumatology", EspecialidadMedica.TRAUMATOLOGIA);
            traumatologyDept.crearSala("TRA-301", "Orthopedics Room");

            hospital.agregarDepartamento(cardiologyDept);
            hospital.agregarDepartamento(pediatricsDept);
            hospital.agregarDepartamento(traumatologyDept);

            // 2. Data Registration: Medical Staff and Patients
            Medico cardiologist = new Medico("Ricardo", "Peralta", "98765432", LocalDate.of(1978, 11, 20), TipoSangre.O_POSITIVO, "MP-98765", EspecialidadMedica.CARDIOLOGIA);
            Medico pediatrician = new Medico("Laura", "Vega", "87654321", LocalDate.of(1982, 2, 10), TipoSangre.A_NEGATIVO, "MP-87654", EspecialidadMedica.PEDIATRIA);
            Medico traumatologist = new Medico("Jorge", "Campos", "76543210", LocalDate.of(1975, 7, 30), TipoSangre.AB_POSITIVO, "MP-76543", EspecialidadMedica.TRAUMATOLOGIA);

            cardiologyDept.agregarMedico(cardiologist);
            pediatricsDept.agregarMedico(pediatrician);
            traumatologyDept.agregarMedico(traumatologist);

            List<Medico> medicalStaff = new ArrayList<>();
            medicalStaff.add(cardiologist);
            medicalStaff.add(pediatrician);
            medicalStaff.add(traumatologist);

            Paciente cardiologyPatient = new Paciente("Roberto", "Gomez", "10101010", LocalDate.of(1960, 4, 1), TipoSangre.B_POSITIVO, "555-1111", "123 False St.");
            cardiologyPatient.getHistoriaClinica().agregarDiagnostico("Arrhythmia");
            cardiologyPatient.getHistoriaClinica().agregarTratamiento("Amiodarone 200mg");

            Paciente pediatricPatient = new Paciente("Sofia", "Luna", "20202020", LocalDate.of(2018, 9, 14), TipoSangre.O_NEGATIVO, "555-2222", "456 Sun Ave.");
            pediatricPatient.getHistoriaClinica().agregarDiagnostico("Well-child checkup");
            pediatricPatient.getHistoriaClinica().agregarAlergia("Lactose");

            Paciente traumaPatient = new Paciente("Marta", "Rios", "30303030", LocalDate.of(1995, 1, 25), TipoSangre.A_POSITIVO, "555-3333", "789 Moon Ln.");
            traumaPatient.getHistoriaClinica().agregarDiagnostico("Grade 2 ankle sprain");

            hospital.agregarPaciente(cardiologyPatient);
            hospital.agregarPaciente(pediatricPatient);
            hospital.agregarPaciente(traumaPatient);

            System.out.println("Initial data registration complete.");

            // 3. Core Logic: Appointment Management
            CitaManager appointmentManager = new CitaManager();

            appointmentManager.programarCita(cardiologyPatient, cardiologist, cardiologyDept.getSalas().getFirst(), LocalDateTime.now().plusDays(2).withHour(9).withMinute(30), new BigDecimal("250.00"));
            appointmentManager.programarCita(pediatricPatient, pediatrician, pediatricsDept.getSalas().getFirst(), LocalDateTime.now().plusDays(3).withHour(11).withMinute(0), new BigDecimal("150.00"));
            Cita traumaAppointment = appointmentManager.programarCita(traumaPatient, traumatologist, traumatologyDept.getSalas().getFirst(), LocalDateTime.now().plusDays(4).withHour(16).withMinute(45), new BigDecimal("200.00"));
            traumaAppointment.setEstado(EstadoCita.PROGRAMADA);
            traumaAppointment.setObservaciones("Patient requires follow-up X-ray.");

            System.out.println("Appointment scheduling complete.");

            // 4. System Reporting
            System.out.println("\n--- System Status Report ---");
            System.out.println("Hospital: " + hospital.getNombre());
            for (Departamento dep : hospital.getDepartamentos()) {
                System.out.println("  Department: " + dep.getNombre() + " (" + dep.getMedicos().size() + " physicians, " + dep.getSalas().size() + " rooms)");
            }

            System.out.println("\n--- Patient Appointment Summary ---");
            for (Paciente patient : hospital.getPacientes()) {
                System.out.println("  Patient: " + patient.getNombreCompleto() + " (DNI: " + patient.getDni() + ")");
                List<Cita> patientAppointments = appointmentManager.getCitasPorPaciente(patient);
                if (patientAppointments.isEmpty()) {
                    System.out.println("    -> No appointments scheduled.");
                } else {
                    patientAppointments.forEach(cita -> System.out.println("    -> " + cita.getFechaHora().toLocalDate() + " at " + cita.getFechaHora().toLocalTime() + " with Dr. " + cita.getMedico().getApellido() + " [" + cita.getEstado() + "]"));
                }
            }

            // 5. Validation Tests
            System.out.println("\n--- Executing Validation Tests ---");
            try {
                appointmentManager.programarCita(cardiologyPatient, cardiologist, cardiologyDept.getSalas().getFirst(), LocalDateTime.now().minusDays(1), new BigDecimal("100"));
                System.out.println("Validation failed: An appointment with a past date was allowed.");
            } catch (CitaException e) {
                System.out.println("Validation successful: Past-date appointment correctly prevented.");
            }

            try {
                appointmentManager.programarCita(pediatricPatient, pediatrician, pediatricsDept.getSalas().getFirst(), LocalDateTime.now().plusDays(10), new BigDecimal("-50.00"));
                System.out.println("Validation failed: An appointment with a negative cost was allowed.");
            } catch (CitaException e) {
                System.out.println("Validation successful: Negative cost appointment correctly prevented.");
            }

            System.out.println("\nProcess finished successfully.");

        } catch (Exception e) {
            System.err.println("A critical error occurred during execution: " + e.getMessage());
            e.printStackTrace();
        }
    }
}