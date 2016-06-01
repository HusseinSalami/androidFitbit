package com.example.generals.fitbitmoniteringapplicationfinal;

import java.util.List;

/**
 * Created by generals on 05/26/2016.
 */
public class Maladie {
    int idMaladie;
    String nom;
    List<Symptomes> list_symptomes;

    public int getIdMaladie() {
        return idMaladie;
    }

    public void setIdMaladie(int idMaladie) {
        this.idMaladie = idMaladie;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public List<Symptomes> getList_Symptomes() {
        return list_symptomes;
    }

    public void setList_symptomes(List<Symptomes> list_symptomes) {
        this.list_symptomes = list_symptomes;
    }
}
