package com.example.zhuk;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "Record", strict = false)
public class MetalRate {
    @Element(name = "Date")
    private String date;

    @Element(name = "Code")
    private String code;

    @Element(name = "Name")
    private String name;

    @Element(name = "Buy")
    private String buy;

    @Element(name = "Sell")
    private String sell;

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBuy() { return buy; }
    public void setBuy(String buy) { this.buy = buy; }

    public String getSell() { return sell; }
    public void setSell(String sell) { this.sell = sell; }

    public double getBuyPrice() {
        try {
            return Double.parseDouble(buy.replace(",", "."));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public double getSellPrice() {
        try {
            return Double.parseDouble(sell.replace(",", "."));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}