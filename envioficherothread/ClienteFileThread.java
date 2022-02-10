/*
 * TAREA PSP04. EJERCICIO 2.
 * Proviene del tema 3. Se deben modificar los archivos de dicha tarea para 
 * que el servidor permita trabajar de forma concurrente con varios clientes.
 *
 * El objetivo del ejercicio es crear una aplicación cliente/servidor 
 * que permita el envío de ficheros al cliente. Para ello, el cliente se 
 * conectará al servidor por el puerto 1500 y le solicitará el nombre de 
 * un fichero del servidor. Si el fichero existe, el servidor, le enviará 
 * el fichero al cliente y éste lo mostrará por pantalla. Si el fichero no 
 * existe, el servidor le enviará al cliente un mensaje de error. Una vez 
 * que el cliente ha mostrado el fichero se finalizará la conexión.
 *
 * Esta clase genera la parte correspondiente al cliente
 * RECORDAR  COMENTAR EL PACKAGE SI SE QUIERE COMPILAR FUERA DE NETBEANS.
 */
//package clientefilethread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author juang <juangmuelas@gmail.com>
 * @since 14/01/2021
 * @version 1
 */

public class ClienteFileThread {

    /**
     * Al igual que con el servidor, aprovecho la mayor parte de la estructura 
     * y ejemplo mostrados en el tema para este tipo de conexiones.
     * El funcionamiento del cliente no se ve modificado, al ser el servidor 
     * el que se encarga de procesar los hilos.
     * @param args the command line arguments
     * @param Puerto integer que indica el puerto de enlace
     * @param HOST String que indica el canal de enlace. Al ser en un equipo 
     * local, lo haremos mediante localhost.
     */
          
    static final String HOST = "localhost";
    static final int Puerto=1500;
    
    public ClienteFileThread(){
        /**
         * @param fileExist boolean para controlar
         * la existencia o no del fichero solicitado.
         */
        boolean fileExist;
             
        /**
         * try-cath para tratar la recogida y muestra de datos desde el cliente.
         * la primera parte, hasta recibir la conexión con el cliente sigue la
         * estructura del temario.
         * Flujos abreviados (en temario se crean primero variables 
         * Input/OutputStream) de entrada y salida mediante objetos 
         * DatainputStream y DataOutpuStream
         * @throws Exception para mostrar mensaje de error en su caso.
         */     
        try{            
            //Me conecto al servidor desde un determinado puerto
            Socket skCliente = new Socket( HOST , Puerto );
            DataOutputStream flujo_salida= new DataOutputStream(skCliente.getOutputStream());
            DataInputStream flujo_entrada = new DataInputStream(skCliente.getInputStream());
                     
            /**
             * @param teclado objeto clase Scanner para recogida de 
             * la entrada de datos con ruta y/o nombre del fichero por consola.
             * @param nombreFichero String recoge el nombre facilitado para 
             * pasarlo a conticuación al flujo de salida.
             */     
            Scanner teclado = new Scanner(System.in);          
            System.out.println("Indique nombre/ruta del archivo: ");
            String nombreFichero = teclado.nextLine();
            /**
             * Lo mostramos para asegurar que se ha recogido antes de mandarlo
             * al flujo de salida hacia el servidor.
             */
            System.out.println("El archivo buscado es: " + nombreFichero);
            flujo_salida.writeUTF(nombreFichero);
            /**
             * Comprobamos si existe según el booleano que nos remita el
             * servidor por el flujo de entrada y mostramos el mensaje
             * correspondiente.
             */
            fileExist = flujo_entrada.readBoolean();
            System.out.println(flujo_entrada.readUTF());
            //Si existe:
            if(fileExist == true){
                //Recogeremos el tamaño del archivo para crear un array con ese tamaño
                 /**
                  * @param longArchivo integer para longitud/tamaño archivo.
                  * @param bytes array que recoge los bytes del archivo
                  * según su tamaño.
                  */
                int longArchivo = flujo_entrada.readInt();
                int bytes[] = new int[longArchivo];
                
                /**
                 * try-cath para tratar posibles errores con el archivo 
                 * @throws IOException para mostrar mensaje de error en su caso.
                 */
                try{
                    /**
                     * @see copiaArchivo objeto de FileOutputStream para  
                     * escribir el archivo copiado.
                     */
                    FileOutputStream copiaArchivo = new FileOutputStream(nombreFichero  + "(copia)");
                    /**
                     * Mediante un for recorremos los bytes recogidos desde el 
                     * servidor y se rellena el array
                     */
                    for(int i=0;i<bytes.length;i++){
                        bytes[i]=flujo_entrada.read();
                        copiaArchivo.write(bytes[i]);
                    }
                    //Cerramos el objeto OutputSream
                    copiaArchivo.close();
                }catch(IOException ex){
                    System.out.println("Error al crear archivo");
                }                     
            }//fin if/else control exists
        /**
         * Tras tratar los datos, se cierran conexiones.
         */
        flujo_entrada.close();
        flujo_salida.close();
        System.out.println("Cerrando conexion.");
        teclado.close();
        skCliente.close();
        } catch( Exception e ) {
            System.out.println( e.getMessage() );
        }
    }//fin constructor ClienteFileThread
    public static void main(String[] args) {
        // TODO code application logic here
        new ClienteFileThread();
    } //Fin main   
}// Fin clase ClienteFileThread
