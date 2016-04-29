package com.leroymerlin.pandroid.ui.animation;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.leroymerlin.pandroid.R;

/**
 * Created by florian on 20/10/2015.
 */
public class ViewInfosContainer implements Parcelable {


    public Class<? extends View> viewClass;
    public int viewId;
    public Integer backgroundColor;
    public int[] padding;
    public float[] position;
    public int[] size;
    public int textColor;
    public float textSize;
    private int textGravity;

    public ViewInfosContainer() {
    }

    public ViewInfosContainer(View view, View parent) {
        size = AnimUtils.getViewSize(view);
        this.position = AnimUtils.getPositionRelativeTo(view, parent);
        padding = new int[]{view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom()};

        Drawable drawable = view.getBackground();
        if (drawable instanceof ColorDrawable) {
            backgroundColor = ((ColorDrawable) drawable).getColor();
        } else {
            backgroundColor = view.getResources().getColor(R.color.transparent);
        }

        viewId = view.getId();
        viewClass = view.getClass();

        if (view instanceof TextView) {
            textColor = ((TextView) view).getCurrentTextColor();
            textSize = ((TextView) view).getTextSize();
            textGravity = ((TextView) view).getGravity();
        }
    }

    protected ViewInfosContainer(Parcel in) {
        viewClass = (Class<? extends View>) in.readSerializable();
        viewId = in.readInt();
        backgroundColor = (Integer) in.readSerializable();
        padding = in.createIntArray();
        position = in.createFloatArray();
        size = in.createIntArray();
        textColor = in.readInt();
        textSize = in.readFloat();
        textGravity = in.readInt();
    }

    public static final Creator<ViewInfosContainer> CREATOR = new Creator<ViewInfosContainer>() {
        @Override
        public ViewInfosContainer createFromParcel(Parcel in) {
            return new ViewInfosContainer(in);
        }

        @Override
        public ViewInfosContainer[] newArray(int size) {
            return new ViewInfosContainer[size];
        }
    };

    public void applyOn(View view) {
        view.setX(position[0]);
        view.setY(position[1]);
        view.setId(viewId);
        if (backgroundColor !=null)
            view.setBackgroundColor(backgroundColor);
        view.getLayoutParams().width = size[0];
        view.getLayoutParams().height = size[1];
        view.setPadding(padding[0], padding[1], padding[2], padding[3]);
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(textColor);
            ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            ((TextView) view).setGravity(textGravity);

        }
    }

    public int getWidth() {
        return size[0];
    }

    public int getHeight() {
        return size[1];
    }

    public float getX() {
        return position[0];
    }

    public float getY() {
        return position[1];
    }

    public int[] getCenter() {
        return new int[]{(int) (getX() + getWidth() / 2), (int) (getY() + getHeight() / 2)};
    }


    @Override
    public boolean equals(Object o) {
        if (o instanceof ViewInfosContainer) {
            return ((ViewInfosContainer) o).viewId == viewId;
        }
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return viewId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(viewClass);
        dest.writeInt(viewId);
        dest.writeSerializable(backgroundColor);
        dest.writeIntArray(padding);
        dest.writeFloatArray(position);
        dest.writeIntArray(size);
        dest.writeInt(textColor);
        dest.writeFloat(textSize);
        dest.writeInt(textGravity);
    }
}
