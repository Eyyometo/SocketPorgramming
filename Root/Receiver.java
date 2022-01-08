package Root;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class Receiver extends Root{
    Socket link = null;
    Scanner input;
    PrintWriter output;


    public Receiver(int PORT) {
        this.openListenPort(PORT);
    }


    @Override
    public void run() {
        while (true) {            
            try {
                //Port dinlemeye a��l�yor
                link = serverSocket.accept();
                //Gelen bilgi
                input = new Scanner(link.getInputStream()); //Step 3.
                //Sockete yazd�rma gibi d���n ��kt�
                output = new PrintWriter(link.getOutputStream(), true); //Step 3.
                //Thread olu�turuluyor
                Thread th = new ClientHandler(link,input,output);
                //Thread ba�lat�l�yor
                th.start();
            } catch (IOException ex) {
                System.err.println("Root.Receiver.startService()");
            }
        }
       
    }
    
    
    class ClientHandler extends Thread
    {
        final Scanner input;
        final PrintWriter output;
        final Socket link;

        // Constructor
        public ClientHandler(Socket s, Scanner dis, PrintWriter dos) {
            this.link   = s;
            this.input  = dis;
            this.output = dos;
        }


        @Override
        public void run()
        {
            try {
                handleRouter();
            } catch (IOException ioEx) {
                ioEx.printStackTrace();
            } finally {
                try {
                    //Ba�lant� kapatal�yor
                    System.out.println("\n* Closing connections (Receiver side)*");
                    link.close();
                } catch (IOException ioEx) {
                    //E�er ba�lant� kapat�lamazsa hata veriyor
                    System.out.println("Unable to disconnect!");
                    System.exit(1);
                }
            }
        }
        private void handleRouter() throws IOException {

            //Paketten gelen bilgi mesaj�
            String message;
            do {
                message = this.getMessage();
                this.sendMessage("ACK"+ message.substring(message.length() - 1));
                System.out.println(" - " +message);
                //Gelen mesaj bitip bitmedi�ini kontrol ediyor
                //Son gelen mesaj close olarak geliyor
            } while (!message.equals("***CLOSE***"));

        }

        //Pakette gelen mesaj� okuma fonksiyonu
        private String getMessage(){
            while (true) {            
                if (input.hasNext()) {
                    return input.nextLine();
                }
            }
        }

        private void sendMessage(String message){
             output.println(message);
        }
    }
}
