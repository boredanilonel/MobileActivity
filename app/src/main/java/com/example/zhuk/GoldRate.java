package com.example.zhuk;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "Record", strict = false)
public class GoldRate {
    @Element(name = "Date", required = false)
    private String date;

    @Element(name = "Code", required = false)
    private String code;

    @Element(name = "Buy", required = false)
    private String buy;

    @Element(name = "Sell", required = false)
    private String sell;

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getBuy() { return buy; }
    public void setBuy(String buy) { this.buy = buy; }

    public String getSell() { return sell; }
    public void setSell(String sell) { this.sell = sell; }

    public double getBuyPrice() {
        if (buy == null || buy.isEmpty()) return 0.0;

        try {
            String cleanValue = buy.replace(",", ".").trim();
            return Double.parseDouble(cleanValue);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    public double getSellPrice() {
        if (sell == null || sell.isEmpty()) return 0.0;

        try {
            String cleanValue = sell.replace(",", ".").trim();
            return Double.parseDouble(cleanValue);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}