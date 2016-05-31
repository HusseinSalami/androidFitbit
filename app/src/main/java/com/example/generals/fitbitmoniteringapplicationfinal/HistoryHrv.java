package com.example.generals.fitbitmoniteringapplicationfinal;

import java.util.List;

/**
 * Created by generals on 05/23/2016.
 */
//correspond a l'historique, en effet, le user a plusieurs HRVhistorique; correspondant a l'id de tous les signaux hrv du user;

public class HistoryHrv {

    int idHistoryHrv;
    List<HrvValue> list_valeur;
    int pnn50;

    public int getPnn50() {
        return pnn50;
    }

    public void setPnn50(int pnn50) {
        this.pnn50 = pnn50;
    }

    public int getIdHistoryHrv() {
        return idHistoryHrv;
    }

    public void setIdHistoryHrv(int idHistoryHrv) {
        this.idHistoryHrv = idHistoryHrv;
    }

    public List<HrvValue> getList_valeur() {
        return list_valeur;
    }

    public void setList_valeur(List<HrvValue> list_valeur) {
        this.list_valeur = list_valeur;
    }
}
