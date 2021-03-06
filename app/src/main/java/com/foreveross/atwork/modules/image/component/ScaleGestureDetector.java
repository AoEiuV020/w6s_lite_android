package com.foreveross.atwork.modules.image.component;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ViewConfiguration;


/**
 * Detects transformation gestures involving more than one pointer
 * ("multitouch") using the supplied {@link android.view.MotionEvent}s. The
 * {@link com.foreveross.atwork.modules.image.component.ScaleGestureDetector.OnScaleGestureListener} callback will notify users when a particular
 * gesture event has occurred. This class should only be used with
 * {@link android.view.MotionEvent}s reported via touch.
 * 
 * To use this class:
 * <ul>
 * <li>Create an instance of the {@code ScaleGestureDetector} for your
 * {@link android.view.View}
 * <li>In the {@link android.view.View#onTouchEvent(android.view.MotionEvent)} method ensure you call
 * {@link #onTouchEvent(android.view.MotionEvent)}. The methods defined in your callback will
 * be executed when the events occur.
 * </ul>
 */
public class ScaleGestureDetector {

	public static final String TAG = ScaleGestureDetector.class.getSimpleName();

	/**
	 * The listener for receiving notifications when gestures occur. If you want
	 * to listen for all the different gestures then implement this interface.
	 * If you only want to listen for a subset it might be easier to extend
	 * {@link com.foreveross.atwork.modules.image.component.ScaleGestureDetector.SimpleOnScaleGestureListener}.
	 * 
	 * An application will receive events in the following order:
	 * <ul>
	 * <li>One {@link com.foreveross.atwork.modules.image.component.ScaleGestureDetector.OnScaleGestureListener#onScaleBegin(com.foreveross.atwork.modules.image.component.ScaleGestureDetector)}
	 * <li>Zero or more
	 * {@link com.foreveross.atwork.modules.image.component.ScaleGestureDetector.OnScaleGestureListener#onScale(com.foreveross.atwork.modules.image.component.ScaleGestureDetector)}
	 * <li>One {@link com.foreveross.atwork.modules.image.component.ScaleGestureDetector.OnScaleGestureListener#onScaleEnd(com.foreveross.atwork.modules.image.component.ScaleGestureDetector)}
	 * </ul>
	 */
	public interface OnScaleGestureListener {
		/**
		 * Responds to scaling events for a gesture in progress. Reported by
		 * pointer motion.
		 * 
		 * @param detector
		 *            The detector reporting the event - use this to retrieve
		 *            extended info about event state.
		 * @return Whether or not the detector should consider this event as
		 *         handled. If an event was not handled, the detector will
		 *         continue to accumulate movement until an event is handled.
		 *         This can be useful if an application, for example, only wants
		 *         to update scaling factors if the change is greater than 0.01.
		 */
		boolean onScale(ScaleGestureDetector detector, float mx, float my);

		/**
		 * Responds to the beginning of a scaling gesture. Reported by new
		 * pointers going down.
		 * 
		 * @param detector
		 *            The detector reporting the event - use this to retrieve
		 *            extended info about event state.
		 * @return Whether or not the detector should continue recognizing this
		 *         gesture. For example, if a gesture is beginning with a focal
		 *         point outside of a region where it makes sense,
		 *         onScaleBegin() may return false to ignore the rest of the
		 *         gesture.
		 */
		boolean onScaleBegin(ScaleGestureDetector detector);

		/**
		 * Responds to the end of a scale gesture. Reported by existing pointers
		 * going up.
		 * 
		 * Once a scale has ended, {@link com.foreveross.atwork.modules.image.component.ScaleGestureDetector#getFocusX()} and
		 * {@link com.foreveross.atwork.modules.image.component.ScaleGestureDetector#getFocusY()} will return the location of
		 * the pointer remaining on the screen.
		 * 
		 * @param detector
		 *            The detector reporting the event - use this to retrieve
		 *            extended info about event state.
		 */
		void onScaleEnd(ScaleGestureDetector detector);
	}

	/**
	 * A convenience class to extend when you only want to listen for a subset
	 * of scaling-related events. This implements all methods in
	 * {@link com.foreveross.atwork.modules.image.component.ScaleGestureDetector.OnScaleGestureListener} but does nothing.
	 * {@link com.foreveross.atwork.modules.image.component.ScaleGestureDetector.OnScaleGestureListener#onScale(com.foreveross.atwork.modules.image.component.ScaleGestureDetector)} returns
	 * {@code false} so that a subclass can retrieve the accumulated scale
	 * factor in an overridden onScaleEnd.
	 * {@link com.foreveross.atwork.modules.image.component.ScaleGestureDetector.OnScaleGestureListener#onScaleBegin(com.foreveross.atwork.modules.image.component.ScaleGestureDetector)} returns
	 * {@code true}.
	 */
	public static class SimpleOnScaleGestureListener implements
			OnScaleGestureListener {

		public boolean onScale(ScaleGestureDetector detector, float mx, float my) {
			return false;
		}

		public boolean onScaleBegin(ScaleGestureDetector detector) {
			return true;
		}

		public void onScaleEnd(ScaleGestureDetector detector) {
			// Intentionally empty
		}
	}

	/**
	 * This value is the threshold ratio between our previous combined pressure
	 * and the current combined pressure. We will only fire an onScale event if
	 * the computed ratio between the current and previous event pressures is
	 * greater than this value. When pressure decreases rapidly between events
	 * the position values can often be imprecise, as it usually indicates that
	 * the user is in the process of lifting a pointer off of the device. Its
	 * value was tuned experimentally.
	 */
	private static final float PRESSURE_THRESHOLD = 0.67f;

	private final Context mContext;
	private final OnScaleGestureListener mListener;
	private boolean mGestureInProgress;

	private MotionEvent mPrevEvent;
	private MotionEvent mCurrEvent;

	private float mMiddleX;
	private float mMiddleY;
	private float mPrevFingerDiffX;
	private float mPrevFingerDiffY;
	private float mCurrFingerDiffX;
	private float mCurrFingerDiffY;
	private float mCurrLen;
	private float mPrevLen;
	private float mScaleFactor;
	private float mCurrPressure;
	private float mPrevPressure;
	private long mTimeDelta;

	private final float mEdgeSlop;
	private float mRightSlopEdge;
	private float mBottomSlopEdge;
	private boolean mSloppyGesture;

	public ScaleGestureDetector(Context context, OnScaleGestureListener listener) {
		ViewConfiguration config = ViewConfiguration.get(context);
		mContext = context;
		mListener = listener;
		mEdgeSlop = config.getScaledEdgeSlop();
	}

	public boolean onTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		boolean handled = true;

		if (!mGestureInProgress) {
			
			switch (action & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_POINTER_DOWN: {
				// We have a new multi-finger gesture

				// as orientation can change, query the metrics in touch down
				DisplayMetrics metrics = mContext.getResources()  
						.getDisplayMetrics();
				mRightSlopEdge = metrics.widthPixels - mEdgeSlop;
				mBottomSlopEdge = metrics.heightPixels - mEdgeSlop;

				// Be paranoid in case we missed an event
				reset();

				mPrevEvent = MotionEvent.obtain(event);
				mTimeDelta = 0;

				setContext(event);

				// Check if we have a sloppy gesture. If so, delay
				// the beginning of the gesture until we're sure that's
				// what the user wanted. Sloppy gestures can happen if the
				// edge of the user's hand is touching the screen, for example.
				final float edgeSlop = mEdgeSlop;
				final float rightSlop = mRightSlopEdge;
				final float bottomSlop = mBottomSlopEdge;
				final float x0 = event.getRawX();
				final float y0 = event.getRawY();
				final float x1 = getRawX(event, 1);
				final float y1 = getRawY(event, 1);

				boolean p0sloppy = x0 < edgeSlop || y0 < edgeSlop
						|| x0 > rightSlop || y0 > bottomSlop;
				boolean p1sloppy = x1 < edgeSlop || y1 < edgeSlop
						|| x1 > rightSlop || y1 > bottomSlop;

				if (p0sloppy && p1sloppy) {
					mSloppyGesture = true;
				} else if (p0sloppy) {
					mSloppyGesture = true;
				} else if (p1sloppy) {
					mSloppyGesture = true;
				} else {
					
					mGestureInProgress = mListener.onScaleBegin(this);
					
				}
			}
				break;

			case MotionEvent.ACTION_MOVE:
				if (mSloppyGesture) {
//					Log.d(TAG, "scale gesture move mSloppyGesture");
					// Initiate sloppy gestures if we've moved outside of the
					// slop area.
					
					try { 
						final float edgeSlop = mEdgeSlop;
						final float rightSlop = mRightSlopEdge;
						final float bottomSlop = mBottomSlopEdge;
						final float x0 = event.getRawX();
						final float y0 = event.getRawY();
						final float x1 = getRawX(event, 1);
						final float y1 = getRawY(event, 1);

						boolean p0sloppy = x0 < edgeSlop || y0 < edgeSlop
								|| x0 > rightSlop || y0 > bottomSlop;
						boolean p1sloppy = x1 < edgeSlop || y1 < edgeSlop
								|| x1 > rightSlop || y1 > bottomSlop;

						if (p0sloppy && p1sloppy) {
						} else if (p0sloppy) {
						} else if (p1sloppy) {
						} else {
							mSloppyGesture = false;
							mGestureInProgress = mListener.onScaleBegin(this);
						}
					    } catch (IllegalArgumentException e) { 
					    }
					    
					    
					
				}
				break;

			case MotionEvent.ACTION_POINTER_UP:
				if (mSloppyGesture) {

				}
				break;
			}
		} else {
			// Transform gesture in progress - attempt to handle it
			switch (action & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_POINTER_UP:
				// Gesture ended
				setContext(event);

				if (!mSloppyGesture) {
					mListener.onScaleEnd(this);
				}

				reset();
				break;

			case MotionEvent.ACTION_CANCEL:
				if (!mSloppyGesture) {
					mListener.onScaleEnd(this);
				}

				reset();
				break;

			case MotionEvent.ACTION_MOVE:
//				Log.d(TAG, "scale gesture move");
				setContext(event);

				mMiddleX = (event.getX(0) + event.getX(1)) / 2;
				mMiddleY = (event.getY(0) + event.getY(1)) / 2;

				// Only accept the event if our relative pressure is within
				// a certain limit - this can help filter shaky data as a
				// finger is lifted.
				if (mCurrPressure / mPrevPressure > PRESSURE_THRESHOLD) {
					final boolean updatePrevious = mListener.onScale(this,
							mMiddleX, mMiddleY);

					if (updatePrevious) {
						mPrevEvent.recycle();
						mPrevEvent = MotionEvent.obtain(event);
					}
				}
				break;
			}
		}
		return handled;
	}

	/**
	 * MotionEvent has no getRawX(int) method; simulate it pending future API
	 * approval.
	 */
	private static float getRawX(MotionEvent event, int pointerIndex) {
		float rawx = 0;
		 try { 
			 float offset = event.getRawX() - event.getX();
			 rawx = event.getX(pointerIndex) + offset;
		    } catch (IllegalArgumentException e) { 
		    }
		    return rawx;
		
	}

	/**
	 * MotionEvent has no getRawY(int) method; simulate it pending future API
	 * approval.
	 */
	private static float getRawY(MotionEvent event, int pointerIndex) {
		float offset = event.getRawY() - event.getY();
		return event.getY(pointerIndex) + offset;
	}

	private void setContext(MotionEvent curr) {
		if (mCurrEvent != null) {
			mCurrEvent.recycle();
		}
		mCurrEvent = MotionEvent.obtain(curr);

		mCurrLen = -1;
		mPrevLen = -1;
		mScaleFactor = -1;

		final MotionEvent prev = mPrevEvent;

		final float px0 = prev.getX(0);
		final float py0 = prev.getY(0);
		final float px1 = prev.getX(1);
		final float py1 = prev.getY(1);
		final float cx0 = curr.getX(0);
		final float cy0 = curr.getY(0);
		final float cx1 = curr.getX(1);
		final float cy1 = curr.getY(1);

		final float pvx = px1 - px0;
		final float pvy = py1 - py0;
		final float cvx = cx1 - cx0;
		final float cvy = cy1 - cy0;
		mPrevFingerDiffX = pvx;
		mPrevFingerDiffY = pvy;
		mCurrFingerDiffX = cvx;
		mCurrFingerDiffY = cvy;

		mTimeDelta = curr.getEventTime() - prev.getEventTime();
		mCurrPressure = curr.getPressure(0) + curr.getPressure(1);
		mPrevPressure = prev.getPressure(0) + prev.getPressure(1);
	}

	private void reset() {
		if (mPrevEvent != null) {
			mPrevEvent.recycle();
			mPrevEvent = null;
		}
		if (mCurrEvent != null) {
			mCurrEvent.recycle();
			mCurrEvent = null;
		}
		mSloppyGesture = false;
		mGestureInProgress = false;
	}

	/**
	 * Returns {@code true} if a two-finger scale gesture is in progress.
	 * 
	 * @return {@code true} if a scale gesture is in progress, {@code false}
	 *         otherwise.
	 */
	public boolean isInProgress() {
		return mGestureInProgress;
	}

	public float getMiddleX() {
		return mMiddleX;
	}

	public float getMiddleY() {
		return mMiddleY;
	}

	/**
	 * Return the current distance between the two pointers forming the gesture
	 * in progress.
	 * 
	 * @return Distance between pointers in pixels.
	 */
	public float getCurrentSpan() {
		if (mCurrLen == -1) {
			final float cvx = mCurrFingerDiffX;
			final float cvy = mCurrFingerDiffY;
			mCurrLen = (float) Math.sqrt(cvx * cvx + cvy * cvy);
		}
		return mCurrLen;
	}

	/**
	 * Return the previous distance between the two pointers forming the gesture
	 * in progress.
	 * 
	 * @return Previous distance between pointers in pixels.
	 */
	public float getPreviousSpan() {
		if (mPrevLen == -1) {
			final float pvx = mPrevFingerDiffX;
			final float pvy = mPrevFingerDiffY;
			mPrevLen = (float) Math.sqrt(pvx * pvx + pvy * pvy);
		}
		return mPrevLen;
	}

	/**
	 * Return the scaling factor from the previous scale event to the current
	 * event. This value is defined as ({@link #getCurrentSpan()} /
	 * {@link #getPreviousSpan()}).
	 * 
	 * @return The current scaling factor.
	 */
	public float getScaleFactor() {
		if (mScaleFactor == -1) {
			mScaleFactor = getCurrentSpan() / getPreviousSpan();
		}
		return mScaleFactor;
	}

	/**
	 * Return the time difference in milliseconds between the previous accepted
	 * scaling event and the current scaling event.
	 * 
	 * @return Time difference since the last scaling event in milliseconds.
	 */
	public long getTimeDelta() {
		return mTimeDelta;
	}

	/**
	 * Return the event time of the current event being processed.
	 * 
	 * @return Current event time in milliseconds.
	 */
	public long getEventTime() {
		return mCurrEvent.getEventTime();
	}
}
