package fyp.iems;

import android.content.Context;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
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
 * Created by PC on 22-Apr-16.
 */
public class ShutdownActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, AdapterView.OnItemSelectedListener {

    private String Id;
    private String Text = "Data.txt";
    private String Folder = "FYP";
    private String Hash = "####";

    private static final int RESOLVE_CONNECTION_REQUEST_CODE = 1;

    private GoogleApiClient mGoogleApiClient;

    File mFile;
    FileOutputStream fileoutputStream;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        try {
            mFile = new File(getExternalFilesDir(Folder), Text);
            fileoutputStream = new FileOutputStream(mFile);

            fileoutputStream.write(Hash.getBytes());
            fileoutputStream.write("F".getBytes());
            fileoutputStream.write("99".getBytes());
            fileoutputStream.write(Hash.getBytes());
            fileoutputStream.write("A".getBytes());
            fileoutputStream.write("99".getBytes());
            fileoutputStream.write(Hash.getBytes());
            fileoutputStream.write("H".getBytes());
            fileoutputStream.write("99".getBytes());
            fileoutputStream.write(Hash.getBytes());
            fileoutputStream.write("S".getBytes());
            fileoutputStream.write("1".getBytes());
            fileoutputStream.write(Hash.getBytes());

            fileoutputStream.close();
            mGoogleApiClient.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(ShutdownActivity.this, RESOLVE_CONNECTION_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                Toast.makeText(this, "Unable to resolve. Not connected", Toast.LENGTH_SHORT).show();
            }
        } else {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), ShutdownActivity.this, 0).show();
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
                                    Toast.makeText(ShutdownActivity.this, "Cannot find DriveId. Are you authorized to view this file?", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                DriveId driveId = result.getDriveId();
                                DriveFile file = driveId.asDriveFile();
                                new EditContentsAsyncTask(ShutdownActivity.this).execute(file);
                            }
                        };

                        if (!result.getStatus().isSuccess()) {
                            Toast.makeText(ShutdownActivity.this, "Cannot create folder in the root.", Toast.LENGTH_SHORT).show();
                        } else {
                            boolean isFound = false;
                            for (Metadata m : result.getMetadataBuffer()) {
                                if (m.getTitle().equals("Data.txt")) {
                                    Toast.makeText(ShutdownActivity.this, "Folder exists", Toast.LENGTH_SHORT).show();
                                    String driveId = m.getDriveId().getResourceId();
                                    Drive.DriveApi.fetchDriveId(mGoogleApiClient, driveId).setResultCallback(idCallback);
                                    isFound = true;
                                    break;
                                }
                            }
                            if (!isFound) {
                                Toast.makeText(ShutdownActivity.this, "Folder not found; creating it.", Toast.LENGTH_SHORT).show();
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
                        outputStream.write("99".getBytes());
                        outputStream.write(Hash.getBytes());
                        outputStream.write("A".getBytes());
                        outputStream.write("99".getBytes());
                        outputStream.write(Hash.getBytes());
                        outputStream.write("H".getBytes());
                        outputStream.write("99".getBytes());
                        outputStream.write(Hash.getBytes());
                        outputStream.write("S".getBytes());
                        outputStream.write("1".getBytes());
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
        }
    };

    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
            ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Toast.makeText(ShutdownActivity.this, "Error adding file to Drive", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Id = result.getDriveFile().getDriveId().getResourceId();
                    Toast.makeText(ShutdownActivity.this, "File successfully added to Drive", Toast.LENGTH_SHORT).show();
                }
            };

    public void onConnectionSuspended(int i) {
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

    }

    /**
     * <p>Callback method to be invoked when an item in this view has been
     * selected. This callback is invoked only when the newly selected
     * position is different from the previously selected position or if
     * there was no selected item.</p>
     * <p/>
     * Impelmenters can call getItemAtPosition(position) if they need to access the
     * data associated with the selected item.
     *
     * @param parent   The AdapterView where the selection happened
     * @param view     The view within the AdapterView that was clicked
     * @param position The position of the view in the adapter
     * @param id       The row id of the item that is selected
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    /**
     * Callback method to be invoked when the selection disappears from this
     * view. The selection can disappear for instance when touch is activated
     * or when the adapter becomes empty.
     *
     * @param parent The AdapterView that now contains no selected item.
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public class EditContentsAsyncTask extends ApiClientAsyncTask<DriveFile, Void, Boolean> {

        public EditContentsAsyncTask(Context context) {
            super(context);
            Toast.makeText(ShutdownActivity.this, "Sequence Initiated", Toast.LENGTH_LONG).show();
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
                outputStream.write("99".getBytes());
                outputStream.write(Hash.getBytes());
                outputStream.write("A".getBytes());
                outputStream.write("99".getBytes());
                outputStream.write(Hash.getBytes());
                outputStream.write("H".getBytes());
                outputStream.write("99".getBytes());
                outputStream.write(Hash.getBytes());
                outputStream.write("S".getBytes());
                outputStream.write("1".getBytes());
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
