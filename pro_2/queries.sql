select count(*) from User;

select count(*) from Items where BINARY Location_content='New York';

select count(*) from (select c.ItemID from Categorys c group by c.ItemID having count(c.category)=4) as T;

create view temper(ItemID,Currently) as select ItemID,Currently from Items where Ends>"2001-12-20 00:00:01" and Number_of_Bids>0;

select ItemID from temper where Currently=(select max(Currently) from temper);

drop view temper; 

select count(*) from User where Sellrate>=1000;

select count(*) from User where Sellrate!=0 and Buyrate!=0; 

select count(*) from (select * from (select * from (select i.ItemID,c.category from Items i join Categorys c on i.ItemID=c.ItemID where i.Number_of_Bids>0 and i.Currently>100) as T1) as T2  group by category) as T3;