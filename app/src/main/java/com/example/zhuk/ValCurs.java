package com.example.zhuk;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "ValCurs", strict = false)
public class ValCurs {
    @Element(name = "Date", required = false)
    private String date;

    @Element(name = "name", required = false)
    private String name;

    @ElementList(inline = true, required = false)
    private List<GoldRate> records;

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<GoldRate> getRecords() { return records; }
    public void setRecords(List<GoldRate> records) { this.records = records; }
}