package gui;

import condivisi.ErrorException;
import java.net.InetAddress;

/**
 * Definisce una riga della tabella dei file in upload
 * @author Bandettini
 */
public class RigaTabellaPubblicati {

    /* Costanti */
    public static final int NONATTIVO = -1;

    /* Variabili d'istanza */
    private String file;
    private String dimensione;
    private String stato;
    private String situazione;
    private int peer;
    private int peercercano;
    private InetAddress identita;

    /**
     * Costruttore
     * @param file nome del file
     * @param dimensione dimensione del file
     * @param pubblicato true se è stato pubblicato dal peer, false altrimenti
     */
    public RigaTabellaPubblicati(String file, String dimensione, boolean pubblicato) {
        this.file = file;
        this.dimensione = dimensione;
        this.stato = "In Upload";
        this.situazione = "Non Attivo";
        this.peer = 0;
        this.identita = null;
        if (pubblicato) {
            this.peercercano = 0;
        } else {
            this.peercercano = NONATTIVO;
        }
    }

    /**
     * Restituisce il nome del file
     * @return file
     */
    public String getFile() {
        return this.file;
    }

    /**
     * Restituisce la dimensione del file
     * @return dimensione
     */
    public String getDimensione() {
        return this.dimensione;
    }

    /**
     * Restituisce lo stato del file
     * @return stato
     */
    public String getStato() {
        return this.stato;
    }

    /**
     * Restituisce la situazione del file
     * @return situazione
     */
    public String getSituazione() {
        return this.situazione;
    }

    /**
     * Restituisce i peer del file
     * @return peer
     */
    public int getPeer() {
        return this.peer;
    }

    /**
     * Restituisce il numero dei peer che hanno cercato il file
     * @return peercercano, -1 se il file non è sttao publicato dal peer
     */
    public int getPeerCerca() {
        return this.peercercano;
    }

    /**
     * restituisce l' ip di chi ha cercato per ultimo il file
     * @return identita, null se il peer non ha pubblicato il file
     */
    public InetAddress getIdentita() {
        return this.identita;
    }

    /**
    public void setFile(String file){
    this.file = file;
    }
    public void setDimensione(String dimensione){
    this.dimensione = dimensione;
    }

    public void setStato(String stato) throws ErrorException{
    if( stato == null ) throw new ErrorException("Param null");
    this.stato = stato;
    }*/
    /**
     * Setta la situazione del file
     * @param situazione
     * @exception condivisi.ErrorException se situaizone è null
     */
    public void setSituazione(String situazione) throws ErrorException {
        if (situazione == null) {
            throw new ErrorException("Param null");
        }
        this.situazione = situazione;
    }

    /**
     * Setta il numero dei peer
     * @param peer
     * @exception condivisi.ErrorException se peer è minore di 0
     */
    public void setPeer(int peer) throws ErrorException {
        if (peer < 0) {
            throw new ErrorException("Param invalid");
        }
        this.peer = peer;
    }

    /**
     * Incrementa il numero dei peer che hanno cercato quel file
     * @exception condivisi.ErrorException se il peer non ha pubblicato quel file
     */
    public void setPeerCerca(int np) {
        if (peercercano != NONATTIVO && np != NONATTIVO) {
            if (np > this.peercercano) {
                this.peercercano = np;
            }
        }
    }

    /**
     * Setta l'ip dell'ultimo peer che ha cercato quel file
     * @param ind
     * @exception condivisi.ErrorException se ind è null o il peer non ha pubblicato quel file
     */
    public void setIdentita(InetAddress ind) {
        if (ind != null && peercercano != NONATTIVO) {
            this.identita = ind;
        }
    }
}