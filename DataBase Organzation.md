
Main -

    - Customers (A)
    - Mess (B)
    - Orders (C)
    - Meals (D)
    - Items (E)
    - Plans (F)
    - Areas (G)
    - City (H)
    - Delivery Phone Numbers (I)
    - Reviews (I)
    - Payments (J)
    - Referral Benefits (K)
    - Delivery Persons (L)
    - Time Management Status (M)

Customers (A) -

    - Customer Id (id)

        - Profile (a)
        - Address (b)
        - Plan (c)
        - Balance (d)
        - History (e)
        - Current Order (f)
        - Upcoming Order (g)


        Profile (A id a) -

            - Name (A)
            - Gender (B)
            - Phone Number (C)
            - Referral Code (D)

        Address (A id b) -

            - City (A)
            - City Id (B)
            - Area (C)
            - Area Id (D)
            - Landmark (E)
            - House Number (F)
            - Room Number (G)

        Plan (A id C) -

            - Plan Id (A)
            - Plan Name (B)
            - Plan Cost (C)

        Balance (A id d)

        History ( A id e) - 

            - Order Id (A id e id) -

                - Order Id (a)

        Current Order (A id f) -

            - Order Id (a) -

        Upcoming Order (A id g) -

            - Order Id (a) -



Mess (B) - 

    - Mess Id (id) -

        - Profile (a)
        - Owner (b)
        - Verified or Not (c) (0 or 1)
        - Rating (d)
        - Current Meals (e)
        - Items (f)
        - Meals (g)
        - Areas (h)
        - Past Orders (i)
        - Delivery Phone Numbers (j)
        - Payment Details (k)
        - Current Orders (l)
        
        
        Profile (B id a)
            
            - Name (A)
            - Description (B)
            - Address (C)
            - City (D)
            - City Id (E)
            - Area (F)
            - Area Id (G)
            - Phone Number (H)
            - Mess Timings (I)
            - Monthly Price (J)
            - Mess Image (K)
            - Home Delivery (L)
            - In Mess (M)

        Owner (B id b)

            - Owner Name (A)
            - Owner Phone Number (B)
            - Owner Address (C)
            - Owner Id Download Link (D)

        Current Meals (B id e) -

            - Lunch (A) -

                - Meal Id (a)

            - Dinner (B) -

                - Meal Id (b)

        Items (B id f) -

            - Item Id (B id j id) -

                - Item Name (A)
                - Detail (B)
                - Amount (C)

        Meals (B id g) -

            - Meal Id (B id k id) -

                - Mess Id (a)
                - Mess Name (b)
                - Picture Download Link (c)
                - Special or Normal (d)
                - Price (e)
                - Delivery Price (f)
                - Items (g)

                Items (B id k id g) -

                    - Item Id (id) -

                        - Name (A)
                        - Detail (B)
                        - Amount (C)

        Areas (B id h) -

            - Area Id (id)

                - Area Id (A)
                - Name (B)

        Past Orders (B id i) -

            - Date (A) -

                - Lunch (a) -

                    - Home Delivery (A) -

                        - Meal Id (A)
                        - Unit Price (B)
                        - Total Price (C)
                        - Orders (D) -

                            - Order Id (id) -

                                - Order Id (A)

                    - In Mess (B) -

                        - Meal Id (A)
                        - Unit Price (B)
                        - Total Price (C)
                        - Orders (D) -

                            - Order Id (id) -

                                - Order Id (A)

                - Dinner (b) -
                    
                    - Home Delivery (A) -

                        - Meal Id (A)
                        - Unit Price (B)
                        - Total Price (C)
                        - Orders (D) -

                            - Order Id (id) -

                                - Order Id (A)

                     - In Mess (B) -
 
                        - Meal Id (A)
                        - Unit Price (B)
                        - Total Price (C)
                        - Orders (D) -

                            - Order Id (id) -

                                - Order Id (A)


        Delivery Phone Numbers (B id j) -

            - Phone Number (id)

                - Phone Number (a)
                - Name (b)

        Payment Details (B id k) -

            - Upi (A)
            - Bank Details (B)

            Upi (A) - 

                - Upi Id (a)
            
            Bank Details (B) - 

                - Account Number (a)
                - IFSC Code (b)
                - Bank Name (c)
                - Account Holder Name (d)

        Current Orders (B id l) -

            - Lunch (a) 
            - Dinner (b)

            Lunch (a) -

                - Delivery (A)
                - In Mess (B)

                Delivery (A) - 

                   - Area (id)

                       - Area Name (A)

                       - Delivery Person (B)

                            - Name (a)
                            - Phone Number (b)

                       - Orders (C) -

                            - Order Id (A)

                                - Order Id (a)
                                - Customer Name (b)
                                - House Number (c)
                                - Room Number (d)
                                - Landmark (e)
                                - Customer Mobile Number (f)

                In Mess (B) - 

                    - Area (id)

                        - Area Name (A)

                        - Delivery Person (B)

                            - Name (a)
                            - Phone Number (b)

                        - Orders (C) -

                            - Order Id (A)

                                - Order Id (a)
                                - Customer Name (b)
                                - House Number (c)
                                - Room Number (d)
                                - Landmark (e)
                                - Customer Mobile Number (f)

            Dinner (b) -

                - Delivery (A)
                - In Mess (B)

                Delivery (A) -

                - Area (id)

                    - Area Name (A)

                    - Delivery Person (B)

                        - Name (a)
                        - Phone Number (b)
                    
                    - Orders (C) -

                        - Order Id (A)
                        
                            - Order Id (a)
                            - Customer Name (b)
                            - House Number (c)
                            - Room Number (d)
                            - Landmark (e)
                            - Customer Mobile Number (f)

                In Mess (B) -

                - Area (id)

                    - Area Name (A)

                    - Delivery Person (B)

                        - Name (a)
                        - Phone Number (b)
                    
                    - Orders (C) -

                        - Order Id (A)
                        
                            - Order Id (a)
                            - Customer Name (b)
                            - House Number (c)
                            - Room Number (d)
                            - Landmark (e)
                            - Customer Mobile Number (f)


Orders (C) -

    - Order Id (id) -

        - Mess Id (A)
        - Dish Id (B)
        - Customer Id (C)
        - Mess Name (D)
        - Customer Name (E)
        - Order Time (F)
        - Address (G)
        - Status (H)
        - Customer Phone Number (I)
        - Mess or Delivery (J)
        - Delivery Phone Number (K)
        - Lunch or Dinner (L)
        - Order Date (M)
        - Delivered Time (N)
        - Security Code (P)
        - Review Id (Q)
        - Order Id (R)
        - Order Price (S)
        - Items (T)
        - Meal Image Link (U)

        Items (T) - 

            - Item Id (id) -

                - Name (a)
                - Amount (b)


Meals (D) -

    - Meal Id (id) -

        - Mess Id (a)
        - Mess Name (b)
        - Picture Download Link (c)
        - Special or Normal (d)
        - Price (e)
        - Delivery Price (f)
        - Items (g)
        - Lunch or Dinner (h)
        - Reviews (i)
        - Rating (j)

        Items (D id g) -

            - Item Id (id) -

                - Name (A)
                - Detail (B)
                - Amount (C)
                - Mess Id (D)

        Reviews (i) -

            - Review Id (id) (a)

Items (E) - 

    - Item Id (id) - 

        - Name (a)
        - Detail (b)
        - Amount (c)
        - Mess Id (d)

Plans (F) -

    - Regular Plan (a)
    - Other Plans (b)

    Regular Plan (a) -

        - Title (A)
        - Detail (B)

    Other Plans (b) -

        - Plan Id (id) -

            - Title (a)
            - Details (b)
            - Price (c)
            - Diets (d)
            - Plan Id (e)

Areas (G) -

    - City Id (A) -

        - Area Id (id) -

            - City Id (A)
            - Name (B)
            - Mess (C)

            Mess (G id C) -

                - Mess Id (id) -

                    - Mess Id (A)

City (H) -

    - City Id (id)

        - Name (A)
        - Areas (B)

        Areas (H id B) -

            - Area Id (id) -

                - Area Id (A)
                - Area Name (B)

Delivery Phone Numbers (I) -

    - Phone Number (id) -

        - Mess Id (a)
        - Name (b)

Reviews (J) -

    - Review Id (id) -

        - Customer Id (a)
        - Mess Id (b)
        - Meal Id (c)
        - Order Id (d)
        - Ratings (e)
        - Review (f)

Payments (J) -

    - Orders (a)
    - Plans (b)

    Orders (J a) -

        - Payment Id (id) 

            - Amount (a)
            - Customer Id (b)
            - Order Id (c)
            - Transaction Id (d)
            - Transaction Platform (e)

    Plans (j b) -

        - Payment Id (id)

            - Amount (a)
            - Customer Id (b)
            - Order Id (C)
            - Transaction Id (d)
            - Transaction Platform (e)

Referral Benefits (K) -

    - Details (A)
    - Amount (B)

Delivery Persons (L) -

    - User Id (id) -

        - Mess Id (a)
        - Mess Name (b)
        - Name (c)
        - Phone Number (d)

Time Management Status (M) -

    - Current Lunch or Dinner (A)
    - Current Date (B)
    - Upcoming Lunch or Dinner (B)
    - Upcoming Date (C)


# Storage #

- Pictures (A)

Pictures (A) -

    - Mess (a)

    Mess (a) -

        - Mess Id (id)

            - Mess Image (A)
            - Meals (B)
            - Owner Id (C)

            Meals (A a id B) -

                - Meal Id (id)

                    - Meal Image (a)

