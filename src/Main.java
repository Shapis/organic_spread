import java.awt.Frame;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Random;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.Animator;


public class Main extends GLCanvas implements GLEventListener, MouseWheelListener {

	private static final int height = 1000;
	private static final int width = height;
	private static final double N = 1E-2;
	private static final double dnaFactor = 4;
	private static final double organisms = 3;
	private static double factor = 3;
	private static double start = 1;
	
	private static double[][][] mySiteConditions = new double[(int)(10*factor)][(int)(10*factor)][2];
	
	
	private static ArrayList<ArrayList<Organism>> myArray = new ArrayList<ArrayList<Organism>>();
	
	public static void main(String[] args)  {
		Random random = new Random();
		for (int i = 0 ; i < 10*factor ; i++) {
			for (int j = 0 ; j < 10*factor ; j++) {
				mySiteConditions[i][j][0] = (double)random.nextInt((int)dnaFactor)+1;
				mySiteConditions[i][j][1] = (double)random.nextInt((int)organisms+1);
			}
		}
		
		
		GLProfile glp = GLProfile.getDefault();
		GLCapabilities capabilities = new GLCapabilities(glp);
        GLCanvas canvas = new GLCanvas(capabilities);
        
        
        Frame frame = new Frame("AWT Window Test");
        frame.setSize(height, width);
        frame.add(canvas);
        frame.setVisible(true);

        
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }});
        
        canvas.addGLEventListener(new Main());
        canvas.addMouseWheelListener(new Main());
        Animator animator = new Animator(canvas);
        animator.start();

	}

	private void executeAlgorithm(){
		Random random = new Random();
		for (int i = 1 ; i < (10*factor)-1 ; i++) {
			for (int j = 1 ; j < (10*factor)-2 ; j++) {
				if (Math.random() <= N){
				if ((mySiteConditions[i][j][0] != mySiteConditions[i][j][1])&&start==1) {
					mySiteConditions[i][j][1] = (int) (mySiteConditions[i+1][j+1][1] + mySiteConditions[i-1][j+1][1] + mySiteConditions[i+1][j-1][1] + mySiteConditions[i-1][j-1][1])/4; 
					if (Math.random() <= N/1E1) {
						mySiteConditions[i][j][1] = (int)random.nextInt((int)dnaFactor);
					}
				}
				}
			}
			}
		
		
	}

	private void update() {
		executeAlgorithm();
	}

	private void drawCircle (GL2 gl,double positionX, double positionY, double screenHeight , double screenWidth, double potential) {
		float red = 1;
		float green = 1;
		float blue = 1;
		
		if (potential>=0) {
			green = green - ((float)potential)/50;
			blue = blue - ((float)potential)/50;
		}else {
			green = green + ((float)potential)/50;
			red = red + ((float)potential)/50;
		}
		
		if (potential == 200 || potential == -200) {
			red = 0;
			green = 1;
			blue = 0;
		}
		
		
		
		gl.glBegin(GL.GL_POINTS);
		gl.glColor3f(red, green, blue);
	    for(int i =0; i <= 80; i++){
	    double angle = 2 * Math.PI * i / 80;
	    double x = Math.cos(angle);
	    double y = Math.sin(angle);
	    for (double j = 1 ; j >= 0.1 ; j = j - 0.02) {
	    gl.glVertex2d(positionX+((x*0.025)*j),positionY+((y*0.025)*j));
	    }
	    }
	}
	
	private void drawSquare(GL2 gl,double x, double y,double organism){
		float red = 1;
		float green = 1;
		float blue = 1;
		
		if (organism == 1) {
		red = 1;
		green = 0;
		blue = 0;
		}
		if (organism == 2) {
			red = 0;
			green = 1;
			blue = 0;
			}
		if (organism == 3) {
			red = 0;
			green = 0;
			blue = 1;
			}
		
		double size = 0.07/factor;
		gl.glRectf((float)(x-size), (float)(y-size), (float)(x+size), (float)(y+size));
		gl.glColor3f(red, green, blue);
	}
	
	private void render(GLAutoDrawable drawable) {
	    GL2 gl =  drawable.getGL().getGL2();
	    
		    gl = drawable.getGL().getGL2();
		    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		   
		    
		    	for (double i = 0 ; i < (10*factor) ; i++) {
		    		for (double j = 0 ; j < (10*factor) ; j++) {
		    			drawSquare(gl, ((i-(5*factor))/(5*factor))+(1/(10*factor)),((j-(5*factor))/((5*factor)))+(1/(10*factor)),mySiteConditions[(int)i][(int)j][1]);
		    		//drawCircle (gl,((i-(5*factor))/(5*factor))+(1/(10*factor)),((j-(5*factor))/((5*factor)))+(1/(10*factor)), height, width,1.0);
		    		}
		    	}
	    gl.glEnd();
	    try {
			Thread.sleep(0);
			}catch(InterruptedException ex) {
				
			}
	
	}

	public void display(GLAutoDrawable drawable) {
		 	update();
		    render(drawable);
		    
	}

	public void init(GLAutoDrawable drawable) {
	    // put your OpenGL initialization code here
		GLU glu = new GLU();
		 drawable.getGL().setSwapInterval(1);
		 
	}

	public void dispose(GLAutoDrawable drawable) {
	    // put your cleanup code here
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL gl = drawable.getGL();
	    gl.glViewport(0, 0, width, height);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		  int notches = e.getWheelRotation();
	       if (notches < 0) {
	    	   for (int i = 0 ; i < 10*factor ; i++) {
	   			for (int j = 0 ; j < 10*factor ; j++) {
	   				if (mySiteConditions[i][j][0] <= organisms) {
	   				mySiteConditions[i][j][0] = mySiteConditions[i][j][0] +1;
	   				}
	   			}
	   		}
	    	   
	       } else {
	    	   for (int i = 0 ; i < 10*factor ; i++) {
		   			for (int j = 0 ; j < 10*factor ; j++) {
		   				if (mySiteConditions[i][j][0] > 1) {
		   				mySiteConditions[i][j][0] = mySiteConditions[i][j][0] -1;
		   				}
		   			}
		   		}
	       }
		
	}

}