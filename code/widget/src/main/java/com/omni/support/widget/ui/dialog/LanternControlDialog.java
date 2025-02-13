package com.omni.support.widget.ui.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.omni.support.widget.R;

import top.defaults.colorpicker.ColorObserver;
import top.defaults.colorpicker.ColorPickerView;

/**
 * @author 邱永恒
 * @time 2019/7/29 10:16
 * @desc 彩灯控制
 */
public class LanternControlDialog extends DialogFragment {
    private static final String TAG = "ColorLightControlDialog";

    private ColorPickerView colorPicker;
    private Switch sw_open;
    private SeekBar sb_speed;
    private SeekBar sb_brightness;
    private SeekBar sb_effect;
    private TextView tv_speed;
    private TextView tv_brightness;
    private TextView tv_effect;
    private OnColorLightControlListener listener;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            int color = colorPicker.getColor();
            control(0, color, 0, 0, 0);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.widget_dialog_color_light, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initListener();
    }

    private void initView(View view) {
        colorPicker = view.findViewById(R.id.colorPicker);
        sw_open = view.findViewById(R.id.sw_open);
        sb_speed = view.findViewById(R.id.sb_speed);
        sb_brightness = view.findViewById(R.id.sb_brightness);
        sb_effect = view.findViewById(R.id.sb_effect);
        tv_speed = view.findViewById(R.id.tv_speed);
        tv_brightness = view.findViewById(R.id.tv_brightness);
        tv_effect = view.findViewById(R.id.tv_effect);

        sb_speed.setMax(254);
        sb_brightness.setMax(254);
        sb_effect.setMax(254);

        tv_speed.setText(String.valueOf(sb_speed.getProgress()));
        tv_brightness.setText(String.valueOf(sb_brightness.getProgress()));
        tv_effect.setText(String.valueOf(sb_effect.getProgress()));

        handler.postDelayed(() -> control(0, 0, 0, 0, 0), 500);
    }

    private void initListener() {
        colorPicker.setInitialColor(0);
        colorPicker.subscribe((color, fromUser, shouldPropagate) -> {
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, 1000);
        });

        sw_open.setOnCheckedChangeListener((compoundButton, b) -> control(b ? 2 : 1, 0, 0, 0, 0));

        sb_speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tv_speed.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int speed = sb_speed.getProgress();
                control(0, 0, speed, 0, 0);
            }
        });

        sb_brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tv_brightness.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int brightness = sb_brightness.getProgress();
                control(0, 0, 0, brightness, 0);
            }
        });

        sb_effect.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tv_effect.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int effect = sb_effect.getProgress();
                control(0, 0, 0, 0, effect);
            }
        });
    }

    public void control(int isOpen, int color, int speed, int brightness, int effect) {
        Log.d(TAG, "control");
        if (listener != null) {
            listener.onControl(isOpen, color, speed, brightness, effect);
        }
    }

    public void setOnColorLightControlListener(OnColorLightControlListener listener) {
        this.listener = listener;
    }

    public interface OnColorLightControlListener {
        void onControl(int openStatus, int color, int speed, int brightness, int effect);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        control(0, 0, 0, 0, 0);
        listener = null;
    }
}
