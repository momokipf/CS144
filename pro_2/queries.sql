select count(*) from User;

select count(*) as sum from Items where lower(Location_content) like '%new york%';

select count(*) as sum from (select c.ItemID from Categorys c group by c.ItemID having count(c.category)=4) as T;


select i.ItemID,b.UserID,b.Amount from Items i join Bider b on i.ItemID=b.ItemID where b.Time>"2001-12-20 00:00:01" and i.Currently=b.Amount and i.Number_of_bids>0; 


select count(*) as sumnumer from User where Sellrate>=1000;

select count(*) as sumnumer from User where Sellrate!=0 and Buyrate!=0;

select count(*) as sumnumer from (select * from (select * from (select i.ItemID,c.category from Items i join Categorys c on i.ItemID=c.ItemID where i.Number_of_Bids>0 and i.Currently>100) as T1) as T2  group by category) as T3;