package org.iop;


import java.io.IOException;
import java.net.URI;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class EventClient {

    static String text = "hola";

    public EventClient() {
    }


    public static void main(String[] args) {
        URI uri = URI.create("ws://192.168.1.111:15300/");
        try {
            try {


                Callable callable = new Callable() {
                    @Override
                    public Object call() throws Exception {
//                        try {
//                            session.getRemote().sendString(text);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                        return null;
                    }
                };

                ExecutorService executorService = Executors.newSingleThreadExecutor();
                while (true) {

                    Scanner scanner = new Scanner(System.in);
                    System.out.print("Introduzca texto: ");
                    text = scanner.nextLine();
                    executorService.submit(callable);

                }
                // Close session
                //session.close();
            }catch (Exception e){
                e.printStackTrace();
            }
//            finally {
//                client.stop();
//            }
        }
        catch (Throwable t) {
            t.printStackTrace(System.err);
        }
    }
}
