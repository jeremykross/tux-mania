package j.kross;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.ViewGroup;

public class GameActivity extends Activity implements TuxManiaGameState.ScoreListener 
{
	public static Activity activity = null;

	final Handler mHandler = new Handler();

	final Runnable mUpdateScore = new Runnable() {
		public void run() {
			setupHud();
		}
	};

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		activity = this;

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		mLayout = new LinearLayout(this);
		mLayout.setOrientation(LinearLayout.VERTICAL);

		mScoreLayout = new LinearLayout(this);
		mScoreLayout.setOrientation(LinearLayout.HORIZONTAL);

		mScoreText = new TextView(this);
		mScoreText.setText("Score:\nMax Combo:");

		mCurrScoreText = new TextView(this);
		mCurrScoreText.setText("Combo Score:\nCurrent Combo:");
	

		LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		p.gravity = android.view.Gravity.LEFT;
		p.weight = 1;
		mScoreLayout.addView(mScoreText, p); 

		p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		p.gravity = android.view.Gravity.RIGHT;
		p.weight = 1;
		mCurrScoreText.setGravity(android.view.Gravity.RIGHT);
		mScoreLayout.addView(mCurrScoreText, p); 

		p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		mLayout.addView(mScoreLayout, p); 

		mGameView = new GLGameView(this);
		mGameState = new TuxManiaGameState();
		mGameState.installScoreListener(this);
		mGameView.setGameState(mGameState);
		//mGameView.getHolder().addCallback(this);

		//mUpdateThread = new UpdateThread();
		mLayout.addView(mGameView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT)); 


		setContentView(mLayout);

		mScratch = new StringBuilder();

		// setContentView(R.layout.main);
	}

	public void onScoreUpdated(int newScore) {
		mScratch.setLength(0);
		mScratch.append(mScoreLabel);
		mScratch.append(newScore);
		mScoreString = mScratch.toString();	

		mHandler.post(mUpdateScore);
	}
	public void onComboUpdated(int newCombo) {
		if(newCombo == 0) {
			mCombo = 0;
			mComboScore = 5;
		} else {
			mCombo = newCombo;
			mComboScore += 5*Math.pow(2, mCombo);
		}
		mScratch.setLength(0);
		mScratch.append(mTemporalComboLabel);
		mScratch.append(newCombo+1);
		mTemporalComboString = mScratch.toString();
		
		mScratch.setLength(0);
		mScratch.append(mTemporalScoreLabel);
		mScratch.append(mComboScore);
		mTemporalScoreString = mScratch.toString(); 

		if(mMaxCombo <= newCombo) {
			mMaxCombo = newCombo;
			mScratch.setLength(0);
			mScratch.append(mComboLabel);
			mScratch.append(mMaxCombo+1);
			mComboString = mScratch.toString();
		}
	} 
	public void onGameOver() {
		finish();
	}

	public void setupHud() {
		mScratch.setLength(0);
		mScratch.append(mScoreString).append(mNewLine).append(mComboString);
		mScoreText.setText(mScratch.toString());	
		mScratch.setLength(0);
		mScratch.append(mTemporalScoreString).append(mNewLine).append(mTemporalComboString);
		mCurrScoreText.setText(mScratch.toString());
	}

	public void onPause() {
		super.onPause();
		mGameView.onPause();

	}
	public void onResume() {
		super.onResume();
		mGameView.onResume();
	}
	public void onDestroy() {
		super.onDestroy();
		mGameState.onFinish();
	}

	private LinearLayout mLayout;
	private LinearLayout mScoreLayout;
	private TextView mScoreText;
	private TextView mCurrScoreText;
	private GLGameView mGameView; 
	private TuxManiaGameState mGameState;

	StringBuilder mScratch;

	String mScoreLabel = "Score: ";
	String mComboLabel = "Max Combo: ";
	String mTemporalScoreLabel = "Combo Score: ";
	String mTemporalComboLabel = "Current Combo: ";
	String mNewLine = "\n";

	String mScoreString = "Score: ";
	String mComboString = "Combo: ";
	String mTemporalScoreString= "Combo Score: ";
	String mTemporalComboString= "Current Combo: ";
	
	int mComboScore=0;
	int mMaxCombo=0;
	int mCombo=0;
}

