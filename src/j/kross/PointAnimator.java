package j.kross;

import android.graphics.PointF;
import java.util.LinkedList;

class PointAnimator {
	class KeyFrame {
		float mInitialX;
		float mInitialY;
		float mToX;
		float mToY;
		float mOverSecs;
		float mCurrTime = 0; 
	}
	PointAnimator(PointF p) {
		mFrames = new LinkedList<KeyFrame>();
		mCoreFrames = new LinkedList<KeyFrame>();
		mPoint = p;
	}
	void update(float deltaTime) {
		if(mRunning == false) return;
		if(mCurrFrame == null) {
			if(mFrames.size() == 0) {
				stop();
				return;
			}
			mCurrFrame = mFrames.remove();
			mCurrFrame.mInitialX = mPoint.x;
			mCurrFrame.mInitialY = mPoint.y;
		}
		mCurrFrame.mCurrTime += deltaTime; 
		boolean done = false;
		if(mCurrFrame.mCurrTime >= mCurrFrame.mOverSecs) {
			mCurrFrame.mCurrTime = mCurrFrame.mOverSecs;
			done = true;
		}
		float ratio = mCurrFrame.mCurrTime/mCurrFrame.mOverSecs;
		
		mPoint.x = mCurrFrame.mInitialX + (mCurrFrame.mToX - mCurrFrame.mInitialX)*ratio;
		mPoint.y = mCurrFrame.mInitialY + (mCurrFrame.mToY - mCurrFrame.mInitialY)*ratio;
		if(done) mCurrFrame = null;
	}

	void addFrame(float toX, float toY, float overSecs) {
		KeyFrame f = new KeyFrame();
		f.mToX = toX;
		f.mToY = toY;
		f.mOverSecs = overSecs;

		mCoreFrames.add(f);
	}
	void clear() {
		mFrames.clear();
		mCoreFrames.clear();
		mCurrFrame = null;
	}
	void start() {
		mRunning = true;
		mCurrFrame = null;
		for(int i=0; i<mCoreFrames.size(); i++) {
			mFrames.add(mCoreFrames.get(i));
		}
	}
	void stop() {
		mRunning = false;
	}
	PointF mPoint;
	LinkedList<KeyFrame> mFrames;
	LinkedList<KeyFrame> mCoreFrames;
	KeyFrame mCurrFrame = null;
	boolean mRunning = false;
}
