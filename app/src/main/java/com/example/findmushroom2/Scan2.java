package com.example.findmushroom2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.findmushroom2.ml.Densenet12110721;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class Scan2 extends AppCompatActivity {

    Button camera, gallery, rincianbtn;
    ImageView imageView;
    TextView result1,classified;
    TextView result2;
    TextView result3;
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

                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, 3);
                }
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

//            // Print the top three confidences and their indices
//            for (int i = 0; i < topThreeConfidences.length; i++) {
//                ("Top " + (i + 1) + " confidence: " + topThreeConfidences[i] + " (Index: " + classes[topThreeIndices[i]] + ")");
//            }

            Log.d("coba", "classifyImage: " + Arrays.toString(topThreeIndices));

            result1.setText("1. " + classes[topThreeIndices[0]] + " (" +  String.format("%.2f%%", topThreeConfidences[2] * 100) + ")");
            result2.setText("2. " + classes[topThreeIndices[1]] + " (" +  String.format("%.2f%%", topThreeConfidences[1] * 100) + ")");
            result3.setText("3. " + classes[topThreeIndices[2]] + " (" +  String.format("%.2f%%", topThreeConfidences[0] * 100) + ")");

            Log.d("oba", "classifyImage: "+ maxConfidence + " " + maxPos + " " + Arrays.toString(confidences));
            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == 3){
                Bitmap image = (Bitmap) data.getExtras().get("data");
                int dimension = Math.min(image.getWidth(), image.getHeight());
                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
                imageView.setImageBitmap(image);

                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                classifyImage(image);
            }else{
                Uri dat = data.getData();
                Bitmap image = null;
                try {
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), dat);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageView.setImageBitmap(image);

                rincianbtn.setVisibility(View.VISIBLE);
                classified.setVisibility(View.VISIBLE);
                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                classifyImage(image);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}