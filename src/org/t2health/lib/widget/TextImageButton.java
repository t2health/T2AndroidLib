package org.t2health.lib.widget;

import org.achartengine.renderer.XYMultipleSeriesRenderer.Orientation;
import org.t2health.lib.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A widget button that contain both text and an image.
 * @author robbiev
 *
 */
public class TextImageButton extends LinearLayout {
	/**
	 * The position of the image relative to the text.
	 * @author robbiev
	 *
	 */
	public static enum IMAGE_POSITION {
		LEFT,
		TOP,
		RIGHT,
		BOTTOM
	};
	private ImageView imageView;
	private Button textView;
	private IMAGE_POSITION imagePosition = IMAGE_POSITION.LEFT;

	public TextImageButton(Context context) {
		super(context);
		init();
		setImagePosition(imagePosition);
	}

	public TextImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
		setImagePosition(imagePosition);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextImageButton);
		for(int i = 0; i < a.getIndexCount(); ++i) {
			int index = a.getIndex(i);
			switch(index) {
				case R.styleable.TextImageButton_text:
					textView.setText(a.getString(index));
					break;

				case R.styleable.TextImageButton_src:
					imageView.setImageResource(a.getResourceId(index, 0));
					break;
					
				case R.styleable.TextImageButton_imagePosition:
					switch(a.getInt(index, 0)) {
						default:
						case 0:
							setImagePosition(IMAGE_POSITION.LEFT);
							break;
						case 1:
							setImagePosition(IMAGE_POSITION.TOP);
							break;
						case 2:
							setImagePosition(IMAGE_POSITION.RIGHT);
							break;
						case 3:
							setImagePosition(IMAGE_POSITION.BOTTOM);
							break;
					}
			}
		}
		a.recycle();
	}
	
	private void init() {
		if(this.getBackground() == null) {
			this.setBackgroundResource(android.R.drawable.btn_default);
		}

		this.setFocusable(true);
		this.setClickable(true);
		
		imageView = new ImageView(this.getContext());
		
		textView = new Button(this.getContext());
		textView.setClickable(false);
		textView.setFocusable(false);
		textView.setBackgroundColor(Color.TRANSPARENT);
		textView.setBackgroundDrawable(null);
	}

	private void layout() {
		this.removeAllViews();
		
		// set the orientation of this view.
		if(this.imagePosition == IMAGE_POSITION.LEFT) {
			this.setGravity(Gravity.CENTER_VERTICAL);
			this.setOrientation(HORIZONTAL);
			
		} else if(this.imagePosition == IMAGE_POSITION.TOP) {
			this.setGravity(Gravity.CENTER_HORIZONTAL);
			this.setOrientation(VERTICAL);
			
		} else if(this.imagePosition == IMAGE_POSITION.RIGHT) {
			this.setGravity(Gravity.CENTER_VERTICAL);
			this.setOrientation(HORIZONTAL);
			
		} else if(this.imagePosition == IMAGE_POSITION.BOTTOM) {
			this.setGravity(Gravity.CENTER_HORIZONTAL);
			this.setOrientation(VERTICAL);
		}
		
		// add the child views.
		if(this.imagePosition == IMAGE_POSITION.LEFT || this.imagePosition == IMAGE_POSITION.TOP) {
			this.addView(imageView,
					new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.WRAP_CONTENT,
							LinearLayout.LayoutParams.WRAP_CONTENT
			));	
			this.addView(textView,
					new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.WRAP_CONTENT,
							LinearLayout.LayoutParams.WRAP_CONTENT,
							1.0f
			));
			
		} else if(this.imagePosition == IMAGE_POSITION.RIGHT || this.imagePosition == IMAGE_POSITION.BOTTOM) {
			this.addView(textView,
					new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.WRAP_CONTENT,
							LinearLayout.LayoutParams.WRAP_CONTENT,
							1.0f
			));
			this.addView(imageView,
					new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.WRAP_CONTENT,
							LinearLayout.LayoutParams.WRAP_CONTENT
			));	
		}
	}

	/**
	 * Set the position of the image relative to the text.
	 * @param pos
	 */
	public void setImagePosition(IMAGE_POSITION pos) {
		imagePosition = pos;
		layout();
	}
	
	/**
	 * Set the resources to use for the image.
	 * @param resId
	 */
	public void setImageResource(int resId) {
		this.imageView.setImageResource(resId);
		this.imageView.setVisibility(View.VISIBLE);
	}

	/**
	 * Set the text of the button.
	 * @param text
	 */
	public void setText(CharSequence text) {
		this.textView.setText(text);
	}

	/**
	 * Set the text of the button.
	 * @param resid
	 */
	public void setText(int resid) {
		this.textView.setText(resid);
	}
	
	/**
	 * Get the text of the button.
	 * @return
	 */
	public CharSequence getText() {
		return this.textView.getText();
	}

	/**
	 * Get the image view that holds the image.
	 * @return
	 */
	public ImageView getImageView() {
		return this.imageView;
	}

	/**
	 * Get the text view.
	 * @return
	 */
	public TextView getTextView() {
		return this.textView;
	}
}
