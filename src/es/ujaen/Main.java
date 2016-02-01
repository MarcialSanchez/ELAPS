package es.ujaen;


import es.ujaen.view.MainView;

import javax.swing.*;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) throws Exception {

//            // prints the resulting compilation unit to default system output
//            //   System.out.println(cu.getChildrenNodes().size());
//        try {
//            //Engine appEngine = new Engine("/media/Almacenamiento/Drive/Ujaen/Septimo/Proyecto/Pruebas/cutre", "/media/Almacenamiento/Drive/Ujaen/Septimo/Proyecto/Pruebas/cutre/java");
//            //Engine appEngine = new Engine("/media/Almacenamiento/Drive/Ujaen/Septimo/Proyecto/Pruebas/WebGoat-Legacy-6.0.1", "/media/Almacenamiento/Drive/Ujaen/Septimo/Proyecto/Pruebas/WebGoat-Legacy-6.0.1/src");
//            Engine appEngine = new Engine("/media/Almacenamiento/Drive/Ujaen/Septimo/Proyecto/SRCPeliculasBD", "/media/Almacenamiento/Drive/Ujaen/Septimo/Proyecto/SRCPeliculasBD/src");
//            appEngine.run();
//        }catch(ParseException e){
//            System.out.println("Error al Parsear el código fuente, puede deberse a un fallo de compilación.");
//        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
//                try {
//                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//                } catch (        ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
//                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//                }
                MainView view = new MainView();
            }
        });
    }

}