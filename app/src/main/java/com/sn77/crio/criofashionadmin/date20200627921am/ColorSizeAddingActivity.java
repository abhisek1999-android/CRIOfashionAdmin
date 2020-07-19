package com.sn77.crio.criofashionadmin.date20200627921am;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.naz013.colorslider.ColorSlider;

public class ColorSizeAddingActivity extends AppCompatActivity {

    TextView colorHexCode;
    RelativeLayout colorLayout;

    private ColorSlider.OnColorSelectedListener mListener = new ColorSlider.OnColorSelectedListener() {
        @Override
        public void onColorChanged(int position, int color) {
            updateView(color);
        }
    };

    private void updateView(int color) {

        colorLayout.setBackgroundColor(color);
        String hex = "#"+Integer.toHexString(color).substring(2);
        colorHexCode.setText(hex);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_size_adding);

        colorHexCode=findViewById(R.id.colorTextCode);
        colorLayout=findViewById(R.id.colorRelativeLayout);


        ColorSlider slider = findViewById(R.id.color_slider);
        slider.setSelectorColor(Color.GREEN);
        slider.setListener(mListener);

        ColorSlider sliderGradientArray = findViewById(R.id.color_slider_gradient_array);
        sliderGradientArray.setGradient(new int[]{Color.MAGENTA,Color.parseColor("#561571"),Color.parseColor("#1c2566"),Color.BLUE, Color.CYAN,Color.parseColor("#00352c"), Color.GREEN, Color.YELLOW, Color.RED,Color.BLACK,Color.WHITE}, 300);
        sliderGradientArray.setListener(mListener);

    }
}