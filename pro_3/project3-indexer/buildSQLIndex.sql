CREATE TABLE Item_loc (
	ItemID int not null,
	g GEOMETRY NOT NULL, 
	SPATIAL INDEX(g),
	PRIMARY KEY(ItemID)
	) ENGINE=MyISAM;


INSERT INTO Item_loc(ItemID,g)
Select ItemID, Point(location_geo_lat,Location_geo_long)
from Items
where location_geo_lat != 'NULL' and Location_geo_long != 'NULL';



DROP INDEX sp_index ON geom;