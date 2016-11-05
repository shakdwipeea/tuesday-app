package com.shakdwipeea.tuesday.profile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.auth.AuthActivity;
import com.shakdwipeea.tuesday.databinding.ActivityProfileBinding;
import com.shakdwipeea.tuesday.util.DeviceStorage;
import com.shakdwipeea.tuesday.util.Util;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ProfileActivity extends AppCompatActivity
        implements ProfileContract.View, ProfileContract.IntentActions {
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 123;
    public static String TAG = "ProfileActivity";

    public static String PROFILE_IMAGE_EXTRA = "profilePic";

    private static final int PHOTO_PICKER_REQUEST_CODE = 260;
    private static final int REQUEST_IMAGE_CAPTURE = 582;

    // we don't want to subscribe to presenter in case the intent has been launched
    // for changing the profile pic as the presenter downloads the high res profile pic
    private boolean profileChangeIntentLaunched;

    private WasteAdapter arrayAdapter;

    ActivityProfileBinding binding;

    private ProfileContract.Presenter presenter;
    private Drawable thumbnailDrawable;

    private String currentPhotoPath;

    MaterialDialog progressBar;

    @Override
    protected void onResume() {
        super.onResume();
        if (!profileChangeIntentLaunched) presenter.subscribe();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        setSupportActionBar(binding.toolbar);

        binding.toolbar.inflateMenu(R.menu.menu_profile);

        profileChangeIntentLaunched = false;

        presenter = new ProfilePresenter(this);
        binding.setHandler(presenter);
        binding.setIntentHandler(this);

        // display the low res profile pic initially
        String profilePic = getIntent().getStringExtra(PROFILE_IMAGE_EXTRA);
        Log.d(TAG, "Profile url " + profilePic);

        if (profilePic == null) {
            displayError("Profile pic not provided");
        } else {
            getLowResDrawable(profilePic);
        }

//        arrayAdapter =new WasteAdapter(items, this);

//        binding.profileToolbarContainer.scrollableview
//                .setLayoutManager(new LinearLayoutManager(this));
//        binding.profileToolbarContainer.scrollableview.setAdapter(arrayAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change_picture:
                openImageMenu();
                return true;

            case R.id.action_logout:
                presenter.logout();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PHOTO_PICKER_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    final Uri imageUri = data.getData();
                    presenter.updateProfilePic(this, imageUri);
                } else {
                    // TODO: 19-10-2016 add error handling
                    new File(currentPhotoPath).delete();
                }
                break;
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    DeviceStorage.galleryAddPic(this, currentPhotoPath);
                    presenter.updateProfilePic(currentPhotoPath);
                } else {
                    // TODO: 19-10-2016 add error handling
                }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    openCamera();

                } else {
                    displayError("Cannot save photo then");
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void launchAuth() {
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
        finish();
    }

    public void displayError(String error) {
        Snackbar.make(binding.getRoot(), error, Snackbar.LENGTH_SHORT)
                .show();
    }

    // a hack to get the thumbnail as placeholder
    // see https://github.com/square/picasso/issues/383
    private void getLowResDrawable(String profilePic) {
        Picasso.with(this)
                .load(profilePic)
                .into(binding.profilePic);
    }

    @Override
    public void displayProfilePic(String url) {
        Picasso.with(this)
                .load(url)
                //.placeholder(thumbnailDrawable)
                .into(binding.profilePic);
    }

    @Override
    public void displayProfilePic(Bitmap image) {
        Util.resizeBitmapTo(image, binding.profilePic.getHeight(), binding.profilePic.getWidth())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(bitmap -> binding.profilePic.setImageBitmap(bitmap))
                .subscribe();
    }

    @Override
    public void displayProfilePicFromPath(String photoPath) {
        Util.resizeBitmapTo(photoPath,
                binding.profilePic.getHeight(), binding.profilePic.getWidth())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(bitmap -> binding.profilePic.setImageBitmap(bitmap))
                .doOnError(throwable -> displayError(throwable.getMessage()))
                .onErrorResumeNext(Observable.empty())
                .subscribe();
    }

    @Override
    public void displayDefaultPic() {
        binding.profilePic.setImageResource(R.drawable.ic_users_11);
    }

    @Override
    public void displayName(String name) {
        binding.toolbar.setTitle(name);
    }

    @Override
    public void displayTuesId(String tuesId) {
        binding.content.tuesid.setText(tuesId);
    }

    @Override
    public void openPhotoPicker() {
        profileChangeIntentLaunched = true;
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, PHOTO_PICKER_REQUEST_CODE);
    }

    @Override
    public void openCamera() {
        if (!hasPermission()) {
            return;
        }

        profileChangeIntentLaunched = true;

        try {
            // create the file on device
            File imageFile = DeviceStorage.createImageFile(this);
            currentPhotoPath = imageFile.getAbsolutePath();
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            // check if anyone is present to handle the camera action
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // get the uri for file using a fileprovider
                Uri photoURI = Uri.fromFile(imageFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                // take the picture
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } else {
                displayError("Could not find any camera app. :/");
            }
        } catch (IOException e) {
            displayError(e.getMessage());
        }
    }

    private boolean hasPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL_STORAGE);

            return false;
        }

        return true;
    }

    @Override
    public void openImageMenu() {
        new MaterialDialog.Builder(this)
                .title("Change Profile Picture")
                .items(R.array.items)
                .itemsCallbackSingleChoice(-1, (dialog, view, which, text) -> {
                    /**
                     * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                     * returning false here won't allow the newly selected radio button to actually be selected.
                     **/
                    switch (which) {
                        case 0:
                            openCamera();
                            return true;
                        case 1:
                            openPhotoPicker();
                            return true;
                        case 2:
                            presenter.deleteProfilePic();
                            return true;
                    }

                    return true;
                })
                .positiveText("Select")
                .show();
    }

    @Override
    public void setProgressBar(boolean show) {
        if (show) {
            progressBar = new MaterialDialog.Builder(this)
                    .title("Please Wait")
                    .content("Updating profile")
                    .progress(true, 0)
                    .show();
        } else {
            progressBar.dismiss();
        }
    }

}
