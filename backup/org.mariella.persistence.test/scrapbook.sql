drop table person

drop table adresse

drop table privatadresse

drop sequence idsequence

create table person(
	id number(30) not null primary key,
	name varchar(30) not null,
    contact_person_id number(30)
)

create table adresse (
	id number(30) not null primary key,
	strasse varchar(30) not null,
	person_id number(30)
)

create table privatadresse (
    person_id integer not null,
    adresse_id integer not null
)

alter table person add constraint person_contact_person 
    foreign key (contact_person_id)
    references person (id)
    enable validate
 
alter table adresse add constraint adresse_person
    foreign key (person_id)
    references person (id)
    enable validate

alter table privatadresse add constraint privatadresse_person
    foreign key (person_id)
    references person (id)
    enable validate

alter table privatadresse add constraint privatadresse_adresse
    foreign key (adresse_id)
    references adresse (id)
    enable validate

create sequence idsequence start with 1 

insert into person (id, name) values (1, 'aim')

insert into person (id, name) values (2, 'ms')

insert into adresse (id, strasse, person_id) values(3, 'schelleingasse', 1)

insert into adresse (id, strasse, person_id) values(4, 'amalienstrasse', 1)

commit


select * from substance_plate_well where plate_code = 'BR3015037'

desc substance_plate_well

SELECT PERSON.ID, ADRESSE.ID, ADRESSE.STRASSE FROM ADRESSE, PERSON WHERE ADRESSE.PERSON_ID (+) = PERSON.ID


SELECT BATCH_RESULT0.ID, BATCH_RESULT_PLATE0.ID, BATCH_RESULT_WELL0.ID, SUBSTANCE0.SUBSTANCE_ID, SUBSTANCE0.SUBSTANCE_CODE 
FROM BATCH BATCH0, BATCH_RESULT BATCH_RESULT0, BATCH_RESULT_PLATE BATCH_RESULT_PLATE0, BATCH_RESULT_WELL BATCH_RESULT_WELL0, SUBSTANCE SUBSTANCE0 
WHERE BATCH_RESULT_PLATE0.ID = 2549360020 AND BATCH_RESULT0.BATCH_ID = BATCH0.ID AND BATCH_RESULT_PLATE0.BATCH_RESULT_ID = BATCH_RESULT0.ID AND BATCH_RESULT_WELL0.PLATE_ID = BATCH_RESULT_PLATE0.ID AND BATCH_RESULT_WELL0.SUBSTANCE_ID (+) = SUBSTANCE0.SUBSTANCE_ID

desc batch_result_plate

select count(*) from substance_plate

SELECT BATCH_RESULT_PLATE0.ID, BATCH_RESULT_PLATE0.PLATE_INDEX, BATCH_RESULT_PLATE0.ASSAY_SET_INDEX, BATCH_RESULT_PLATE0.PLATE_DIMENSION, BATCH_RESULT_PLATE0.QUALIFIED 
FROM BATCH_RESULT_PLATE BATCH_RESULT_PLATE0 
WHERE BATCH_RESULT_PLATE0.BATCH_RESULT_ID = 2549357324 AND BATCH_RESULT_PLATE0.PLATE_INDEX = 0


SELECT BATCH_RESULT_PLATE0.ID, BATCH_RESULT_PLATE0.PLATE_INDEX, BATCH_RESULT_PLATE0.ASSAY_SET_INDEX, BATCH_RESULT_PLATE0.PLATE_DIMENSION, BATCH_RESULT_PLATE0.QUALIFIED FROM BATCH_RESULT_PLATE BATCH_RESULT_PLATE0 WHERE BATCH_RESULT_PLATE0.BATCH_RESULT_ID = 2549357324 AND BATCH_RESULT_PLATE0.PLATE_INDEX = 5

SELECT BATCH_RESULT_PLATE0.ID, ASSAY0.ID, ASSAY0.NAME FROM BATCH_RESULT_PLATE BATCH_RESULT_PLATE0, ASSAY ASSAY0 WHERE BATCH_RESULT_PLATE0.BATCH_RESULT_ID = 2549357324 AND BATCH_RESULT_PLATE0.PLATE_INDEX = 5 AND BATCH_RESULT_PLATE0.ASSAY_ID = ASSAY0.ID

SELECT BATCH_RESULT_PLATE0.ID, BATCH_RESULT_WELL0.ID, BATCH_RESULT_WELL0.WELL_TYPE, BATCH_RESULT_WELL0.QUALIFIED, BATCH_RESULT_WELL0.RAW_RESULT, BATCH_RESULT_WELL0.DILLUTION_FACTOR, BATCH_RESULT_WELL0.CONTROL_SUBSTANCE_TYPE, BATCH_RESULT_WELL0.WELL_POSITION FROM BATCH_RESULT_PLATE BATCH_RESULT_PLATE0, BATCH_RESULT_WELL BATCH_RESULT_WELL0 WHERE BATCH_RESULT_PLATE0.BATCH_RESULT_ID = 2549357324 AND BATCH_RESULT_PLATE0.PLATE_INDEX = 5 AND BATCH_RESULT_WELL0.PLATE_ID = BATCH_RESULT_PLATE0.ID

SELECT BATCH_RESULT_PLATE0.ID, BATCH_RESULT_WELL0.ID, SUBSTANCE0.SUBSTANCE_ID, SUBSTANCE0.SUBSTANCE_CODE FROM BATCH_RESULT_PLATE BATCH_RESULT_PLATE0, BATCH_RESULT_WELL BATCH_RESULT_WELL0, SUBSTANCE SUBSTANCE0 WHERE BATCH_RESULT_PLATE0.BATCH_RESULT_ID = 2549357324 AND BATCH_RESULT_PLATE0.PLATE_INDEX = 5 AND BATCH_RESULT_WELL0.PLATE_ID = BATCH_RESULT_PLATE0.ID AND BATCH_RESULT_WELL0.SUBSTANCE_ID (+) = SUBSTANCE0.SUBSTANCE_ID

SELECT BATCH_RESULT_PLATE0.ID, BATCH_RESULT_WELL0.ID, CONTROL_SUBSTANCE0.ID, CONTROL_SUBSTANCE0.NAME, CONTROL_SUBSTANCE0.CONTROL_SUBSTANCE_TYPE FROM BATCH_RESULT_PLATE BATCH_RESULT_PLATE0, BATCH_RESULT_WELL BATCH_RESULT_WELL0, CONTROL_SUBSTANCE CONTROL_SUBSTANCE0 WHERE BATCH_RESULT_PLATE0.BATCH_RESULT_ID = 2549357324 AND BATCH_RESULT_PLATE0.PLATE_INDEX = 5 AND BATCH_RESULT_WELL0.PLATE_ID = BATCH_RESULT_PLATE0.ID AND BATCH_RESULT_WELL0.CONTROL_SUBSTANCE_ID (+) = CONTROL_SUBSTANCE0.ID




drop index batch_result_batch_id

create index batch_result_batch_id on batch_result (batch_id)

drop index brw_batch_result

create index brp_batch_result on batch_result_plate(batch_result_id)

drop index brw_bp

create index brw_bp on batch_result_well (plate_id)

drop index brw_substance

create index brw_substance on batch_result_well (substance_id)






SELECT BATCH_RESULT_PLATE0.ID, BATCH_RESULT_WELL0.ID, SUBSTANCE0.SUBSTANCE_ID, SUBSTANCE0.SUBSTANCE_CODE 
FROM 
    BATCH_RESULT_PLATE BATCH_RESULT_PLATE0, 
    BATCH_RESULT_WELL BATCH_RESULT_WELL0, 
    SUBSTANCE SUBSTANCE0 
WHERE 
        BATCH_RESULT_PLATE0.BATCH_RESULT_ID = 2549357324 
    AND BATCH_RESULT_PLATE0.PLATE_INDEX = 2 
    AND BATCH_RESULT_WELL0.PLATE_ID = BATCH_RESULT_PLATE0.ID 
    AND BATCH_RESULT_WELL0.SUBSTANCE_ID (+) = SUBSTANCE0.SUBSTANCE_ID

EXEC DBMS_STATS.delete_schema_stats('AIM');

commit

desc batch_result_well

desc batch_result_plate

analyze table substance compute statistics;

analyze table batch_result_well compute statistics 

desc substance

select count(*) from batch_result_well

select count(*) from substance

select count(*) from substance_plate_well

