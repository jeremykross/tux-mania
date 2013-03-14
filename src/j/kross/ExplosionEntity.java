package j.kross;

import android.graphics.Color;
import java.util.ArrayList;

class ExplosionEntity extends Entity {
	class ParticleEntity extends Entity {
		public ParticleEntity() { super(); }
		float mInitialRadius;

	}
	public ExplosionEntity() {
		mTime = 0;
		mLifeTime = 1f;	

		mBaseParticleRadius = 7;
		mParticleVariance = 5;
		mMaxParticleVelocity = 150;

		mNeedsWorldCollision = false;
		mNeedsObjectCollision = true;

		setRadius((mMaxParticleVelocity/2)*mLifeTime);


		mParticles = new ArrayList<Entity>();
		float particleCount = 30;
		for(int i=0; i<particleCount; i++) {
			mParticles.add(new ExplosionEntity.ParticleEntity());		
		}

		android.util.Log.d("Tux", "Crated Explosion");
	}

	public int getDrawableId() {
		return 0;
	}

	private int getSign() {
		return (((int)(Math.random()*100))%2==0)?-1:1;
	}

	public void explode() {
		mIsAlive = true;
		mIsExploding = true;
		mNeedsObjectCollision = true;
		for(int i=0; i<mParticles.size(); i++) {
			ParticleEntity e = (ParticleEntity)mParticles.get(i);
			e.mInitialRadius = mBaseParticleRadius + mParticleVariance*(float)Math.random();
			e.setRadius(e.mInitialRadius);
			e.setPos(mPos.x, mPos.y);
			e.setVelocityX(getSign() * mMaxParticleVelocity*(float)Math.random());
			e.setVelocityY(getSign() * mMaxParticleVelocity*(float)Math.random());
		}
		mInitialColor = Color.argb(255,(int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255.0));
		mFinalColor = Color.argb(0,0,0,0);
		mTime = 0;
	}

	public void update(float deltaTime) {
		super.update(deltaTime);
		

		mTime += deltaTime;
		if(mTime > mLifeTime) mIsAlive = false;
		if(mIsAlive == false) return;

		if(mTime > mLifeTime/2) {
			mNeedsObjectCollision = false;
		}


		float ratio = mTime/mLifeTime;

		for(int i=0; i<mParticles.size(); i++) {
			Entity e = mParticles.get(i);
			e.update(deltaTime);	
			e.addVelocityY(100*deltaTime);
			float initialRadius = ((ParticleEntity)e).mInitialRadius;
			e.setRadius(initialRadius-initialRadius*ratio);
		}
	}

	public ArrayList<Entity> getParticles() {
		if(mIsAlive)
			return mParticles;
		return null;
	}

	public int getColor() {
		float ratio = mTime/mLifeTime;
		int initialAlpha = Color.alpha(mInitialColor);
		int initialRed = Color.red(mInitialColor);
		int initialGreen = Color.green(mInitialColor);
		int initialBlue = Color.blue(mInitialColor);
		int diffAlpha = Color.alpha(mFinalColor) - initialAlpha;
		int diffRed = Color.red(mFinalColor) - initialRed;
		int diffGreen = Color.green(mFinalColor) - initialGreen;
		int diffBlue = Color.blue(mFinalColor) - initialBlue;

		return Color.argb((int)(initialAlpha+diffAlpha*ratio), (int)(initialRed+diffRed*ratio), (int)(initialGreen + diffGreen*ratio), (int)(initialBlue + diffBlue*ratio));
	}

	public int getComboCount() {
		return mComboCount;
	}
	public void setComboCount(int value) {
		mComboCount = value;
	}
	
	protected ArrayList<Entity> mParticles;	
	float mLifeTime;
	float mTime;
	float mBaseParticleRadius;
	float mParticleVariance;
	float mMaxParticleVelocity;
	int mInitialColor;
	int mFinalColor;
	
	boolean mIsExploding = false;

	int mComboCount;
};
