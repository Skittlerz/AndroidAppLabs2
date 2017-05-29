package com.comp262.braun.imageswitcher;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

public class MainActivity extends AppCompatActivity {

    private ImageSwitcher imgSwitcher;
    private Button btnViewBender, btnViewSpaceman;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgSwitcher = (ImageSwitcher) findViewById(R.id.imageSwitcher);
        //set the animation of the imageswitcher
        imgSwitcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        imgSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
        btnViewBender = (Button) findViewById(R.id.button2);
        btnViewSpaceman = (Button) findViewById(R.id.button);

        //To use a imageswitcher the interface viewfactory must be implemented
        //the method makeView() must be used
        //in this context, it creates an imageview that will be added to the imageswitcher
        imgSwitcher.setFactory(new ViewSwitcher.ViewFactory(){
            @Override
            public View makeView(){
                ImageView myView = new ImageView(getApplicationContext());
                myView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                myView.setLayoutParams(new ImageSwitcher.LayoutParams(
                        ActionBar.LayoutParams.WRAP_CONTENT,ActionBar.LayoutParams.WRAP_CONTENT));
                return myView;
            }
        });

        btnViewBender.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(getApplicationContext(), "View Bender", Toast.LENGTH_LONG).show();
                imgSwitcher.setImageResource(R.mipmap.bender);
            }
        });

        btnViewSpaceman.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(getApplicationContext(), "View Spaceman", Toast.LENGTH_LONG).show();
                imgSwitcher.setImageResource(R.mipmap.spaceman);
            }
        });
    }
}
