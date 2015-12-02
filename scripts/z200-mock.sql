insert into series(name,title_sponsor) values ('Zappos Race Series','Zappos');
insert into series_region(series,name) values (1,'San Francisco');
insert into series_region(series,name) values (1,'Portland');
insert into series_region(series,name) values (1,'Seattle');
insert into series_region(series,name) values (1,'Austin');
insert into series_region(series,name) values (1,'Boston');
insert into series_region(series,name) values (1,'Charlotte');
insert into series_region(series,name) values (1,'Atlanta');
insert into series_region(series,name) values (1,'New York');
update event set series=1 where id=1;
update event set region=1 where id=1;

insert into event(name, series, region, city, state, country, time_start_local, time_start,featured) VALUES("Brazen Blast Run", 1, 2, 'Portland', 'OR', 'US', '01/07/2016 08:00:00 am', '2016-01-07 08:00:00',0);
