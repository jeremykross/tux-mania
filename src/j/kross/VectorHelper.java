package j.kross;

import android.graphics.PointF;

class VectorHelper {
	public static float distBetween(PointF a, PointF b) {
		scratch.set(a.x, a.y);	
		scratch.offset(-b.x, -b.y);
		return scratch.length();
	}
	public static void normalize(PointF a) {
		float length = a.length();
		a.x /= length;
		a.y /= length;
	}
	public static float dotProduct(PointF a, PointF b) {
		return a.x*b.x + a.y*b.y;
	}
	public static void scale(PointF a, float s) {
		android.util.Log.d("Tux", "Scaling by: " + s);
		a.x *= s;
		a.y *= s;
	}

	protected static PointF scratch = new PointF();
}


