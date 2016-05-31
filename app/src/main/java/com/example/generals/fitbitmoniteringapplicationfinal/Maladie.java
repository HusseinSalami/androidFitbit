package com.example.generals.fitbitmoniteringapplicationfinal;

import java.util.List;

/**
 * Created by generals on 05/26/2016.
 */
public class Maladie {
    int idMaladie;
    String nom;
    List<Symptomes> list_Ingredient;

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

    public List<Symptomes> getList_Ingredient() {
        return list_Ingredient;
    }

    public void setList_Ingredient(List<Symptomes> list_Ingredient) {
        this.list_Ingredient = list_Ingredient;
    }
}
