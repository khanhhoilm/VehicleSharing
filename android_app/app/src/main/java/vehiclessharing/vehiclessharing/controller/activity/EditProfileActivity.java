package vehiclessharing.vehiclessharing.controller.activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Calendar;

import co.vehiclessharing.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vehiclessharing.vehiclessharing.api.RestManager;
import vehiclessharing.vehiclessharing.authentication.SessionManager;
import vehiclessharing.vehiclessharing.constant.Utils;
import vehiclessharing.vehiclessharing.database.DatabaseHelper;
import vehiclessharing.vehiclessharing.model.StatusResponse;
import vehiclessharing.vehiclessharing.model.User;
import vehiclessharing.vehiclessharing.utils.HashAlgorithm;
import vehiclessharing.vehiclessharing.utils.Helper;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    private ImageView avatarUser;
    private EditText txtFullName;
    private EditText txtPhoneNumber, txtEmail, txtPassword;
    private RadioButton rdMale;
    private RadioButton rdFemale;
    private RadioGroup groupGender;

    public static TextView txtBirthday;
    private EditText edAddress;
    private static Button btnSave;
    private Toolbar toolbar;

    private static int REQUEST_IMAGE_SDCARD = 100;//Value request activity pick image in SDcard
    private static int REQUEST_IMAGE_CAMERA = 200;//Value request activity take image using CAMERA
    private static int MY_CAMERA_REQUEST_CODE = 69;//Value request permission Camera
    public static int REQUEST_CHOOSE_ADDRESS = 1;
    private static Bitmap bmImageUser;//Instance to save image picked by user in SDcard / from camera

    private static boolean isFullNameChanged = false;//Controll fullname's user is changed or not
    private static boolean isPhoneNumberChanged = false;//Controll phonenumber's user is changed or not
    private static boolean isSexChanged = false;//Controll fullname's user is changed or not
    public static boolean isBirthdayChanged = false;//Controll fullname's user is changed or not
    public static boolean isAddressChanged = false;//Controll fullname's user is changed or not

    private java.util.Calendar calendar = java.util.Calendar.getInstance();
    private java.text.SimpleDateFormat sdf2 = new java.text.SimpleDateFormat("dd/MM/yyyy");

    private DatabaseHelper mDatabase;//Instance reference Realtime Database Firebase
    private static Drawable mDrawable;//Icon edittext when text invalid
    private User userInfo;
    private RestManager restManager;
    private String apiToken = "";
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//DO NOT ROTATE the screen even if the user is shaking his phone like mad

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        addControlls();
        setContentUI();
        addEvents();
    }

    /**
     * Set content to all fields
     */
    private void setContentUI() {
        // String fullName="";
        storage = FirebaseStorage.getInstance();
        txtFullName.setText(userInfo.getName());
        txtPhoneNumber.setText(userInfo.getPhone());
        //txtBirthday.setText("");
        if (userInfo.getBirthday() != null && !userInfo.equals("")) {
        //    txtBirthday.setText(sdf2.format(calendar));
            txtBirthday.setText(userInfo.getBirthday());
        }
        if (userInfo.getAddress() != null && !userInfo.equals("")) {
            edAddress.setText(userInfo.getAddress());
        }
        if (userInfo.getGender() == 0) {
            rdMale.setChecked(true);
        } else {
            rdFemale.setChecked(true);
        }

        // Log.d("image_path", String.valueOf(url));
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.temp);
        if (userInfo.getPicture() == null || userInfo.getPicture().equals("")) {
            if (userInfo.getAvatarLink() != null && !userInfo.getAvatarLink().equals("")) {
                try {
                    bitmap = BitmapFactory.decodeStream(Helper.getStreamByURL(userInfo.getAvatarLink()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            avatarUser.setImageBitmap(userInfo.getPicture());
        }
        avatarUser.setImageBitmap(bitmap);
    }

    private void addEvents() {
        rdFemale.setOnClickListener(this);
        rdMale.setOnClickListener(this);
        avatarUser.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        txtFullName.addTextChangedListener(this);
        txtPhoneNumber.addTextChangedListener(this);
        rdMale.setOnClickListener(this);
        rdFemale.setOnClickListener(this);
        txtBirthday.setOnClickListener(this);
        edAddress.setOnClickListener(this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Back button", "Back button click");
                onBackPressed();
            }
        });
    }

    private void addControlls() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_General);
        toolbar.setTitle(getString(R.string.edit_profile));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
            // if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        }

        avatarUser = (ImageView) findViewById(R.id.img_user);
        //      progressBar = (ProgressBar) findViewById(R.id.loading_progress_img);
        txtFullName = (EditText) findViewById(R.id.ed_full_name);
        txtPhoneNumber = (EditText) findViewById(R.id.ed_phone_number);
        edAddress = (EditText) findViewById(R.id.ed_address);
        txtBirthday = (TextView) findViewById(R.id.txt_birthday);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        groupGender = (RadioGroup) findViewById(R.id.rdgGender);
        rdMale = (RadioButton) findViewById(R.id.rd_male);
        rdFemale = (RadioButton) findViewById(R.id.rd_female);
        btnSave = (Button) findViewById(R.id.btn_save);


        //    mDatabase = FirebaseDatabase.getInstance().getReference();

        mDrawable = getResources().getDrawable(R.drawable.ic_warning_red_600_24dp);
        mDrawable.setBounds(0, 0, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight());

        mDatabase = new DatabaseHelper(this);

        loadInfoUser();
    }

    private void loadInfoUser() {
        SharedPreferences sharedPreferences = getSharedPreferences(SessionManager.PREF_NAME_LOGIN, MODE_PRIVATE);
        int userId = sharedPreferences.getInt(SessionManager.USER_ID, 0);
        apiToken = sharedPreferences.getString(SessionManager.KEY_SESSION, "");
        userInfo = mDatabase.getUser(userId);
    }

    private void showDatePicker() {
        DatePickerDialog.OnDateSetListener callBack = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //Hiển thị sự thay đổi theo người dùng
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.YEAR, year);
                txtBirthday.setText(sdf2.format(calendar.getTime()));
                displayButtonUpdate(true);

            }
        };


        DatePickerDialog datePickerDialog = new DatePickerDialog(this, callBack, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        //nếu đối số cuối =true thì định dạng 24h, =false định dạng 12h
        datePickerDialog.show();
    }


    /**
     * Pick image in SDcard
     */
    private void callIntentPickImg() {
        Intent intent = new Intent(
                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(
                Intent.createChooser(intent, "Select File"),
                REQUEST_IMAGE_SDCARD);
    }

    /**
     * Take picture using Camera
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void callIntentTakePicture() {
        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    MY_CAMERA_REQUEST_CODE);
            return;
        }
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAMERA);
    }


    /**
     * Visible button when user changed profile
     */
    public static void displayButtonUpdate(boolean isShow) {
        if (isShow) {
            btnSave.setAlpha(1);
        } else {
            btnSave.setAlpha(0.2f);
        }
    }


    /**
     * Update data profile
     *
     * @param file a local file
     */
    private void updateImage(Uri file) {

        if (file != null) {
            avatarUser.setImageURI(file);
            BitmapDrawable bitmapDrawable = (BitmapDrawable) avatarUser.getDrawable();
            userInfo.setPicture(bitmapDrawable.getBitmap());
            Log.d("Update Image", "path Image: " + file.toString());
            //   avatarUser.setImageBitmap(BitmapFactory.decodeFile(file));

            //  progressBar.setVisibility(View.VISIBLE);
            // Create a storage reference from our app
            StorageReference storageRef = storage.getReference();

            // Create a child reference
            // imagesRef now points to "images"
            StorageReference riversRef = storageRef.child("avatars/userid_" + userInfo.getId());
            UploadTask uploadTask = riversRef.putFile(file);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Toast.makeText(EditProfileActivity.this, "Uploads image unsuccessful!", Toast.LENGTH_SHORT).show();
                    Log.e(Utils.TAG_ERROR_UPLOAD_IMAGE_FIREBASE, String.valueOf(exception.getMessage()));
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(EditProfileActivity.this, "Uploads image successful!", Toast.LENGTH_SHORT).show();
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    displayButtonUpdate(true);
                }
            });
        }

    }

    /**
     * Internet is avaibalility
     *
     * @return true if can access internet
     */
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * Select image from Library or take picture using Camera
     */
    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    callIntentTakePicture();
                } else if (items[item].equals("Choose from Library")) {
                    callIntentPickImg();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_CHOOSE_ADDRESS) {
            getNewAddress(data);
        } else {
            if (resultCode == EditProfileActivity.RESULT_OK) {
                Uri targetUri = data.getData();
                try {
                    updateImage(targetUri);
//                bmImageUser = ImageClass.decodeUri(EditProfileActivity.this,targetUri,100);
//                bmImageUser = ImageClass.rotateImage(bmImageUser);//Rotating avatar's user before upload Storage Firebase
                } catch (Exception e) {
                    Log.e(Utils.TAG_ERROR_SELECT_IMAGE, String.valueOf(e.getMessage()));
                }
//            updateImage(getByteFromBitmap());
            }
        }
    }

    private void getNewAddress(Intent data) {
        Place place = PlaceAutocomplete.getPlace(this, data);

        if (edAddress != null && place != null) {
            edAddress.setText(place.getAddress());
            userInfo.setAddress((String) place.getAddress());
            displayButtonUpdate(true);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rd_male: {
                break;
            }
            case R.id.rd_female: {
            }
            case R.id.btn_save: {
                updateProfileUser();
                break;
            }
            case R.id.img_user: {
                selectImage();
                break;
            }
            case R.id.txt_birthday: {
                showDatePicker();
                break;
            }
            case R.id.ed_address: {
                try {
                    //   txtCurLocation.setText("");
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .build(this);
                    startActivityForResult(intent, REQUEST_CHOOSE_ADDRESS);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
                break;
            }
        }
    }

    private void updateProfileUser() {

        int radId = groupGender.getCheckedRadioButtonId();
        if (radId == R.id.rd_male) {
            userInfo.setGender(0);
        } else {
            userInfo.setGender(1);
        }
        userInfo.setName(txtFullName.getText().toString());
        userInfo.setEmail(txtEmail.getText().toString());
        //userInfo.setAvatarLink();
        userInfo.setBirthday(txtBirthday.getText().toString());

        String password ="";
        if(txtPassword.getText().toString().length()>0) {
            HashAlgorithm.md5(txtPassword.getText().toString());
        }
        restManager.getApiService().updateInfoUser(apiToken, userInfo.getName(), userInfo.getEmail(), userInfo.getAvatarLink(), password, userInfo.getGender(), userInfo.getAddress(), userInfo.getBirthday()).enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                if (response.isSuccessful() && response.body().getStatus().getError() == 0) {
                    Toast.makeText(EditProfileActivity.this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                    mDatabase.updateInfoUser(userInfo);

                    finish();
                }
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Cập nhật thông tin thất bại vui lòng thử lại", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        displayButtonUpdate(true);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
