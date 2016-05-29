package com.lvdi.ruitianxia_cus.util;

import android.view.MotionEvent;

public class TouchManager {
	private final int maxNumberOfTouchPoints;
	private final Vector2D[] points;
	private final Vector2D[] previousPoints;

	public TouchManager(int paramInt) {
		this.maxNumberOfTouchPoints = paramInt;
		this.points = new Vector2D[paramInt];
		this.previousPoints = new Vector2D[paramInt];
	}

	private static Vector2D getVector(Vector2D mVector2D1,Vector2D mVector2D2) {
		if ((mVector2D1 != null) && (mVector2D2 != null)) {
			return Vector2D.subtract(mVector2D2, mVector2D1);
		}
		return null;
	}

	public Vector2D getPoint(int paramInt) {
		if (this.points[paramInt] != null) {
			return this.points[paramInt];
		} else {
			return new Vector2D();
		}
	}

	public int getPressCount() {
		int i = 0;
		for (int k = 0; k < this.points.length; k++) {
			if (this.points[k] != null) {
				i += 1;
			}
		}
		return i;
	}

	public Vector2D getPreviousPoint(int paramInt) {
		if (this.previousPoints[paramInt] != null) {
			return this.previousPoints[paramInt];
		} else {
			return new Vector2D();
		}
	}

	public Vector2D getPreviousVector(int paramInt1, int paramInt2) {
		if ((this.previousPoints[paramInt1] == null)
				|| (this.previousPoints[paramInt2] == null)) {
			return getVector(this.points[paramInt1], this.points[paramInt2]);
		} else {
			return getVector(this.previousPoints[paramInt1],
					this.previousPoints[paramInt2]);
		}
	}

	public Vector2D getVector(int paramInt1, int paramInt2) {
		return getVector(this.points[paramInt1], this.points[paramInt2]);
	}

	public boolean isPressed(int paramInt) {
		if (this.points[paramInt] != null) {
			return true;
		} else {
			return false;
		}
	}

	public Vector2D moveDelta(int paramInt) {
		if (isPressed(paramInt)) {
			Vector2D localVector2D2;
			if (this.previousPoints[paramInt] != null) {
				localVector2D2 = this.previousPoints[paramInt];
			} else {
				localVector2D2 = this.points[paramInt];
			}
			return Vector2D.subtract(this.points[paramInt], localVector2D2);
		} else {
			return new Vector2D();
		}
	}

	public void update(MotionEvent event) {
		int action = 0xFF & event.getAction();
		// 抬起触发
		if ((action == 6) || (action == 1)) {
			// j的取值为0 1 2代表几个手指按下
			// int j = paramMotionEvent.getAction() >> 8;
			// if (j < maxNumberOfTouchPoints) {
			// this.points[j] = null;
			// this.previousPoints[j]=null;
			// }
			if (event.getPointerCount() == 1) {
				this.points[0] = null;
				this.previousPoints[0] = null;
			} else {
				this.points[0] = null;
				this.previousPoints[0] = null;
				this.points[1] = null;
				this.previousPoints[1] = null;
			}
			return;
		}

		if (event.getPointerCount() == 1) {
			Vector2D localVector2D = new Vector2D(event.getX(0),event.getY(0));
			if (this.previousPoints[0] != null) {
				if (this.points[0] != null) {
					this.previousPoints[0].set(this.points[0]);
				}
			} else {
				this.previousPoints[0] = new Vector2D(localVector2D);
			}
			if (this.points[0] == null) {
				this.points[0] = localVector2D;
			} else {
				this.points[0].set(localVector2D);
			}
		} else {
			Vector2D localVector2D0 = new Vector2D(event.getX(0),event.getY(0));
			if (this.previousPoints[0] != null) {
				if (this.points[0] != null) {
					this.previousPoints[0].set(this.points[0]);
				}
			} else {
				this.previousPoints[0] = new Vector2D(localVector2D0);
			}
			if (this.points[0] == null) {
				this.points[0] = localVector2D0;
			} else {
				this.points[0].set(localVector2D0);
			}
			Vector2D localVector2D1 = new Vector2D(event.getX(1),event.getY(1));
			if (this.previousPoints[1] != null) {
				if (this.points[1] != null) {
					this.previousPoints[1].set(this.points[1]);
				}
			} else {
				this.previousPoints[1] = new Vector2D(localVector2D1);
			}
			if (this.points[1] == null) {
				this.points[1] = localVector2D1;
			} else {
				this.points[1].set(localVector2D1);
			}
		}
	}
}