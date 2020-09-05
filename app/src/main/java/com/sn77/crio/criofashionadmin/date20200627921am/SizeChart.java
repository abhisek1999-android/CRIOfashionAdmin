package com.sn77.crio.criofashionadmin.date20200627921am;

public class SizeChart {

    public SizeChart(){

    }

    String size_chart_name;
    String chart_url;

    public SizeChart(String size_chart_name, String chart_url) {
        this.size_chart_name = size_chart_name;
        this.chart_url = chart_url;
    }

    public String getSize_chart_name() {
        return size_chart_name;
    }

    public void setSize_chart_name(String size_chart_name) {
        this.size_chart_name = size_chart_name;
    }

    public String getChart_url() {
        return chart_url;
    }

    public void setChart_url(String chart_url) {
        this.chart_url = chart_url;
    }
}
