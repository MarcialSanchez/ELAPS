package es.ujaen;


import es.ujaen.controller.Controller;
import es.ujaen.model.Engine;

public class Main {



    public static void main(String[] args) throws Exception {

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {

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