/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import Persistencia.GestorPersistencia;

import javax.persistence.EntityExistsException;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**
 *
 * @author CH
 */
public class AccesoBD{ 
    
    int variable;
    float variable2;
    boolean variable3;

    public void anadirUsuarioBD(GestorPersistencia gestorPersistencia, Usuario u){
        
        try{
            gestorPersistencia.getEntityManager().persist(u);
        }catch(EntityExistsException e){
            if(gestorPersistencia.getEntityManager().getTransaction().isActive())
                gestorPersistencia.getEntityManager().getTransaction().rollback();
        }
        
    } 
    
    public void anadirPeliculaBD(GestorPersistencia gestorPersistencia,Pelicula p){
       try{
           gestorPersistencia.getEntityManager().persist(p);
       }catch(EntityExistsException e){
           if(gestorPersistencia.getEntityManager().getTransaction().isActive())
                gestorPersistencia.getEntityManager().getTransaction().rollback();
       }
       
    }
    
    public void  anadirValoracionBD(GestorPersistencia gestorPersistencia,Valoracion v){
        
        try{
            gestorPersistencia.getEntityManager().persist(v);
        }catch(EntityExistsException e){
            if(gestorPersistencia.getEntityManager().getTransaction().isActive())
                gestorPersistencia.getEntityManager().getTransaction().rollback();
        }
        
    }
    
    public void anadirValoracionesBD(GestorPersistencia gestorPersistencia, List valoraciones) {
        Iterator<Valoracion> it = valoraciones.iterator();
        Valoracion v;
        
        gestorPersistencia.getEntityManager().getTransaction().begin();
        while (it.hasNext()){
            v = it.next();
            this.anadirValoracionBD(gestorPersistencia, v);
        }
        gestorPersistencia.getEntityManager().getTransaction().commit();
    }

    public void anadirUsuariosBD(GestorPersistencia gestorPersistencia, ArrayList<Usuario> usuarios) {
        Iterator<Usuario> it = usuarios.iterator();
        Usuario u;
        
        gestorPersistencia.getEntityManager().getTransaction().begin();
        while (it.hasNext()){
            u = it.next();
            this.anadirUsuarioBD(gestorPersistencia, u);
        }
        gestorPersistencia.getEntityManager().getTransaction().commit();
    }

    public void anadirPeliculasBD(GestorPersistencia gestorPersistencia, ArrayList<Pelicula> peliculas) {
        Iterator<Pelicula> it = peliculas.iterator();
        Pelicula p;
        
        gestorPersistencia.getEntityManager().getTransaction().begin();
        while (it.hasNext()){
            p = it.next();
            this.anadirPeliculaBD(gestorPersistencia, p);
        }
        gestorPersistencia.getEntityManager().getTransaction().commit();
    }

    public long getNumPeliculasBD(GestorPersistencia gestorPersistencia){
        Query consulta = gestorPersistencia.getEntityManager().createQuery("SELECT COUNT(*) FROM Pelicula");
        return (Long) consulta.getSingleResult();
    }
    
    public long getNumUsuariosBD(GestorPersistencia gestorPersistencia){
        Query consulta = gestorPersistencia.getEntityManager().createQuery("SELECT COUNT(*) FROM Usuario");
        return (Long) consulta.getSingleResult();
    }
    
    
    //###
    public Pelicula getPeliculaBD_byPos(GestorPersistencia gestorPersistencia,long pos) {
        Query consulta = gestorPersistencia.getEntityManager().createQuery("SELECT s FROM Pelicula s WHERE s.id = :pos ").setParameter("pos", pos);
        return (Pelicula) consulta.getSingleResult();
    }
    
    public Pelicula getPeliculaBD_byID(GestorPersistencia gestorPersistencia,long id) {
        return gestorPersistencia.getEntityManager().find(Pelicula.class, id);
    }
    
    public Usuario getUsuarioBD(GestorPersistencia gestorPersistencia,long id) {
        return gestorPersistencia.getEntityManager().find(Usuario.class, id);
    }
    
    public List<Pelicula> getPeliculasBD(GestorPersistencia gestorPersistencia) {
        Query consulta = gestorPersistencia.getEntityManager().createQuery("SELECT s FROM Pelicula s ");
        return (List<Pelicula>) consulta.getResultList();
    }
    
    public double getMediaPeliculaBD(GestorPersistencia gestorPersistencia,long id){
        Query consulta = gestorPersistencia.getEntityManager().createQuery("SELECT s.media FROM Pelicula s WHERE s.idPelicula = :id").setParameter("id", id);
        return (Double) consulta.getSingleResult();
    }
    
    public List<Object> getMediasPeliculasBD_List(GestorPersistencia gestorPersistencia){
        Query consulta = gestorPersistencia.getEntityManager().createQuery("SELECT s.idPelicula,s.media FROM Pelicula s ");
        return consulta.getResultList();
    }
    
    public HashMap<Long,Double> getMediasPeliculasBD_HashMap(GestorPersistencia gestorPersistencia){
        HashMap<Long,Double> medias = new HashMap();
        
        List<Object> lista =  this.getMediasPeliculasBD_List(gestorPersistencia);
        Iterator<Object> it = lista.iterator();
        Object object[];
                
        while(it.hasNext()){
            object = (Object[]) it.next();
            
            medias.put((Long) object[0], (Double) object[1]);
            
        }
        
        return medias;
    }
    
    
    //###
    /**
     * Método para extraer los usuarios de la base de datos, de los cuales se van a predecir sus valoraciones
     * @param posIniTest Posicion (o identificador) en la base de datos desde donde se van a extraer los usuarios.
     * @param posFinTest Posicion (o identificador) en la base de datos hasta donde se van a extraer los usuarios.
     * @return Devuelve una lista de Usuarios (una ArrayList<Usuario>, concretamente).
     * @throws No se lanzan excepciones.
    */
    public List<Usuario> getUsuariosBD(GestorPersistencia gestorPersistencia, long posIniTest, long posFinTest) {
        //Nota: los ids de las peliculas deben estar comprendidos entre los parametros
        Query consulta = gestorPersistencia.getEntityManager().createQuery("SELECT s FROM Usuario s WHERE s.id >= :posIniTest AND s.id <= :posFinTest").setParameter("posIniTest", posIniTest).setParameter("posFinTest", posFinTest);
        return (List<Usuario>) consulta.getResultList();
    }
    

    public Pelicula getPeliculaBD_sinValoraciones(GestorPersistencia gestorPersistencia, long id) {
        Query consulta = gestorPersistencia.getEntityManager().createQuery("SELECT s.idPelicula, s.id, s.anio, s.titulo, s.media FROM Pelicula s WHERE s.idPelicula = :id").setParameter("id", id);
        Object object[] = (Object[]) consulta.getSingleResult();
        return new Pelicula((Long)object[0], (Long)object[1], (Integer)object[2], (String)object[3], (Double)object[4]);
    }

// ESTO VA EN LA CLASE modelo.Algoritmos. (borra la funcion antigua)
    public double similitudCoseno(Pelicula i1, Pelicula i2, ArrayList<Long> test){
        // Variables auxiliares:
        double norma1 = 0;
        double norma2 = 0;
        int val1;
        int val2;
        Long key;
        double numerador = 0;
        int comun = 0;
        // Constante de la MEJORA del Factor de relevancia
        int N = 20;
        
        // 1. Nos quedamos con la películas que tenga menos valoraciones.
        if (i1.getValoraciones().size() < i2.getValoraciones().size()){
            for (Entry<Long,Valoracion> e : i1.getValoraciones().entrySet()) {
                key = e.getKey();
                // 2. Descartamos los usuarios de la partición test.
                if (!test.contains(key)){
                    // 3. Comprobamos que la otra película haya sido valorada por el mismo usuario.
                    if (i2.getValoraciones().containsKey(key)){
                        // 4. Realizamos los cálculos de similitud.
                        val1 = e.getValue().getValor();
                        val2 = i2.getValoraciones().get(key).getValor();

                        norma1 = norma1 + val1 * val1;
                        norma2 = norma2 + val2 * val2;

                        numerador = numerador + val1 * val2;
                        ++comun;
                    }
                }
            }
        }else{
            for (Entry<Long,Valoracion> e : i2.getValoraciones().entrySet()) {
                key = e.getKey();
                if (!test.contains(key)){
                    if (i1.getValoraciones().containsKey(key)){
                        val2 = e.getValue().getValor();
                        val1 = i1.getValoraciones().get(key).getValor();

                        norma1 = norma1 + val1 * val1;
                        norma2 = norma2 + val2 * val2;

                        numerador = numerador + val1 * val2;
                        ++comun;
                    }
                }
            }
        }
        
        if (norma1 != 0 && norma2 !=0){
            double sim = numerador / (Math.sqrt(norma1) * Math.sqrt(norma2));
            // Aplicamos la MEJORA del Factor de relevancia.
            if (comun < N){
                sim = sim * ((comun*1.0)/N);
            }
            if (sim > 1){
                return 1;
            }
            return sim;
        }else{
            return 0;
        }
        
    }
    
}
