package vehiclessharing.vehiclessharing.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import co.vehiclessharing.R;
import vehiclessharing.vehiclessharing.api.FavoriteUserAPI;

public class LikeUserActivity extends AppCompatActivity implements View.OnClickListener, FavoriteUserAPI.FavoriteCallback {

    private TextView mTxtLike, mTxtIntermediate;
    public static String PARTNER_ID = "partner_id";
    private int mPartnerId;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like_user);
        mPartnerId = getIntent().getExtras().getInt(PARTNER_ID, 0);
        addControls();
        addEvents();
    }

    private void addEvents() {
        mTxtLike.setOnClickListener(this);
        mTxtIntermediate.setOnClickListener(this);
    }

    private void addControls() {
        mTxtLike = findViewById(R.id.txtLike);
        mTxtIntermediate = findViewById(R.id.txtIntermediate);
        mProgressBar=findViewById(R.id.progressBar);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.txtLike) {
            mProgressBar.setVisibility(View.VISIBLE);
            addToFavorite();
        } else if (view.getId() == R.id.txtIntermediate) {
            backToMainActivity();
        }
    }

    private void backToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void addToFavorite() {
        if (mPartnerId != 0) {
            FavoriteUserAPI favoriteUserAPI = new FavoriteUserAPI(this);
            favoriteUserAPI.like(MainActivity.sessionId, mPartnerId);
        }
    }

    @Override
    public void favoriteSuccess() {
        mProgressBar.setVisibility(View.GONE);
        backToMainActivity();
    }

    @Override
    public void favoriteFailure() {
        Toast.makeText(this, "failure", Toast.LENGTH_SHORT).show();
        mProgressBar.setVisibility(View.GONE);
    }
}
