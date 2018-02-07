package com.example.macbook.ear4music.service;

import android.content.Context;
import android.content.res.Resources;

import com.example.macbook.ear4music.R;
import com.example.macbook.ear4music.model.SubTask;

/**
 * Created by macbook on 20.02.2018.
 */

public final class Utils {
    public static String getSubTaskDescription(Resources res, SubTask subTask) {
        if (subTask.getIsPlayWithScale()) {
            return res.getString(R.string.notes_with_intonations);
        } else if (subTask.getNotesInSequence() > 1) {
            return res.getString(R.string.series_of_notes, subTask.getNotesInSequence());
        } else {
            return res.getString(R.string.just_notes);
        }
    }

    private String getStringFromResourcesByName(Context context, String resourceName) {
        String packageName = context.getPackageName();
        int resourceId = context.getResources().getIdentifier(resourceName,"string", packageName);
        return context.getResources().getString(resourceId);
    }
}
