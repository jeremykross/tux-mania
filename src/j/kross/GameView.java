package j.kross;

import android.view.View;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.view.KeyEvent;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap;

import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ShapeDrawable;

import java.util.ArrayList;

public class GameView extends SurfaceView {
	public GameView(Context c) {
		super(c);
		initialize();
	}

	protected void initialize() {
		setFocusable(true);

		mDrawingRect = new Rect();

		mShell = (BitmapDrawable)getResources().getDrawable(R.drawable.shell);

		mTux = (BitmapDrawable)getResources().getDrawable(R.drawable.tux);
		mStar = (BitmapDrawable)getResources().getDrawable(R.drawable.star);
		mExplosionParticle = new ShapeDrawable(new OvalShape());

		mCornflowerPaint = new Paint();
		mCornflowerPaint.setARGB(255,100,149,237);
	}

	public void setGameState(GameState gs) {
		mGameState = gs;
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

	protected void onSizeChanged(int w, int h, int oldW, int oldH) {
		if(mGameState != null) {
			Rect r = new Rect(0,0,w,h);
			mGameState.updateBounds(r);
		}
		mWidth = w;
		mHeight = h;
		super.onSizeChanged(w,h,oldW,oldH);
	}

	protected void onDraw(Canvas c) {
		getDrawingRect(mDrawingRect);
		c.drawRect(mDrawingRect, mCornflowerPaint);
		
		ArrayList<Entity> gameEntities = mGameState.getEntities();
		for(int i=0; i<gameEntities.size(); i++) {
			Entity e = gameEntities.get(i);
			if(e.getDrawableId() == R.drawable.shell) {
				mShell.setBounds(e.getRect());
				mShell.draw(c);	
			}else if(e.getDrawableId() == R.drawable.tux) {
				mTux.setBounds(e.getRect());
				mTux.draw(c);	
			} else if(e.getDrawableId() == 0) {
				ExplosionEntity ee = (ExplosionEntity)e;
				ArrayList<Entity> particles = ee.getParticles();
				int color = ee.getColor();
				if(particles != null) {
					for(int j=0; j<particles.size(); j++) {
						Entity p = particles.get(j);
						mExplosionParticle.setBounds(p.getRect());
						mExplosionParticle.getPaint().setColor(color);
						mExplosionParticle.draw(c);
					}
				}
			} else if(e.getDrawableId() == R.drawable.star) {
				mStar.setBounds(e.getRect());
				mStar.draw(c);
			}
		}

	}

	Rect mDrawingRect;
	Paint mCornflowerPaint;
	BitmapDrawable mShell;
	BitmapDrawable mTux;
	BitmapDrawable mStar;
	ShapeDrawable mExplosionParticle;

	int mWidth;
	int mHeight;

	GameState mGameState;
};
