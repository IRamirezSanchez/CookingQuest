package com.example.cookingquest.model;


import android.content.Context;
import android.content.res.Resources;

import com.example.cookingquest.R;

import java.util.ArrayList;

public class Repositorios_Recetas {
    private static ArrayList<Receta> recetas = new ArrayList();

    public static void initializeRecetas(Context context) {

        Resources resources = context.getResources(); //No funciona

        recetas.add(new Receta(Pais.FRANCIA, R.string.nombreRecetaFrancia1, R.string.rs_categoria_fr1, R.string.rs_ingredientes_fr1,
                R.string.rs_tiempo_fr1, R.string.rs_link_fr1, R.drawable.comida_francia_r1, R.string.rs_raciones_fr1));
        recetas.add(new Receta(Pais.FRANCIA, R.string.nombreRecetaFrancia2, R.string.rs_categoria_fr2, R.string.rs_ingredientes_fr2,
                R.string.rs_tiempo_fr2, R.string.rs_link_fr2, R.drawable.comida_francia_r2, R.string.rs_raciones_fr2));
        recetas.add(new Receta(Pais.FRANCIA, R.string.nombreRecetaFrancia3, R.string.rs_categoria_fr3, R.string.rs_ingredientes_fr3,
                R.string.rs_tiempo_fr3, R.string.rs_link_fr3, R.drawable.comida_francia_r3, R.string.rs_raciones_fr3));
        recetas.add(new Receta(Pais.FRANCIA, R.string.nombreRecetaFrancia4, R.string.rs_categoria_fr4, R.string.rs_ingredientes_fr4,
                R.string.rs_tiempo_fr4, R.string.rs_link_fr4, R.drawable.comida_francia_r4, R.string.rs_raciones_fr4));
        recetas.add(new Receta(Pais.FRANCIA, R.string.nombreRecetaFrancia5, R.string.rs_categoria_fr5, R.string.rs_ingredientes_fr5,
                R.string.rs_tiempo_fr5, R.string.rs_link_fr5, R.drawable.comida_francia_r5, R.string.rs_raciones_fr5));


        recetas.add(new Receta(Pais.ALEMANIA, R.string.nombreRecetaAlemania1, R.string.rs_categoria_al1, R.string.rs_ingredientes_al1,
                R.string.rs_tiempo_al1, R.string.rs_link_al1, R.drawable.comida_alemania_r1, R.string.rs_raciones_al1));
        recetas.add(new Receta(Pais.ALEMANIA, R.string.nombreRecetaAlemania2, R.string.rs_categoria_al2, R.string.rs_ingredientes_al2,
                R.string.rs_tiempo_al2, R.string.rs_link_al2, R.drawable.comida_alemania_r2, R.string.rs_raciones_al2));
        recetas.add(new Receta(Pais.ALEMANIA, R.string.nombreRecetaAlemania3, R.string.rs_categoria_al3, R.string.rs_ingredientes_al3,
                R.string.rs_tiempo_al3, R.string.rs_link_al3, R.drawable.comida_alemania_r3, R.string.rs_raciones_al3));
        recetas.add(new Receta(Pais.ALEMANIA, R.string.nombreRecetaAlemania4, R.string.rs_categoria_al4, R.string.rs_ingredientes_al4,
                R.string.rs_tiempo_al3, R.string.rs_link_al4, R.drawable.comida_alemania_r4, R.string.rs_raciones_al4));
        recetas.add(new Receta(Pais.ALEMANIA, R.string.nombreRecetaAlemania5, R.string.rs_categoria_al5, R.string.rs_ingredientes_al5,
                R.string.rs_tiempo_al5, R.string.rs_link_al5, R.drawable.comida_alemania_r5, R.string.rs_raciones_al5));

        recetas.add(new Receta(Pais.INGLATERRA, R.string.nombreRecetaInglaterra1, R.string.rs_categoria_ing1, R.string.rs_ingredientes_ing1,
                R.string.rs_tiempo_ing1, R.string.rs_link_ing1, R.drawable.comida_inglaterra_r1, R.string.rs_raciones_ing1));
        recetas.add(new Receta(Pais.INGLATERRA, R.string.nombreRecetaInglaterra2, R.string.rs_categoria_ing2, R.string.rs_ingredientes_ing2,
                R.string.rs_tiempo_ing2, R.string.rs_link_ing2, R.drawable.comida_inglaterra_r2, R.string.rs_raciones_ing2));
        recetas.add(new Receta(Pais.INGLATERRA, R.string.nombreRecetaInglaterra3, R.string.rs_categoria_ing3, R.string.rs_ingredientes_ing3,
                R.string.rs_tiempo_ing3, R.string.rs_link_ing3, R.drawable.comida_inglaterra_r3, R.string.rs_raciones_ing3));
        recetas.add(new Receta(Pais.INGLATERRA, R.string.nombreRecetaInglaterra4, R.string.rs_categoria_ing4, R.string.rs_ingredientes_ing4,
                R.string.rs_tiempo_ing4, R.string.rs_link_ing4, R.drawable.comida_inglaterra_r4, R.string.rs_raciones_ing4));
        recetas.add(new Receta(Pais.INGLATERRA, R.string.nombreRecetaInglaterra5, R.string.rs_categoria_ing5, R.string.rs_ingredientes_ing5,
                R.string.rs_tiempo_ing5, R.string.rs_link_ing5, R.drawable.comida_inglaterra_r5, R.string.rs_raciones_ing5));

        recetas.add(new Receta(Pais.ESPAÑA, R.string.nombreRecetaSpain1, R.string.rs_categoria_sp1, R.string.rs_ingredientes_sp1,
                R.string.rs_tiempo_sp1, R.string.rs_link_sp1, R.drawable.comida_spain_r1, R.string.rs_raciones_sp1));
        recetas.add(new Receta(Pais.ESPAÑA, R.string.nombreRecetaSpain2, R.string.rs_categoria_sp2, R.string.rs_ingredientes_sp2,
                R.string.rs_tiempo_sp2, R.string.rs_link_sp2, R.drawable.comida_spain_r2, R.string.rs_raciones_sp2));
        recetas.add(new Receta(Pais.ESPAÑA, R.string.nombreRecetaSpain3, R.string.rs_categoria_sp3, R.string.rs_ingredientes_sp3,
                R.string.rs_tiempo_sp3, R.string.rs_link_sp3, R.drawable.comida_spain_r3, R.string.rs_raciones_sp3));
        recetas.add(new Receta(Pais.ESPAÑA, R.string.nombreRecetaSpain4, R.string.rs_categoria_sp4, R.string.rs_ingredientes_sp4,
                R.string.rs_tiempo_sp4, R.string.rs_link_sp4, R.drawable.comida_spain_r4, R.string.rs_raciones_sp4));
        recetas.add(new Receta(Pais.ESPAÑA, R.string.nombreRecetaSpain5, R.string.rs_categoria_sp5, R.string.rs_ingredientes_sp5,
                R.string.rs_tiempo_sp5, R.string.rs_link_sp5, R.drawable.comida_spain_r5, R.string.rs_raciones_sp5));

    }


    public static ArrayList<Receta> getRecetas() {
        return recetas;
    }

    public static void setRecetas(ArrayList<Receta> recetas) {
        Repositorios_Recetas.recetas = recetas;
    }
}
