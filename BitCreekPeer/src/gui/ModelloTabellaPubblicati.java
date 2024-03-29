package gui;

import condivisi.ErrorException;
import java.net.InetAddress;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 * Modello della tabella che visualizza i file pubblicati (in upload)
 * @author Bandettini Alberto
 * @author Lottarini Andrea
 * @version BitCreekPeer 1.0
 */
public class ModelloTabellaPubblicati extends AbstractTableModel {

    /* Costanti */
    /** Costante che definisce la versione della classe */
    public static final long serialVersionUID = 11;
    /** Vettore di headers */
    private final String[] headers = {"Id", "File", "Dimensione", "Stato", "Situazione", "Peer in scarimento", "Peer che cercano", "Identita' ultimo peer"};
    /** Vettore di tipi */
    private final Class[] tipi = {"".getClass(), "".getClass(), "".getClass(), "".getClass(), "".getClass(), "".getClass(), "".getClass(), "".getClass()};

    /* Variabili d'istanza */
    /** Vettore di righe */
    private ArrayList<RigaTabellaPubblicati> vettore;

    /**
     * Costruttore
     */
    public ModelloTabellaPubblicati() {
        this.vettore = new ArrayList<RigaTabellaPubblicati>();
    }

    /**
     * Aggiunge la riga passata come parametro alla tabella
     * @param r riga da aggiungere
     * @exception condivisi.ErrorException se riga è null
     */
    public void addRiga(RigaTabellaPubblicati r) throws ErrorException {
        if (r == null) {
            throw new ErrorException("Param null");
        }
        vettore.add(r);
    }

    /**
     * Restituisce la riga in posizone i se i è corretto
     * @param i posizione
     * @return riga
     * @throws condivisi.ErrorException se i non valido
     */
    public RigaTabellaPubblicati getRiga(int i) throws ErrorException {
        if (i < 0 || i >= vettore.size()) {
            throw new ErrorException("Param invalid");
        }
        return vettore.get(i);
    }

    /**
     * Rimuove una riga dal modello in posizione i
     * se i è valido
     * @param i posizione
     * @exception condivisi.ErrorException se i non valido
     */
    public void removeRiga(int i) throws ErrorException {
        if (i < 0 || i >= vettore.size()) {
            throw new ErrorException("Param invalid");
        }
        vettore.remove(i);
    }

    /**
     * Controlla la presenza di una riga con nome uguale al nome
     * passato come parametro
     * @param nome da cercare
     * @return r se riga è stata trovata, null altrimenti
     */
    public RigaTabellaPubblicati presenza(String nome) {
        if (nome == null) {
            return null;
        }
        for (RigaTabellaPubblicati r : vettore) {
            if (r.getFile().compareTo(nome) == 0) {
                return r;
            }
        }
        return null;
    }

    /**
     * Restituisce il numero di righe del modello
     * @return numero righe
     */
    public int getRowCount() {
        return vettore.size();
    }

    /**
     * Restituisce il numero di colonne del modello
     * @return numero colonne
     */
    public int getColumnCount() {
        return headers.length;
    }

    /**
     * Restituisce l'oggetto in posizione [rowIndex][columnIndex]
     * se gli indici sono corretti
     * @param rowIndex indice riga
     * @param columnIndex indice colonna
     * @return oggetto [rowIndex][columnIndex] se esiste, altrimenti null
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= vettore.size()) {
            return null;
        }
        if (columnIndex < 0 || columnIndex >= headers.length) {
            return null;
        }
        RigaTabellaPubblicati r = vettore.get(rowIndex);
        if (columnIndex == 0) {
            return rowIndex + 1;
        }
        if (columnIndex == 1) {
            return r.getFile();
        }
        if (columnIndex == 2) {
            return r.getDimensione();
        }
        if (columnIndex == 3) {
            return r.getStato();
        }
        if (columnIndex == 4) {
            return r.getSituazione();
        }
        if (columnIndex == 5) {
            return "" + r.getPeer();
        }
        if (columnIndex == 6) {
            int ris = 0;
            if ((ris = r.getPeerCerca()) == RigaTabellaPubblicati.NONATTIVO) {
                return "Non visibile";
            } else {
                return "" + ris;
            }
        }
        if (columnIndex == 7) {
            InetAddress ind = r.getIdentita();
            if (ind == null) {
                return "";
            } else {
                return "" + ind.getHostAddress();
            }
        }
        return "";
    }

    /**
     * Restituisce il titolo della colonna in posizione c
     * se c è corretto
     * @param c indice colonna
     * @return titolo se tutto va bene, null altrimenti
     */
    @Override
    public String getColumnName(int c) {
        if (c < 0 || c >= headers.length) {
            return null;
        }
        return headers[c];
    }

    /**
     * Restituisce il tipo della colonna in posizione c
     * se c è corretto
     * @param c indice colonna
     * @return tipo della colonna
     */
    @Override
    public Class getColumnClass(int c) {
        if (c < 0 || c >= tipi.length) {
            return null;
        }
        return tipi[c];
    }

    /**
     * Controlla se la cella[row][col] è editabile;
     * restituisce sempre false
     * @param row riga
     * @param col colonna
     * @return sempre false
     */
    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }
}
