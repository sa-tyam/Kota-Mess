# Customers #

1. Customer Id
2. Name
3. Mobile Number
4. Gender
5. Address
6. Balance
7. Plan
8. History
9. Current Orders
10. Refferal Code

# Mess #

1. Id
2. Name
3. Address
4. Phone Number
5. Owner Name
6. Owner Phone Number
7. Owner Address
8. Owner Aadhar Download Link
9. Current Meal
10. Mess Timings
11. Items
12. Meals
13. Monthly Price
14. Area
15. Description
16. Past Orders
17. City
19. Delivery Phone Numbers
20. Verified or Not
21. Ratings

# Meal #

1. Id
2. Mess Id
3. Mess Name
4. Picture Download Link
5. Special or Normal
6. Price
7. Delivery Price
8. Items
9. Lunch or Dinner
11. Ratings

# Items #

1. Item Id
2. Name
3. Detail
4. Amount
5. Mess Id

# Order #

1. Id
2. Mess Id
3. Dish Id
4. Customer Id
5. Mess Name
6. Customer name
7. Order Time
8. Address
9. Status
10. Customer Phone Number
11. Mess or Delivery
12. Delivery Phone Number
13. Lunch or Dinner
14. Order Date
15. Delivered Time
16. Order Type
17. Security Code
18. Review Id

# Plans #

1. Id
2. Name
3. Detail
4. Price

# Area #

1. Id
2. City Id
3. Name
4. Mess

# Mess Past Order #

1. Meal Id
2. Date
3. Lunch / Dinner
4. Unit Price
5. Total Price
6. Order Ids
7. Reviews

# City #

1. Id
2. Name
3. Areas

# Delivery Phone Numbers #

1. Phone Number
2. Mess Id
3. Name

# Reviews #
1. Review Id
2. Customer Id 
3. Mess Id
4. Meal Id 
5. Order Id 
6. Ratings
7. Review

# Payments #

1. Payment Id
2. Amount
3. Customer Id
4. Order Id (if present)
5. Plan Id (if Present)