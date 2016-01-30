
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 *
 * @author Typhoid
 */
public class helper {
    public static ByteBuffer string2bb(String msg, Charset charset){
    return ByteBuffer.wrap(msg.getBytes(charset));
}
  public static String bb2string(ByteBuffer buffer, Charset charset){
    byte[] bytes;
    if(buffer.hasArray()) {
        bytes = buffer.array();
    } else {
        bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
    }
    return new String(bytes, charset);
}
  
      public static int transferAsMuchAsPossible(ByteBuffer bbuf_dest, ByteBuffer bbuf_src)
{
  int nTransfer = Math.min(bbuf_dest.remaining(), bbuf_src.remaining());
  System.out.println(bbuf_dest.remaining()+" YO REMAINING");
  if (nTransfer > 0)
  {
    bbuf_dest.put(bbuf_src.array(), 
                  bbuf_src.arrayOffset()+bbuf_src.position(), 
                  nTransfer);
    bbuf_src.position(bbuf_src.position()+nTransfer);
                String v = bb2string( bbuf_dest, Charset.forName("UTF-8") );
System.out.println(v+"Yo this the string boi");
  String b = bb2string(bbuf_src, Charset.forName("UTF-8"));
  System.out.println(bbuf_dest.position());
  System.out.println("wtf man "+b);
   }
  return nTransfer;
}
  
  
  
}
