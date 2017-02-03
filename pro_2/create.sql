CREATE TABLE IF NOT EXISTS Items(
	ItemID int not null,
	Name varchar(100) not null,
	Currently decimal(8,2),
	Buy_Price decimal(8,2),
	First_Bid decimal(8,2),
	Number_of_Bids int,
	Location_content varchar(100),
	Location_geo varchar(100),
	Country varchar(100),
	Started TIMESTAMP not null,
 	Ends TIMESTAMP,
 	sellerid varchar(100) not null,
 	Description varchar(4000),
 	PRIMARY KEY(ItemID)
)ENGINE=INNODB;


CREATE TABLE IF NOT EXISTS Categorys(
	ItemID int not null,
	Category varchar(100) not null,
	FOREIGN KEY (ItemID) references Items(ItemID)
)ENGINE=INNODB;


CREATE TABLE IF NOT EXISTS User(
	UserID varchar(100) not null,
	Sellrate int not null,
	Buyrate int not null,
	Location varchar(100),
	Country varchar(100),
	PRIMARY KEY (UserID)

)ENGINE=INNODB;


CREATE TABLE IF NOT EXISTS Bider(
	ItemID int not null,
	Time TIMESTAMP not null,
	UserID varchar(100) not null,
	Amount int not null,
	PRIMARY KEY (ItemID,Time),
	FOREIGN KEY(ItemId) references Items(ItemID),
	FOREIGN KEY(UserID) references User(UserID)
)ENGINE=INNODB;


