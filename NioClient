
/**
 *
 * @author Typhoid
 */
// to do: an auto growing bytebuffer
import licenseloadertest.socket.handler.RspHandler;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.Charset;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import static licenseloadertest.helper.helper.*;

public class NioClient implements Runnable {
	// The host:port combination to connect to
	private InetAddress hostAddress;
	private int port;

	// The selector we'll be monitoring
	private Selector selector;

	// The buffer into which we'll read data when it's available
	private ByteBuffer readBuffer = ByteBuffer.allocate(8192);
//storage buffer
              private ByteBuffer storage = ByteBuffer.allocate(9000);

	// A list of PendingChange instances
	private List pendingChanges = new LinkedList();

	// Maps a SocketChannel to a list of ByteBuffer instances
	private Map pendingData = new HashMap();
	
	// Maps a SocketChannel to a RspHandler
	private Map rspHandlers = Collections.synchronizedMap(new HashMap());
	private SocketChannel socket;
        
        
        private static final Object Waiter = new Object();
private static boolean wait = false;
	public NioClient(InetAddress hostAddress, int port) throws IOException {
		this.hostAddress = hostAddress;
		this.port = port;
		this.selector = this.initSelector();
	}
public boolean connect(RspHandler handler) {
    // Start a new connection
try{	System.out.println("what");
    socket = this.initiateConnection();
//3	
    System.out.println("what");	
		// Register the response handler
		this.rspHandlers.put(socket, handler);
		
    // Check if we're connected or if connection is pending. 9/10 sure if its not pending then it diggidly failed 
    System.out.println( socket.isConnected()+"lol"+ socket.isConnectionPending() );
    
    
    
           /*	this.selector.select();

				// Iterate over the set of keys for which events are available
				Iterator selectedKeys = this.selector.selectedKeys().iterator();
				while (selectedKeys.hasNext()) {
					SelectionKey key = (SelectionKey) selectedKeys.next();
					selectedKeys.remove();
        System.out.println("WHAT THE FUCK MAN");
                                        if (key.isValid()) {
		//System.out.println("invalid");*/
                 //   SocketChannel sc = (SocketChannel)key.channel();
                    SelectionKey key =socket.keyFor(this.selector);
                    if(key.isValid()&& socket.isConnectionPending()) {
                        System.out.println("Valid key and connection pending");
              //    if(key.isConnectable()){
                      System.out.println("Am i cool yet mom");
                      return finishConnection(key);
              //    }
                               
                           } 
                            
    //   bob.flip();
  
                
					                   
                  
    
    
    
    return  socket.isConnected();
}
          catch(IOException ex){
              //some shot happened yo
                        return false;
                        }
}


//will try to use this for a timeout later
/* final long startTime = System.currentTimeMillis();
    log(startTime, "calling runWithTimeout!");
    try {
      TimeLimitedCodeBlock.runWithTimeout(new Runnable() {
        @Override
        public void run() {
          try {
            log(startTime, "starting sleep!");
            Thread.sleep(10000);
            log(startTime, "woke up!");
          }
          catch (InterruptedException e) {
            log(startTime, "was interrupted!");
          }
        }
      }, 5, TimeUnit.SECONDS);
    }
    catch (TimeoutException e) {
      log(startTime, "got timeout!");
    }
    log(startTime, "end of main method!");
  }

  private static void log(long startTime, String msg) {
    long elapsedSeconds = (System.currentTimeMillis() - startTime);
    System.out.format("%1$5sms [%2$16s] %3$s\n", elapsedSeconds, Thread.currentThread().getName(), msg);
  }*/
private boolean middleConnection(SocketChannel socket) throws IOException{
     SelectionKey key =socket.keyFor(this.selector);
                    if(key.isValid()&& socket.isConnectionPending()) {
                  if(key.isConnectable()){
                      System.out.println("Am i cool yet mom");
                      //connection is pending and she's connectable.Let's finish up with the money shot
                     return  finishConnection(key);
                      
                  }
                  else{key.interestOps(SelectionKey.OP_CONNECT);
                 return middleConnection(socket);
                 // lets see if she's ready this time!
                      
                  }
                               
                           } else return socket.isConnected(); //already connected or failed yolo
     
                    
                    
}
	protected void send(byte[] data) throws IOException {
            System.out.println("WE sendin some shit boi dw" + new String(data,Charset.forName("UTF-8")));
		// Start a new connection
	//	SocketChannel socket = this.initiateConnection();
		
		// Register the response handler
		//this.rspHandlers.put(socket, handler);
		
		// And queue the data we want written
		synchronized (this.pendingData) {
			List queue = (List) this.pendingData.get(socket);
			if (queue == null) {
				queue = new ArrayList();
				this.pendingData.put(socket, queue);
			}
			queue.add(ByteBuffer.wrap(data));
		}
synchronized(this.pendingChanges) {
			this.pendingChanges.add(new ChangeRequest(socket, ChangeRequest.CHANGEOPS, SelectionKey.OP_WRITE));
		}
		// Finally, wake up our selecting thread so it can make the required changes
		this.selector.wakeup();
/*this.selector.select();

				// Iterate over the set of keys for which events are available
				Iterator selectedKeys = this.selector.selectedKeys().iterator();
				while (selectedKeys.hasNext()) {
					SelectionKey key = (SelectionKey) selectedKeys.next();
					selectedKeys.remove();
        System.out.println("WHAT THE FUCK MAN");
                                        if (key.isValid()) {                        key.interestOps(SelectionKey.OP_WRITE);
                                        }
                                }
  */
                }

	public void run() {
		while (true) {
			try {
				// Process any pending changes
				synchronized (this.pendingChanges) {
					Iterator changes = this.pendingChanges.iterator();
					while (changes.hasNext()) {
						ChangeRequest change = (ChangeRequest) changes.next();
						switch (change.type) {
						case ChangeRequest.CHANGEOPS:
							SelectionKey key = change.socket.keyFor(this.selector);
							key.interestOps(change.ops);
							break;
						case ChangeRequest.REGISTER:
							change.socket.register(this.selector, change.ops);
							break;
						}
					}
					this.pendingChanges.clear();
				}

				// Wait for an event one of the registered channels
				this.selector.select();

				// Iterate over the set of keys for which events are available
				Iterator selectedKeys = this.selector.selectedKeys().iterator();
				while (selectedKeys.hasNext()) {
					SelectionKey key = (SelectionKey) selectedKeys.next();
					selectedKeys.remove();

					if (!key.isValid()) {
                                            
						continue;
					}

					// Check what event is available and deal with it
					if (key.isConnectable()) {
						this.finishConnection(key);
					} else if (key.isReadable()) {
						this.read(key);
					} else if (key.isWritable()) {
						this.write(key);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void read(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();

		// Clear out our read buffer so it's ready for new data
		this.readBuffer.clear();

		// Attempt to read off the channel
		int numRead;
		try {
			numRead = socketChannel.read(this.readBuffer);
		} catch (IOException e) {
			// The remote forcibly closed the connection, cancel
			// the selection key and close the channel.
			key.cancel();
			socketChannel.close();
			return;
		}

		if (numRead == -1) {
			// Remote entity shut the socket down cleanly. Do the
			// same from our end and cancel the channel.
		//	key.channel().close();
		//	key.cancel();
			return;
		}

		// Handle the response
		//this.handleResponse(socketChannel, this.readBuffer.array(), numRead);
stringDecoder(cloneByteBuffer(this.readBuffer),socketChannel, numRead);
        }

        
        private static ByteBuffer cloneByteBuffer(final ByteBuffer original) {
    // Create clone with same capacity as original.
    final ByteBuffer clone = (original.isDirect()) ?
        ByteBuffer.allocateDirect(original.capacity()) :
        ByteBuffer.allocate(original.capacity());

    // Create a read-only copy of the original.
    // This allows reading from the original without modifying it.
    final ByteBuffer readOnlyCopy = original.asReadOnlyBuffer();

    // Flip and read from the original.
    readOnlyCopy.flip();
    clone.put(readOnlyCopy);

    return clone;
}
        
	private void handleResponse(SocketChannel socketChannel, byte[] data, int numRead) throws IOException {
		// Make a correctly sized copy of the data before handing it
		// to the client
		byte[] rspData = new byte[numRead];
		System.arraycopy(data, 0, rspData, 0, numRead);
		
		// Look up the handler for this channel
		RspHandler handler = (RspHandler) this.rspHandlers.get(socketChannel);
		
		// And pass the response to it
		handler.handleResponse(rspData,socketChannel);
			// The handler has seen enough, close the connection
			//socketChannel.close();
			//socketChannel.keyFor(this.selector).cancel();
		
	}
    protected void write(byte[] bytes) throws IOException{
        
        Set selectedKeys = selector.selectedKeys();
        Iterator it = selectedKeys.iterator();

        while (it.hasNext()) {
            System.out.println("wut");
          SelectionKey key = (SelectionKey) it.next();
          if (!key.isValid()) {
						continue;
					}
                       if (key.isWritable()) {
            // Read the data
            SocketChannel sc = (SocketChannel)key.channel();
     //      ByteBuffer bob= str_to_bb("LoginOwner~admin~password \r\n\r\n",Charset.forName("UTF-8"));  
    //   bob.flip();
           int wordk = sc.write(ByteBuffer.wrap(bytes));
           System.out.println(wordk);
                       }
    }
    } 
    
    protected void write(String message) throws IOException{

 int writed = 0;

          	this.selector.select();

				// Iterate over the set of keys for which events are available
				Iterator selectedKeys = this.selector.selectedKeys().iterator();
				while (selectedKeys.hasNext()) {
					SelectionKey key = (SelectionKey) selectedKeys.next();
					selectedKeys.remove();
        System.out.println("WHAT THE FUCK MAN");
                                        if (key.isValid()) {
		//System.out.println("invalid");
                    SocketChannel sc = (SocketChannel)key.channel();
         ByteBuffer bob= string2bb(message +" \r\n\r\n",Charset.forName("UTF-8"));  
    //   bob.flip();
           writed = sc.write(bob);
           System.out.println(writed+" bob");
           if(writed == 0) {key.interestOps(SelectionKey.OP_WRITE);
           write(message);
           }
                break;
					}                      
                  
    }
                           

    }
	private void write(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();

		synchronized (this.pendingData) {
			List queue = (List) this.pendingData.get(socketChannel);
                                System.out.println("We at the socketbuffer");

			// Write until there's not more data ...
			while (!queue.isEmpty()) {
				ByteBuffer buf = (ByteBuffer) queue.get(0);
                                System.out.println("We at the socketbuffer"+ bb2string(buf, Charset.forName("UTF-8")));
                                
				socketChannel.write(buf);
				if (buf.remaining() > 0) {
					// ... or the socket's buffer fills up
					break;
				}
				queue.remove(0);
			}

			if (queue.isEmpty()) {
				// We wrote away all data, so we're no longer interested
				// in writing on this socket. Switch back to waiting for
				// data.
				key.interestOps(SelectionKey.OP_READ);
			}
		}
	}

	private boolean finishConnection(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();
	
		// Finish the connection. If the connection operation failed
		// this will raise an IOException.
		try {
			socketChannel.finishConnect();
		} catch (IOException e) {
			// Cancel the channel's registration with our selector
			System.out.println(e);
			key.cancel();
			return false;
		}
	if(socketChannel.isConnected())
        {RspHandler handler = (RspHandler) this.rspHandlers.get(socketChannel);
        handler.connected();
        System.out.println("Is connected");
        // Register an interest in writing on this channel
		key.interestOps(SelectionKey.OP_WRITE);
                return true;
        }else return false;
        //We are no longer waiting for this  function to fire.
        //unlockWaiter();
           
	}

	private  SocketChannel initiateConnection() throws IOException {
		// Create a non-blocking socket channel
		SocketChannel socketChannel = SocketChannel.open();
		socketChannel.configureBlocking(false);
                
		// Kick off connection establishment
		socketChannel.connect(new InetSocketAddress(this.hostAddress, this.port));
	
		// Queue a channel registration since the caller is not the 
		// selecting thread. As part of the registration we'll register
		// an interest in connection events. These are raised when a channel
		// is ready to complete connection establishment.
                //yeah we dont want to queue it TBH FUCK MAN 
	/*	synchronized(this.pendingChanges) {
			this.pendingChanges.add(new ChangeRequest(socketChannel, ChangeRequest.REGISTER, SelectionKey.OP_CONNECT));
		}
	*/
        
        //Lets just register it instead man fucck
        System.out.println("WHAT");
        this.selector.wakeup();

                	socketChannel.register(this.selector, SelectionKey.OP_CONNECT);


		return socketChannel;
	}

	private Selector initSelector() throws IOException {
		// Create a new selector
		return SelectorProvider.provider().openSelector();
	}
 
             private static final byte DELIMITERMAC = (byte) '\r';
        private static final byte DELIMITERUNIX = (byte) '\n';
        private static final byte DELIMITERNUL = (byte) '\0';

        private int matchCount = 0;
        private int position = 0;
        private int limit = 0;
        private int oldLimit = 0;
        private int oldPosition = 0;
        private int oldRemaining = 0;
        byte[] temp;
        ByteBuffer copy;
       private  void stringDecoder(ByteBuffer in,SocketChannel channel, int numRead){
//REWIND THE VHS SO WE CAN WATCH IT AGAIN LOL 
in.flip();


// This seperates the line thereby seperating our commands

position = 0;
oldPosition = in.position();
oldLimit = in.limit();
oldRemaining = in.remaining();
System.out.println(in.remaining());
System.out.println("We are beginnig ze loop");
// create a destination array that is the size of the two arrays
//byte[] destination; 
        //new byte[ciphertext.length + mac.length];
//byte[] worker;
// copy ciphertext into start of destination (from pos 0, copy ciphertext.length bytes)
//System.arraycopy(ciphertext, 0, destination, 0, ciphertext.length);

// copy mac into end of destination (from pos ciphertext.length, copy mac.length bytes)
//System.arraycopy(mac, 0, destination, ciphertext.length, mac.length);
System.out.println(DELIMITERMAC);
System.out.println(DELIMITERUNIX);
System.out.println(DELIMITERNUL);
// String yo = new String(b);
//String(b,Charset.forName("UTF-8"));
while (in.hasRemaining()) {
       
            byte b = in.get();    
            String yu=  Byte.toString(b);
           
            System.out.println(b + " " +  (char) b);
            boolean matched = false;


            switch (b) {
            case '\r':
                // Might be Mac EOL or some other gay shit
      
                matchCount++;
                break;

            case '\n':
                // UNIX
                matchCount++;
                matched = true;
                break;
          case '\0':
                //NULL/ xml sock EOL
                matchCount++;
                matched = true;
                break;
            default:
                matchCount = 0;
            }
            if(matched){//get current position in the loop!
                if(in.limit() > oldLimit){
                    System.out.println("YO WHAT HOW LOL");
                }
                System.out.println("THIS IS WHAT REMAINS:" + in.remaining());
                System.out.println("OLD LIMIT: "+ oldLimit + "NEW LIMIT :" +in.limit());
                               position =   in.position();
                               in.limit(position);
                               in.position(oldPosition);
                               temp = new byte[in.remaining()];
                               in.duplicate().get(temp, 0, temp.length);     
                               in.limit(oldLimit);
                               in.position(position);
                               oldPosition = position;
                                ByteBuffer lit = ByteBuffer.wrap(temp, 0, temp.length);
                System.out.println("it found"+ matchCount);
                     int n =  transferAsMuchAsPossible(storage,lit);
System.out.println(n+"  doin it "); 
              stringHandler(bb2string(storage,Charset.forName("UTF-8")),channel);
                             storage.clear();
               storage.put(new byte[1024]);
             storage.clear();
                             //  limit = in.limit();
                             //set position to the old position and the limit to the position in the loop 
                             // so we can get the chunk of bytes after the previous match  but before the current match!
                 
                             //   in.limit(position);
                         //    in.position(oldPosition);
                         
                               //gotta get the shit from befoare
                               //to put it in but idk how  i wanna do it rn

                               //REWIND THE TAPE SO WE CAN WATCH IT AGAIN LOL
                              // in.flip();
//byte[] source = {};
/*
System.out.println("OLD position: "+ oldPosition);
System.out.println("Position: "+ position);
System.out.println("position vs old position: "+(position - oldPosition) + "yo" + (position - (oldPosition +1)));
//should create a  byte array thats the same size as the chunk we want 
// aka after old position and before current position


//ByteBuffer tony = in.duplicate();
//tony.flip();
//tony.position(oldPosition);
//tony.limit(position);
//tony.position(oldPosition);

//buff.position(oldPosition);
//buff.limit(position);

temp = new byte[buff.remaining() ];
          System.out.println("CHAR of temp after creation" + Arrays.toString(temp));
          System.out.println("temp.length: " + temp.length);
System.out.println("Tony limit: "+ buff.limit());
System.out.println("Tony position: "+ buff.position());
int yolo = position - oldPosition;
System.out.println("Heres yolo ugh: "+ yolo+" YEY WHAT"+ (temp.length - yolo));
    
//tony.duplicate().get(temp, oldPosition, temp.length );
          System.out.println(temp.length + " :length of source");
          System.out.println("CHAR" + Arrays.toString(temp));
System.out.println("ACTUAL VALUE:" +new String(temp));
         //     if (in.hasArray()){
        //        byte[] old = in.array();
       //         byte[] New = new byte[]{};
       //       System.arraycopy(old,0,New,0,position);
          //  System.out.println(  New.length+ " : length of new");
           // }
              ByteBuffer lit = ByteBuffer.wrap(temp, 0, temp.length);
                System.out.println("it found"+ matchCount);
                     int n =  transferAsMuchAsPossible(storage,lit);
System.out.println(n+"  doin it "); 
              stringHandler(bb2string(storage,Charset.forName("UTF-8")),channel);
               
             //  RspHandler handler = (RspHandler) this.rspHandlers.get(channel);
             //  handler.handleResponse(line,channel);
               storage.clear();
               storage.put(new byte[1024]);
             storage.clear();
       //        in.compact();
       System.out.println("position substracted by limit"+( position - limit));
       System.out.println("WHERE ARE YOU?");
//dont touch the in bytebuffer i dont think is wise idk l0l :(     
//           in.position(position);
      //          in.compact();
          //      in.rewind();
                //in.limit(limit);
                oldPosition = position;
          //      in.position(position);
        //        in.limit(oldLimit);
                System.out.println("THIS IS THE FUCKING LIMIT IDIOT " + in.limit());
*/
   }        
   
}

//set to the matched position.
in.position(position);
in.compact();
         in.flip();
          //  ByteBuffer in1 = in.slice();
     int n =  transferAsMuchAsPossible(storage,in);
System.out.println(n+"  doin it "); 
System.out.println(bb2string(storage,Charset.forName("UTF-8")));

       }   
     
       
      private void  stringHandler(String message, SocketChannel channel   ){
           System.out.println("String handler message:"+ message);
           
           //remove delimiters
                           message = message.replaceAll("\n", "");
                message = message.replaceAll("\r", "");
                                message = message.replaceAll("\0", "");
                                //just in case i guess
                                System.out.println("We're at the stringhandler");
message = message.replaceAll("[\n\r]", "");
RspHandler handler = (RspHandler) this.rspHandlers.get(channel);
              handler.handleResponse(message,channel);
       }
     
     /*	public static void main(String[] args) {
		try {
			NioClient client = new NioClient(InetAddress.getByName("127.0.0.1"), 1234);
			Thread t = new Thread(client);
			//t.setDaemon(true);
			t.start();
			RspHandler handler = new RspHandler(client.this);
			client.send("LoginOwner~admin~password \r\n\r\n".getBytes(), handler);
			handler.waitForResponse();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}*/
      public boolean isConnected(){
                System.out.println("boolean isconnected"); 
    return socket.isConnected();
      }
      private static void unlockWaiter() {
  synchronized (Waiter) {
    wait = false;
System.out.println("waited done done done");  
Waiter.notifyAll(); // unlock again
  }
}
      public static  void waitForConnection() {
  wait = true;  
 synchronized (Waiter) {
  while (wait) {
  
      try {//socket.wait();
         // System.out.println(socket.isConnectionPending());
       Waiter.wait(); // wait until notified
      } catch (Exception e) {}
    }
  }
}
      
      
}
