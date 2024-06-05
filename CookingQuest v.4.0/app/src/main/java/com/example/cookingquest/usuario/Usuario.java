package com.example.cookingquest.usuario;
import java.util.List;
public class Usuario {

    private String Nombre;
    private Long Puntos;
    private String Email;
    private String Telefono;
    private List<Favorito> recetasFavoritas;
    public Usuario() {}

    public Usuario(String nombre, Long puntos) {
        this.Nombre = nombre;
        this.Puntos = puntos;
    }

    public String getNombre() { return Nombre; }
    public void setNombre(String nombre) { Nombre = nombre; }

    public String getEmail() { return Email; }
    public void setEmail(String email) { Email = email; }

    public Long getPuntos() { return Puntos; }
    public void setPuntos(Long puntos) { Puntos = puntos; }

    public String getTelefono() { return Telefono; }
    public void setTelefono(String telefono) { Telefono = telefono; }

    public List<Favorito> getRecetasFavoritas() { return recetasFavoritas; }
    public void setRecetasFavoritas(List<Favorito> recetasFavoritas) { this.recetasFavoritas = recetasFavoritas; }

    public static class Favorito {
        private String recetaId;
        private String pais;

        public Favorito() {}

        public String getRecetaId() {
            return recetaId;
        }

        public void setRecetaId(String recetaId) {
            this.recetaId = recetaId;
        }

        public String getPais() {
            return pais;
        }

        public void setPais(String pais) {
            this.pais = pais;
        }
    }
}
