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
 
  public void initGL()
  {
    //GL11.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);

  }
  public void initShaders()
  {
    ShaderProgram program = new ShaderProgram();
    program.addShader("glsl/Fragment.frag");
    program.linkProgram();
    program.bind();
  }
  public void displayThings ()
  {
    /*GL11.glBegin(GL11.GL_POINTS);
    GL11.glVertex3f(0.5f,0.0f,0.0f);
    GL11.glVertex3f(0.0f,0.5f,0.0f);
    GL11.glVertex3f(0.0f,-0.5f,0.0f);
    GL11.glVertex3f(-0.5f,0.0f,0.0f);    
    GL11.glEnd();*/
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