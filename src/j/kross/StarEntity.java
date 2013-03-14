package j.kross;

class StarEntity extends Entity {
	public StarEntity() {
		super();
		mNeedsWorldCollision = true;
		mNeedsObjectCollision = true;

		android.util.Log.d("Tux", "Craeted Star");
	}
	public int getDrawableId() {
		return R.drawable.star;
	}
	public void shoot(float dirX, float dirY) {
		setVelocityX(dirX);
		setVelocityY(dirY);
	}
	public void shoot() {
		setVelocityY(-750);
		mIsAlive = true;
	}
}
