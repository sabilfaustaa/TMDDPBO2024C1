// Saya Muhamad Sabil Fausta mengerjakan evaluasi Tugas Masa Depan dalam mata kuliah
// Desain dan Pemrograman Berorientasi Objek untuk keberkahanNya maka saya
// tidak melakukan kecurangan seperti yang telah dispesifikasikan. Aamiin.

import viewmodels.MenuViewModel;
import views.MainMenu;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainMenu mainMenu = new MainMenu();
            MenuViewModel menuViewModel = new MenuViewModel(mainMenu);
            mainMenu.setVisible(true);
        });
    }
}