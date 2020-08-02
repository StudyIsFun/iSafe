package com.crime_mapping.electrothon.sos;

public class Data {

    private String precautions;

    private double x;

    private String description;

    private  double y;

    public String getPrecautions ()
    {
        return precautions;
    }

    public void setPrecautions (String precautions)
    {
        this.precautions = precautions;
    }

    public double getX ()
    {
        return x;
    }

    public void setX (double x)
    {
        this.x = x;
    }

    public String getDescription ()
    {
        return description;
    }

    public void setDescription (String description)
    {
        this.description = description;
    }

    public double getY ()
    {
        return y;
    }

    public void setY (double y)
    {
        this.y = y;
    }

    public Data(String precautions, double x, String description, double y) {
        this.precautions = precautions;
        this.x = x;
        this.description = description;
        this.y = y;
    }
}
