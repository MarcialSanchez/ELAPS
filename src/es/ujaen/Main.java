package es.ujaen;


import java.text.ParseException;

public class Main {

    public static void main(String[] args) throws Exception {

            // prints the resulting compilation unit to default system output
            //   System.out.println(cu.getChildrenNodes().size());
        try {
            Engine appEngine = new Engine("/media/Almacenamiento/Drive/Ujaen/Septimo/Proyecto/Pruebas/cutre");
            //Engine appEngine = new Engine("/media/Almacenamiento/Drive/Ujaen/Septimo/Proyecto/Pruebas/cutre");
            //Engine appEngine = new Engine("/media/Almacenamiento/Drive/Ujaen/Septimo/Proyecto/SRCPeliculasBD");
            appEngine.run();
        }catch(ParseException e){
            System.out.println("Error al Parsear el código fuente, puede deberse a un fallo de compilación.");
        }
    }

}