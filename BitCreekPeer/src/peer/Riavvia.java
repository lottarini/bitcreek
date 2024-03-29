package peer;

import condivisi.NetRecord;
import java.util.ArrayList;

/**
 * Task che si occupa di far ripartire tutti i download
 * in seguito alla riconnessione
 * @author Bandettini Alberto
 * @author Lottarini Andrea
 * @version BitCreekPeer 1.0
 */
public class Riavvia implements Runnable {

    /* Variabili d' istanza */
    /** Peer */
    private BitCreekPeer peer;

    /**
     * Costruttore
     * @param peer
     */
    public Riavvia(BitCreekPeer peer) {
        this.peer = peer;
    }

    /**
     * Corpo del task
     */
    public void run() {

        ArrayList<NetRecord> lista = new ArrayList<NetRecord>();
        ArrayList<Creek> array = null;
        array = this.peer.getDescr();
        if (array != null) {
            for (Creek c : array) {
                /* inizializzo il creek */
                c.init();
                /* se il file è in download */
                if (c.getStato()) {
                    
                    lista = peer.contattaTracker(c);
                    
                    peer.aggiungiLista(c, lista);
                    
                    /* inutile continuare a ciclare se non posso creare connessioni */
                    if (peer.getConnessioni() >= BitCreekPeer.MAXCONNESSIONI) {
                        break;
                    }
                }
                
                peer.addTask(new UploadManager(peer, c));
            }
        }
    }
}
