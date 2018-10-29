package in.learncodeonline.imagelabellingwithfirebase;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions;
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabel;
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabelDetector;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetector;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetectorOptions;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.util.List;

import in.learncodeonline.imagelabellingwithfirebase.Helper.InternetCheck;

public class MainActivity extends AppCompatActivity {
    private CameraView cameraKitView;
    Button bdetect;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        cameraKitView =(CameraView) findViewById(R.id.camera);
        bdetect=(Button)findViewById(R.id.btn_detect);

        bdetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cameraKitView.start();
                cameraKitView.captureImage();


            }
        });
        cameraKitView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {



            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {

                Bitmap bitmap=cameraKitImage.getBitmap();
                bitmap=Bitmap.createScaledBitmap(bitmap,cameraKitView.getWidth(),cameraKitView.getHeight(),false);
                cameraKitView.stop();

                rundetector(bitmap);

            }



            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });
    }

    private void rundetector(Bitmap bitmap) {

        final FirebaseVisionImage image=FirebaseVisionImage.fromBitmap(bitmap);
      new InternetCheck(new InternetCheck.Consumer() {
          @Override
          public void accept(boolean internert) {


              if(internert)
              {

                  FirebaseVisionCloudDetectorOptions options=new FirebaseVisionCloudDetectorOptions.Builder().setMaxResults(1).build();//get 1 Resultwithn highest confidence threshold
                  FirebaseVisionCloudLabelDetector detector=FirebaseVision.getInstance().getVisionCloudLabelDetector(options);
                  detector.detectInImage(image)
                          .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionCloudLabel>>() {
                              @Override
                              public void onSuccess(List<FirebaseVisionCloudLabel> firebaseVisionCloudLabels) {

                                  processdataresult(firebaseVisionCloudLabels);

                              }
                          })
                  .addOnFailureListener(new OnFailureListener() {
                      @Override
                      public void onFailure(@NonNull Exception e) {


                          Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();

                      }
                  });
              }
              else
              {
                  FirebaseVisionLabelDetectorOptions options=new FirebaseVisionLabelDetectorOptions
                          .Builder()
                          .setConfidenceThreshold(0.8f)
                          .build();//get 1 Resultwithn highest confidence threshold
                  FirebaseVisionLabelDetector detector=FirebaseVision.getInstance().getVisionLabelDetector(options);
                  detector.detectInImage(image)
                          .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionLabel>>() {
                              @Override
                              public void onSuccess(List<FirebaseVisionLabel> firebaseVisionLabels) {

                                  processdataresult1(firebaseVisionLabels);

                              }
                          })
                          .addOnFailureListener(new OnFailureListener() {
                              @Override
                              public void onFailure(@NonNull Exception e) {

                              }
                          });

              }
          }
      });


    }

    private void processdataresult(List<FirebaseVisionCloudLabel> firebaseVisionCloudLabels) {

        for(FirebaseVisionCloudLabel label:firebaseVisionCloudLabels)
        {
            Toast.makeText(getApplicationContext(),"Cloud Result:"+label.getLabel(),Toast.LENGTH_LONG).show();
        }
    }
    private void processdataresult1(List<FirebaseVisionLabel> firebaseVisionCloudLabels) {

        for(FirebaseVisionLabel label:firebaseVisionCloudLabels)
        {
            Toast.makeText(getApplicationContext(),"Local Result:"+label.getLabel(),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        cameraKitView.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraKitView.start();
    }

    @Override
    protected void onPause() {
        cameraKitView.stop();
        super.onPause();
    }

    @Override
    protected void onStop() {
        cameraKitView.stop();
        super.onStop();
    }
}
