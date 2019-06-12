import java.io.*;
import java.net.Socket;

//HttpServer继承子Thread类
public class HttpServer extends Thread {

    public Socket client = null; // 连接Web浏览器的socket
    public int counter = 0; // 计数器

    public HttpServer(Socket cl , int c) {
        client=cl;
        counter=c;
    }

    public void run()
    {
        try{
            String destIP=client.getInetAddress().toString(); // 客户机IP地址
            int destPort=client.getPort(); // 客户机端口号

            System.out.println("Connection times "+counter);
            System.out.println("connected to "+destIP+" on port "+destPort+".");
            PrintStream outStream=new PrintStream(client.getOutputStream());
            DataInputStream inStream=new DataInputStream(client.getInputStream());
            String inline=inStream.readLine();
            System.out.println("Received:"+inline);
            if(!inline.equals("null")){
                if (get_request_type(inline)) {

                    String filename=get_file_name(inline);
                    File file=new File(filename);

                    if (file.exists()) { // 若文件存在，则将文件送给Web浏览器
                        System.out.println(filename+" requested.");
                        //发送HTML的head信息
                        outStream.println("HTTP/1.0 200 OK");
                        outStream.println("MIME_version:1.0");
                        outStream.println("Content_Type:text/html");
                        int len=(int)file.length();
                        outStream.println("Content_Length:"+len);
                        outStream.println("");
                        sendFile(outStream,file); // 发送文件
                        outStream.flush();
                    } else { // 文件不存在时,返回error.html错误页面

                        String fileName="error.html";
                        File file1=new File(fileName);

                        System.out.println(filename+" requested.");
                        //输出HTML的头信息
                        outStream.println("HTTP/1.0 200 OK");
                        outStream.println("MIME_version:1.0");
                        outStream.println("Content_Type:text/html");
                        int len=(int)file.length();
                        outStream.println("Content_Length:"+len);
                        outStream.println("");
                        sendFile(outStream,file1);
                        outStream.flush();
                    }
                }
            }

            //设置延时，等待文件传送完毕
            long timer=1;
            while (timer<11100000)
            {
                timer++;
            }
            //关闭客户端socket
            client.close();
        }catch(IOException e) {
            System.out.println("Exception:"+e);
        }
    }
    /* 获取请求类型是否为“GET” */
    boolean get_request_type(String s) {
        if (s.length()>0)
        {
            if(s.substring(0,3).equalsIgnoreCase("GET"))
                return true;
        }
        return false;
    }

    /* 获取要访问的文件名 */

    String get_file_name(String s) {
    /*get请求的第一行信息格式为：“GET /books/?name=Professional%20Ajax HTTP/1.1”
      String.substring(int i)方法是从第i个字符开始取，取出后面所有的字符
      String.substring(int begin,int end)方法是取出从begin到end的所有字符
      */
        String file_name = s.substring(s.indexOf(' ')+1);//这一条是把get后面所有的字符串取出来
        file_name = file_name.substring(0,file_name.indexOf(' '));

        try{
            if(file_name.charAt(0)=='/')
                file_name=file_name.substring(1);
        }catch(StringIndexOutOfBoundsException e) {
            System.out.println("Exception:"+e);
        }
        if (file_name.equals("")) {
            file_name="index.html";
        }
        return file_name;
    }

    /*把指定文件发送给Web浏览器 */

    void sendFile(PrintStream outs,File file){
        try{
            DataInputStream in=new DataInputStream(new FileInputStream(file));
            int len=(int)file.length();
            byte buf[]=new byte[len];

            in.readFully(buf);
            outs.write(buf,0,len);
            outs.flush();
            in.close();
        }catch(Exception e){
            System.out.println("Error retrieving file.");
            System.exit(1);
        }
    }
}
