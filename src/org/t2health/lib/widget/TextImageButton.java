package org.t2health.lib.widget;

import org.t2health.lib.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
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
		textView.setPadding(0, 0, 0, 0);
	}

	private void layout() {
		this.removeAllViews();
		
		// add the child views.
		switch(this.imagePosition) {
			case LEFT:
				this.setGravity(Gravity.CENTER_VERTICAL);
				this.setOrientation(HORIZONTAL);
				this.addView(imageView,
						new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.WRAP_CONTENT,
								LinearLayout.LayoutParams.FILL_PARENT
				));	
				this.addView(textView,
						new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.WRAP_CONTENT,
								LinearLayout.LayoutParams.FILL_PARENT,
								1.0f
				));
				break;
				
			case TOP:
				this.setGravity(Gravity.CENTER_HORIZONTAL);
				this.setOrientation(VERTICAL);
				this.addView(imageView,
						new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.FILL_PARENT,
								LinearLayout.LayoutParams.WRAP_CONTENT
				));	
				this.addView(textView,
						new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.FILL_PARENT,
								LinearLayout.LayoutParams.WRAP_CONTENT,
								1.0f
				));
				break;
				
			case RIGHT:
				this.setGravity(Gravity.CENTER_VERTICAL);
				this.setOrientation(HORIZONTAL);
				this.addView(textView,
						new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.WRAP_CONTENT,
								LinearLayout.LayoutParams.FILL_PARENT,
								1.0f
				));
				this.addView(imageView,
						new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.WRAP_CONTENT,
								LinearLayout.LayoutParams.FILL_PARENT
				));	
				break;
				
			case BOTTOM:
				this.setGravity(Gravity.CENTER_HORIZONTAL);
				this.setOrientation(VERTICAL);
				this.addView(textView,
						new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.FILL_PARENT,
								LinearLayout.LayoutParams.WRAP_CONTENT,
								1.0f
				));
				this.addView(imageView,
						new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.FILL_PARENT,
								LinearLayout.LayoutParams.WRAP_CONTENT
				));	
				break;
		}
		
		/*if(this.imagePosition == IMAGE_POSITION.LEFT || this.imagePosition == IMAGE_POSITION.TOP) {
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
		}*/
	}
	
	/**
	 * Return the button's drawable image, or null if no drawable has be assigned.
	 * @return
	 */
	public Drawable getImageDrawable() {
		return this.imageView.getDrawable();
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

	/**
	 * Set the position of the image relative to the text.
	 * @param pos
	 */
	public void setImagePosition(IMAGE_POSITION pos) {
		imagePosition = pos;
		layout();
	}
	
	/**
	 * Sets a drawable as the content of this button.
	 * @param resId the resource identifier of the the drawable
	 */
	public void setImageResource(int resId) {
		this.imageView.setImageResource(resId);
		this.imageView.setVisibility(View.VISIBLE);
	}
	
	/**
	 * Sets a drawable as the content of this button
	 * @param bm The bitmap to set.
	 */
	public void setImageBitmap(Bitmap bm) {
		this.imageView.setImageBitmap(bm);
		this.imageView.setVisibility(View.VISIBLE);
	}
	
	/**
	 * Sets a drawable as the content of this button.
	 * @param drawable The drawable to set
	 */
	public void setImageDrawable(Drawable drawable) {
		this.imageView.setImageDrawable(drawable);
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
	 * Set the visibility of the image in the button.
	 * @param visibility the visibility as defined in @see android.view.View
	 */
	public void setImageVisibility(int visibility) {
		this.imageView.setVisibility(visibility);
	}
	
	/**
	 * Set the visibility of the text in the button.
	 * @param visibility the visibility as defined in @see android.view.View
	 */
	public void setTextVisibility(int visibility) {
		this.textView.setVisibility(visibility);
	}
}
