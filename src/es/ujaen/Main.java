package es.ujaen;


public class Main {

    public static void main(String[] args) throws Exception {

            // prints the resulting compilation unit to default system output
            //   System.out.println(cu.getChildrenNodes().size());
            Engine appEngine = new Engine("/media/Almacenamiento/Drive/Ujaen/Septimo/Proyecto/SRCPeliculasBD");
            appEngine.run();
    }

}