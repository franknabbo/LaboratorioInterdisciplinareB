package org.example.db;

    public class Utente {
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


        public String getNome() { return nome; }
        public String getCognome() { return cognome; }
        public String getCodiceFiscale() { return codiceFiscale; }
        public String getEmail() { return email; }
        public String getPassword() { return password; }

    }