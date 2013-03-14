package j.kross;

import android.graphics.PointF;
import android.graphics.Rect;

class Entity {
	public Entity() {
		mRect = new Rect();
		mPos = new PointF(0,0);
		mVelX = mVelY = 0;
		mRadius = 25;	

		mNeedsWorldCollision = true;
		mNeedsObjectCollision = false;
		mGravityEffects = false;
	}

	public void setPos(float x, float y) {
		mPos.set(x,y);
		recalcRect();
	}
	public PointF getPos() {
		return mPos;
	}
	public void setRadius(float radius) {
		mRadius = radius;
		recalcRect();
	}
	public float getRadius() {
		return mRadius;
	}

	public void setVelocityX(float velX) {
		mVelX = velX;
	}
	public void setVelocityY(float velY) {
		mVelY = velY;
	}
	public void addVelocityX(float dVelX) {
		mVelX += dVelX;
	}
	public void addVelocityY(float dVelY) {
		mVelY += dVelY;
	}
	public float getVelocityX() {
		return mVelX;
	}
	public float getVelocityY() {
		return mVelY;
	}	

	public Rect getRect() {
		return mRect;
	}
	
	public void update(float deltaTime) {
		float differentialX = (float)(mVelX*deltaTime);
		float differentialY = (float)(mVelY*deltaTime);
		setPos(mPos.x + differentialX, mPos.y + differentialY);	
	}
	
	protected void recalcRect() {
		float enlargedRadius = mRadius*1.3f;
		mRect.set((int)(mPos.x-enlargedRadius), (int)(mPos.y-enlargedRadius), (int)(mPos.x+enlargedRadius), (int)(mPos.y+enlargedRadius));
	}

	public int getDrawableId() {
		return -1;
	}

	public boolean affectedByGravity() {
		return mGravityEffects;
	}
	public boolean needsWorldCollision() {
		return mNeedsWorldCollision;
	}
	public boolean needsObjectCollision() {
		return mNeedsObjectCollision;
	}
	public void setAlive(boolean value) {
		mIsAlive = value;
	}
	public boolean isAlive() {
		return mIsAlive;
	}



	protected PointF mPos;
	protected float mVelX, mVelY;
	protected float mRadius;
	protected Rect mRect;

	protected boolean mGravityEffects;
	protected boolean mNeedsWorldCollision;
	protected boolean mNeedsObjectCollision;

	protected boolean mIsAlive = true;
};
