package com.makspasich.journal.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makspasich.journal.App;
import com.makspasich.journal.R;
import com.makspasich.journal.data.utils.CircularTransformation;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class StartGroupActivity extends AppCompatActivity {
    private static final String TAG = "StartGroupActivity";

    private Unbinder mUnbinder;

    private final FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private final DatabaseReference mRootReference;

    //region BindView
    @BindView(R.id.avatar_user)
    protected ImageView avatarUser;
    @BindView(R.id.display_name)
    protected TextView displayNameUser;
    @BindView(R.id.sign_out)
    protected Button signOutButton;
    @BindView(R.id.create_group)
    protected Button createGroupButton;
    @BindView(R.id.join_to_group)
    protected Button joinGroupButton;
    //endregion

    public StartGroupActivity() {
        mAuth = FirebaseAuth.getInstance();
        mRootReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_group);
        mUnbinder = ButterKnife.bind(this);

        signOutButton.setOnClickListener(v -> signOut());
        createGroupButton.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(StartGroupActivity.this);
            builder.setTitle("Use website")
                    .setMessage("You need use website " + App.URL_CREATE_GROUP)
                    .setNegativeButton("Go to website", (dialog, id) -> {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(App.URL_CREATE_GROUP));
                        startActivity(browserIntent);
                        dialog.cancel();
                    });
            AlertDialog alert = builder.create();
            alert.show();
        });
        if (mAuth.getCurrentUser() != null) {
            displayNameUser.setText(mAuth.getCurrentUser().getDisplayName());
            Uri photoUri = mAuth.getCurrentUser().getPhotoUrl();
            Picasso.get()
                    .load(photoUri)
                    .placeholder(R.drawable.account_circle_outline)
                    .error(R.drawable.ic_warning)
                    .transform(new CircularTransformation(0))
                    .into(avatarUser);
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    private void signOut() {
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Intent intent = new Intent(StartGroupActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }
}
