/*
 * Ver: 10.0
 * 
 * 4 Novembre 2023
 * 
 * Adriano Di Nunzio
 * Marco Perrotta
 * Lorenzo Negrini
 * 
 * L.T. Informatica
 * 
 * Reti di Telecomunicazioni
 * 
 * Università degli Studi di Ferrara
 * 
*/

import java.lang.Math;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import java.io.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class QueueSystemMMC {
    static double lambda;
    static double mu;
    static double mod;
    static short c;
    static long simu_time;
    static long inizioOre;
    static long inizioMin;
    static long inizioSec;
    static Boolean flag_grafica = false;
    static Boolean stop = false;
    static Boolean stopGuarda = false;
    static Random rand = new Random();
    static Boolean pieno[];
    static ConcurrentLinkedQueue<pkt> coda = new ConcurrentLinkedQueue<pkt>();
    static ArrayList<pkt> pktUsciti = new ArrayList<pkt>();
    static LinkedList<Short> Lq = new LinkedList<Short>();
    static LinkedList<Short> Ls = new LinkedList<Short>();
    static QueueSystemMMC m = new QueueSystemMMC();

    static JFrame njf = new JFrame();
    static JPanel pannelloQuadratini = new JPanel();
    static ImageIcon[] immaginiPkt;

    public class pkt {
        public double tempoCoda;
        public double tempoServizio;

        public pkt(double inizio) {
            tempoCoda = -inizio;
            tempoServizio = 0;
        }

        public pkt(double tc, double ts) {
            tempoCoda = tc;
            tempoServizio = ts;
        }

        public pkt(pkt p) {
            this.tempoCoda = p.tempoCoda;
            this.tempoServizio = p.tempoServizio;
        }

        public String toString() {
            return tempoCoda + " " + tempoServizio + " " + (tempoCoda + tempoServizio);
        }
    }

    static Image getScaledImage(Image srcImg, int width, int height) {
        BufferedImage resizedImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, width, height, null);
        g2.dispose();
        return resizedImg;
    }

    static class OverSpeed extends Exception{ 
        public OverSpeed(){
            super();
        }
    }
    static void aggiornaQuadratini() {
        int numQuadratiniAttuali = pannelloQuadratini.getComponentCount();
        ConcurrentLinkedQueue<pkt> q = new ConcurrentLinkedQueue<pkt>(coda);
        int contatore = q.size();

        for (Boolean b : pieno) contatore += (b) ? 1 : 0; 
    
        for (int i = 0; i < numQuadratiniAttuali - contatore; i++) {
            try {
                pannelloQuadratini.remove(0);
            } catch (Exception e) {}
        }

        for(int i = numQuadratiniAttuali; i < contatore; i++) {
            JLabel quadrato = new JLabel(immaginiPkt[rand.nextInt(11)]);
            pannelloQuadratini.add(quadrato);
        }

        pannelloQuadratini.revalidate();
        pannelloQuadratini.repaint();
    }

    static void inputDati() {
        JFrame frame = new JFrame("Input M/M/C");
        ImageIcon icon = new ImageIcon("src\\IconaInput.png");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(380, 308);
        frame.setLayout(new FlowLayout());
        frame.setResizable(false);
        frame.setIconImage(icon.getImage());
        frame.setAlwaysOnTop(true);
        frame.setLocationRelativeTo(null);

        JPanel panelLM = new JPanel(new GridLayout(2, 2, 45, 0));

        JLabel labelLam = new JLabel("  Valore di λ:");
        labelLam.setFont(new Font("Trebuchet MS", Font.BOLD, 17));
        JTextField textFieldLam = new JTextField(6);
        textFieldLam.setFont(new Font("Trebuchet MS", Font.BOLD, 19));
        textFieldLam.setBorder(new LineBorder(Color.LIGHT_GRAY, 2));

        JLabel labelMu = new JLabel("  Valore di μ:");
        labelMu.setFont(new Font("Trebuchet MS", Font.BOLD, 17));
        JTextField textFieldMu = new JTextField(6);
        textFieldMu.setFont(new Font("Trebuchet MS", Font.BOLD, 19));
        textFieldMu.setBorder(new LineBorder(Color.LIGHT_GRAY, 2));

        panelLM.add(labelLam);
        panelLM.add(labelMu);
        panelLM.add(textFieldLam);
        panelLM.add(textFieldMu);

        JPanel panelC = new JPanel(new GridLayout(2, 1));

        JLabel labelC = new JLabel("  Valore di C:");
        labelC.setFont(new Font("Trebuchet MS", Font.BOLD, 17));
        JTextField textFieldC = new JTextField(6);
        textFieldC.setFont(new Font("Trebuchet MS", Font.BOLD, 19));
        textFieldC.setBorder(new LineBorder(Color.LIGHT_GRAY, 2));

        panelC.add(labelC);
        panelC.add(textFieldC);

        JLabel labelTime = new JLabel("Tempo di Simulazione:");
        labelTime.setFont(new Font("Trebuchet MS", Font.BOLD, 17));

        JPanel panelTime = new JPanel(new GridLayout(2, 3, 5, 0));

        JLabel labelSec = new JLabel("    Secondi:");
        labelSec.setFont(new Font("Trebuchet MS", Font.BOLD, 15));
        JLabel labelMin = new JLabel("      Minuti:");
        labelMin.setFont(new Font("Trebuchet MS", Font.BOLD, 15));
        JLabel labelOre = new JLabel("        Ore:");
        labelOre.setFont(new Font("Trebuchet MS", Font.BOLD, 15));

        JTextField textFieldSec = new JTextField(8);
        textFieldSec.setFont(new Font("Trebuchet MS", Font.BOLD, 15));
        textFieldSec.setBorder(new LineBorder(Color.LIGHT_GRAY, 2));
        textFieldSec.setText("0");
        JTextField textFieldMin = new JTextField(8);
        textFieldMin.setFont(new Font("Trebuchet MS", Font.BOLD, 15));
        textFieldMin.setText("0");
        textFieldMin.setBorder(new LineBorder(Color.LIGHT_GRAY, 2));
        JTextField textFieldOre = new JTextField(8);
        textFieldOre.setFont(new Font("Trebuchet MS", Font.BOLD, 15));
        textFieldOre.setText("0");
        textFieldOre.setBorder(new LineBorder(Color.LIGHT_GRAY, 2));

        panelTime.add(labelOre);
        panelTime.add(labelMin);
        panelTime.add(labelSec);
        panelTime.add(textFieldOre);
        panelTime.add(textFieldMin);
        panelTime.add(textFieldSec);

        JPanel panelmol = new JPanel(new GridLayout(2, 1));
        JLabel moltime = new JLabel("Velocità Simulazione: ");
        moltime.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
        Double[] opzioni = { 1.0, 2.0, 4.0, 8.0 };
        JComboBox<Double> molBox = new JComboBox<>(opzioni);
        molBox.setFont(new Font("Trebuchet MS", Font.BOLD, 15));

        panelmol.add(moltime);
        panelmol.add(molBox);

        JButton buttonGrafica = new JButton("Grafica");
        buttonGrafica.setFont(new Font("Trebuchet MS", Font.BOLD, 18));
        buttonGrafica.setBackground(new Color(0x00cc99));
        buttonGrafica.setBorderPainted(true);
        buttonGrafica.setFocusPainted(false);

        JButton button = new JButton("Simulazione");
        button.setFont(new Font("Trebuchet MS", Font.BOLD, 18));
        button.setBackground(new Color(0x00cc99));
        button.setBorderPainted(true);
        button.setFocusPainted(false);

        JPanel bottoni = new JPanel(new GridLayout(1, 2, 20, 0));
        bottoni.add(button);
        bottoni.add(buttonGrafica);

        frame.add(panelLM);
        frame.add(panelC);
        frame.add(panelTime);
        frame.add(panelmol);
        frame.add(bottoni);

        button.addActionListener(new ActionListener() {
            boolean flag = true;
            @Override
            public void actionPerformed(ActionEvent e) {
                if (flag) {
                    try {
                        String input1 = textFieldLam.getText();
                        String input2 = textFieldMu.getText();
                        String input3 = textFieldC.getText();
                        String inputSec = textFieldSec.getText();
                        String inputMin = textFieldMin.getText();
                        String inputOre = textFieldOre.getText();
                        mod = (Double) molBox.getSelectedItem();

                        if (input1.isBlank() || input2.isBlank() || input3.isBlank() || inputSec.isBlank() || inputMin.isBlank() || inputOre.isBlank()) {
                            throw new IllegalArgumentException();
                        }

                        short inp3 = Short.parseShort(input3);
                        double inp2 = Double.parseDouble(input2);
                        double inp1 = Double.parseDouble(input1);
                        inizioSec = Integer.parseInt(inputSec);
                        inizioMin = Integer.parseInt(inputMin);
                        inizioOre = Integer.parseInt(inputOre);
                        lambda = inp1;
                        mu = inp2;
                        c = inp3;
                        simu_time = (inizioSec) + (inizioMin * 60) + (inizioOre * 3600);

                        if(simu_time == 0) throw new TimeoutException();

                        flag = false;
                        simulazione();
                        frame.setVisible(false);
                    } catch (NumberFormatException nonnumeri) { // eccezione se i valori inseriti non sono numeri
                        JLabel notNum = new JLabel("Inserire solo valori numerici!");
                        notNum.setFont(new Font("Roboto", Font.BOLD, 14));
                        JOptionPane.showMessageDialog(frame, notNum, "Attenzione!", JOptionPane.WARNING_MESSAGE);
                        textFieldLam.setText(null);
                        textFieldMu.setText(null);
                        textFieldC.setText(null);
                        textFieldSec.setText("0");
                        textFieldMin.setText("0");
                        textFieldOre.setText("0");
                    } catch (IllegalArgumentException vuoto) {
                        JLabel isBlanck = new JLabel("Riemepire tutti campi!");
                        isBlanck.setFont(new Font("Roboto", Font.BOLD, 15));
                        JOptionPane.showMessageDialog(frame, isBlanck, "Attenzione!", JOptionPane.WARNING_MESSAGE);
                    }
                    catch(TimeoutException t){
                        JLabel t_null = new JLabel("Inserire un tempo maggiore di 0!");
                        t_null.setFont(new Font("Roboto", Font.BOLD, 14));
                        JOptionPane.showMessageDialog(frame, t_null, "Fuori Tempo", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        buttonGrafica.addActionListener(new ActionListener() {
            boolean flag = true;
            @Override
            public void actionPerformed(ActionEvent e) {
                if (flag) {
                    try {
                        String input1 = textFieldLam.getText();
                        String input2 = textFieldMu.getText();
                        String input3 = textFieldC.getText();
                        String inputSec = textFieldSec.getText();
                        String inputMin = textFieldMin.getText();
                        String inputOre = textFieldOre.getText();
                        mod = (Double) molBox.getSelectedItem();

                        if (mod > 2.0) throw new OverSpeed();

                        if (input1.isBlank() || input2.isBlank() || input3.isBlank() || inputSec.isBlank()|| inputMin.isBlank() || inputOre.isBlank()) {
                            throw new IllegalArgumentException();
                        }

                        short inp3 = Short.parseShort(input3);
                        double inp2 = Double.parseDouble(input2);
                        double inp1 = Double.parseDouble(input1);
                        inizioSec = Integer.parseInt(inputSec);
                        inizioMin = Integer.parseInt(inputMin);
                        inizioOre = Integer.parseInt(inputOre);
                        lambda = inp1;
                        mu = inp2;
                        c = inp3;
                        simu_time = (inizioSec) + (inizioMin * 60) + (inizioOre * 3600);
                        
                        if(simu_time == 0) throw new TimeoutException();
                        
                        flag = false;
                        flag_grafica = true;
                        simulazione();
                        frame.setVisible(false);
                    } catch (NumberFormatException nonnumeri) { 
                        JLabel notNum = new JLabel("Inserire solo valori numerici!");
                        notNum.setFont(new Font("Roboto", Font.BOLD, 14));
                        JOptionPane.showMessageDialog(frame, notNum, "Attenzione!", JOptionPane.WARNING_MESSAGE);
                        textFieldLam.setText(null);
                        textFieldMu.setText(null);
                        textFieldC.setText(null);
                        textFieldSec.setText("0");
                        textFieldMin.setText("0");
                        textFieldOre.setText("0");
                    } catch (IllegalArgumentException vuoto) {
                        JLabel isBlanck = new JLabel("Riemepire tutti campi!");
                        isBlanck.setFont(new Font("Roboto", Font.BOLD, 15));
                        JOptionPane.showMessageDialog(frame, isBlanck, "Attenzione!", JOptionPane.WARNING_MESSAGE);
                    } catch (OverSpeed noGrafica) {
                        JLabel noGraf = new JLabel("Velocità troppo elevata per una simulazione grafica!");
                        noGraf.setFont(new Font("Roboto", Font.BOLD, 14));
                        JOptionPane.showMessageDialog(frame, noGraf, "Sovraccarico!", JOptionPane.ERROR_MESSAGE);
                    }
                    catch(TimeoutException t){
                        JLabel t_null = new JLabel("Inserire un tempo maggiore di 0!");
                        t_null.setFont(new Font("Roboto", Font.BOLD, 14));
                        JOptionPane.showMessageDialog(frame, t_null, "Fuori Tempo", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        frame.setVisible(true);
    }

    static class SimulationWorker extends SwingWorker<Void, Void>{
        @Override
        protected Void doInBackground() throws Exception{
            principale();
            return null;
        }
    }

    static void simulazione(){
        njf = new JFrame("Simulazione di un Sistema a Coda M/M/" + c);
        njf.setSize(1000, 150);
        njf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        njf.setLocationRelativeTo(null);
        njf.setResizable(false);
        njf.setIconImage((new ImageIcon("src\\IconaSimu.png")).getImage());

        LocalTime orario = LocalTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm:ss");

        long oreinizio = orario.getHour();
        long minutiinizio = orario.getMinute();
        long secondiinizio = orario.getSecond();
        long totaleSecondiCorrenti = oreinizio * 3600 + minutiinizio * 60 + secondiinizio;
        long totaleSecondiPassati = inizioOre * 3600 + inizioMin * 60 + inizioSec + 10;
        long totaleSecondifine = (totaleSecondiCorrenti + totaleSecondiPassati) % (24 * 3600);
        long nuoveOre = totaleSecondifine / 3600;
        long nuoviMinuti = (totaleSecondifine % 3600) / 60;
        long nuoviSecondi = (totaleSecondifine % 3600) % 60;

        String orariofine = String.format("%02d:%02d:%02d", nuoveOre, nuoviMinuti, nuoviSecondi);
        String orario_sim = String.format("%02d:%02d:%02d", inizioOre, inizioMin, inizioSec);

        JLabel title = new JLabel("  Sistema a Coda M/M/" + c);
        title.setForeground(new Color(0, 0, 160));
        title.setFont(new Font("ProFont", Font.BOLD, 40));

        JPanel dati = new JPanel(new GridLayout(4, 2));
        JLabel lambda_text = new JLabel("Ritmo degli Arrivi (λ):   " + lambda + " pkt/s");
        JLabel mu_text = new JLabel("Ritmo delle Uscite (μ):  " + mu + " pkt/s");
        JLabel c_text = new JLabel("Numero di Servitori:     " + c);
        JLabel time_text = new JLabel("Durata di Simulazione:   " + orario_sim);
        JLabel mol_text = new JLabel("Velocità Simulazione:   " + mod);
        JLabel or_inizio = new JLabel("Orario di Inizio:  " + orario.format(formato));
        JLabel or_fine = new JLabel("Orario di Fine:    " + orariofine);

        Font d = new Font("Roboto", Font.BOLD, 15);
        lambda_text.setFont(d);
        mu_text.setFont(d);
        c_text.setFont(d);
        time_text.setFont(d);
        mol_text.setFont(d);
        or_inizio.setFont(d);
        or_fine.setFont(d);

        dati.add(lambda_text);
        dati.add(time_text);
        dati.add(mu_text);
        dati.add(or_inizio);
        dati.add(c_text);
        dati.add(or_fine);
        dati.add(mol_text);

        Border vuoto = BorderFactory.createEmptyBorder(15, 0, 0, 0);
        JPanel intestazione = new JPanel(new GridLayout(1, 2));
        intestazione.setBorder(vuoto);
        intestazione.add(title);
        intestazione.add(dati);

        njf.add(intestazione, BorderLayout.PAGE_START);

        if(flag_grafica) {
            JPanel mainPanel = new JPanel(new BorderLayout());
            njf.setSize(1000, 500);
            njf.setLocationRelativeTo(null);

            pannelloQuadratini = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if(stop){
                        pannelloQuadratini.removeAll();
                        aggiornaQuadratini();
                    }
                }
            };
            mainPanel.add(pannelloQuadratini, BorderLayout.CENTER);

            njf.add(mainPanel, BorderLayout.CENTER);
           
            immaginiPkt = new ImageIcon[11];

            for (int i = 0; i < 11; i++) {
                String n = "src\\pkt (" + (i + 1) + ").png";
                immaginiPkt[i] = new ImageIcon(n);
                immaginiPkt[i].setImage(getScaledImage(immaginiPkt[i].getImage(), 70, 70));
            }
        }
        String spazio = "                                                               ";
        JLabel diritti = new JLabel(" A.A. 2023-2024 - L.T. Informatica - 2° Anno - Reti di Telecomunicazioni "+ spazio +"A. Di Nunzio - M. Perrotta - L. Negrini");
        diritti.setFont(new Font("Comic Sans Ms",Font.ROMAN_BASELINE,14));
        njf.add(diritti,BorderLayout.PAGE_END);
        
        njf.setVisible(true);

        SimulationWorker worker = new SimulationWorker();
        worker.execute();
    }
    
    static void showDati(double mq, double ms, double mt, float lq, float ls){
        njf.setVisible(false);
        njf.dispose();

        String orario_sim = String.format("%02d:%02d:%02d", inizioOre, inizioMin, inizioSec);

        JLabel title = new JLabel("  Sistema a Coda M/M/" + c);
        title.setForeground(new Color(0, 0, 160));
        title.setFont(new Font("ProFont", Font.BOLD, 40));

        JPanel dati = new JPanel(new GridLayout(4, 2));
        JLabel lambda_text = new JLabel("Ritmo degli Arrivi (λ):   " + lambda + " pkt/s");
        JLabel mu_text = new JLabel("Ritmo delle Uscite (μ):  " + mu + " pkt/s");
        JLabel c_text = new JLabel("Numero di Servitori:     " + c);
        JLabel time_text = new JLabel("Durata di Simulazione:   " + orario_sim);
        JLabel mol_text = new JLabel("Velocità Simulazione:   " + mod);

        Font d = new Font("Roboto", Font.BOLD, 15);
        lambda_text.setFont(d);
        mu_text.setFont(d);
        c_text.setFont(d);
        time_text.setFont(d);
        mol_text.setFont(d);

        dati.add(lambda_text);
        dati.add(time_text);
        dati.add(mu_text);
        dati.add(c_text);
        dati.add(mol_text);

        Border vuoto = BorderFactory.createEmptyBorder(15, 0, 0, 0);
        JPanel intestazione = new JPanel(new GridLayout(1, 2));
        intestazione.setBorder(vuoto);
        intestazione.add(title,BorderLayout.CENTER);
        intestazione.add(dati);

        JPanel risultati1 = new JPanel(new GridLayout(3,1));
        JPanel risultati2 = new JPanel(new GridLayout(2,1));

        BigDecimal arr = new BigDecimal(mq);
        arr = arr.setScale(5,RoundingMode.HALF_UP);
        arr = arr.stripTrailingZeros();
        JLabel mediaQ = new JLabel("Tempo medio in Coda (s):           " + arr);
        mediaQ.setFont(d);

        arr = new BigDecimal(ms);
        arr = arr.setScale(4,RoundingMode.HALF_UP);
        arr = arr.stripTrailingZeros();
        JLabel mediaSer = new JLabel("Tempo medio nei Servitori (s):   " + arr);
        mediaSer.setFont(d);
        
        arr = new BigDecimal(mt);
        arr = arr.setScale(4,RoundingMode.HALF_UP);
        arr = arr.stripTrailingZeros();
        JLabel mediaSys = new JLabel("Tempo medio nel Sistema (s):    " + arr);
        mediaSys.setFont(d);

        arr = new BigDecimal(lq);
        arr = arr.setScale(4,RoundingMode.HALF_UP);
        arr = arr.stripTrailingZeros();
        JLabel nPktQ = new JLabel("Numero medio pkt in Coda: " + arr);
        nPktQ.setFont(d);

        arr = new BigDecimal(ls);
        arr = arr.setScale(4,RoundingMode.HALF_UP);
        arr = arr.stripTrailingZeros();
        JLabel nPktS = new JLabel("Numero medio pkt nel Sistema: " + arr);
        nPktS.setFont(d);

        risultati1.add(mediaQ);
        risultati1.add(mediaSer);
        risultati1.add(mediaSys);
        risultati2.add(nPktQ);
        risultati2.add(nPktS);

        JPanel datifinali = new JPanel(new GridLayout(1, 3));
        vuoto = BorderFactory.createEmptyBorder(0, 0, 20, 0);
        title = new JLabel("       Risultati: ");
        title.setForeground(new Color(0, 0, 160));
        title.setFont(new Font("ProFont", Font.BOLD, 40));
        datifinali.setBorder(vuoto);
        datifinali.add(title,BorderLayout.CENTER);
        datifinali.add(risultati1);
        datifinali.add(risultati2);

        JFrame finale = new JFrame("Fine Simulazione M/M/" + c);
        finale.setSize(1000,240);
        finale.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        finale.setAlwaysOnTop(true);
        finale.setResizable(false);
        finale.setLocationRelativeTo(null);
        finale.setIconImage(new ImageIcon("src\\icondati.png").getImage());

        finale.add(intestazione, BorderLayout.PAGE_START);
        finale.add(datifinali,BorderLayout.PAGE_END);
        finale.setVisible(true);
    }

    static class servitore extends Thread {
        public servitore(String str) {
            super(str);
        }

        public void run() {
            try {
                pkt p = m.new pkt(coda.poll());
                p.tempoCoda += (double) System.currentTimeMillis();
                p.tempoCoda = p.tempoCoda / 1000 * mod;
                double time = (-(1 / mu)) * Math.log(1 - rand.nextDouble());
                long timeMillis = (long) (time * 1000 / mod);
                p.tempoServizio = ((double) timeMillis) / 1000 * mod;
                try {
                    sleep(timeMillis);
                } catch (InterruptedException e) {
                    pieno[Integer.parseInt(getName())] = false;
                }
                System.out.println(p.toString());
                pktUsciti.add(p);
                pieno[Integer.parseInt(getName())] = false;
            } catch (Exception e) {
                pieno[Integer.parseInt(getName())] = false;
            }
        }
    }

    static class generatore extends Thread {
        public generatore(String str) {
            super(str);
        }

        public void run() {
            while (!stop) {
                double time = (-(1 / lambda)) * Math.log(1 - rand.nextDouble());
                long timeMillis = (long) (time * 1000 / mod);

                try {
                    sleep(timeMillis);
                    pkt p = m.new pkt(((double) System.currentTimeMillis()));
                    coda.add(p);
                } 
                catch (InterruptedException e) {
                }
            }
        }
    }

    static class codaThread extends Thread {

        public codaThread(String str) {
            super(str);
        }

        public void run() {
            int index;
            while (!stop) {
                System.out.flush();
                if (!(coda.isEmpty())) {
                    index = Arrays.asList(pieno).indexOf(false);
                    if (index >= 0) {
                        pieno[index] = true;
                        new servitore(Integer.toString(index)).start();
                    }
                }
                if (flag_grafica)
                    aggiornaQuadratini();
            }
        }
    }

    static class contapkt extends Thread {
        public contapkt(String str) {
            super(str);
        }

        public void run() {
            short count = 0;
            ConcurrentLinkedQueue<pkt> q;
            while (!stopGuarda) {
                q = new ConcurrentLinkedQueue<pkt>(coda);
                count = (short) Math.max(0, q.size());
                Lq.add(count);

                for (Boolean b : pieno)
                    count += (b) ? 1 : 0; // se b = true somma 1 se false 026
                Ls.add(count);

                try {
                    sleep((long) (1000 / mod));
                } catch (InterruptedException e) {
                }
            }
        }
    }

    static void principale() {
        long tempoInizio = System.currentTimeMillis();
        LinkedList<pkt> medie = new LinkedList<pkt>();
        LinkedList<Float> medieLq = new LinkedList<Float>();
        LinkedList<Float> medieLs = new LinkedList<Float>();
        HashMap<Short, Double> Pk = new HashMap<Short, Double>();
        FileWriter fw;
        PrintWriter outFile;

        pieno = new Boolean[c];

        for (int i = 0; i < pieno.length; i++) pieno[i] = false;

        generatore g = new generatore("gen");
        codaThread cod = new codaThread("coda");
        contapkt cont = new contapkt("contatore");

        g.start();
        cod.start();
        cont.start();
        try {
            TimeUnit.SECONDS.sleep(simu_time);
            stopGuarda = true;
            stop = true;
            TimeUnit.SECONDS.sleep(10);
            g.interrupt();
            cod.interrupt();
            cont.interrupt();
            coda.clear();
            System.out.println("Fine Simulazione");
            System.out.flush();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long tempoFine = System.currentTimeMillis() - tempoInizio;
        tempoFine = (long) (((double) tempoFine) * mod);

        double mediaC = 0, mediaS = 0, mediaT = 0;

        int i = 1;
        try {
            for (pkt p : pktUsciti) {
                mediaC += p.tempoCoda;
                mediaS += p.tempoServizio;

                pkt me = m.new pkt((mediaC / i), (mediaS / i));
                medie.add(me);
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mediaC = medie.getLast().tempoCoda;
        mediaS = medie.getLast().tempoServizio;
        mediaT = mediaC + mediaS;

        i = 1;
        float numLq = 0, numLs = 0;
        float N = (float) Ls.size();
        Short elem;
        Double val;
        while (!Lq.isEmpty()) {
            numLq += (float) Lq.remove(0);
            medieLq.add(numLq / i);

            elem = Ls.remove(0);
            val = (Double) Pk.get(elem);
            Pk.put(elem, (val == null) ? 1.0 : ++val);

            numLs += (float) elem;
            medieLs.add(numLs / i);
            i++;
        }

        showDati(mediaC, mediaS, mediaT,medieLq.getLast(), medieLs.getLast());

        try {
            fw = new FileWriter("probabilità.txt");
            outFile = new PrintWriter(fw);
        } catch (IOException e) {
            e.printStackTrace();
            outFile = null;
        }

        double prob;
        for (Map.Entry<Short, Double> P : Pk.entrySet()) {
            prob = P.getValue() / N;
            outFile.println("P" + P.getKey() + " " + prob);
        }
        outFile.close();

        try {
            fw = new FileWriter("parametri.txt");
            outFile = new PrintWriter(fw);

            outFile.println(lambda + " " + mu + " " + c + " " + medieLq.getLast() + " " + medieLs.getLast());
            outFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            fw = new FileWriter("dati.txt");
            outFile = new PrintWriter(fw);

            outFile.println("T_Coda T_Server T_Sistema");
            int iodio=0;
            while (!medie.isEmpty()){
                iodio ++;
                outFile.println(iodio);
                outFile.println((medie.remove(0)));
            }
            outFile.close();
        } catch (IOException e){
            e.printStackTrace();
        }

        Date orario_finale = new Date();
        SimpleDateFormat formato_orario = new SimpleDateFormat("ddHHmm");
        String orario_file = formato_orario.format(orario_finale);
        
        String file_risultati[] = {"dati.txt", "parametri.txt", "probabilità.txt"};
        String nome_zip = orario_file + "_risultati_" + lambda + "_" + mu + "_" + c + ".zip";
        
        try(ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(nome_zip))){
            for(String file : file_risultati){
                File fileCompresso = new File(file);

                ZipEntry entry = new ZipEntry(fileCompresso.getName());
                zip.putNextEntry(entry);

                FileInputStream reader = new FileInputStream(fileCompresso);
                byte dimensioneCompressione[] = new byte[1024];
                int len;

                while((len = reader.read(dimensioneCompressione)) > 0){
                    zip.write(dimensioneCompressione, 0, len);
                }
                reader.close();

                zip.closeEntry();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        inputDati();
    }
}