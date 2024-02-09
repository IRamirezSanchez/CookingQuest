package com.example.cookingquest.model;

public class Receta {
    private int nombre_receta;
    private int categoria;
    private int ingredientes;
    private int tiempo_ejecucion;
    private int link_preparacion;
    private int imagen_receta;
    private int raciones;

    private Pais pais;
    //ImageView hola;




    public Receta(Pais pais, int nombre_receta, int categoria, int ingredientes, int tiempo_ejecucion, int link_preparacion, int imagen_receta, int raciones) {
        this.nombre_receta=nombre_receta;
        this.pais=pais;
        this.categoria=categoria;
        this.ingredientes=ingredientes;
        this.tiempo_ejecucion=tiempo_ejecucion;
        this.link_preparacion=link_preparacion;
        this.imagen_receta=imagen_receta;
        this.raciones=raciones;
    }
    public void algo(){
        for (Receta receta: Repositorios_Recetas.getRecetas()) {

        }
    }
    public int getNombre_receta() {
        return nombre_receta;
    }

    public void setNombre_receta(int nombre_receta) {
        this.nombre_receta = nombre_receta;
    }

    public int getCategoria() {
        return categoria;
    }

    public void setCategoria(int categoria) {
        this.categoria = categoria;
    }

    public int getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(int ingredientes) {
        this.ingredientes = ingredientes;
    }

    public int getTiempo_ejecucion() {
        return tiempo_ejecucion;
    }

    public void setTiempo_ejecucion(int tiempo_ejecucion) {
        this.tiempo_ejecucion = tiempo_ejecucion;
    }

    public int getLink_preparacion() {
        return link_preparacion;
    }

    public void setLink_preparacion(int link_preparacion) {
        this.link_preparacion = link_preparacion;
    }

    public int getImagen_receta() {
        return imagen_receta;
    }

    public void setImagen_receta(int imagen_receta) {
        this.imagen_receta = imagen_receta;
    }

    public int getRaciones() {
        return raciones;
    }

    public void setRaciones(int raciones) {
        this.raciones = raciones;
    }

    public Pais getPais() {
        return pais;
    }

    public void setPais(Pais pais) {
        this.pais = pais;
    }


}
