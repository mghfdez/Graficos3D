package firstSteps;

    import org.lwjgl.Sys;
    import org.lwjgl.glfw.*;
    import org.lwjgl.opengl.*;

    import java.nio.ByteBuffer;

    import static org.lwjgl.glfw.Callbacks.*;
    import static org.lwjgl.glfw.GLFW.*;
    import static org.lwjgl.opengl.GL11.*;
    import static org.lwjgl.system.MemoryUtil.*;

    public class FirstDraws {

        // We need to strongly reference callback instances.
        private GLFWErrorCallback errorCallback;
        private GLFWKeyCallback   keyCallback;

        // The window handle
        private long window;

        public void run() {
            System.out.println("Hello LWJGL " + Sys.getVersion() + "!");

            try {
                init();
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

        private void init() {
            // Setup an error callback. The default implementation
            // will print the error message in System.err.
            glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));

            // Initialize GLFW. Most GLFW functions will not work before doing this.
            if ( glfwInit() != GL11.GL_TRUE )
                throw new IllegalStateException("Unable to initialize GLFW");

            // Configure our window
            glfwDefaultWindowHints(); // optional, the current window hints are already the default
            glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
            glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable

            int WIDTH =  300;
            int HEIGHT = 300;

            // Create the window
            window = glfwCreateWindow(WIDTH, HEIGHT, "Hello world!", NULL, NULL);
            if ( window == NULL )
                throw new RuntimeException("Failed to create the GLFW window");

            // Setup a key callback. It will be called every time a key is pressed, repeated or released.
            glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
                @Override
                public void invoke(long window, int key, int scancode, int action, int mods) {
                    if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                        glfwSetWindowShouldClose(window, GL_TRUE); // We will detect this in our rendering loop
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
        }

        private void loop() {
            // This line is critical for LWJGL's interoperation with GLFW's
            // OpenGL context, or any context that is managed externally.
            // LWJGL detects the context that is current in the current thread,
            // creates the ContextCapabilities instance and makes the OpenGL
            // bindings available for use.
            GLContext.createFromCurrent();

            // Set the clear color
            
            // Reset the coordinate system to center of screen
            glLoadIdentity();
            // draw a triangle centered around 0,0,0
                glBegin(GL_TRIANGLES);           // draw triangles
                glVertex3f( 0.0f, 1.0f, 0.0f);         // Top
                glVertex3f(-1.0f,-1.0f, 0.0f);         // Bottom Left
                glVertex3f( 1.0f,-1.0f, 0.0f);         // Bottom Right
            glEnd(); 

            // Run the rendering loop until the user has attempted to close
            // the window or has pressed the ESCAPE key.
            while ( glfwWindowShouldClose(window) == GL_FALSE ) {
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

                glfwSwapBuffers(window); // swap the color buffers

                // Poll for window events. The key callback above will only be
                // invoked during this call.
                glfwPollEvents();
            }
            System.out.println("Has cerrado la ventana");
        }

        public static void main(String[] args) {
            new FirstDraws().run();
        }
        
    }