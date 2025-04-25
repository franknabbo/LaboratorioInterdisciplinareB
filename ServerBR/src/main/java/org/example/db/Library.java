package org.example.db;

public class Library {
    private int idLibreria;
    private int idUtente;
    private String nome;

    public Library(int idLibreria, int idUtente, String nome) {
        this.idLibreria = idLibreria;
        this.idUtente = idUtente;
        this.nome = nome;
    }

    public int getIdLibreria() {
        return idLibreria;
    }

    public int getIdUtente() {
        return idUtente;
    }

    public String getNome() {
        return nome;
    }
}