Relations (R):
1. ItemID(key) ->-> Name,Category,Currently,Buy_Price,First_BId,Number_of_Bids,Bids,Location,Country,Started,Ends,Seller,Descrption,
2. ItemID,Bids,Time -> UserID,Rating
3. Location -> latitude,longtitude







Table1.	ItemID -> Name,Currently,Buy_Price,First_Bid,Number_of_Bids,Started,Ends,Description
Table2. ItemID -> Category
Table3. ItemID,Time -> UserID,Amount
Table4. UserId -> rating(b),rating(s),Location,Country
Table5. Itemid,UserId -> latitude,longitude



	public String itemid = null;	

	public String name = null;
	public List<String> category = null;
	public String currently;
	public String buy_price;
	public String first_bid;
	public int number_of_Bids;
	private List<Bid> bids;
	public Location loc = null;
	public String country = null;
	public String started = null;
	public String ends = null;
	public Seller sel = null;
	public String description = null;

