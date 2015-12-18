package org.opencorpora.sync;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import org.jetbrains.annotations.NotNull;

/**
 * Stub class only for data sync
 * @see <a href="http://developer.android.com/intl/ru/training/sync-adapters/creating-stub-provider.html" >
 */
public class TaskProvider extends ContentProvider {
    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public Cursor query(@NotNull Uri uri,
                        String[] projection,
                        String selection,
                        String[] selectionArgs,
                        String sortOrder) {
        return null;
    }

    @Override
    public String getType(@NotNull Uri uri) {
        return null;
    }

    @Override
    public Uri insert(@NotNull Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NotNull Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NotNull Uri uri,
                      ContentValues values,
                      String selection,
                      String[] selectionArgs) {
        return 0;
    }

}
