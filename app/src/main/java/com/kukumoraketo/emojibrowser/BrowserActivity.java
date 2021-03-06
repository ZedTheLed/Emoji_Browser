package com.kukumoraketo.emojibrowser;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.kukumoraketo.emojibrowser.Emoji.Emoji.EmojiTone;
import com.kukumoraketo.emojibrowser.Emoji.EmojiDb.EmojiDb;
import com.kukumoraketo.emojibrowser.Emoji.EmojiDb.EmojiDbHelper;
import com.kukumoraketo.emojibrowser.Emoji.Providers.All_EmojiLite_Provider;
import com.kukumoraketo.emojibrowser.EmojiDisplay.BasicEmojiDisplayFragment;
import com.kukumoraketo.emojibrowser.EmojiDisplay.ChangeToneDialogFragment;
import com.kukumoraketo.emojibrowser.EmojiDisplay.EmojiDisplayAdapter;
import com.kukumoraketo.emojibrowser.EmojiDisplay.EmojiDisplayFragment;
import com.kukumoraketo.emojibrowser.EmojiDisplay.EmojiDisplayPagerAdapter;
import com.kukumoraketo.emojibrowser.EmojiDisplay.FragmentType;

public class BrowserActivity extends AppCompatActivity implements ChangeToneDialogFragment.ChangeToneListener,
                                                                   IActivitiContainingSearchEmojiFragment{

    private All_EmojiLite_Provider provider;
    private EmojiDisplayPagerAdapter pagerAdapter;

    private MenuItem toneIndicator;

    private InputMethodManager inputMethodManager;

    private boolean isLargeScreen; //  true if app is running on a tablet (large screen); false if application is running on a phone


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        try {

            this.provider = new All_EmojiLite_Provider(getApplicationContext(), EmojiTone.TONE_00);
        }
        catch (Exception e){
            // if some exception happened while setting provider clear the database when starting the app next time
            EmojiDb.setForceClearOnNextInstance(getApplicationContext());
            throw new RuntimeException();
        }

        final ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        pagerAdapter = new EmojiDisplayPagerAdapter(getSupportFragmentManager(), provider, getApplicationContext());
        viewPager.setAdapter(pagerAdapter);

        viewPager.setCurrentItem(1);

        //region sets if app is running on a large screen
        isLargeScreen =  (getApplicationContext().getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
        //endregion

        //region sets TabLayout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        //endregion

        //region sets Toolbar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                switch (itemId){

                    case R.id.mi_change_tone: {
                        ChangeToneDialogFragment changeToneDialog = ChangeToneDialogFragment.newInstance(provider.getTone());
                        changeToneDialog.show(getSupportFragmentManager(), "change_tone_dialog");
                        return true;
                    }
                    case R.id.mi_about: {
                        AboutDialogFragment aboutDialog = AboutDialogFragment.newInstance();
                        aboutDialog.show(getSupportFragmentManager(), "about_dialog");
                        return true;
                    }
                    default:
                        break;
                }

                return true;
            }
        });

        //endregion

        // sets InputMethodManager
        this.inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        //region sets that Keyboard hides when changing tabs
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0){
                    // If position is 0 it is on the search fragments and so it shows keyboard

                    // if app is running on a phone (small screen) and is in landscape, the keyboard won't open when going to search tab
                    if (! isLargeScreen){
                        boolean isInLandscape = getApplicationContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

                        if (isInLandscape)
                            return;
                    }

                    showKeyboard(getCurrentFocus());
                }
                else {
                    hideKeyboard(getCurrentFocus());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        //endregion

        //region Exceptions
        // code to handle uncaught exceptions
        // if uncaught exception happens clear the EmojiDb

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                EmojiDb.setForceClearOnNextInstance(getApplicationContext());
            }
        });
        //endregion

        getWindow().setBackgroundDrawableResource(R.color.color_material_gray_light);
    }

    @Override
    public void onChangeToneDialogPositiveClick(EmojiTone selectedTone) {

        this.provider.setTone(selectedTone);
        this.updateToneIndicator();

        // Do this if tone changed
        for (EmojiDisplayFragment fragment: pagerAdapter.fragments){
            if (fragment != null) {
                fragment.forceEmojiRefresh();
                fragment.forceGridViewRefresh();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_emoji_display, menu);

        this.toneIndicator =  menu.findItem(R.id.mi_change_tone);
        updateToneIndicator();

        return true;
    }

    private void updateToneIndicator(){
        this.toneIndicator.setIcon(EmojiTone.getIcon(this.provider.getTone()));
    }

    @Override
    public void hideKeyboard(View v) {
        if (v == null)
            return;

        this.inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public void showKeyboard(View v){
        this.inputMethodManager.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
    }
}
