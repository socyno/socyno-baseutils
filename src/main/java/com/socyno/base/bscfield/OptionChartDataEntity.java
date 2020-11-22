package com.socyno.base.bscfield;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
public class OptionChartDataEntity {

    private String name;

    private final Map<String, Integer> seriesData;

    public OptionChartDataEntity(String name, Map<String, Integer> seresData) {
        this.name = name;
        this.seriesData = seresData;
    }

    public OptionChartDataEntity(String name) {
        this(name, new HashMap<>());
    }

    public OptionChartDataEntity setSeriesValue (String seriesName, Integer seresValue) {
        this.seriesData.put(seriesName, seresValue);
        return this;
    }
}
