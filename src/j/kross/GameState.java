package j.kross;

import java.util.ArrayList;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.graphics.Rect;
import android.graphics.PointF;
import android.graphics.Color;

class GameState {
	public GameState() {
		mEntities = new ArrayList<Entity>();

		mZoomFactor = new PointF(1,1);
		mTranslateFactor = new PointF(0,0);
		mRotateFactor = new PointF(0,0);

		mClearColor = Color.argb(255, 100, 149, 237);
	}
	public void onPause() {}
	public void onResume() {}
	public void onFinish() {}

	public boolean onTouchEvent(MotionEvent evt) {return false;} 
	public boolean onKeyEvent(KeyEvent evt) {return false;}

	public void update(float deltaTime) {
		for(int i=0; i<mEntities.size(); i++) {
			mEntities.get(i).update(deltaTime);	
		}
		for(int i=0; i<mEntities.size(); i++) {
				Entity one = mEntities.get(i);
				if(one.needsObjectCollision() == false || one.isAlive() == false) continue;
			for(int j=0; j<mEntities.size(); j++) {
				Entity two = mEntities.get(j);
				float radii = one.getRadius() + two.getRadius();
				float distBetween = VectorHelper.distBetween(one.getPos(), two.getPos());
				if(radii > distBetween) {
					onCollision(one, two);		
				}
			}
		} 

		int maxWidth = Integer.MAX_VALUE;
		int maxHeight = Integer.MAX_VALUE;
		if(mBounds != null) {
			maxWidth = mBounds.width();
			maxHeight = mBounds.height();
		}
		for(int i=0; i<mEntities.size(); i++) {
			Entity e = mEntities.get(i);
			if(e.needsWorldCollision() == false || e.isAlive() == false) continue;
			PointF pos = e.getPos();
			float radius = e.getRadius();

			if(pos.x-radius <= 0) {
				onCollision(e, RIGHT, radius-pos.x); 
			} else if(pos.x+radius >= maxWidth) {
				onCollision(e, LEFT, (pos.x+radius)-maxWidth);
			}
			if(pos.y-radius <= 0) {
				onCollision(e, DOWN, radius-pos.y);
			} else if(pos.y+radius >= maxHeight) {
				onCollision(e, UP, (pos.y+radius)-maxHeight);
			}
		}
	}

	public void updateBounds(Rect r) {
		mBounds = r;
	}

	public ArrayList<Entity> getEntities() {
		return mEntities;
	}

	public PointF zoomFactor() { return mZoomFactor; }
	public PointF translateFactor() { return mTranslateFactor; }
	public PointF rotateFactor() { return mRotateFactor; }

	public int clearColor() { return mClearColor; }

	protected PointF mZoomFactor;
	protected PointF mTranslateFactor;
	protected PointF mRotateFactor;

	protected int mClearColor;

	protected void onCollision(Entity one, Entity two) {}
	protected void onCollision(Entity one, PointF normal, float overlap) {}
	
	protected ArrayList<Entity> mEntities;	
	protected Rect mBounds;
	protected PointF mScratch = new PointF();

	protected static PointF UP = new PointF(0,-1);
	protected static PointF DOWN = new PointF(0,1);
	protected static PointF LEFT = new PointF(-1,0);
	protected static PointF RIGHT = new PointF(1,0);
};
