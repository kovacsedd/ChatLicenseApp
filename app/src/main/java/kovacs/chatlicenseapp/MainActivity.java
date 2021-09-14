package kovacs.chatlicenseapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import kovacs.chatlicenseapp.contact.Contact;
import kovacs.chatlicenseapp.databinding.ActivityMainBinding;
import kovacs.chatlicenseapp.fragments.ChatFragment;
import kovacs.chatlicenseapp.fragments.StatusFragment;
import kovacs.chatlicenseapp.profile.Profile;
import kovacs.chatlicenseapp.settings.SettingsTab;
import kovacs.chatlicenseapp.startup.SplashScreen;
import kovacs.chatlicenseapp.status.AddStatusActivity;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    public static Uri imageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setUpWithViewPager(binding.viewPager);
        binding.tabLayout.setupWithViewPager(binding.viewPager);
        setSupportActionBar(binding.toolbar);

        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changeFabIcon(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    // SET MENU SETTINGS
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_search:
                Toast.makeText(this, "menu_search", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menu_profile:
                startActivity(new Intent(this, Profile.class));
                break;

            case R.id.menu_settings:
                startActivity(new Intent(this, SettingsTab.class));
                break;

            case R.id.menu_leaveAccount:
                dialogLeaveAccount();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    // SET VIEW_PAGER
    private void setUpWithViewPager(ViewPager viewPager) {
        MainActivity.SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ChatFragment(), "Conversații");
        adapter.addFragment(new StatusFragment(), "Stare");

        viewPager.setAdapter(adapter);
    }

    // CHANGE SRC FROM FloatingActionButton WHILE CHANGE FRAGMENT
    private void changeFabIcon(int index) {
        binding.floatingButton.hide();
        binding.buttonEditStatus.setVisibility(View.GONE);

        new Handler().postDelayed(new Runnable() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void run() {
                switch (index) {
                    case 0:
                        binding.floatingButton.setImageDrawable(getDrawable(R.drawable.ic_baseline_chat_24));
                        break;

                    case 1:
                        binding.buttonEditStatus.setVisibility(View.VISIBLE);
                        binding.floatingButton.setImageDrawable(getDrawable(R.drawable.ic_baseline_photo_camera_24));
                        break;
                }
                binding.floatingButton.show();
            }
        }, 100);

        checkIndex(index);
    }

    private void checkIndex(final int index) {
        binding.floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index == 0) {
                    startActivity(new Intent(MainActivity.this, Contact.class));
                } else if (index == 1) {
                    // Toast.makeText(MainActivity.this, "Camera action ... ", Toast.LENGTH_SHORT).show();
                    // Open camera
                    checkCameraPermission();
                } else {
                    Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_LONG).show();
                }
            }
        });

        binding.buttonEditStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Add Status ...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @NotNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    231);

        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    232);
        } else {
            openCamera();
        }
    }

//    private void openCamera() {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        String timeStamp = new SimpleDateFormat("yyyyMMDD_HHmmss", Locale.getDefault()).format(new Date());
//        String imageFileName = "IMG_" + timeStamp + ".jpg";
//
//        try {
//            File file = File.createTempFile("IMG_" + timeStamp, ".jps", getExternalFilesDir(Environment.DIRECTORY_PICTURES));
//            imageUri = FileProvider.getUriForFile(Objects.requireNonNull(this), "kovacs.chatlicenseapp" + ".provider", file);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//            intent.putExtra("listPhotoName", imageFileName);
//            startActivityForResult(intent, 440);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private void openCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image Description");

        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, 440);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 440 && resultCode == RESULT_OK) {
            //uploadToFirebase();
            if (imageUri != null) {
                startActivity(new Intent(MainActivity.this, AddStatusActivity.class).putExtra("image", imageUri));
                finish();
            }
        }
    }

    private void dialogLeaveAccount() {
        AlertDialog.Builder builderAlertDialog = new AlertDialog.Builder(MainActivity.this);
        builderAlertDialog.setMessage("Dorești să părăsești contul?");
        builderAlertDialog.setPositiveButton("DA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, SplashScreen.class));
                finish();
            }
        }).setNegativeButton("NU", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builderAlertDialog.create();
        alertDialog.show();
    }
}