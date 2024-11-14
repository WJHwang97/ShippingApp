package com.example.rfidwriter;

public class Model {

    private String sNo;
    private String product;
    private String category;
    private String price;

    private String docks;

    public Model(String sNo, String product, String category, String price) {
        this.sNo = sNo;
        this.product = product;
        this.category = category;
        this.price = price;
    }

    public Model(String docks) {
        this.docks = docks;
    }

    public String getsNo() {
        return sNo;
    }

    public String getProduct() {
        return product;
    }

    public String getCategory() {
        return category;
    }

    public String getPrice() {
        return price;
    }

    public String DockHolder()
    {
        return docks;
    }
}
