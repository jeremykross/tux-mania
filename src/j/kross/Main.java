package j.kross;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.SurfaceHolder;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;

public class Main extends Activity implements OnClickListener 
{
	public static Activity activity = null;
	/** Called when the activity is first created. */
	@Override
		public void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.main);
			mNewButton = (Button)findViewById(R.id.newGame);

			mNewButton.setOnClickListener(this);
		}

		public void onClick(View v) {
			startActivity(new Intent(this, GameActivity.class));
		}

		public void onPause() {
			super.onPause();
		}
		public void onDestroy() {
			super.onDestroy();
		}

		Button mNewButton;
		Button mHighScores; 
}
