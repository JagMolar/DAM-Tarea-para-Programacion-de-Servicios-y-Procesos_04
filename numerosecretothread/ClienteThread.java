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
 * Esta clase genera la parte correspondiente al cliente
 * RECORDAR  COMENTAR EL PACKAGE SI SE QUIERE COMPILAR FUERA DE NETBEANS.
 */
//package clientethread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author juang <juangmuelas@gmail.com>
 * @since 14/01/2021
 * @version 1
 */
public class ClienteThread {
    
    /**
     * Al igual que con el servidor, aprovecho la mayor parte de la estructura 
     * y ejemplo mostrados en el tema para este tipo de conexiones.
     * @param args the command line arguments
     * @param Puerto integer que indica el puerto de enlace
     * @param HOST String que indica el canal de enlace. Al ser en un equipo 
     * local, lo haremos mediante localhost.
     */
          
    static final String HOST = "localhost";
    static final int Puerto=2000;
    
    
    public ClienteThread(){
        
        /**
         * @param numCliente integer para los datos que se introducen a consola
         * para verificar.
         * @param numeroCorrecto boolean inicializado en false para tantear las 
         * entradas desde el cliente.
         * @param seguir boolean para manejar la entrada o no de datos
         * Usamos la clase Scanner para la recogida.
         */

        int numCliente=0;             
        boolean numeroCorrecto=false;
        boolean seguir;   
        
        System.out.println("*********************************");
        System.out.println("ADIVINANDO UN NUMERO DEL 0 AL 100");
        System.out.println("*********************************");
        
        Scanner teclado = new Scanner(System.in);
        
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
            Socket sSkCliente = new Socket( HOST , Puerto );
            DataOutputStream flujo_salida= new DataOutputStream(sSkCliente.getOutputStream());
            DataInputStream flujo_entrada = new DataInputStream(sSkCliente.getInputStream());     
            /**
             * Mientras reciba un dato incorrecto, se trata de forma individual
             * la instrucción, dentro del bucle while.
             */
            while(numeroCorrecto==false){
                do{                                  
                    try{
                        /**
                         * try-cath para tratar errores de formato, por no usar
                         * enteros.
                         * @param texto String que recoge el dato para asegurar
                         * el correcto tratamiento de los errores.
                         * la primera parte, hasta recibir la conexión con el 
                         * cliente sigue la estructura del temario.
                         * @throws NumberFormatException para mostrar mensaje 
                         * de error en su caso.
                         */
                        seguir=false;    
                        //Se pide un numero (0-100)
                        System.out.println("Debe introducir un numero entre el 0 y el 100: ");
                        String texto=teclado.nextLine();
                        /**
                         * Lo parseamos para buscar errores a entero.
                         * Lo mostramos en consola para asegurar la entrada.
                         */
                        numCliente=Integer.parseInt(texto); 
                        System.out.println("Ha introducido el  " + numCliente);
                        /**
                         * Mediante @if tratamos si el número es menor/mayor
                         * al registrado desde el servidor.
                         * Utilizamos marcas de colorescapando en ANSI para 
                         * resaltar la salida en consola.
                         */
                        if(numCliente < 0){
                            System.out.println("\033[33mEl número introducido es menor que 0");
                            seguir=true;
                        }
                        if(numCliente > 100){
                            System.out.println("\033[33mEl número introducido es mayor que 100");
                            seguir=true;
                        }
                    } catch (NumberFormatException ex){
                        System.out.println("\033[31mNo ha introducido un entero!");
                        seguir=true;
                    }
                }while(seguir);
                /**
                 * Verificado, Mandamos el dato al servidor, que nos indica
                 * primero mediante un booleano si la opción era correcta o no 
                 * para seguir buscando, y segundo, el mensaje correspondiente
                 * para mostrar porla consola.
                 */
                flujo_salida.writeInt(numCliente);
                numeroCorrecto=flujo_entrada.readBoolean();
                System.out.println(flujo_entrada.readUTF());
            }  
            /**
             * Tras tratar los datos, se cierran conexiones.
             */
            teclado.close();
            flujo_entrada.close();
            flujo_salida.close();
            sSkCliente.close();
            } catch( Exception e ) {
                System.out.println( e.getMessage() );
            }
    }//Fin constructor ClienteThread
    
    public static void main(String[] args) {
        new ClienteThread();
    }//Fin main  
} //Fin clase ClienteThread
