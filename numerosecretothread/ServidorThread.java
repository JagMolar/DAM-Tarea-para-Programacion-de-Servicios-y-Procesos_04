/*
 * TAREA PSP04. EJERCICIO 1.
 * Proviene del tema 3. Se deben modificar los archivos de dicha tarea para 
 * que el servidor permita trabajar de forma concurrente con varios clientes.
 *
 * El objetivo del ejercicio es crear una aplicación cliente/servidor que se 
 * comunique por el puerto 2000 y realice lo siguiente:
 * El servidor debe generar un número secreto de forma aleatoria entre el 0 
 * al 100. El objetivo de cliente es solicitarle al usuario un número y 
 * enviarlo al servidor hasta que adivine el número secreto. Para ello, el 
 * servidor para cada número que le envía el cliente le indicará si es menor,
 * mayor o es el número secreto del servidor.
 *
 * Esta clase genera la parte correspondiente al servidor
 * RECORDAR  COMENTAR EL PACKAGE SI SE QUIERE COMPILAR FUERA DE NETBEANS.
 */
//package servidorthread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author juang <juangmuelas@gmail.com>
 * @since 14/01/2021
 * @version 1
 */

public class ServidorThread extends Thread{
    
    /**
     * El hecho de pedir números secretos, lo interpreto como parte de una app
     * segura y por ello, hacemos las comunicaciones mediante TCP.
     * Sigo por ello, la estructura y ejemplo mostrados en el tema para este tipo
     * de conexiones.
     * @param skCliente Socket de enlace entre procesos.
     * @param Puerto integer que indica el puerto de enlace
     * @param numeroCorrecto boolean inicializado en false para tantear las 
     * entradas desde el cliente.
     */
    
    Socket skCliente;
    static final int Puerto = 2000;   
    boolean numeroCorrecto = false;    
    
    /**
     * Inicializamos los valores que reciba el hilo
     * @param sCliente 
     */   
    
    public ServidorThread(Socket sCliente){
        skCliente = sCliente;
    }

    public static void main(String[] args) {      
        /**
         * try-cath para tratar la recogida y muestra de datos desde el cliente.
         * la primera parte, hasta recibir la conexión con el cliente sigue la
         * estructura del temario.
         * @throws Exception para mostrar mensaje de error en su caso.
         */
        try{
        // Inicio la escucha del servidor en un determinado puerto
            ServerSocket skServidor = new ServerSocket(Puerto);
            //Confirmamos que se recibe le puerto de escucha
            System.out.println("Escucho el puerto " + Puerto );
            // Espero a que se conecte un cliente y creo un nuevo socket para el cliente
            while(true){
                // Se conecta un cliente
                Socket skCliente = skServidor.accept(); 
                System.out.println("Cliente conectado");

                // Atiendo al cliente mediante un thread
                new ServidorThread(skCliente).start();
            }
        } catch (Exception e) {
            System.out.println( e.getMessage() );
        }
    }//Fin  main 
       
    /**
     * Run se encarga de realizar las tareas del hilo
     */
    public void run(){       
        /**
         * @param numAleatorio objeto de la clase Random para determinar el 
         * numero secreto.
         * Lo mostramos en consola para verificar que recoge el dato.
         */
            
        int numAleatorio = (int)Math.round(Math.random()*100);
        System.out.println("Numero secreto: " + numAleatorio);
        
        try {       
            /**
             * Flujos abreviados (en temario se crean primero variables 
             * Input/OutputStream) de entrada y salida mediante objetos 
             * DatainputStream y DataOutpuStream
             */
            DataInputStream flujo_entrada = new DataInputStream(skCliente.getInputStream());
            DataOutputStream flujo_salida= new DataOutputStream(skCliente.getOutputStream());
            
            // ATENDER PETICIÓN DEL CLIENTE
            //flujo_salida.writeUTF("Se ha conectado el cliente de forma correcta");
//            int numCliente=flujo_entrada.readInt();
//            System.out.println("Recibido numero cliente: " + numCliente + ". \n");
            //flujo_salida.writeUTF("Recibido numero cliente: " + numCliente + ". \n");
            
            while(numeroCorrecto==false){
                int numCliente = flujo_entrada.readInt();
                /**
                 * Mediante @if tratamos primero la posibilidad correcta
                 * y en el @else los posibles errores.
                 * Utilizamos marcas de colorescapando en ANSI para resaltar.
                 */
                if(numCliente==numAleatorio){
                    numeroCorrecto=true;
                    flujo_salida.writeBoolean(numeroCorrecto); 
                    //Indicar por la salida que es la opción correcta
                    flujo_salida.writeUTF("\033[36m" + numCliente + " es el numero correcto. \n");
                }else if(numCliente<numAleatorio){
                        numeroCorrecto=false;
                        flujo_salida.writeBoolean(numeroCorrecto);
                        //Indicar por la salida que es menor
                        flujo_salida.writeUTF("\033[33mEl numero "+ numCliente +" es menor que el requerido.\n");
                    }else{
                         numeroCorrecto=false;
                         flujo_salida.writeBoolean(numeroCorrecto);
                         //Indicar por la salida que es mayor
                        flujo_salida.writeUTF("\033[33mEl numero "+ numCliente +" es mayor que el requerido\n");
                    }  //Fin if/else                                   
            }//Fin while
            /**
             * Tras tratar los datos, se cierran conexiones y se avisa de ello.
             */
            System.out.println("Cerrando conexion.");
            skCliente.close();  
            System.out.println("Cliente desconectado");
        } catch( Exception e ) {
            System.out.println( e.getMessage() );
        }//Fin bloque try-catch
    } //Fin run() 
} //Fin clase ServidorThread
