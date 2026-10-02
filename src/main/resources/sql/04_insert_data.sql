-- Script de inserción de datos de ejemplo para 01_create_database.sql

-- Tabla identification
insert into identification (name, description) values
('DNI', 'Documento Nacional de Identidad'),
('Pasaporte', 'Documento de viaje internacional'),
('Carnet de Extranjería', 'Documento para extranjeros');

-- Tabla speciality
insert into speciality (name) values
('Cardiología'),
('Pediatría'),
('Dermatología');

-- Tabla person
insert into person (document_type_code, document_number, names, surnames, date_of_birth, email, phone_number) values
('01', '46117318', 'Juan', 'Pérez Gómez', '1980-05-12', 'juan.perez@email.com', '987654321'),
('07', '7896951738', 'María', 'López Ruiz', '1990-08-23', 'mar.lopez@email.com', '912345678'),
('04', '009698', 'Carlos', 'Ramírez Torres', '1975-11-30', 'carlos.rez@email.com', '934567890');

-- Tabla doctor
insert into doctor (id, speciality_id) values
(1, 1),
(2, 2);

-- Tabla patient
insert into patient (id) values
(3);

-- Tabla appointment
insert into appointment (doctor_id, patient_id, appointment_date, reason) values
(1, 3, '2025-07-25 09:00:00', 'Chequeo general'),
(2, 3, '2025-07-26 10:30:00', 'Consulta pediátrica');
