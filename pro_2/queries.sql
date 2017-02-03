select count(*) from User;

select count(*) as sum from Items where lower(Location_content) like '%new york%';

select count(*) as sum from (select c.ItemID from Categorys c group by c.ItemID having count(c.category)=4) as T;