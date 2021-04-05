
Main -

    - Users (A)
    - Mess (B)
    - Orders (C)
    - Meals (D)
    - Items (E)
    - Plans (F)

Users (A) -

    - User Id (id)
        - Profile (a)
        - Address (b)
        - Plan (c)
        - Balance (d)
        - History (e)
        - Current Orders (f)


        Profile (A id a) -

            - Name (A)
            - Gender (B)
            - Address (C)
            - Phone Number (D)

        Address (A id b) 

        Plan (A id C) -

            - Plan Id (A)
            - Plan Name (B)
            - Plan Cost (C)

        Balance (A id d)

        History ( A id e) - 

            - Order Id (A id e id) -

                - Order Id (a)

        Current Orders (A id f) -

            - Order Id (A id f id) -

                -order Id (a)


Mess (B) - 

    - Mess Id (id) -

        - Name (a)
        - Address (b)
        - Phone Number (c)
        - Owner Name (d)
        - Owner Phone Number (e)
        - Owner Address (f)
        - Owner Aadhar Download Link (g)
        - Current Dish (h)
        - Mess Timings (i)
        - Items (j)
        - Meals (k)
        - Monthly Price (l)

        Items (B id j) -

            - Item Id (B id j id) -

                - Item Name (A)
                - Detail (B)
                - Amount (C)

        Meals (B id k) -

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


Orders (C) -

    - Order Id (id) -

        - Mess Id (A)
        - Dish Id (B)
        - User Id (C)
        - Mess Name (D)
        - User name (E)
        - Order Time (F)
        - Address (G)
        - Status (H)
        - User Phone Number (I)
        - Mess or Delivery (J)
        - Delivery Phone Number (K)

Meals (D) -

    - Meal Id (id) -

        - Mess Id (a)
        - Mess Name (b)
        - Picture Download Link (c)
        - Special or Normal (d)
        - Price (e)
        - Delivery Price (f)
        - Items (g)

        Items (D id g) -

            - Item Id (id) -

                - Name (A)
                - Detail (B)
                - Amount (C)

Items (E) - 

    - Item Id (id) - 

        - Name (a)
        - Detail (b)
        - Amount (c)

Plans (F) -

    - Plan Id (id) -

        - Name (a)
        - Detail (b)
        - Price (c)