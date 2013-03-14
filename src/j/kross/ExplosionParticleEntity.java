package j.kross;

import android.graphics.Color;

class ExplosionParticleEntity extends Entity {
	public ExplosionParticleEntity() {
		mTime = 0;
		mLifeTime = 1;
		mInitialRadius = 10;
		mInitialColor = Color.argb(255,255,0,0);
		mFinalColor = Color.argb(255,0,0,0);
		mColorDiff = mFinalColor - mInitialColor;
		setRadius(mInitialRadius);
	}
	public int getDrawableId() {
		return 0;
	}
	public void update(float deltaTime) {
		super.update(deltaTime);
		mTime += deltaTime;
		float ratio = mTime/mLifeTime;
		setRadius(mInitialRadius - mInitialRadius*ratio);
		int initialAlpha = Color.alpha(mInitialColor);
		int initialRed = Color.red(mInitialColor);
		int initialGreen = Color.green(mInitialColor);
		int initialBlue = Color.blue(mInitialColor);
		int diffAlpha = Color.alpha(mFinalColor) - initialAlpha;
		int diffRed = Color.red(mFinalColor) - initialRed;
		int diffGreen = Color.green(mFinalColor) - initialGreen;
		int diffBlue = Color.blue(mFinalColor)-initialBlue;
		mColor = Color.argb((int)(initialAlpha + diffAlpha*ratio), (int)(initialRed + diffRed*ratio), (int)(initialGreen+diffGreen*ratio), (int)(initialBlue+diffBlue*ratio));
	}

	public void setInitialColor(int color) {
		mInitialColor = color;
	}
	public int getColor() {
		return mColor;
	}
	float mTime;
	float mLifeTime;
	float mInitialRadius;
	int mInitialColor;
	int mFinalColor;
	int mColorDiff;
	int mColor;
}
