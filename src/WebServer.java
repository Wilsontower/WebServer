import java.net.*;

public class WebServer{

    public static void main(String args[]) {

        int client_id = 1;
        int PORT = 8080;
        ServerSocket server=null;
        Socket client=null;

        try{
            server=new ServerSocket(PORT);
            System.out.println("Web Server is listening on port："+server.getLocalPort());
            //socket始终保持监听状态
            while(true) {
                client=server.accept();
                new HttpServer(client,client_id).start();
                client_id++;
            }
        }catch(Exception e) {
            System.out.println(e);
        }
    }
}
