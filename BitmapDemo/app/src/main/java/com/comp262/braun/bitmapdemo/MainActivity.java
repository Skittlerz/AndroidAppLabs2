package com.comp262.braun.bitmapdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.StringBuilderPrinter;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    int sampleSize = 2;
    int imageId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        refreshImage();
    }

    public void scaleDown(View view){
        //increased the sampleSize  of the image
        //which makes the image smaller (ex. 4 == 1/4 of the original image, 8 == 1/8 of the original image)
        if (sampleSize < 8){
            sampleSize++;
            refreshImage();
        }
    }

    public void scaleUp(View view){
        //decrease the sampleSize of the image
        //which makes the image larger (ex. 4 == 1/4 of the original image, 8 == 1/8 of the original image)
        //sampleSize of 1 is the full image
        if (sampleSize > 2){
            sampleSize--;
            refreshImage();
        }
    }

    private void refreshImage(){
        //bitmap is an image file that can store images independently of the display device
        BitmapFactory.Options options = new BitmapFactory.Options();
        //this line allows the bitmap to be decoded without actually loading the bitmap
        options.inJustDecodeBounds = true;
        //here image1 is passed to the decodeResource method
        //will return null for the bitmap (we do not want the actual image here)
        //options will be populated with the image properties
        BitmapFactory.decodeResource(getResources(), R.drawable.image1, options);
        //reads the dimensions and image type of the bitmap
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        String imageType = options.outMimeType;

        StringBuilder imageInfo = new StringBuilder();

        //assign the appropriate image resource depending on the imageId selected
        int id = R.drawable.image1;
        if(imageId == 2){
            id = R.drawable.image2;
            imageInfo.append("Image 2.");
        }else if (imageId == 3){
            id = R.drawable.image3;
            imageInfo.append("Image 3.");
        }else if (imageId == 4){
            id = R.drawable.image4;
            imageInfo.append("Image 4.");
        }else{
            imageInfo.append("Image 1");
        }
        imageInfo.append(" Original Dimension: " + imageWidth + " x " + imageHeight);
        imageInfo.append(". MIME type: " + imageType);
        //if value is > 1, requests the decoder to subsample the original image, returning a smaller image
        //uses a final value based on powers of 2, any other value will be rounded down to the nearest power of 2
        options.inSampleSize = sampleSize;
        //now set this to false, we want a bitmap to be returned
        //as it will be used to populate the image view
        options.inJustDecodeBounds = false;
        Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), id, options);
        ImageView imageView1 = (ImageView) findViewById(R.id.image_view1);
        //display the image by setting the bitmap
        imageView1.setImageBitmap(bitmap1);

        //display the text information
        TextView sampleSizeText = (TextView) findViewById(R.id.sample_size);
        sampleSizeText.setText("" + sampleSize);
        TextView infoText = (TextView) findViewById(R.id.image_info);
        infoText.setText(imageInfo.toString());
    }

    public void changeImage(View view){
        //changes the image id
        //which will change image in the refreshImage() method
        if (imageId < 4){
            imageId++;
        }else {
            imageId = 1;
        }
        refreshImage();
    }
}
