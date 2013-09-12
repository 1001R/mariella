create table person (
	id varchar(50) not null primary key,
	name varchar(30) not null
);

create table address (
	id varchar(50) not null primary key,
	person_id varchar(50) references person(id),
	description varchar(30) not null,
);

create table friend (
	my_id varchar(50) not null references person(id),
	friend_id varchar(50) not null references person(id)
);