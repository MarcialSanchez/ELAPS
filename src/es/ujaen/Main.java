package es.ujaen;


import es.ujaen.controller.Controller;
import es.ujaen.model.Engine;
import es.ujaen.view.MainView;

public class Main {



    public static void main(String[] args) throws Exception {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                /*try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (        ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }*/
                Engine engine = new Engine();
                Controller controller = new Controller(engine);

            }
        });
    }

}


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