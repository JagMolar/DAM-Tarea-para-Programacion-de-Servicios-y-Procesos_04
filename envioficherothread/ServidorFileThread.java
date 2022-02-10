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
 * Esta clase genera la parte correspondiente al servidor
 * RECORDAR  COMENTAR EL PACKAGE SI SE QUIERE COMPILAR FUERA DE NETBEANS.
 */
//package servidorfilethread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author juang <juangmuelas@gmail.com>
 * @since 14/01/2021
 * @version 1
 */

public class ServidorFileThread  extends Thread{

    /**
     * El hecho de pedir compartir documentos/archivos, lo interpreto como 
     * parte de una app segura y por ello,
     * hacemos las comunicaciones mediante TCP.
     * Sigo por ello, la estructura y ejemplo mostrados en el tema para 
     * este tipo de conexiones.
     * Las instrucciones piden conexión por puerto 1500, pedir nombre del 
     * fichero y ver si existe. Creamos las variables para controlarlo
     * @param skCliente Socket de enlace entre procesos
     * @param Puerto integer que indica el puerto de enlace
     * @param nombreFichero String recoge el nombre facilitado
     * @param fileExist boolean inicializado en false para controlar
     * la existencia o no del fichero solicitado
     */
    Socket skCliente;
    static final int Puerto = 1500;
    String nombreFichero;
    boolean fileExist=false;
    
    /**
     * Inicializamos los valores que reciba el hilo
     * @param sCliente 
     */   
    public ServidorFileThread(Socket sCliente ) {
        skCliente = sCliente;
    }//Fin ServidorFileTCP
    
    public static void main(String[] args) {
        /**
         * try-cath para tratar la recogida y muestra de datos desde el cliente.
         * la primera parte, hasta recibir la conexión con el cliente sigue la
         * estructura del temario.
         * @throws Exception para mostrar mensaje de error en su caso.
         */
         
        try { 
            // Inicio la escucha del servidor en un determinado puerto
            ServerSocket skServidor = new ServerSocket(Puerto);
            //Confirmamos que se recibe le puerto de escucha
            System.out.println("Escucho el puerto " + Puerto );// TODO code application logic here

            while(true){
		 // Se conecta un cliente y genera su socket
		Socket skCliente = skServidor.accept(); 
		//Confirmamos, por confianza, que se recibe.
                System.out.println("Servicio conectado a cliente...");    
		/**
                 * Atiendo al cliente mediante un thread que se inicia con start
                 */
		new ServidorFileThread(skCliente).start();
            }
        } catch (Exception e) {
            System.out.println( e.getMessage() );
        } //Fin try-catch
    }//Fin main
    
    /**
     * Run se encarga de realizar las tareas del hilo
     */
    public void run(){
        try {
            
            /**
             * Flujos abreviados (en temario se crean primero variables 
             * Input/OutputStream) de entrada y salida mediante objetos 
             * DatainputStream y DataOutpuStream
             */
            DataInputStream flujo_entrada = new DataInputStream(skCliente.getInputStream());            
            DataOutputStream flujo_salida= new DataOutputStream(skCliente.getOutputStream());  

            /**
             * Recibimos el nombre del fichero y por asegurar la 
             * recogida, lo mostramos.
             */
            nombreFichero=flujo_entrada.readUTF();
            System.out.println("Fichero solicitado: " + nombreFichero);
            /**
             * Creamos un objeto de la clase File para comprobar si existe
             * correlación con nuestros archivos.
             * @see archivoPedido recibe el flujo de entrada para asociarlo.
             * Mediante condicionales, comprobamos si existe o no.
             */
            File archivoPedido =new File(nombreFichero);
             
            if(!archivoPedido.exists()) {
                //No existe
                fileExist=false;
                flujo_salida.writeBoolean(fileExist);
                flujo_salida.writeUTF("No existen coincidencias con: " + nombreFichero);
            }else{
                //Si existe:
                if(archivoPedido.isFile()){                    
                    fileExist=true;
                    flujo_salida.writeBoolean(fileExist);
                    flujo_salida.writeUTF("Encontrada coincidencia con: " + nombreFichero);
                    /**
                     * try-cath para tratar posibles errores con el archivo 
                     * @throws IOException para mostrar mensaje de error en su caso.
                     */
                    try{ 
                        /**
                         * @see leeArchivo objeto de FileInputStream para  
                         * leer el archivo recogido.
                         */
                        FileInputStream leeArchivo = new FileInputStream(nombreFichero);
                        /**
                         * @param longArchivo integer para longitud archivoPedido.
                         */
                        int longArchivo = (int)archivoPedido.length();
  
                        flujo_salida.writeInt(longArchivo);
                        /**
                         * @param bytes array que recoge los bytes del archivo
                         * según su tamaño.
                         */
                        int bytes[]=new int[longArchivo];
                        /**
                         * @param final_archivo boolean inicializado en false
                         * para recorrer archivo hasta el final.
                         * @param contador integer local para el control 
                         * de la lectura del flujo de bytes.
                         * @param bytesArchivo integer para la lectura de bytes.
                         * Leerá hasta llegar a -1, que nos marca el punto final.
                         */
                        boolean final_archivo = false;
                        int contador = 0;
                        while(final_archivo == false){   
                            int bytesArchivo=leeArchivo.read();
                            if(bytesArchivo != -1){                               
                                bytes[contador]=bytesArchivo;
                                flujo_salida.write(bytesArchivo);
                            }else{
                            final_archivo=true;
                            }
                            contador++;
                        }
                        //Cerramos el objeto InputSream
                        leeArchivo.close();
                    }catch(IOException ex){
                        System.out.println("Error en acceso a archivo");
                    }
                    //Por asegurar que se ha llegado a este punto, lo mostramos por pantalla
                    System.out.println("Enviando archivo: " + nombreFichero);   
                /**
                 * Nos queda controlar que hacer si hay coincidencias en el 
                 * nombre pero no es un archivo.
                 */
                }else{
                    fileExist=false;
                    flujo_salida.writeBoolean(fileExist);
                    flujo_salida.writeUTF(nombreFichero + " no corresponde con ningún archivo o ruta correcta."); 
                }                
            } 
        /**
         * Tras tratar los datos, se cierran conexiones y se avisa de ello.
         */
        System.out.println("Cerrando conexion.");         
        skCliente.close();              
        } catch (Exception e) {
            System.out.println( e.getMessage() );
        }
    } //Fin run()    
} //Fin clase ServidorFileThread
