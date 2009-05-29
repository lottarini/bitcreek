package peer;

import condivisi.Descrittore;
import condivisi.ErrorException;
import condivisi.InterfacciaRMI;
import condivisi.Porte;
import gui.BitCreekGui;
import java.awt.Cursor;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * Task che si occupa di creare e pubblicare un creek
 * @author Bandettini
 */
public class Crea implements Runnable {

    
    /* Variabili d'istanza */
    File sorgente;
    BitCreekPeer peer;
    BitCreekGui gui;

    /**
     * Costruttore
     * @param sorgente file da pubblicare
     */
    public Crea(File sorgente, BitCreekPeer peer, BitCreekGui gui) throws ErrorException {
        if (sorgente == null || peer == null || gui == null) {
            throw new ErrorException("Param null");
        }
        if (sorgente.length() == 0) {
            throw new ErrorException("Empty File");
        }
        this.sorgente = sorgente;
        this.peer = peer;
        this.gui = gui;
    }

    /**
     * Corpo del task
     */
    public void run() {

        /* cambio il cursore */
        gui.getRootPane().setCursor(new Cursor(Cursor.WAIT_CURSOR));

        FileInputStream input = null;
        FileOutputStream output = null;
        
        long dimensione = sorgente.length();
        String nomefilesorgente = sorgente.getName();

        /* controllo che non sia presente */

        boolean problema = false;
        boolean presenza = false;
        
        System.out.println(Thread.currentThread().getName()+" INIZIO CREA");
        //QUESTO CONTROLLO LO DEVE FARE IL SERVER ... E A REGOLA ERA IMPLEMENTATO
        try {
            presenza = peer.presenza(nomefilesorgente);
        } catch (ErrorException ex) {
            problema = true;
        }

        Creek c = null;

        /* se non presente lo copio, aggiorno arraydescr e poi pubblico*/

        if (!presenza) {
            try {
                input = new FileInputStream(sorgente);
                output = new FileOutputStream("./FileCondivisi/" + nomefilesorgente);
                try {
                    copia(input, output);
                } catch (ErrorException ex) {
                    problema = true;
                }
                input.close();
                output.close();
            } catch (FileNotFoundException e) {
                problema = true;
            } catch (IOException e) {
                problema = true;
            }

            Descrittore descr = null;
            
            if(problema) System.out.println("AIA1");
            
            if (!problema) {
                try {
                    System.out.println("CREA - NEW DESCRITTORE");
                    descr = new Descrittore(nomefilesorgente, dimensione, hash(), peer.getStubCb());
                } catch (ErrorException ex) {
                    problema = true;
                }
            }
            
            if(problema) System.out.println("AIA2");
            /* invio al server il descrittore e contestualmente mi registro per la callback */

            Porte p = null;
            InterfacciaRMI stub = peer.getStub();

            if (!problema && stub != null) {
                try {
                    p = stub.inviaDescr(descr, peer.getMioIp(), peer.getPortaRichieste());
                    System.out.println(Thread.currentThread().getName()+" INVIATO AL SERVER!");
                } catch (RemoteException ex) {
                    problema = true;
                    System.out.println("TRAGGEDIA RMI");
                }
            } else {
                problema = true;
            }
            
            if(problema) System.out.println("AIA3");
            
            if (!problema && p != null) {
                descr.setPortaTCP(p.getPortaTCP());
                descr.setPortaUDP(p.getPortaUDP());
                System.out.println( Thread.currentThread().getName() + " Crea : getId() = " +p.getId());
                descr.setId(p.getId());
                try {
                    c = new Creek(descr, false, p.getPubblicato());
                } catch (ErrorException ex) {
                    problema = true;
                }
            } else {
                problema = true;
            }

            if (!problema) {
                try {
                    System.out.println("Entro in addcreek");
                    peer.addCreek(c);
                } catch (ErrorException ex) {
                    problema = true;
                }
            }
        }
        if (problema) {
            try {
                if(peer == null){
                    System.out.println("il peer e` null");
                }
                peer.deleteCreek(c.getName());
            } catch (ErrorException ex) {
            }
            File f = new File("./FileCondivisi/" + nomefilesorgente);
            f.delete();
            gui.PrintInformation("Errore Server", gui.ERRORE);
        }
        /* ricambio il cursore */
        gui.getRootPane().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        System.out.println(Thread.currentThread().getName()+  " MUORO " );
    }

    /**
     * Copia il  file input in output
     * @param input
     * @param output
     */
    private void copia(FileInputStream input, FileOutputStream output) throws ErrorException {

        if (input == null || output == null) {
            throw new ErrorException("Param null");
        }

        boolean exit = false;
        int c = 0;

        BufferedInputStream in = new BufferedInputStream(input);
        BufferedOutputStream out = new BufferedOutputStream(output);
        while (!exit) {
            try {
                while ((c = in.read()) != -1) {
                    out.write((char) c);
                }
                out.write((char) c);
                exit = true;
            } catch (IOException ex) {
            }
        }
    }

    /**
     * Crea la stringa hash
     * @return hash
     * @throws condivisi.ErrorException
     */
    private byte[] hash() throws ErrorException {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException ex) {
            throw new ErrorException("No such Algorithm");
        }

        byte[] arraybyte = null; /* bytes letti dal file */
        long dim = sorgente.length();

        if (dim < BitCreekPeer.DIMBLOCCO) {
            arraybyte = new byte[(int) dim];
        } else {
            arraybyte = new byte[BitCreekPeer.DIMBLOCCO];
        }

        for (int i = 0; i <
                arraybyte.length; i++) {
            arraybyte[i] = 0;
        }

        FileInputStream input = null;
        try {
            input = new FileInputStream(sorgente);
        } catch (FileNotFoundException ex) {
            throw new ErrorException("File not Found");
        }

        byte[] arrayris = null;
        int dimhash = 0; /* dimensione della stringa risultante */

        /* leggo a blocchi e applico SHA-1 */

        ArrayList<byte[]> array = new ArrayList<byte[]>();

        try {
            while (input.read(arraybyte) != -1) {
                dim -= arraybyte.length;
                md.update(arraybyte);
                arrayris = md.digest();
                dimhash += arrayris.length;
                array.add(arrayris);
                if (dim != 0) {
                    if (dim < BitCreekPeer.DIMBLOCCO) {
                        arraybyte = new byte[(int) dim];
                    } else {
                        arraybyte = new byte[BitCreekPeer.DIMBLOCCO];
                    }

                    for (int i = 0; i <
                            arraybyte.length; i++) {
                        arraybyte[i] = 0;
                    }

                }
            }
        } catch (IOException ex) {
            throw new ErrorException("IO Problem");
        }

        /* creo la stringa di dimensione dimhash e ci copio i byte */
        byte[] hash = new byte[dimhash];
        int k = 0;
        for (int i = 0; i < array.size(); i++) {
            arrayris = array.get(i);
            for (int j = 0; j < arrayris.length; j++) {
                hash[k] = arrayris[j];
            }
            k++;
        }
        return hash;
    }
}