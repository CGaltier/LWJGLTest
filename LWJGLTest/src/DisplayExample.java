import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import GLSLProgram.ShaderProgram;

public class DisplayExample 
{
  public ByteBuffer readShader(String filename)
  {
    ByteBuffer buffer = null;
    URL url = this.getClass().getResource(filename);
    URLConnection connection;
    try
    {
      connection = (URLConnection)url.openConnection();
      java.io.InputStream is = connection.getInputStream();
      buffer = ByteBuffer.allocateDirect(connection.getContentLength());
      byte [] b = new byte [connection.getContentLength()];
      is.read (b);
      buffer.put(b);
      buffer.flip();
      
    } catch (IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
      Logger.getLogger(DisplayExample.class.getName()).log(Level.SEVERE,null,e);
    }
    
    return buffer;
  }
  
  public void initGL()
  {
    GL11.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);

  }
  public void initShaders()
  {
    ShaderProgram program = new ShaderProgram();
  }
  public void displayThings ()
  {
    GL11.glBegin(GL11.GL_POINTS);
    GL11.glVertex3f(0.5f,0.0f,0.0f);
    GL11.glVertex3f(0.0f,0.5f,0.0f);
    GL11.glVertex3f(0.0f,-0.5f,0.0f);
    GL11.glVertex3f(-0.5f,0.0f,0.0f);    
    GL11.glEnd();
  }
	public void start() 
	{
		try 
		{
		  DisplayMode mode = Display.getDesktopDisplayMode();
		  int width = mode.getWidth();
		  int height = mode.getHeight();
		  int frequency = mode.getFrequency();
		  int bits = mode.getBitsPerPixel();
			Display.setDisplayMode(new DisplayMode(800,600));//windowed
		  //Display.setFullscreen(true);//remove borders
		  //Display.setDisplayMode(mode);//fullscreen
			Display.create();
			initShaders();
			initGL();
		} 
		catch (LWJGLException e) 
		{
			e.printStackTrace();
			Logger.getLogger(DisplayExample.class.getName()).log(Level.SEVERE,null,e);
			System.exit(0);
		}
		
		// init OpenGL here
		
		while (!Display.isCloseRequested()) 
		{
		  GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		  displayThings ();
	    
		  if (Keyboard.next())
		  {
		    if(Keyboard.getEventKey()==Keyboard.KEY_ESCAPE)
		    {
		      break;
		    }
		  }
		  // render OpenGL here
			Display.update();
		}
		
		Display.destroy();
	}
	
	public static void main(String[] argv) 
	{
		DisplayExample displayExample = new DisplayExample();
		displayExample.start();
	}
}