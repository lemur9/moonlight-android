package com.limelight.preferences;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * A remote-friendly replacement for Android's ListPreference dialog.
 * Some Android TV firmwares show an empty list for the framework dialog.
 */
public class TvListPreference extends ListPreference {
    public TvListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TvListPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TvListPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public TvListPreference(Context context) {
        super(context);
    }

    @Override
    protected void onClick() {
        final Context context = getContext();
        final Dialog dialog = new Dialog(context);
        final int padding = dp(context, 24);
        final List<Button> buttons = new ArrayList<>();

        ScrollView scrollView = new ScrollView(context);
        scrollView.setFocusable(false);
        scrollView.setFocusableInTouchMode(false);

        LinearLayout content = new LinearLayout(context);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setFocusable(false);
        content.setPadding(padding, padding, padding, padding);
        scrollView.addView(content);

        TextView title = new TextView(context);
        title.setText(getTitle());
        title.setTextColor(Color.BLACK);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
        title.setGravity(Gravity.CENTER);
        content.addView(title, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        CharSequence[] entries = getEntries();
        CharSequence[] values = getEntryValues();
        int selectedIndex = 0;
        for (int i = 0; i < entries.length; i++) {
            final String value = values[i].toString();
            final boolean selected = value.equals(getValue());
            if (selected) {
                selectedIndex = i;
            }

            final Button button = new Button(context);
            button.setId(View.generateViewId());
            button.setAllCaps(false);
            button.setText(entries[i]);
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            button.setTextColor(Color.BLACK);
            button.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
            button.setPadding(padding, 0, padding, 0);
            button.setFocusable(true);
            button.setFocusableInTouchMode(true);
            updateButtonBackground(button, selected, false);
            button.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    updateButtonBackground(button, selected, hasFocus);
                }
            });
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (callChangeListener(value)) {
                        setValue(value);
                    }
                    dialog.dismiss();
                }
            });
            LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, dp(context, 64));
            buttonParams.topMargin = dp(context, 8);
            content.addView(button, buttonParams);
            buttons.add(button);
        }

        for (int i = 0; i < buttons.size(); i++) {
            Button button = buttons.get(i);
            button.setNextFocusUpId(buttons.get(i == 0 ? 0 : i - 1).getId());
            button.setNextFocusDownId(buttons.get(i == buttons.size() - 1 ? i : i + 1).getId());
        }

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(scrollView);
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(dp(context, 720), WindowManager.LayoutParams.WRAP_CONTENT);
        }
        if (!buttons.isEmpty()) {
            buttons.get(selectedIndex).requestFocus();
        }
    }

    private static void updateButtonBackground(Button button, boolean selected, boolean focused) {
        GradientDrawable background = new GradientDrawable();
        background.setColor(focused ? Color.rgb(122, 192, 245) :
                (selected ? Color.rgb(210, 235, 255) : Color.rgb(240, 240, 240)));
        background.setCornerRadius(dp(button.getContext(), 4));
        button.setBackground(background);
    }

    private static int dp(Context context, int value) {
        return (int) (value * context.getResources().getDisplayMetrics().density + 0.5f);
    }
}
