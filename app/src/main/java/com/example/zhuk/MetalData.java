package com.example.zhuk;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "Metall", strict = false)
public class MetalData {
    @ElementList(inline = true, required = false)
    private List<MetalRate> records;

    public List<MetalRate> getRecords() { return records; }
    public void setRecords(List<MetalRate> records) { this.records = records; }

    public MetalRate getGoldRate() {
        if (records != null && !records.isEmpty()) {
            for (MetalRate record : records) {
                if ("1".equals(record.getCode())) {
                    return record;
                }
            }
        }
        return null;
    }
}