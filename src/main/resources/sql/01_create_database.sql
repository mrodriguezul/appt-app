create table speciality (
  id bigint primary key generated always as identity,
  name varchar(40) not null
);

create table person (
  id bigint primary key generated always as identity,
  document_type_code varchar(2) not null,
  document_number varchar(20) not null,
  names varchar(30) not null,
  surnames varchar(40),
  date_of_birth date not null,
  email varchar(30),
  phone_number varchar(15)
);

CREATE UNIQUE INDEX person_document_type_code_idx ON public.person (document_type_code,document_number);

create table doctor (
  id bigint primary key references person (id),
  speciality_id bigint not null references speciality (id)
);

create table patient (
  id bigint primary key references person (id),
  medical_record_number varchar(20) not null
);

create table appointment (
  id bigint primary key generated always as identity,
  doctor_id bigint not null references doctor (id),
  patient_id bigint not null references patient (id),
  appointment_date timestamp not null,
  reason text not null
);

CREATE UNIQUE INDEX appointment_doctor_id_idx ON public.appointment (doctor_id,appointment_date);

create table users (
    username varchar(30)  not null primary key,
    password varchar(100) not null,
    email    varchar(50),
    locked   boolean      not null,
    disabled boolean      not null
);

CREATE TABLE users_role (
    username VARCHAR(50) NOT NULL,
    role VARCHAR(50) NOT NULL,
    granted_date TIMESTAMP NOT NULL,
    PRIMARY KEY (username, role),
    CONSTRAINT fk_users_role_user
        FOREIGN KEY (username)
            REFERENCES users(username)
);
