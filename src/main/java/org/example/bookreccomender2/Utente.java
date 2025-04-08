package org.example.bookreccomender2;

public class Utente {
    private int id;
    private String nome;
    private String cognome;
    private String codiceFiscale;
    private String email;
    private String password; // password cifrata

    public Utente(String nome, String cognome, String codiceFiscale, String email, String password) {
        this.nome = nome;
        this.cognome = cognome;
        this.codiceFiscale = codiceFiscale;
        this.email = email;
        this.password = password;
    }

    public String getEmail() { return email; }
    public String getPassword() { return password; }

    public int getId() { return id; }

    public String getNome() { return nome; }
    public String getCognome() { return cognome; }
    public String getCodiceFiscale() { return codiceFiscale;}
    public void setId(int id) { this.id = id;}
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setNome(String nome) { this.nome = nome; }
    public void setCognome(String cognome) { this.cognome = cognome; }
    public void setCodiceFiscale(String codiceFiscale) { this.codiceFiscale = codiceFiscale; }


}
