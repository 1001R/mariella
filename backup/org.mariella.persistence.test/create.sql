drop table adresse;

drop table privatadresse;

drop table person;

create table adresse (
    ID NUMBER PRIMARY KEY,
    ART CHAR(1) NOT NULL,
    STREET VARCHAR(64),
    PERSON_ID NUMBER
);

create table person (
    ID NUMBER PRIMARY KEY,
    NAME VARCHAR(64) NOT NULL,
    CONTACT_PERSON_ID NUMBER
);

create table privatadresse (
    PERSON_ID NUMBER NOT NULL,
    ADRESSE_ID NUMBER NOT NULL,
    PRIMARY KEY (PERSON_ID, ADRESSE_ID)
);

