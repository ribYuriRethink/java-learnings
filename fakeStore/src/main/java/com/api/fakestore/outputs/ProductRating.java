package com.api.fakestore.outputs;

public class ProductRating {

    public ProductRating() {
    }

    public ProductRating(double rate, Integer count) {
        this.rate = rate;
        this.count = count;
    }

    private double rate;
    private Integer count;

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
