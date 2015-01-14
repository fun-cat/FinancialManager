Create table user (
	login text primary key not null,
	password text not null, 
	name text default ''
);

Create table account (
	accountID integer primary key AUTOINCREMENT not null,
	userLogin text not null,
	name text not null,
	rest money default 0,
	unique (name, userLogin)
);

Create table record (
	recordID integer primary key AUTOINCREMENT not null,
	accountID integer not null,
	date NUMERIC not null, 
	amount money not null, 
	categoryID integer not null, 
	description text default '',
	unique (accountID, date, amount, categoryID)
);

Create table category (
	categoryID integer primary key AUTOINCREMENT not null, 
	name text not null,
	unique (name)
);