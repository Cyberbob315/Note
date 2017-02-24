package nhannt.note.base;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;

/**
 * Created by nhannt on 24/02/2017.
 * A Square card view
 */

public class SquareCardView extends CardView {

    public SquareCardView(Context context) {
        super(context);
    }

    public SquareCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
