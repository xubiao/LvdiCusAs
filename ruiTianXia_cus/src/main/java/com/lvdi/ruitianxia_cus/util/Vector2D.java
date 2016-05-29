package com.lvdi.ruitianxia_cus.util;

public class Vector2D {
	private float x;
	private float y;

	public Vector2D() {
	}

	public Vector2D(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Vector2D(Vector2D mVector2D) {
		this.x = mVector2D.x;
		this.y = mVector2D.y;
	}

	/**
	 * 得到两个点之间的距离
	 * @param mVector2D1
	 * @param mVector2D2
	 * @return
	 */
	public static float getDistance(Vector2D mVector2D1,Vector2D mVector2D2) {
		return subtract(mVector2D1, mVector2D2).getLength();
	}

	/**
	 * 得到单位向量
	 * @param mVector2D
	 * @return
	 */
	public static Vector2D getNormalized(Vector2D mVector2D) {
		float f = mVector2D.getLength();
		if (f == 0.0F){
			return new Vector2D();
		}else{
			return new Vector2D(
					mVector2D.x / f, mVector2D.y / f);
		}
	}

	/**
	 * 得到两个点之间的夹角--根据单位向量
	 * @param mVector2D1
	 * @param mVector2D2
	 * @return
	 */
	public static float getSignedAngleBetween(Vector2D mVector2D1,
			Vector2D mVector2D2) {
		Vector2D vector2D1 = getNormalized(mVector2D1);
		Vector2D vector2D2 = getNormalized(mVector2D2);
		return (float) (Math.atan2(vector2D2.y, vector2D2.x) - Math
				.atan2(vector2D1.y, vector2D1.x));
	}

	/**
	 * 两个点相减
	 * @param mVector2D1
	 * @param mVector2D2
	 * @return
	 */
	public static Vector2D subtract(Vector2D mVector2D1,
			Vector2D mVector2D2) {
		return new Vector2D(mVector2D1.x - mVector2D2.x,
				mVector2D1.y - mVector2D2.y);
	}

	/**
	 * 两个点相加
	 * @param mVector2D
	 * @return
	 */
	public Vector2D add(Vector2D mVector2D) {
		this.x += mVector2D.getX();
		this.y += mVector2D.getY();
		return this;
	}

	/**
	 * 得到该点到原点的距离
	 * @return
	 */
	public float getLength() {
		return (float) Math.sqrt(this.x * this.x + this.y * this.y);
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}

	public Vector2D set(float x, float y) {
		this.x = x;
		this.y = y;
		return this;
	}

	public Vector2D set(Vector2D mVector2D) {
		this.x = mVector2D.getX();
		this.y = mVector2D.getY();
		return this;
	}

	public String toString() {
		Object[] arrayOfObject = new Object[2];
		arrayOfObject[0] = Float.valueOf(this.x);
		arrayOfObject[1] = Float.valueOf(this.y);
		return String.format("(%.4f, %.4f)", arrayOfObject);
	}
}