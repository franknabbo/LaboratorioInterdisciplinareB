// Francesco Del Rosso (Matricola: 758295) Como
// Davide Cartolano (Matricola: 757603) Como
// Tommaso Ferloni (Matricola: 757581) Como
// Andrea Riva (Matricola: 757580) Como

package org.example.db;


public class Library {
    private int idLibreria;
    private String user_id;
    private String nome;

    public Library(int idLibreria, String user_id, String nome) {
        this.idLibreria = idLibreria;
        this.user_id = user_id;
        this.nome = nome;
    }

    public int getIdLibreria() {
        return idLibreria;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getNome() {
        return nome;
    }
}