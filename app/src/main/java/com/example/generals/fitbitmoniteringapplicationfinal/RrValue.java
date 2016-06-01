package com.example.generals.fitbitmoniteringapplicationfinal;

import org.json.JSONException;
import org.json.JSONObject;

public class RrValue {

    int id_rr_value;
    double value;
    double time;

    public RrValue(JSONObject obj) {
        id_rr_value = obj.optInt("idRRValue");
        value = obj.optDouble("value");
    }

    public int getId_rr_value() {
        return id_rr_value;
    }

    public void setId_rr_value(int id_rr_value) {
        this.id_rr_value = id_rr_value;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }
}
