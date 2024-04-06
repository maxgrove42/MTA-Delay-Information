create database if not exists mtaDelays;
use mtaDelays;
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
