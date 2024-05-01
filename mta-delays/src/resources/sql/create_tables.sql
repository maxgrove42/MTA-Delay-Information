create database if not exists mtaDelays;
use mtaDelays;
drop table if exists arrivalTimeCache;
drop table if exists lastUpdate;
drop table if exists stations;
drop table if exists apis;
drop table if exists boroughs;



create table stations (
    stopID		varchar(5),
    line		varchar(3),
    stopName		varchar(40),
    borough		varchar(2),
    direction		varchar(40),
    PRIMARY KEY	(stopID, line)
);

create table apis (
    line		varchar(3),
    api			varchar(100),
    primary key (line)
);

create table boroughs (
    borough		varchar(3),
    boroughName		varchar(40),
    primary key (borough)
);

	create table lastUpdate (
		stopID		varchar(10),
		lastUpdate	DATETIME DEFAULT current_timestamp,
		PRIMARY KEY (stopID),
		FOREIGN KEY (stopID) references stations (stopID)
	);

create table arrivalTimeCache (
    stopID		varchar(10),
    line		varchar(10),
    arrivalTime	BIGINT,
	PRIMARY KEY (stopID, line, arrivalTime),
    FOREIGN KEY (stopID) references stations (stopID)
    -- FOREIGN KEY (line) references apis (line) -- excluding this as the API returns 6X and 7X for express trains!
);
