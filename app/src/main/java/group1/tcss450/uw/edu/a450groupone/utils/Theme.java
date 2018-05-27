package group1.tcss450.uw.edu.a450groupone.utils;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import group1.tcss450.uw.edu.a450groupone.R;


public class Theme {

    private static int sTheme;
    public final static int THEME_DEFAULT = 0;
    public final static int THEME_FIRST = 1;
    public final static int THEME_SECOND = 2;
    public final static int THEME_THIRD = 3;

    /**
     * Set the theme of the Activity, and restart it by creating a new Activity of the same type.
     */
    public static void changeToTheme(AppCompatActivity activity, int theme)
    {
        sTheme = theme;
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }
    /** Set the theme of the activity, according to the configuration. */
    public static void onActivityCreateSetTheme(Activity activity)
    {
        switch (sTheme)
        {
            default:
            case THEME_DEFAULT:
                activity.setTheme(R.style.AppTheme);
                break;
            case THEME_FIRST:
                activity.setTheme(R.style.FirstTheme);
                break;
            case THEME_SECOND:
                activity.setTheme(R.style.SecondTheme);
                break;
            case THEME_THIRD:
                activity.setTheme(R.style.ThirdTheme);
                break;
        }
    }
}