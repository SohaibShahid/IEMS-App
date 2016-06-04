package fyp.iems;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by PC on 13-Apr-16.
 */
public class OperateHeaterActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, AdapterView.OnItemSelectedListener {

    //Declarations
    EditText editTemp;
    Button mainButton;

    public String Id;
    private String HeaterValue;
    private String ACValue = "50";
    private String Text = "Data.txt";
    private String Folder = "FYP";
    private String Hash = "####";
    private Spinner spinner;
    private static final String[]HeaterList = {"Heater Threshold","00","01","02","03","04","05","06","07","08","09","10"};

    private static final int RESOLVE_CONNECTION_REQUEST_CODE = 1;

    private GoogleApiClient mGoogleApiClient;

    File mFile;
    FileOutputStream fileoutputStream;

    TextView currentTemperatureField, weatherIcon, cityField, updatedField;
    Typeface weatherFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Sets what is displayed on the screen
        super.onCreate(savedInstanceState);
        setContentView(R.layout.operateac);
        getSupportActionBar().hide();

        weatherFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "Fonts/weathericons-regular-webfont.ttf");

        cityField = (TextView) findViewById(R.id.city_field);
        updatedField = (TextView) findViewById(R.id.updated_field);
        currentTemperatureField = (TextView) findViewById(R.id.current_temperature_field);
        weatherIcon = (TextView) findViewById(R.id.weather_icon);
        weatherIcon.setTypeface(weatherFont);

        //Google's Method:
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        //Link .java and .xml data
        editTemp = (EditText) findViewById(R.id.editTemp);
        mainButton = (Button) findViewById(R.id.mainButton);
        spinner = (Spinner) findViewById(R.id.spinnerAC);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(OperateHeaterActivity.this,
                android.R.layout.simple_spinner_item, HeaterList);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        //listens to when the main button is pressed
        mainButton.setOnClickListener(this);

        Functions.placeIdTask asyncTask = new Functions.placeIdTask(new Functions.AsyncResponse() {
            public void processFinish(String weather_city, String weather_description, String weather_temperature, String weather_humidity, String weather_pressure, String weather_updatedOn, String weather_iconText, String sun_rise) {

                cityField.setText(weather_city);
                updatedField.setText(weather_updatedOn);
                currentTemperatureField.setText(weather_temperature);
                weatherIcon.setText(Html.fromHtml(weather_iconText));
            }
        });
        asyncTask.execute("33.7139", "73.0247"); //  asyncTask.execute("Latitude", "Longitude")
    }

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        if (parent.getItemAtPosition(position).toString() == "Heater Threshold") {
            HeaterValue = "00";
        } else {
            HeaterValue = parent.getItemAtPosition(position).toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        HeaterValue = "5";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        try {
            mFile = new File(getExternalFilesDir(Folder), Text);
            fileoutputStream = new FileOutputStream(mFile);

            fileoutputStream.write(Hash.getBytes());
            fileoutputStream.write("F".getBytes());
            fileoutputStream.write(editTemp.getText().toString().getBytes());
            fileoutputStream.write(Hash.getBytes());
            fileoutputStream.write("A".getBytes());
            fileoutputStream.write(ACValue.getBytes());
            fileoutputStream.write(Hash.getBytes());
            fileoutputStream.write("H".getBytes());
            fileoutputStream.write(HeaterValue.getBytes());
            fileoutputStream.write(Hash.getBytes());
            fileoutputStream.write("S".getBytes());
            fileoutputStream.write("0".getBytes());
            fileoutputStream.write(Hash.getBytes());

            fileoutputStream.close();
            mGoogleApiClient.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, RESOLVE_CONNECTION_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                Toast.makeText(this, "Unable to resolve. Not connected", Toast.LENGTH_SHORT).show();
            }
        } else {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            //...
            case RESOLVE_CONNECTION_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    mGoogleApiClient.connect();
                }
                break;
        }
    }

    public void onConnected(Bundle bundle) {


        Query query = new Query.Builder()
                .addFilter(Filters.and(
                        Filters.contains(SearchableField.TITLE, "Data.txt"),
                        Filters.eq(SearchableField.TRASHED, Boolean.FALSE),
                        Filters.eq(SearchableField.IS_PINNED, Boolean.TRUE),
                        Filters.eq(SearchableField.STARRED, Boolean.TRUE)))
                .build();
        Drive.DriveApi.query(mGoogleApiClient, query)
                .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
                    @Override
                    public void onResult(DriveApi.MetadataBufferResult result) {

                        final ResultCallback<DriveApi.DriveIdResult> idCallback = new ResultCallback<DriveApi.DriveIdResult>() {
                            @Override
                            public void onResult(DriveApi.DriveIdResult result) {
                                if (!result.getStatus().isSuccess()) {
                                    Toast.makeText(OperateHeaterActivity.this, "Cannot find DriveId. Are you authorized to view this file?", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                    DriveId driveId = result.getDriveId();
                    DriveFile file = driveId.asDriveFile();
                                new EditContentsAsyncTask(OperateHeaterActivity.this).execute(file);
                            }
                        };

                        if (!result.getStatus().isSuccess()) {
                            Toast.makeText(OperateHeaterActivity.this, "Cannot create folder in the root.", Toast.LENGTH_SHORT).show();
                        } else {
                            boolean isFound = false;
                            for (Metadata m : result.getMetadataBuffer()) {
                                if (m.getTitle().equals("Data.txt")) {
                                    Toast.makeText(OperateHeaterActivity.this, "Folder exists", Toast.LENGTH_SHORT).show();
                                    String driveId = m.getDriveId().getResourceId();
                                    Drive.DriveApi.fetchDriveId(mGoogleApiClient, driveId).setResultCallback(idCallback);
                                    isFound = true;
                                    break;
                                }
                            }
                            if (!isFound) {
                                Toast.makeText(OperateHeaterActivity.this, "Folder not found; creating it.", Toast.LENGTH_SHORT).show();
                                Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(mContentsCallback);
                            }
                            }
                        }
                    });
        }

    /*callback after creating the file, can get file info out of the result*/
        final private ResultCallback<DriveApi.DriveContentsResult> mContentsCallback = new ResultCallback<DriveApi.DriveContentsResult>() {

            @Override
            public void onResult(@NonNull DriveApi.DriveContentsResult driveContentsResult) {

                if (!driveContentsResult.getStatus().isSuccess()) {
                    return;
                }

                final DriveContents mDriveContents = driveContentsResult.getDriveContents();

                new Thread() {
                    @Override
                    public void run() {

                        try {
                            OutputStream outputStream = mDriveContents.getOutputStream();
                            outputStream.write(Hash.getBytes());
                            outputStream.write("F".getBytes());
                            outputStream.write(editTemp.getText().toString().getBytes());
                            outputStream.write(Hash.getBytes());
                            outputStream.write("A".getBytes());
                            outputStream.write(ACValue.getBytes());
                            outputStream.write(Hash.getBytes());
                            outputStream.write("H".getBytes());
                            outputStream.write(HeaterValue.getBytes());
                            outputStream.write(Hash.getBytes());
                            outputStream.write("S".getBytes());
                            outputStream.write("0".getBytes());
                            outputStream.write(Hash.getBytes());
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        MetadataChangeSet mMetaData = new MetadataChangeSet.Builder()
                                .setTitle("Data.txt")
                                .setPinned(true)
                                .setStarred(true).build();

                        Drive.DriveApi.getRootFolder(mGoogleApiClient)
                                .createFile(mGoogleApiClient, mMetaData, mDriveContents)
                                .setResultCallback(fileCallback);
                    }
                }.start();
                //Toast.makeText(OperateACActivity.this, driveContentsResult.getDriveContents().getDriveId().getResourceId(),Toast.LENGTH_SHORT).show();
            }
        };

    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
            ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Toast.makeText(OperateHeaterActivity.this, "Error adding file to Drive", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Id = result.getDriveFile().getDriveId().getResourceId();
                    Toast.makeText(OperateHeaterActivity.this, "File successfully added to Drive", Toast.LENGTH_SHORT).show();
                }
            };

    public void onConnectionSuspended(int i) {
    }

    public class EditContentsAsyncTask extends ApiClientAsyncTask<DriveFile, Void, Boolean> {

        public EditContentsAsyncTask(Context context) {
            super(context);
            Toast.makeText(OperateHeaterActivity.this, "File Added to Drive", Toast.LENGTH_LONG).show();
        }


        protected Boolean doInBackgroundConnected(DriveFile... args) {
            DriveFile file = args[0];
            try {
                DriveApi.DriveContentsResult driveContentsResult = file.open(
                        getGoogleApiClient(), DriveFile.MODE_WRITE_ONLY, null).await();
                if (!driveContentsResult.getStatus().isSuccess()) {
                    return false;
                }
                DriveContents driveContents = driveContentsResult.getDriveContents();
                OutputStream outputStream = driveContents.getOutputStream();
                outputStream.write(Hash.getBytes());
                outputStream.write("F".getBytes());
                    outputStream.write(editTemp.getText().toString().getBytes());
                    outputStream.write(Hash.getBytes());
                    outputStream.write("A".getBytes());
                    outputStream.write(ACValue.getBytes());
                    outputStream.write(Hash.getBytes());
                    outputStream.write("H".getBytes());
                    outputStream.write(HeaterValue.getBytes());
                    outputStream.write(Hash.getBytes());
                outputStream.write("S".getBytes());
                outputStream.write("0".getBytes());
                outputStream.write(Hash.getBytes());
                com.google.android.gms.common.api.Status status =
                        driveContents.commit(getGoogleApiClient(), null).await();
                return status.getStatus().isSuccess();
            } catch (IOException e) {
            }
            return false;
        }
    }
}
