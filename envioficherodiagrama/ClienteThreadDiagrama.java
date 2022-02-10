/*
 * TAREA PSP04. EJERCICIO 3.
 *
 * Proviene del tema 3. El objetivo del ejercicio es crear una aplicación 
 * cliente/servidor que permita el envío de ficheros al cliente. Para ello, 
 * el cliente se conectará al servidor por el puerto 1500 y le solicitará 
 * el nombre de un fichero del servidor. Si el fichero existe, el servidor, 
 * le enviará el fichero al cliente y éste lo mostrará por pantalla. Si el 
 * fichero no existe, el servidor le enviará al cliente un mensaje de error. 
 * Una vez que el cliente ha mostrado el fichero se finalizará la conexión.
 *
 * Se deben modificar los archivos de dicha tarea para 
 * que el servidor permita trabajar de forma concurrente con varios clientes.
 * A partir del ejercicio anterior crea un servidor que una vez iniciada 
 * sesión a través de un nombre de usuario y contraseña específico (por 
 * ejemplo, javier / secreta) el sistema permita Ver el contenido del 
 * directorio actual, mostrar el contenido de un determinado archivo y salir.
 * Para realizar el ejercicio primero debes crear un diagrama de estados 
 * que muestre el funcionamiento del servidor.
 * Esta clase genera la parte correspondiente al cliente
 * RECORDAR  COMENTAR EL PACKAGE SI SE QUIERE COMPILAR FUERA DE NETBEANS.
 */

//package clientethreaddiagrama;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author juang <juangmuelas@gmail.com>
 * @since 15/01/2021
 * @version 1
 */

public class ClienteThreadDiagrama {

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
    String  usuario, contrasena;
    
    public ClienteThreadDiagrama(){
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
             * @param estado integer que recoge ese dato (El servidor se 
             * inicia a 0, para conectar con el cliente 1, listar con 2,
             * acceso archivos 3,y salir -1)
             * @param orden guarda los datos del comando solicitado.
             * @param teclado objeto clase Scanner para recogida de 
             * la entrada de datos con ruta y/o nombre del fichero por consola.
             * @param nombreFichero String recoge el nombre facilitado para 
             * pasarlo a conticuación al flujo de salida.
             */
            
            int estado =0;
            String orden;
            int longArchivo=0;
            Scanner teclado = new Scanner(System.in); 
            
            //Solicitamos usuario y contraseña
            while(estado!=1){
                System.out.println("Introduzca usuario: ");
                usuario =teclado.nextLine();
                System.out.println("Introduzca contraseña: ");
                contrasena=teclado.nextLine();
                //Enviamos el nombre de usuario y contraseña mediante flujo_salida
                flujo_salida.writeUTF(usuario);
                flujo_salida.writeUTF(contrasena);
                System.out.println(flujo_entrada.readUTF());
                estado=flujo_entrada.readInt();                
            }
            estado=flujo_entrada.readInt();
            //Una vez comprobados correctamente usuario y contraseña, se nos da a elegir lo que quiere hacer
            while(estado!=-1){
                System.out.println(flujo_entrada.readUTF());
                orden=teclado.nextLine();
                flujo_salida.writeUTF(orden); 
                estado=flujo_entrada.readInt();
               //Se nos devuelve el comando transformado en estado(String a Int) para entrar en el Switch 
               switch(estado){
                   case 2:
                       //Para mostrar el contenido del directorio (ls)                    
                       longArchivo = flujo_entrada.readInt();
                       if(longArchivo==0){
                           //Si la ubicación está vacía
                            System.out.println(flujo_entrada.readUTF());
                       }else{
                            System.out.println("Contenido del directorio actual: ");
                            for(int i=0;i<longArchivo;i++){
                                System.out.println(flujo_entrada.readUTF()); 
                            }
                        }
                       estado=flujo_entrada.readInt();
                       break;
                                              
                   case 3:
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
                        
                        estado=flujo_entrada.readInt();
                        break;
                    case -1:
                         //Este es el caso en el que se desea salir
                        estado=flujo_entrada.readInt();
                        break;
                   
               }//Fin switch
            } //Fin while
            
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
    }//Fin constructor ClienteThreadDiagrama
    
    public static void main(String[] args) {
        // TODO code application logic here
        new ClienteThreadDiagrama();
    } //Fin main
    
} //Fin clase ClienteThreadDiagrama
