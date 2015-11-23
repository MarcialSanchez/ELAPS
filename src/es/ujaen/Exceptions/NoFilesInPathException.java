package es.ujaen.Exceptions;

/**
 * Created by blitzer on 20/11/15.
 */
public class NoFilesInPathException extends Exception{


        public NoFilesInPathException() {
            super();
        }

        public NoFilesInPathException(String cadena){
            super(cadena);
        }

}
