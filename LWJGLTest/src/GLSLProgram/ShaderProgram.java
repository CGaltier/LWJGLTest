package GLSLProgram;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.lwjgl.util.vector.Matrix2f;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Matrix4f;

public class ShaderProgram
{
  public static ShaderProgram current= null ;
  public static HashMap <String,Object> ShadersMap = new HashMap<String,Object>();
    
  private int program = -1;
  private int vertexShaderIndex=-1;
  private int fragmentShaderIndex=-1;
  private int geometryShaderIndex =-1;
  private String vertexShaderFile =null;
  private String fragmentShaderFile =null;
  private String geometryShaderFile =null;
  
  public HashMap <String,Object> uniformsMap = new HashMap <String,Object>();
  public FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
  public int attributeIndex = -1;
  public HashMap <String,Object> attributesMap = new HashMap <String,Object>();
  
  public ShaderProgram ()
  {
    program = GL20.glCreateProgram();
  }
  
  public void addShader (String filename)
  {
    ByteBuffer buffer = null ;
    int shader = 0;
    if ( ShadersMap.containsKey(filename) )
    {
      shader = (int) ShadersMap.get(filename);
      GL20.glAttachShader(program, shader);
      return ;
    }
    else
    {
      try
      {
        URL url = this.getClass().getResource(filename);
        if (url==null)
        {
          throw new Exception ("Shader program URL is null");
        }
        String [] groups = filename.split("\\.");
        String type = groups [groups.length-1];
        URLConnection connection = (URLConnection)url.openConnection();
        InputStream is = connection.getInputStream();
        buffer = ByteBuffer.allocateDirect(connection.getContentLength());
        byte [] bytearray = new byte [connection.getContentLength()];
        is.read(bytearray);
        buffer.put(bytearray);
        buffer.flip();
        
        if(type.equals("vert"))
        {
          this.vertexShaderFile = filename ;
          if (compileVertexShader (buffer))
          {
            GL20.glAttachShader(program, vertexShaderIndex);
            ShadersMap.put(filename, vertexShaderIndex);
          }
          else
          {
            System.out.println("********Start vertex shader log info********");
            System.out.println("file :"+this.vertexShaderFile);
            printInformationLog (this.vertexShaderIndex);
            System.out.println("*********End vertex shader log info*********");
          }
        }
        if (type.equals("frag"))
        {
          this.fragmentShaderFile = filename ;
          if (compileFragmentShader (buffer))
          {
            GL20.glAttachShader(program, fragmentShaderIndex);            
            ShadersMap.put(filename, fragmentShaderIndex);
          }
          else
          {
            System.out.println("********Start fragment shader log info********");
            System.out.println("file :"+this.fragmentShaderFile);
            printInformationLog (this.fragmentShaderIndex);
            System.out.println("*********End fragment shader log info*********");
          }  
        }
        if (type.equals("geom"))
        {
          this.geometryShaderFile = filename ;
          if (compileGeometryShader (buffer))
          {
            GL20.glAttachShader(program, geometryShaderIndex);            
            ShadersMap.put(filename, geometryShaderIndex);
          }
          else
          {
            System.out.println("********Start geometry shader log info********");
            System.out.println("file :"+this.geometryShaderFile);
            printInformationLog (this.geometryShaderIndex);
            System.out.println("*********End geometry shader log info*********");
          }  
        }
      }
      catch (Exception ex)
      {
        System.out.println("The following file does not exist "+filename);
      }
    }
  }

  public void printInformationLog(int ShaderIndex)
  {
    IntBuffer length = BufferUtils.createIntBuffer(1);
    GL20.glGetShader(ShaderIndex, GL20.GL_INFO_LOG_LENGTH,length);
    ByteBuffer log = BufferUtils.createByteBuffer(length.get(0));
    GL20.glGetShaderInfoLog(ShaderIndex, length, log);
    for (int i=0;i<log.capacity();++i)
    {
      System.out.print((char)log.get(i));
    }
    
  }

  public boolean compileFragmentShader(ByteBuffer buffer)
  {
    fragmentShaderIndex = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
    GL20.glShaderSource(fragmentShaderIndex, buffer);
    GL20.glCompileShader(fragmentShaderIndex);
    return (GL20.glGetShaderi(fragmentShaderIndex, GL20.GL_COMPILE_STATUS)==GL11.GL_TRUE);
  }

  public boolean compileVertexShader(ByteBuffer buffer)
  {
    vertexShaderIndex = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
    GL20.glShaderSource(vertexShaderIndex, buffer);
    GL20.glCompileShader(vertexShaderIndex);
    return (GL20.glGetShaderi(vertexShaderIndex, GL20.GL_COMPILE_STATUS)==GL11.GL_TRUE);
  }
  
  public boolean compileGeometryShader(ByteBuffer buffer)
  {
    geometryShaderIndex = GL20.glCreateShader(GL32.GL_GEOMETRY_SHADER);
    GL20.glShaderSource(vertexShaderIndex, buffer);
    GL20.glCompileShader(vertexShaderIndex);
    return (GL20.glGetShaderi(vertexShaderIndex, GL20.GL_COMPILE_STATUS)==GL11.GL_TRUE);
  }
  
  public void linkProgram ()
  {
    System.out.print(getVersion());
    GL20.glLinkProgram(program);
    IntBuffer ib=BufferUtils.createIntBuffer(1000);
    ib.position(0);
    GL20.glGetProgram(program, GL20.GL_LINK_STATUS, ib);
    System.out.print("program link status"+ib.get(0));
    if (ib.get(0)==0)
    {
      System.out.print("Link failed");
    }
  }
  
  public void bind ()
  {
    GL20.glUseProgram(program);
    ShaderProgram.current = this ;
  }
  
  public void unbind ()
  {
    GL20.glUseProgram(0);
    ShaderProgram.current = null ;
  }
  
  public void defineUniform (String name)
  {
    uniformsMap.put (name, GL20.glGetUniformLocation(program, name));
  }
  
  public void setUniform (String name, int value)
  {
    GL20.glUniform1i((int) uniformsMap.get(name), value);
  }
  
  public void setUniform (String name, int value1, int value2)
  {
    GL20.glUniform2i((int) uniformsMap.get(name), value1,value2);
  }
  
  public void setUniform (String name, int value1, int value2, int value3)
  {
    GL20.glUniform3i((int) uniformsMap.get(name), value1,value2,value3);
  }
  
  public void setUniform (String name, int value1, int value2, int value3, int value4)
  {
    GL20.glUniform4i((int) uniformsMap.get(name), value1,value2,value3,value4);
  }
  
  public void setUniform (String name, float value)
  {
    GL20.glUniform1f((int) uniformsMap.get(name), value);
  }
  
  public void setUniform (String name, float value1, float value2)
  {
    GL20.glUniform2f((int) uniformsMap.get(name), value1, value2);
  }
  
  public void setUniform (String name, float value1, float value2, float value3)
  {
    GL20.glUniform3f((int) uniformsMap.get(name), value1, value2, value3);
  }
  
  public void setUniform (String name, float value1, float value2, float value3, float value4)
  {
    GL20.glUniform4f((int) uniformsMap.get(name), value1, value2, value3, value4);
  }
  
  public void setUniform (String name, boolean transpose, Matrix4f matrix)
  {
    buffer .position(0);
    buffer.limit (16);
    matrix.store(buffer);
    buffer.position(0);
    GL20.glUniformMatrix4((int) uniformsMap.get(name), transpose, buffer );
  }
  
  public void setUniform (String name, boolean transpose, Matrix3f matrix)
  {
    buffer .position(0);
    buffer.limit (9);
    matrix.store(buffer);
    buffer.position(0);
    GL20.glUniformMatrix3((int) uniformsMap.get(name), transpose, buffer );
  }
  
  public void setUniform (String name, boolean transpose, Matrix2f matrix)
  {
    buffer .position(0);
    buffer.limit (4);
    matrix.store(buffer);
    buffer.position(0);
    GL20.glUniformMatrix4((int) uniformsMap.get(name), transpose, buffer );
  }
  
  public void setUniform (String name, boolean transpose, FloatBuffer buffer)
  {
    buffer .position(0);
    switch (buffer.capacity())
    {
      case 4:
        GL20.glUniformMatrix2((int) uniformsMap.get(name), transpose, buffer );
        break;
      case 9:
        GL20.glUniformMatrix3((int) uniformsMap.get(name), transpose, buffer );
        break;
      case 16:
        GL20.glUniformMatrix4((int) uniformsMap.get(name), transpose, buffer );
        break;
    }
  }
  
  public void defineFragOut (int index, String variableName)
  {
    GL30.glBindFragDataLocation(index, index, variableName);
  }
  
  public void bindAttribute (String attributeName)
  {
    attributeIndex++;
    attributesMap.put (attributeName, attributeIndex);
    GL20.glBindAttribLocation(program, attributeIndex, attributeName);
  }
  
  public int getAttributeIndex (String name)
  {
    return (int) attributesMap.get(name);
  }
  
  public boolean containsAttribute (String name)
  {
    return attributesMap.containsKey(name);
  }
  
  public void delete()
  {
    IntBuffer shaderCount = BufferUtils.createIntBuffer(16);
    GL20.glGetProgram(program, GL20.GL_ATTACHED_SHADERS, shaderCount);
    System.out.println("delete "+ shaderCount.get(0)+ " shaders");
    IntBuffer shaders = BufferUtils.createIntBuffer(shaderCount.get(0));
    GL20.glGetAttachedShaders(program, shaderCount, shaders);
    for (int i=0 ; i < shaderCount.get(0);++i)
    {
      GL20.glDetachShader(program, shaders.get(i));
      GL20.glDeleteShader(shaders.get(i));
    }
    GL20.glUseProgram(0);
    GL20.glDeleteProgram(program);    
  }
  
  public int getProgram ()
  {
    return program;
  }

  public static String getVersion()
  {
    return "Shader language version :"+GL11.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION);
  }
  public static int getAttributeCapacity()
  {
    return GL11.glGetInteger(GL20.GL_MAX_VERTEX_ATTRIBS);
  }
}
