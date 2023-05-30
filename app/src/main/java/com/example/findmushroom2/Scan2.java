package com.example.findmushroom2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.findmushroom2.ml.Densenet12110721;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class Scan2 extends AppCompatActivity {

    Button camera, gallery, rincianbtn;
    ImageView imageView;
    TextView result1,classified;
    TextView result2;
    TextView result3;
    File imageFile;
    private Uri imageUri; // Global variable to store the captured image URI
    int imageSize = 224;

    Database database = new Database();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan2);
        camera = findViewById(R.id.button);
        gallery = findViewById(R.id.button2);

        result1 = findViewById(R.id.result1);
        result2 = findViewById(R.id.result2);
        result3 = findViewById(R.id.result3);
        imageView = findViewById(R.id.imageView);
        rincianbtn = findViewById(R.id.rincianbtn);
        classified = findViewById(R.id.classified);

        rincianbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String hasil = result1.getText().toString();
                database.ambilData(hasil);
                startActivity(new Intent(getApplicationContext(), RincianJamur.class));
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                // Check if permission is granted
                if (ContextCompat.checkSelfPermission(Scan2.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted, proceed with creating the image file
                    // ...
                    // Create a file to save the image
                    imageFile = createImageFile();
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        imageUri = FileProvider.getUriForFile(Scan2.this, "com.example.findmushroom2.fileprovider", imageFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(takePictureIntent, 3);
                    }
//                    if (imageFile != null) {
//                        imageUri = FileProvider.getUriForFile(Scan2.this, "com.example.findmushroom2.fileprovider", imageFile);
//                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                        startActivityForResult(takePictureIntent, 3);
//                    }
                } else {
                    // Permission is not granted, request it
                    ActivityCompat.requestPermissions(Scan2.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);
                }



//                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//                    startActivityForResult(takePictureIntent, 3);
//                }
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(cameraIntent, 1);
            }
        });
    }

    private File createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";

        // Get the directory to store the image file
        File storageDir = Scan2.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            return File.createTempFile(
                    "IMG_${timeStamp}_",
                    ".jpg",
                    storageDir
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void classifyImage(Bitmap image){
        try {
            Densenet12110721 model = Densenet12110721.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            int pixel = 0;
            //iterate over each pixel and extract R, G, and B values. Add those values individually to the byte buffer.
            for(int i = 0; i < imageSize; i ++){
                for(int j = 0; j < imageSize; j++){
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            Densenet12110721.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            // find the index of the class with the biggest confidence.
            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidences.length; i++) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }

            float[] sortedConfidences = Arrays.copyOf(confidences, confidences.length);
            // Sort the array in descending order
            Arrays.sort(sortedConfidences);
            float[] topThreeConfidences = Arrays.copyOfRange(sortedConfidences, sortedConfidences.length - 3, sortedConfidences.length);

            // Get the indices of the top three confidences
            int[] topThreeIndices = getTop3Indices(confidences);

            String[] classes = {"Beracun_AmanitaBisporigera",
                    "Beracun_AmanitaMuscaria",
                    "Beracun_AmanitaPhalloides",
                    "Beracun_AmanitaRubescens",
                    "JamurKonsumsi_JamurEnoki",
                    "JamurKonsumsi_JamurKancing",
                    "JamurKonsumsi_JamurKuping",
                    "JamurKonsumsi_JamurMaitake",
                    "JamurKonsumsi_JamurPorcini",
                    "JamurKonsumsi_JamurTiram"};

            Log.d("coba", "classifyImage: " + Arrays.toString(topThreeIndices));

            setResult(
                    result1,
                    topThreeConfidences[2],
                    "Jamur tidak terdeteksi",
                    "1. " + classes[topThreeIndices[0]] + " (" +  String.format("%.2f%%", topThreeConfidences[2] * 100) + ")"
            );

            setResult(
                    result2,
                    topThreeConfidences[1],
                    "",
                    "2. " + classes[topThreeIndices[1]] + " (" +  String.format("%.2f%%", topThreeConfidences[1] * 100) + ")"
            );

            setResult(
                    result3,
                    topThreeConfidences[0],
                    "",
                    "3. " + classes[topThreeIndices[2]] + " (" +  String.format("%.2f%%", topThreeConfidences[0] * 100) + ")"
            );

            Log.d("oba", "classifyImage: "+ maxConfidence + " " + maxPos + " " + Arrays.toString(confidences));
            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
    }

    private void setResult(TextView tv, float confidence, String underThresholdText, String normalText) {
        float THRESHOLD = 0.5F;

        if (confidence < THRESHOLD) {
            tv.setText(underThresholdText);
        } else {
            tv.setText(normalText);
        }
    }

    public static int[] getTop3Indices(float[] confidences) {
        Integer[] indices = new Integer[confidences.length];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = i;
        }
        Arrays.sort(indices, (i1, i2) -> {
            return Float.compare(confidences[i2], confidences[i1]); // sort in descending order
        });
        return new int[]{indices[0], indices[1], indices[2]};
    }

    public Bitmap getBitmapFromUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == 3){
                imageView.setImageURI(imageUri);
                Bitmap image = getBitmapFromUri(imageUri);
                if (image != null) {
                    int dimension = Math.min(image.getWidth(), image.getHeight());
                    image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
//                    imageView.setImageBitmap(image);

                    image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                    classifyImage(image);
                }
//                Bitmap image = (Bitmap) data.getExtras().get("data");
//                int dimension = Math.min(image.getWidth(), image.getHeight());
//                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
//                imageView.setImageBitmap(image);
                // Assuming you have the image URI stored in a variable called "imageUri"
//                imageView.setImageURI(imageUri);
//                Bitmap bitmap = getBitmapFromUri(imageUri);

//                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
//                classifyImage(image);
            }else{
                Uri dat = data.getData();
                Bitmap image = null;
                try {
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), dat);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageView.setImageBitmap(image);
//                imageView.setImageURI(imageUri);

                rincianbtn.setVisibility(View.VISIBLE);
                classified.setVisibility(View.VISIBLE);
                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                classifyImage(image);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}