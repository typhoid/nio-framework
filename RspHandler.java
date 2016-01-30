
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import static licenseloadertest.MultiPortEcho.str_to_bb;
import static licenseloadertest.helper.helper.string2bb;

/**
 *
 * @author Typhoid
 */

public interface RspHandler  {

   

   
        
	 void connected();
void disconnected();
                void handleResponse(Object message, SocketChannel channel);

	  void handleResponse(byte[] rsp,SocketChannel socketchannel);
        
	 void write(SocketChannel sc, String message);
        
	 void waitForResponse();
    /*     public void write() throws IOException{
        
        Set selectedKeys = selector.selectedKeys();
        Iterator it = selectedKeys.iterator();
  
        while (it.hasNext()) {
            System.out.println("wut");
          SelectionKey key = (SelectionKey) it.next();
                       if ((SelectionKey.OP_WRITE)
            == SelectionKey.OP_WRITE) {
            // Read the data
            SocketChannel sc = (SocketChannel)key.channel();
           ByteBuffer bob= str_to_bb("LoginOwner~admin~password \r\n\r\n",Charset.forName("UTF-8"));  
    //   bob.flip();
           int wordk = sc.write(bob);
           System.out.println(wordk);
                       }
    }
    }
        */
}
