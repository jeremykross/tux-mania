package j.kross;

import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.ArrayList;

class TuxManiaGameState extends GameState {
	interface ScoreListener {
		void onScoreUpdated(int newScore);
		void onComboUpdated(int newCombo);
		void onGameOver();
	}

	public TuxManiaGameState() {
		super();

		mSoundEffects = new SoundPool(20, AudioManager.STREAM_MUSIC,100); 
		mBonkId = mSoundEffects.load(GameActivity.activity, R.raw.bass, 1);

		mTuxEntity = new TuxEntity();
		mEntities.add(mTuxEntity);

		mShells = new ArrayList<ShellEntity>();
		mStars = new ArrayList<StarEntity>();
		mExplosions = new ArrayList<ExplosionEntity>();

//		mAudioManager = (AudioManager)Main.activity.getSystemService(Context.AUDIO_SERVICE);

		mBGMusic = MediaPlayer.create(GameActivity.activity, R.raw.polka);
		mBGMusic.setLooping(true);
		mBGMusic.start();
		mDeadMusic = MediaPlayer.create(GameActivity.activity, R.raw.decay);
	}

	public void onPause() {
		mBGMusic.pause();
	}
	public void onResume() {
		if(mIsDead == false) {
			mBGMusic.start();
			for(int i=0; i<mShells.size(); i++) {
				ShellEntity s = mShells.get(i);
				mEntities.remove(s);
				s.setAlive(false);
			}
		}	
	}

	protected void reviveIf() {
		if(mReviveCount > mNeededToRevive) revive();
	}
	public boolean onTouchEvent(android.view.MotionEvent evt) {
		PointF tuxPos = mTuxEntity.getPos();
		float deltaX =  evt.getX()-tuxPos.x;

		if(evt.getAction() == MotionEvent.ACTION_DOWN || evt.getAction() == MotionEvent.ACTION_MOVE) {
			if(evt.getY() < mBounds.height()/2) {
				return true;
			}

			mDownX = evt.getX();
			mDownY = evt.getY(); 

			mDownTuxX = tuxPos.x;

			float distToTuxX = (float)Math.sqrt(deltaX*deltaX);
			float direction = Math.signum(deltaX);
			float tuxSpeed = direction*300;	
			float accelerationInterval = 50;
			if(distToTuxX > accelerationInterval) {	
				moveTux(tuxSpeed);
			} else {
				moveTux(tuxSpeed*(distToTuxX/accelerationInterval));
			}
			return true;
		} else if(evt.getAction() == MotionEvent.ACTION_UP) {
			if(mIsDead) {
				mReviveCount++;
				reviveIf();
				return true;
			}
			/*if(evt.getEventTime() - evt.getDownTime() < 75) {
				tuxShoot();
			}*/

			if(evt.getY() < mBounds.height()/2 ) {
				tuxShoot();
			}
		
			mDownX = 0;
			mDownY = 0;
			mDownTuxX = 0;
			moveTux(0);
		}

		return false;
	}

	public boolean onKeyEvent(android.view.KeyEvent evt) {
		int keyCode = evt.getKeyCode();

		if(evt.getAction() == KeyEvent.ACTION_DOWN) {
			if(mIsDead) {  
				if(mPressed == false) {
					mReviveCount++;
					reviveIf();
					mPressed = true;
				}
				return true;
			}
			if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
				moveTux(-300);
				return true;
			} else if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
				moveTux(300);
				return true;
			} else if(keyCode == KeyEvent.KEYCODE_DPAD_UP) {
				if(mFired == false) {
					mFired = true;
					tuxShoot();
					return true;
				}
			}
		} else if(evt.getAction() == KeyEvent.ACTION_UP) {
			mPressed = false;
			if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
				moveTux(0);	
				return true;
			} else if(keyCode == KeyEvent.KEYCODE_DPAD_UP) {
				mFired = false;
				return true;
			}
		}
		return false;
	}

	protected void tuxShoot() {
		PointF tuxPos = mTuxEntity.getPos();
		StarEntity newS = null;
		for(int i=0; i<mStars.size(); i++) {
			StarEntity s = mStars.get(i);	
			if(s.isAlive() == false) {
				newS = s;
				break;
			}
		}
		if(newS == null) {
			newS = new StarEntity();
			mStars.add(newS);
		}
		mEntities.add(newS);
		newS.setPos(tuxPos.x, tuxPos.y-mTuxEntity.getRadius() - 10);
		newS.shoot();

	}


	public void updateBounds(Rect r) {
		super.updateBounds(r);
		mTuxEntity.setPos(r.width()/2, r.height()-mTuxEntity.getRadius());
	}

	public void gameOver() {
		if(mScoreListener != null) {
			mScoreListener.onGameOver();
		}
	}
	public void update(float deltaTime) {
		if(mTranslationAnimator!=null) mTranslationAnimator.update(deltaTime);
		if(mScaleAnimator!=null) mScaleAnimator.update(deltaTime);
		if(mIsDead) {
			if(mDeadMusic.getCurrentPosition() > 7000 && mDeadMusic.isPlaying() == false) {
				gameOver();
			}
			return;
		}
		super.update(deltaTime);

		mShellTime += deltaTime;
		if(mShellTime > mShellInterval) {
			//if(mShells.size() < 50) {
				for(int i=0; i<mShellsGenerated; i++) addRandomShell();	
			//}
			mShellTime = 0;
			mRounds++;
			if(mRounds == 5) { 
				if(mShellsGenerated < 10)
					mShellsGenerated++; 
				mRounds=0;
			}
			if(mShellTime == 2.5f && mShellsGenerated == 4) {
				mShellTime++;
			}
		}

		float gravInfluence = 98 * deltaTime;
		for(int i=0; i<mShells.size(); i++) {
			mShells.get(i).addVelocityY(gravInfluence);
		}

		if(mDownX != 0) {
			//modulate tux' speed	
			float accelerationInterval = 50;
			float deltaX = mDownX-mTuxEntity.getPos().x;
			float direction = Math.signum(deltaX);
			float distToTuxX = (float)Math.sqrt(deltaX*deltaX); 

			if(distToTuxX < accelerationInterval) {
				moveTux(direction*(300*(distToTuxX/accelerationInterval)));	
			}
		}
	}
	public void revive() {
		mTranslationAnimator = new PointAnimator(mTranslateFactor); 
		mTranslationAnimator.addFrame(0,0, .5f);
		mScaleAnimator = new PointAnimator(mZoomFactor);
		mScaleAnimator.addFrame(1,1,.5f);

		mTranslationAnimator.start();
		mScaleAnimator.start();

		mDeadMusic.pause();
		mBGMusic.start();

		mClearColor = Color.argb(255, 100, 149,237);
		mIsDead = false;
		mNeededToRevive += 7;

		for(int i=0; i<mShells.size(); i++) {
			ShellEntity s = mShells.get(i);
			mEntities.remove(s);
			s.setAlive(false);
		}

		mTuxEntity.setDrawableId(R.drawable.tux);

	}
	public void die() {
		mBGMusic.pause();
		mDeadMusic.seekTo(0);
		mDeadMusic.start();

		mTranslationAnimator = new PointAnimator(mTranslateFactor); 
		mTranslationAnimator.addFrame(mTuxEntity.getPos().x*3, (mTuxEntity.getPos().y+mTuxEntity.getRadius())*3, .5f);
		mScaleAnimator = new PointAnimator(mZoomFactor);
		mScaleAnimator.addFrame(4f,4f,.5f);

		mTranslationAnimator.start();
		mScaleAnimator.start();


		mClearColor = Color.argb(255, 255, 0,0);
		mIsDead = true;
		mReviveCount = 0;


		mShellTime = 0;
		mRounds = 0;
	
		mTuxEntity.setDrawableId(R.drawable.tuxhurt);

	}
	public void onCollision(Entity a, Entity b) {
		super.onCollision(a,b);
		if(a.getDrawableId() == R.drawable.tux) {
			if(b.getDrawableId() == R.drawable.shell) {
				if(mIsDead == false) {
					die();	
				}
			}
		}
		if(a.getDrawableId() == R.drawable.star || a.getDrawableId() == 0) {
			if(b.getDrawableId() == R.drawable.shell) {
				if(b.getPos().y < 0) return;
				ExplosionEntity newE = null;
				for(int i=0; i<mExplosions.size(); i++) {
					ExplosionEntity e = mExplosions.get(i);
					if(e.isAlive() == false) {
						newE = e;
					}
				}
				if(newE == null) {
					newE = new ExplosionEntity();
					mExplosions.add(newE);
					mEntities.add(newE);
				}
				newE.setPos(b.getPos().x, b.getPos().y);
				newE.explode();

				playSound(mBonkId);


				mEntities.remove(b);
				b.setAlive(false);

				if(a.getDrawableId() == R.drawable.star) {
					mEntities.remove(a);
					a.setAlive(false);
					newE.setComboCount(0);
				} else {
					ExplosionEntity tempE = (ExplosionEntity)a;
					newE.setComboCount(tempE.getComboCount()+1);	
					//tempE.setComboCount(newE.getComboCount()+1);
				}

				mScore += 5*Math.pow(2,newE.getComboCount());
				//if(newE.getComboCount() > mMaxCombo) {
					//mMaxCombo = newE.getComboCount();
					if(mScoreListener != null)
						mScoreListener.onComboUpdated(newE.getComboCount());
				//}
				
				if(mScoreListener != null) {
					mScoreListener.onScoreUpdated(mScore);
				}
			}
		}
	/*	
		float aRadius = a.getRadius();
		float bRadius = b.getRadius();
		float aVelX = a.getVelocityX();
		float aVelY = a.getVelocityY();
		float bVelX = b.getVelocityX();
		float bVelY = b.getVelocityY();
		PointF aPos = a.getPos();
		PointF bPos = b.getPos();
		*/
	}

	//protected void onCollision(ShellEntity a, TuxEntity b) {}
	//protected void onCollision(ShellEntity a, ShotEntity b) {}

	public void onCollision(Entity e, PointF normal, float overlap) {
		super.onCollision(e, normal, overlap);
		if(e == mTuxEntity) {
			e.getPos().x += normal.x*overlap;
			return;
		} 
		if(mStars.contains(e) && normal.y == 1) {
			//mStars.remove(e);
			mEntities.remove(e);
			e.setAlive(false);
		}
		if(normal.x != 0) {
			if(Math.signum(normal.x) != Math.signum(e.getVelocityX())) {
				e.setVelocityX(e.getVelocityX() * -1);
			}
		} else if(normal.y != 0) {
			if(Math.signum(normal.y) != Math.signum(e.getVelocityY())) {
				float bounce = 0; 
				if(normal.y > 0) {
					bounce = (float)-Math.random()*100;
				}
				e.setVelocityY(e.getVelocityY() * -1);
				e.addVelocityY(bounce);
			}
		}
	}

	public void moveTux(float speed) {
		mTuxEntity.setVelocityX(speed);
	}

	protected void addRandomShell() {	
		ShellEntity se = null;
		for(int i=0; i<mShells.size(); i++) {
			ShellEntity e = mShells.get(i);
			if(mShells.get(i).isAlive() == false) {
				se = e;
				break;
			}
		}
		if(se == null) {
			se = new ShellEntity();
			mShells.add(se);
		}
		int width = 300;
		if(mBounds != null) {
			width = mBounds.width();	
		}
		se.setPos(width/2+(float)(Math.random()*100), (float)(-50 + -300*Math.random()));	
		int sign = 1;
		if(((int)(Math.random()*100))%2 == 0) {
			sign = -1;	
		}
		se.setVelocityX((float)Math.random()*150*sign);
		se.setVelocityY((float)Math.random()*150);
		se.setAlive(true);
		mEntities.add(se);
	}

	protected void playSound(int soundHandle) {
		//float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		//streamVolume /= mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		mSoundEffects.play(soundHandle, 0.9f,.9f, 1 ,0,1f);
	
	}

	public void onFinish() {
		mSoundEffects.release();
		mBGMusic.stop();
		mBGMusic.release();
		mDeadMusic.stop();
		mDeadMusic.release();
	}

	public void installScoreListener(ScoreListener s) {
		mScoreListener = s;
	}
	
	private ArrayList<ShellEntity> mShells;
	private ArrayList<StarEntity> mStars;
	private ArrayList<ExplosionEntity> mExplosions;
	private TuxEntity mTuxEntity;

	private AudioManager mAudioManager;
	private SoundPool mSoundEffects;
	int mBonkId;
	private MediaPlayer mBGMusic;
	private MediaPlayer mDeadMusic;

	float mDownX, mDownY;
	float mDownTuxX;
	boolean mFired = false;
	boolean mPressed = false;

	PointAnimator mTranslationAnimator;
	PointAnimator mScaleAnimator;

	boolean mIsDead;
	int mReviveCount;
	int mNeededToRevive = 3;

	float mShellInterval = 2.5f;
	float mShellTime = 0;
	int mShellsGenerated = 1;
	int mRounds;

	int mScore;
	int mMaxCombo;

	ScoreListener mScoreListener = null;

	View mContentView = null;
};
