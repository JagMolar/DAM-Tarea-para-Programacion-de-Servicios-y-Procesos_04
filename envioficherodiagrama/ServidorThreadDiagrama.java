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
 * ejemplo, juan / secreta) el sistema permita Ver el contenido del 
 * directorio actual, mostrar el contenido de un determinado archivo y salir.
 * Para realizar el ejercicio primero debes crear un diagrama de estados 
 * que muestre el funcionamiento del servidor.
 * Esta clase genera la parte correspondiente al cliente
 * RECORDAR  COMENTAR EL PACKAGE SI SE QUIERE COMPILAR FUERA DE NETBEANS.
 */

//package servidorthreaddiagrama;

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
 * @since 15/01/2021
 * @version 1
 */

public class ServidorThreadDiagrama extends Thread {

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
     * @param usuario String recoge el nombre del usuario
     * @param contrasena String recoge su contraseña
     * @param fileExist boolean inicializado en false para controlar
     * la existencia o no del fichero solicitado
     */
    
    Socket skCliente;
    static final int Puerto = 1500;
    String nombreFichero, usuario, contrasena;
    boolean fileExist=false;
    
    /**
     * Inicializamos los valores que reciba el hilo
     * @param sCliente 
     */  
    
    
    public ServidorThreadDiagrama(Socket skCliente) {
        this.skCliente = skCliente;
        // TODO code application logic here
    }

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
		new ServidorThreadDiagrama(skCliente).start();
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
             * Siguiendo el contenido del punto 2.5 y 2.6 modelamos el flujo 
             * de información mediante un diagrama de estados/autómata,
             * lo hacemos a través de un entero.
             * @param estado integer que recoge ese dato (El servidor se 
             * inicia a 0, para conectar con el cliente 1, listar con 2,
             * acceso archivos 3,y salir -1)
             * Lo manejamos a través de un while para poder asumir diferentes
             * hilos en el servidor.
             */
            
            int estado = 0;
            
            while ( estado!=1 ) {
                           
                /**
                 * Recibimos el nombre del usuario y su contraseña.
                 * Al ser un ejemplo dado, no nos importa poder mostrar
                 * los datos para asegurar su recogida.
                 */
                usuario = flujo_entrada.readUTF();
                System.out.println("Nombre: " + usuario);
                contrasena = flujo_entrada.readUTF();
                System.out.println("Contraseña: " + contrasena);
                
                /**
                 * Comprobamos datos y redirigimos según resultado.
                 * Recordar que por personalización, uso de ejemplo juan.
                 */
                if (usuario.equals("juan") && contrasena.equals("secreta")) {
                    estado = 1;
                    flujo_salida.writeUTF("Usuario correcto");
                    flujo_salida.writeInt(estado);
                } else {
                    flujo_salida.writeUTF("Usuario inválido");
                    flujo_salida.writeInt(estado);
                }//Fin if-else de sesion                
            }//Fin bucle while             
            estado = 1;
            flujo_salida.writeInt(estado);

            /**
             * Recordar que los comandos son:
             * para el contenido del directorio actual "ls"
             * mostrar el contenido de un determinado archivo "get" 
             * y salir "exit".
             * 
             * Ahora que el usuario está autenticado, damos opciones.
             */
                            
            while (estado != 0 ){              
                flujo_salida.writeUTF("Indique el comando a utilizar");
                /**
                 * @param orden guarda los datos del comando solicitado
                 * @param ls guarda los datos del directorio solicitado
                 * @param carpeta objeto clase File que ayuda en la obtención
                 * de archivos que comiencen con '.' y así evitamos algunos
                 * errores que dependan de una extensión u otra.
                 * @param longArchivo integer para longitud archivoPedido.
                 */
                String orden;
                String[]ls;
                File carpeta = new File(".");
                int longArchivo;
                
                /**
                 * Mediante @if @else ordenamos el flujo según la orden
                 */
                orden = flujo_entrada.readUTF();
                if(orden.equals("ls")){
                    estado=2;
                }else{
                    if(orden.equals("get")){
                        estado=3;
                    }else{
                        if(orden.equals("exit")){
                            estado=-1;
                        }else{
                            estado=1;
                        }
                    }
                }//Fin  opciones
                
                flujo_salida.writeInt(estado);
                 /**
                  * Con todo lo anterior podemos usar un bucle switch para
                  * cada instrucción.
                  */              
                switch (estado){
                    case 2:
                        System.out.println("Mostrar listado directorio");
                        // Muestro el directorio
                        ls = carpeta.list();
                        longArchivo = ls.length;
                        flujo_salida.writeInt(longArchivo);
                        if (ls == null){
                            //Si la carpeta está vacía
                            flujo_salida.writeUTF("Directorio vacío.");
                        }else{
                            for(int i=0;i<longArchivo;i++){
                               flujo_salida.writeUTF(ls[i]); 
                            }
                        }
                        /**
                         * Retornamos a estado 1 para poder seguir a escucha
                         */
                        estado=1;
                        flujo_salida.writeInt(estado);
                        break;
                        
                    case 3:
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
                                    longArchivo = (int)archivoPedido.length();

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
                        //Reseteamos el estado a 1 para que repita el while
                        estado = 1;
                        flujo_salida.writeInt(estado);
                        break;
                        
                        case -1:
                        //Esta opción es para salir
                        System.out.println("\tEl cliente quiere salir");
                        estado =-1;
                        flujo_salida.writeInt(estado);
                        break;                                               
                }//Fin switch menú opciones
            }//Fin while opciones
            
            
            /**
             * Tras tratar los datos, se cierran conexiones y se avisa de ello.
             */
            System.out.println("Cerrando conexion.");         
            skCliente.close();              
            } catch (Exception e) {
                System.out.println( e.getMessage() );
            }
    } //Fin run()    
}//Fin clase ServidorThreadDiagrama
