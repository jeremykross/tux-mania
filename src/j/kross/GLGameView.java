package j.kross;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Color;

import android.view.KeyEvent;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ByteBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLGameView extends GLSurfaceView implements GLSurfaceView.Renderer {
	public GLGameView(Context c) {
		super(c);
		initialize();
	}
	protected void initialize() {
		setFocusable(true);

		mVertexBuffer = ByteBuffer.allocateDirect(6*2*4).order(ByteOrder.nativeOrder()).asFloatBuffer();//FloatBuffer.allocate(6*2);
		mVertexBuffer.put( new float[] {0,0, 1,0, 1,1,
				   1,1, 0,1, 0,0 });
		mVertexBuffer.rewind();

		mTexCoordBuffer = ByteBuffer.allocateDirect(6*2*4).order(ByteOrder.nativeOrder()).asFloatBuffer();//FloatBuffer.allocate(6*2);
		mTexCoordBuffer.put( new float[] {0,0, 1,0, 1,1,
				   1,1, 0,1, 0,0 });
		mTexCoordBuffer.rewind();

		setRenderer(this);
		 
	}
	public void setGameState(GameState gs) {
		mGameState = gs;
	}

	public void onPause() {
		super.onPause();
		mGameState.onPause();
	}
	public void onResume() {
		super.onResume();
		mGameState.onResume();
	}
	public boolean onKeyUp(int keyCode, KeyEvent evt) {
		if(mGameState != null) 
			return mGameState.onKeyEvent(evt);
		return false;
	}
	public boolean onKeyDown(int keyCode, KeyEvent evt) {
		if(mGameState != null)
			return mGameState.onKeyEvent(evt);
		return false;
	}
	public boolean onTouchEvent(MotionEvent evt) {
		if(mGameState != null) {
			evt.setLocation(evt.getX()*mVirtScaleX, evt.getY()*mVirtScaleY);
			return mGameState.onTouchEvent(evt);
		}
		return false;
	}
	protected void onSizeChanged(int w, int h, int oldWidth, int oldHeight) {
		if(mGameState != null) {
			Rect r;
			if(w < h) {
				r = new Rect(0,0,320,480);
				mVirtScaleX = 320/(float)w;
				mVirtScaleY = 480/(float)h;
			} else {
				r = new Rect(0,0,480,320);
				mVirtScaleX = 480/(float)w;
				mVirtScaleY = 320/(float)h;
			}

			android.util.Log.d("Tux", "   SET: " + mVirtScaleX + " " + mVirtScaleY);
			mGameState.updateBounds(r); 
		}
	}
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		/*gl.glDisable(GL10.GL_LIGHTING);	
		gl.glDisable(GL10.GL_DITHER);
		gl.glDisable(GL10.GL_BLEND);*/
		gl.glDisable(GL10.GL_DEPTH_TEST);
		gl.glDisable(GL10.GL_LIGHTING);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		

		Bitmap shell = BitmapFactory.decodeResource(getResources(), R.drawable.shell);
		Bitmap tux = BitmapFactory.decodeResource(getResources(), R.drawable.tux);
		Bitmap tuxhurt = BitmapFactory.decodeResource(getResources(), R.drawable.tuxhurt);
		Bitmap star = BitmapFactory.decodeResource(getResources(), R.drawable.star);
		Bitmap particle = BitmapFactory.decodeResource(getResources(), R.drawable.particle);


		int[] texNames = new int[5];
		gl.glGenTextures(5, texNames, 0);

		mShell = texNames[0];
		mTux   = texNames[1];
		mStar  = texNames[2];
		mParticle = texNames[3];
		mTuxHurt = texNames[4];

		gl.glBindTexture(GL10.GL_TEXTURE_2D, mShell);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, shell, 0);		
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);

		
		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTux);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, tux, 0);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);

		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTuxHurt);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, tuxhurt, 0);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);
		

		gl.glBindTexture(GL10.GL_TEXTURE_2D, mStar);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, star, 0);		
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);

		gl.glBindTexture(GL10.GL_TEXTURE_2D, mParticle);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, particle, 0);		
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);

		shell.recycle();
		tux.recycle();
		tuxhurt.recycle();
		star.recycle();
		particle.recycle();
		
	}

	public void onSurfaceChanged(GL10 gl, int w, int h) {	
		gl.glViewport(0,0,w,h);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();

		if(w < h) {
			GLU.gluOrtho2D(gl, 0,320,0,480);
			mWidth = 320;
			mHeight = 480;
		} else {
			GLU.gluOrtho2D(gl, 0,480,0,320);
			mWidth = 480;
			mHeight = 320;
		}


		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	public void updateFrame() {
		long currentTime = System.nanoTime();
		float deltaTime = 0;
		if(mGameTime != -1) {
			deltaTime = (currentTime-mGameTime)/1000000000.0f;
		}
		mGameTime = currentTime;

		if(mGameState != null)
			mGameState.update(deltaTime);
	}
	public void onDrawFrame(GL10 gl) {
		updateFrame();
		
		int c= mGameState.clearColor();

		gl.glClearColor(Color.red(c)/255.0f,Color.green(c)/255.0f,Color.blue(c)/255.0f,1.0f);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);	

		gl.glPushMatrix();
	/*	if(mIsLandscape) {
			gl.glTranslatef(0,285, 0);
		} else {
			gl.glTranslatef(0,445, 0);
		}*/
		
		gl.glTranslatef(0, mHeight, 0);
		gl.glScalef(1,-1,1);

		PointF zoomFactor = mGameState.zoomFactor();
		PointF translateFactor = mGameState.translateFactor();
		gl.glTranslatef(-translateFactor.x, -translateFactor.y,0);
		gl.glScalef(zoomFactor.x, zoomFactor.y, 0);

		ArrayList<Entity> gameEntities = mGameState.getEntities();
		for(int i=0; i<gameEntities.size(); i++) {
			Entity e = gameEntities.get(i);
			if(e.isAlive() == false) continue;
			if(e.getDrawableId() == R.drawable.shell) {
				drawElement(gl, e.getRect(), mShell);
			} else if(e.getDrawableId() == R.drawable.tux) {
				drawElement(gl, e.getRect(), mTux);
			} else if(e.getDrawableId() == R.drawable.tuxhurt) {
				drawElement(gl, e.getRect(), mTuxHurt);
			} else if(e.getDrawableId() == 0) {
				ExplosionEntity ee = (ExplosionEntity)e;
				ArrayList<Entity> particles = ee.getParticles();
				int color = ee.getColor();
				//gl.glDisable(GL10.GL_TEXTURE_2D);
				gl.glColor4f(Color.red(color)/255.0f, Color.green(color)/255.0f, Color.blue(color)/255.0f, 1.0f); //Color.alpha(color)/255.0f);
				gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE);
				//gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
				if(particles != null) {
					for(int j=0; j<particles.size(); j++) {
						Entity p = particles.get(j);
						drawElement(gl, p.getRect(), mParticle);
					}
				}
				gl.glColor4f(1.0f,1.0f,1.0f,1.0f);
				gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
				//gl.glEnable(GL10.GL_TEXTURE_2D);
			} else if(e.getDrawableId() == R.drawable.star) {
				drawElement(gl, e.getRect(), mStar);
			}
		}


		gl.glPopMatrix();
	}

	protected void drawElement(GL10 gl, Rect r, int texId) {
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texId);
		gl.glPushMatrix();
		gl.glTranslatef(r.left, r.top, 0);
		gl.glScalef(r.width(), r.height(), 1);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);	
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glVertexPointer(2, GL10.GL_FLOAT, 0, mVertexBuffer);	
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTexCoordBuffer); 
		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 6);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glPopMatrix();
	}
	
	int mShell;
	int mTux;
	int mTuxHurt;
	int mStar;
	int mParticle;

	boolean mIsLandscape = false;
	//Bitmap mShell;
	//Bitmap mTux;
	//Bitmap mStar;

	FloatBuffer mVertexBuffer;
	FloatBuffer mTexCoordBuffer;

	GameState mGameState;
	
	long mGameTime = -1;

	int mWidth, mHeight;

	float mVirtScaleX, mVirtScaleY;
}
