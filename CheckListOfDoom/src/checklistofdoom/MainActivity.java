package checklistofdoom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import listviews.*;

import com.example.checklistofdoom.R;

import util.BackgroundContainer;
import util.Cheeses;
import util.Identifier;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;


public class MainActivity extends Activity {

	private Identifier identifier;
	private boolean mItemPressed = false;
	private boolean mSwiping = false;
	private ListView mListView;
	private ListViewItemIdentifierAdapter mAdapter;
	private static final int SWIPE_DURATION = 250;
	private static final int MOVE_DURATION = 150;

	private HashMap<Long, Integer> mItemIdTopMap = new HashMap<Long, Integer>();
	private BackgroundContainer mBackgroundContainer;
	
	private Button addButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//		 Inflate login procedure
//		 Intent intent = new Intent(this, LoginActivity.class);
//		 startActivityForResult(intent, 1);
		 
		run();
	}

	private void run() {
		setContentView(R.layout.activity_list_view);

		mListView = (ListView) findViewById(R.id.list_view);
		addButton = (Button) findViewById(R.id.add_button);
		addButton.setOnClickListener(addButtonOnClickListener);
		
		List<ListItem> values = new ArrayList<ListItem>();
//		for (int i = 0; i < 4; i++) {
//			values.add(new ListItem(Cheeses.sCheeseStrings[i], this));
//		}

		mBackgroundContainer = (BackgroundContainer) findViewById(R.id.backgroundContainer1);

		mAdapter = new ListViewItemIdentifierAdapter(this,
				R.layout.single_listview_item_identifier, R.id.stuff, values, mTouchListener);
		mListView.setAdapter(mAdapter);
		mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case (1):
			if (resultCode == Activity.RESULT_OK) {
				Bundle dataBundle = data.getExtras();
				identifier = new Identifier(dataBundle.getString("username"),
						dataBundle.getString("identifier"));
			}
			break;
		}
	}
	
	View.OnClickListener addButtonOnClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mAdapter.add(new ListItem("HAHAHAH", MainActivity.this));
		}
	};

	/**
	 * Handle touch events to fade/move dragged items as they are swiped out
	 */
	private View.OnTouchListener mTouchListener = new View.OnTouchListener() {

		float mDownX;
		private int mSwipeSlop = -1;

		@Override
		public boolean onTouch(final View v, MotionEvent event) {
			if (mSwipeSlop < 0) {
				mSwipeSlop = ViewConfiguration.get(MainActivity.this)
						.getScaledTouchSlop();
			}
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (mItemPressed) {
					// Multi-item swipes not handled
					return false;
				}
				mItemPressed = true;
				mDownX = event.getX();
				break;
			case MotionEvent.ACTION_CANCEL:
				v.setAlpha(1);
				v.setTranslationX(0);
				mItemPressed = false;
				break;
			case MotionEvent.ACTION_MOVE: {
				float x = event.getX() + v.getTranslationX();
				float deltaX = x - mDownX;
				float deltaXAbs = Math.abs(deltaX);
				if (!mSwiping) {
					if (deltaXAbs > mSwipeSlop) {
						mSwiping = true;
						mListView.requestDisallowInterceptTouchEvent(true);
						mBackgroundContainer.showBackground(v.getTop(),
								v.getHeight());
					}
				}
				if (mSwiping) {
					v.setTranslationX((x - mDownX));
					v.setAlpha(1 - deltaXAbs / v.getWidth());
				}
			}
				break;
			case MotionEvent.ACTION_UP: {
				// User let go - figure out whether to animate the view out, or
				// back into place
				if (mSwiping) {
					float x = event.getX() + v.getTranslationX();
					float deltaX = x - mDownX;
					float deltaXAbs = Math.abs(deltaX);
					float fractionCovered;
					float endX;
					float endAlpha;
					final boolean remove;
					if (deltaXAbs > v.getWidth() / 4) {
						// Greater than a quarter of the width - animate it out
						fractionCovered = deltaXAbs / v.getWidth();
						endX = deltaX < 0 ? -v.getWidth() : v.getWidth();
						endAlpha = 0;
						remove = true;
					} else {
						// Not far enough - animate it back
						fractionCovered = 1 - (deltaXAbs / v.getWidth());
						endX = 0;
						endAlpha = 1;
						remove = false;
					}
					// Animate position and alpha of swiped item
					// NOTE: This is a simplified version of swipe behavior, for
					// the
					// purposes of this demo about animation. A real version
					// should use
					// velocity (via the VelocityTracker class) to send the item
					// off or
					// back at an appropriate speed.
					long duration = (int) ((1 - fractionCovered) * SWIPE_DURATION);
					mListView.setEnabled(false);
					v.animate().setDuration(duration).alpha(endAlpha)
							.translationX(endX).withEndAction(new Runnable() {
								@Override
								public void run() {
									// Restore animated values
									v.setAlpha(1);
									v.setTranslationX(0);
									if (remove) {
										animateRemoval(mListView, v);
									} else {
										mBackgroundContainer.hideBackground();
										mSwiping = false;
										mListView.setEnabled(true);
									}
								}
							});
				}
			}
				mItemPressed = false;
				break;
			default:
				return false;
			}
			return true;
		}
	};

	/**
	 * This method animates all other views in the ListView container (not
	 * including ignoreView) into their final positions. It is called after
	 * ignoreView has been removed from the adapter, but before layout has been
	 * run. The approach here is to figure out where everything is now, then
	 * allow layout to run, then figure out where everything is after layout,
	 * and then to run animations between all of those start/end positions.
	 */
	private void animateRemoval(final ListView listview, View viewToRemove) {
		int firstVisiblePosition = listview.getFirstVisiblePosition();
		for (int i = 0; i < listview.getChildCount(); ++i) {
			View child = listview.getChildAt(i);
			if (child != viewToRemove) {
				int position = firstVisiblePosition + i;
				long itemId = mAdapter.getItemId(position);
				mItemIdTopMap.put(itemId, child.getTop());
			}
		}
		// Delete the item from the adapter
		int position = mListView.getPositionForView(viewToRemove);
		mAdapter.remove(mAdapter.getItem(position));

		final ViewTreeObserver observer = listview.getViewTreeObserver();
		observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {
				observer.removeOnPreDrawListener(this);
				boolean firstAnimation = true;
				int firstVisiblePosition = listview.getFirstVisiblePosition();
				for (int i = 0; i < listview.getChildCount(); ++i) {
					final View child = listview.getChildAt(i);
					int position = firstVisiblePosition + i;
					long itemId = mAdapter.getItemId(position);
					Integer startTop = mItemIdTopMap.get(itemId);
					int top = child.getTop();
					if (startTop != null) {
						if (startTop != top) {
							int delta = startTop - top;
							child.setTranslationY(delta);
							child.animate().setDuration(MOVE_DURATION)
									.translationY(0);
							if (firstAnimation) {
								child.animate().withEndAction(new Runnable() {
									public void run() {
										mBackgroundContainer.hideBackground();
										mSwiping = false;
										mListView.setEnabled(true);
									}
								});
								firstAnimation = false;
							}
						}
					} else {
						// Animate new views along with the others. The catch is
						// that they did not
						// exist in the start state, so we must calculate their
						// starting position
						// based on neighboring views.
						int childHeight = child.getHeight()
								+ listview.getDividerHeight();
						startTop = top + (i > 0 ? childHeight : -childHeight);
						int delta = startTop - top;
						child.setTranslationY(delta);
						child.animate().setDuration(MOVE_DURATION)
								.translationY(0);
						if (firstAnimation) {
							child.animate().withEndAction(new Runnable() {
								public void run() {
									mBackgroundContainer.hideBackground();
									mSwiping = false;
									mListView.setEnabled(true);
								}
							});
							firstAnimation = false;
						}
					}
				}
				mItemIdTopMap.clear();
				return true;
			}
		});
	}
}
