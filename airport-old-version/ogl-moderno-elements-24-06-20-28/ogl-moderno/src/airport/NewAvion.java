package airport;


import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
 
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import airport.ModelLoader;
import Utils.CubeModel;
import Utils.Matrix4f;
import Utils.OpenGLHelper;
import Utils.PlaneModel;
import Utils.ShaderProgram;
import Utils.SphereModel;
import Utils.Texture;

public class NewAvion {
    
	private ModelLoader fmodel;
	
	private float count = 0.0f;
    private float count2 = 0.0f;
    private float count3 = 0.0f;
    
    public float posX = 0.0f;
    public float posY = 0.0f;
    public float posZ = 0.0f;
    private float speed = 0.0f;
    
    int vbo_v;
    int vbo_n;
    int vbo_c;
    //int vbo_t;
    int vbo_e;
    
    //private Texture texture;
    
    float x, y, z;
    float sx, sy, sz;
    float rx, ry, rz, angle;
        

    public NewAvion() {
        
    	
    	fmodel = new ModelLoader("src/airport/a380.obj");
    	
    	vbo_v = fmodel.createVerticesBuffer();
        vbo_c = fmodel.createColorsBuffer();
        //vbo_t = sphereModel.createTextCoordsBuffer();
        //vbo_e = sphereModel.createIndicesBuffer();
        //vbo_n = sphereModel.createNormalsBuffer();

        // ----------------------- TEXTURE IMAGE -----------------------------//
        //texture =  Texture.loadTexture("grass.png");
        //texture1 =  Texture.loadTexture("stone-128px.jpg");
        //texture2 =  Texture.loadTexture("fieldstone.jpg");

        x = y = z = rx = ry = rz = 0.0f;
        sx = sy =sz = 1.0f;
    }


	public int getVertexPositions() {
		return vbo_v;
	}


	public int getVertexColor() {
		return 0;//vbo_c;
	}


	public int getTexCoord() {
		return  0;//vbo_t;
	}


	public int getElementsBuffer() {
		return vbo_e;
	}


	public void draw(OpenGLHelper openGLHelper) {
		
		count += this.speed;
    	count2 += 0.01f;
    	
	    int posAttrib = openGLHelper.getShaderProgram().getAttributeLocation("aVertexPosition");
        glEnableVertexAttribArray(posAttrib);
        glBindBuffer(GL_ARRAY_BUFFER, getVertexPositions());

        glVertexAttribPointer(posAttrib, 3, GL_FLOAT, false, 0, 0);

        int colAttrib = openGLHelper.getShaderProgram().getUniformLocation("color");
		glEnableVertexAttribArray(colAttrib);
		glVertexAttribPointer(colAttrib, 3, GL_FLOAT, false, 3 * GL_FLOAT, 0);
		
        
        // ----------------------- COLORS DATA -------------------------------//
        
        
        int vertexColorAttribute = openGLHelper.getShaderProgram().getAttributeLocation("aVertexColor");
        glEnableVertexAttribArray(vertexColorAttribute);
        glBindBuffer(GL_ARRAY_BUFFER, getVertexColor());
        glVertexAttribPointer(vertexColorAttribute, 3, GL_FLOAT, false, 0, 0);
        
        posX = this.x + count;
        System.out.println(count);
        if (posX > 400.0){
        	posX = this.x;
        	count = this.speed;
        	posY = this.y;
        	count3 = 0.0f;
        }
        
        if (posX > 35.0){
        	count3 += 0.03f;
        	if (count3 > 5){
        		count3 += 0.03f;
        	}
        	posY = this.y + count3;
        }else{
        	posY = this.y;
        }
		
        //float posY = this.y + (float)Math.sin(count2);
        float posZ = this.z; //+ (float)Math.sin(count3);
        // ----------------------- TEXTURE COORDS ----------------------------//
     
        /*int texcoordAttribute = openGLHelper.getShaderProgram().getAttributeLocation("texcoord");
        glEnableVertexAttribArray(texcoordAttribute);
        glBindBuffer(GL_ARRAY_BUFFER, getTexCoord());
        glVertexAttribPointer(texcoordAttribute, 2, GL_FLOAT, false, 0, 0);
        */
        // ----------------------- ELEMENTS INDICES --------------------------//
        
        /*vbo_n = sphereModel.createNormalsBuffer();
        glBindBuffer(GL_ARRAY_BUFFER, vbo_n);
        glEnableVertexAttribArray(vertexNormalAttribute);
        glVertexAttribPointer(vertexNormalAttribute, 3, GL_FLOAT, false, 0, 0);*/

        //int ebo = getElementsBuffer();
        //glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        //glBufferData(GL_ELEMENT_ARRAY_BUFFER, ebo, GL_STATIC_DRAW);

        int uniTex = openGLHelper.getShaderProgram().getUniformLocation("texImage");
        openGLHelper.getShaderProgram().setUniform(uniTex, 0);
	  
	    Matrix4f model = Matrix4f.rotate(angle, rx, ry, rz); 
        model = Matrix4f.scale(sx, sy, sz).multiply(model);
        model = Matrix4f.translate(posX, posY, posZ).multiply(model);
        glUniformMatrix4(openGLHelper.getShaderProgram().getUniformLocation("model"), false, model.getBuffer());

        //texture.bind();
       
        glDrawArrays(GL11.GL_TRIANGLES, 0, fmodel.getNumVertices()*3);
	        
	      
		
	}


	public void setRotation(float nangle, float x, float y, float z) {
		
		rx = x;
		ry = y;
		rz = z;
		angle = nangle;
		
	}


	public void setTexture(String textfile) {
		
		 //texture =  Texture.loadTexture(textfile);
		
	}


	public void setPosition(float px, float py, float pz, float veloc) {
		
		x = px;
		posX = x;
		y = py;
		posY = y;
		z = pz;
		speed = veloc;
		
	}


	public void setScale(float nsx, float nsy, float nsz) {
		
		sx = nsx;
		sy = nsy;
		sz = nsz;
	}
    
}