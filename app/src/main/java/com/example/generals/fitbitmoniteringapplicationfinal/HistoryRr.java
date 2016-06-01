package com.example.generals.fitbitmoniteringapplicationfinal;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by generals on 05/23/2016.
 */
public class HistoryRr {

    List<RrValue> list_rr_value;
    int idHistoryRr;
    String user;

    public HistoryRr(int id) {
        idHistoryRr = id;
        list_rr_value = new ArrayList<>();
    }

    public List<RrValue> getList_rr_value() {
        return list_rr_value;
    }



    public void setList_rr_value(List<RrValue> list_rr_value) {
        this.list_rr_value = list_rr_value;
    }

    public int getIdHistoryRr() {
        return idHistoryRr;
    }

    public void setIdHistoryRr(int idHistoryRr) {
        this.idHistoryRr = idHistoryRr;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
