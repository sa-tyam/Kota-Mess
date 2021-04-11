package com.pkan.official.customer.plans;

public class OtherPlans {
    String planId;
    String title;
    String description;
    int diets;
    int price;

    public OtherPlans(String planId, String title, String description, int diets, int price) {
        this.planId = planId;
        this.title = title;
        this.description = description;
        this.diets = diets;
        this.price = price;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDiets() {
        return diets;
    }

    public void setDiets(int diets) {
        this.diets = diets;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
