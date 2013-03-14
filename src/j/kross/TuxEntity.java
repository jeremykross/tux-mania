package j.kross;

class TuxEntity extends Entity {
	public TuxEntity() {
		super();
		mNeedsObjectCollision = true;
		mNeedsWorldCollision = true;
	}
	public void setDrawableId(int newId) {
		mDrawableId = newId;
	}
	public int getDrawableId() {
		return mDrawableId;
	}

	int mDrawableId = R.drawable.tux;
}
