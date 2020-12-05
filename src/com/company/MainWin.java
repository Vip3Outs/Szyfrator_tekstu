package com.company;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.List;

/**
 * Klasa GUI dla wyświetlania okna podstawowego.
 */
public class MainWin extends JFrame {
    /**Panel główny. */
    private JPanel panelMain;
    /**Przycisk do szyfrowania wybranego tekstu. */
    private JButton encryptButton;
    /**Przycisk do deszyfrowania wybranego tekstu. */
    private JButton decryptButton;
    /**Przycisk służący do wyboru odpowiedniego pliku. */
    private JButton fileButton;
    /**Pole wyświetlające aktualne informacje oraz stany programu (np. błędy/ wybrany plik, pomyślność dokonania enkrypcji lub dekrypcji). */
    private JLabel chosenFileLabel;
    /**Zmienna pomocnicza odpowiedzialna za przechowywanie wybranego pliku oraz jego atrybutów z klasy File. */
    private File file = null;

    /**
     * Domyślny konstruktor.
     * @param title Pole oznaczające nazwę programu.
     */
    private MainWin(String title){
        super(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(panelMain);
        this.pack();
        fileButton.addActionListener(actionEvent -> openFile());
        encryptButton.addActionListener(actionEvent -> { try { encryptData(file); } catch (IOException e) { e.printStackTrace(); }});
        decryptButton.addActionListener(actionEvent -> { try { decryptData(file); } catch (IOException e) { e.printStackTrace(); }});
    }

    /**
     * Główna metoda programu. Punkt wejściowy.
     * @param args Argumenty uruchomieniowe programu.
     */
    public static void main(String[] args){
        JFrame frame = new MainWin("Szyfrator tekstu");
        frame.setVisible(true);
        frame.setSize(400,250); //rozmiar okna
        frame.setLocationRelativeTo(null); //ustawienie okna aplikacji środek ekranu
    }

    /**
     * Metoda służąca do wyboru rządanego pliku tekstowego. Wybór pliku następuje za pomocą klasy JFileChooser.
     * @see JFileChooser
     */
    public void openFile(){
        JFileChooser fileChooser = new JFileChooser(); //klasa do otwierania i wybieranie plików z lokalnego dysku
        String userDir = System.getProperty("user.home");
        fileChooser.setCurrentDirectory(new java.io.File(userDir + "/Desktop")); //ustawienie domyślnej (początkowej ścieżki) na pulpit)
        FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
        fileChooser.setFileFilter(filter); //ustawienie filtrów, aby pokazywane były jedynie pliki .txt i katalogi
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if(fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
            file = fileChooser.getSelectedFile();
            chosenFileLabel.setText("Wybrano plik: " + file.getName());
        }
    }

    /**
     * Metoda służąca do enkrypcji wybranego wcześniej pliku tekstowego (wybrany poprzednio przez metodę openFile()).
     * @param file1 Zmienna typu File która zawiera atrybuty oraz ścieżkę do pliku tekstowego który ma zozstać poddany enkrypcji.
     * @throws IOException Wyjątek jest potrzebny gdy istnieje możliwość braku istnienia pliku.
     */
    private void encryptData(File file1) throws IOException {
        if (file != null) {
            Path path = file1.toPath();
            BasicFileAttributes fatr = Files.readAttributes(path, BasicFileAttributes.class);
            String tmp = fatr.creationTime().toString(); //pobranie atrybutu - stworzenie pliku
            tmp = tmp.substring(11, 19); //pobranie samej godziny oraz zmiana ":" na puste znaki
            tmp = tmp.replaceAll(":", "");
            String fileTimestamp = tmp;
            long seed = Long.parseLong(fileTimestamp); //stworzenie zmiennej typu Long z wcześniej pobranej godziny
            Random rand = new Random(seed); //stworzenie nowego generatora liczb pseudolosowych, którego zarodkiem jest godzina utworzenia pliku
            Scanner input = new Scanner(file1);
            StringBuilder text= new StringBuilder();
            while (input.hasNext()) { //pobranie wartosci z pliku tekstowego i ich zapis do nowej zmiennej
                text.append(input.nextLine());
                text.append("\n");
            }
            input.close();

            PrintWriter fileSave = new PrintWriter(new FileWriter(path.toString())); //nadpis pliku
                int[] output = new int[text.length()];
                for(int j = 0; j<text.length();j++) {
                    int o = ((int) text.charAt(j) ^ rand.nextInt()); //użycie funkcji XOR do enkrypcji każdego znaku przez kolejną liczbę pseudolosową
                    output[j] = o;
                    fileSave.println(output[j]);
                }
                fileSave.close();
                file = null;
                chosenFileLabel.setText("Zaszyfrowano plik: " + file1.getName());
        }
        else {
            chosenFileLabel.setText("Nie wybrano żadnego pliku!");
            return;
        }
    }

    /**
     * Metoda służąca do enkrypcji wybranego wcześniej pliku tekstowego (wybrany poprzednio przez metodę openFile()).
     * @param file1 Zmienna typu File która zawiera atrybuty oraz ścieżkę do pliku tekstowego który ma zostać poddany dekrypcji.
     * @throws IOException Wyjątek jest potrzebny gdy istnieje możliwość braku istnienia pliku.
     */
    private void decryptData(File file1) throws IOException {
        if (file != null) {
            Path path = file1.toPath();
            BasicFileAttributes fatr = Files.readAttributes(path, BasicFileAttributes.class);
            String tmp = fatr.creationTime().toString(); //pobranie atrybutu - stworzenie pliku
            tmp = tmp.substring(11, 19); //pobranie samej godziny oraz zmiana ":" na puste znaki
            tmp = tmp.replaceAll(":", "");
            String fileTimestamp = tmp;
            long seed = Long.parseLong(fileTimestamp); //stworzenie zmiennej typu Long z wcześniej pobranej godziny
            Random rand2 = new Random(seed); //stworzenie nowego generatora liczb pseudolosowych, którego zarodkiem jest godzina utworzenia pliku
            Scanner input = new Scanner(file1);

            List<Integer> text = new ArrayList<>();
            while (input.hasNext()) { //pobranie wartosci z pliku tekstowego i ich zapis do nowej zmiennej typu List<String>
                text.add(Integer.parseInt(input.nextLine()));
            }
            input.close();
            Integer[] output = new Integer[text.size()];
            output = text.toArray(output);

            PrintWriter fileSave = new PrintWriter(new FileWriter(path.toString())); //nadpis pliku
                for(int j = 0; j<text.size();j++) {
                    fileSave.print((char)((output[j]) ^  rand2.nextInt())); //użycie funkcji XOR do dekrypcji każdej linii (znaku) przez kolejną liczbę pseudolosową
                }
            fileSave.close();
            file = null;
            chosenFileLabel.setText("Rozszyfrowano plik: " + file1.getName());
        }
        else {
            chosenFileLabel.setText("Nie wybrano żadnego pliku!");
            return;
        }
    }
}