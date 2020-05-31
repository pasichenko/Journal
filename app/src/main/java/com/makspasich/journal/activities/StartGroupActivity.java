package com.makspasich.journal.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makspasich.journal.R;
import com.makspasich.journal.data.utils.CircularTransformation;
import com.makspasich.journal.dialogs.AddGroupDialog;
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
    protected MaterialButton signOutButton;
    @BindView(R.id.create_group)
    protected MaterialButton createGroupButton;
    @BindView(R.id.join_to_group)
    protected MaterialButton joinGroupButton;
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
            AddGroupDialog custom = new AddGroupDialog(this);
            custom.show(getSupportFragmentManager(), "");
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
        joinGroupButton.setOnClickListener(v -> {
            String uid = mAuth.getCurrentUser().getUid();
            AlertDialog.Builder builder = new AlertDialog.Builder(StartGroupActivity.this);
            builder.setTitle(R.string.join_to_group)
                    .setCancelable(true)
                    .setMessage(getString(R.string.message_send_code, uid))
                    .setPositiveButton(R.string.send_code, (dialog, id) -> {
                        Intent sendIntent = new Intent(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, uid);
                        sendIntent.setType("text/plain");
                        Intent shareIntent = Intent.createChooser(sendIntent, null);
                        startActivity(shareIntent);
                    })
                    .setNegativeButton(R.string.copy_code, (dialog, id) -> {
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("label", uid);
                        clipboard.setPrimaryClip(clip);
                        dialog.cancel();
                    });
            AlertDialog alert = builder.create();
            alert.show();
        });
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
