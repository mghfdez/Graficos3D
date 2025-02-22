/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package airport;

import Utils.Matrix4f;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import static org.lwjgl.glfw.Callbacks.errorCallbackPrint;
import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.glfw.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

import org.lwjgl.opengl.GLContext;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 *
 * @author Miguel Angel Fernandez
 */
public class Main {
    
    // Valor inicial de variables
	private GLFWErrorCallback errorCallback;
    private GLFWKeyCallback keyCallback;

    private static final int WIDTH = 500;
    private static final int HEIGHT = 500;
    public static final int FLOAT_SIZE = 4;
    
    private long window;
    private int shaderProgram;
    private int uniModel;
    private Dibujable dibujables[] = new Dibujable[10];
    
    public void run() {
        System.out.println("Hello LWJGL " + Sys.getVersion() + "!. Tutorial 1");

        try {
            initGL();
            loop();

            // Release window and window callbacks
            glfwDestroyWindow(window);
            keyCallback.release();
        } finally {
            // Terminate GLFW and release the GLFWerrorfun
            glfwTerminate();
            errorCallback.release();
        }
    }
    
    static final String VertexShaderSrc = 
            "#version 130\n" +
            "\n" +
            "   in vec3 aVertexPosition;\n" +
            "   in vec3 aVertexColor;\n" +
            "\n" +
            "   out vec3 vColor;\n" +
            "\n" +
            "   uniform mat4 model;\n" +
            "   uniform mat4 projection;\n" +
            "\n" +
            "   void main() {\n" +
            "       vColor = aVertexColor;\n" +
            "       mat4 mvp = projection * model;\n" +
            "       gl_Position = mvp * vec4(aVertexPosition, 1.0);\n" +
            "   }";

    static final String FragmentShaderSrc = 
            "#version 130\n" +
            "\n" +
            "   in vec3 vColor;\n" +
            "\n" +
            "   void main() {\n" +
            "        gl_FragColor = vec4(vColor, 1.0);\n" +
            "   }";

    private void initGL() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
    	
    	dibujables[1] = new Avion(0.4f, -0.8f, 0f);
    	
        glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (glfwInit() != GL_TRUE) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure our window
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(WIDTH, HEIGHT, "Simulator by mghfdez", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                    glfwSetWindowShouldClose(window, GL_TRUE); // We will detect this in our rendering loop
                }
            }
        });

        // Get the resolution of the primary monitor
        ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        // Center our window
        glfwSetWindowPos(
                window,
                (GLFWvidmode.width(vidmode) - WIDTH) / 2,
                (GLFWvidmode.height(vidmode) - HEIGHT) / 2
        );

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
        
        GLContext.createFromCurrent();

        // Set the clear color
        glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
        
        glEnable( GL_DEPTH_TEST);
        
        // Compilar shaders
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, VertexShaderSrc);
        glCompileShader(vertexShader);
        int status = glGetShaderi(vertexShader, GL_COMPILE_STATUS);
        if (status != GL_TRUE) {
            throw new RuntimeException(glGetShaderInfoLog(vertexShader));
        }
        
        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, FragmentShaderSrc);
        glCompileShader(fragmentShader);
        status = glGetShaderi(fragmentShader, GL_COMPILE_STATUS);
        if (status != GL_TRUE) {
            throw new RuntimeException(glGetShaderInfoLog(vertexShader));
        }

        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);
        glUseProgram(shaderProgram);
    }

    private void loop() {
    
        //OpenGL es una maquina de estados
    	/// Create a FloatBuffer of vertices
        /// Do not forget to do vertices.flip()! This is important, because passing the buffer without 
        // flipping will crash your JVM because of a EXCEPTION_ACCESS_VIOLATION.
        
    	uniModel = glGetUniformLocation(shaderProgram, "model");
        
        int uniProjection = glGetUniformLocation(shaderProgram, "projection");
        float ratio = (float)WIDTH / (float)HEIGHT;
        Matrix4f projection = Matrix4f.perspective(60, ratio, 0.1f, 100.0f );
        glUniformMatrix4(uniProjection, false, projection.getBuffer());
        
        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        Aeropuerto aeropuerto = new Aeropuerto();
        
        while (glfwWindowShouldClose(window) == GL_FALSE) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
            
            
            dibujables[1].Draw(shaderProgram, uniModel);
            /* Swap buffers and poll Events */
            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();  
        }
    }
    public static void main(String[] args) {
            new Main().run();
        }
	 
}