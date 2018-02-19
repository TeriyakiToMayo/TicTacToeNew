package com.wiley.fordummies.androidsdk.tictactoe;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Fragment for displaying contacts.
 *
 * Source: https://github.com/alfongj/android-recyclerview-contacts-example,
 *         https://developer.android.com/training/contacts-provider/retrieve-names.html
 *
 * Created by adamcchampion on 2017/08/16.
 */

public class ContactsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private ListView mContactsListView;

    @SuppressLint("InlinedApi")
    private static final String[] PROJECTION = {
        ContactsContract.Contacts._ID,
        ContactsContract.Contacts.LOOKUP_KEY,
        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
    };

    // The column index for the _ID column
    private static final int CONTACT_ID_INDEX = 0;
    // The column index for the LOOKUP_KEY column
    private static final int LOOKUP_KEY_INDEX = 1;


    // The contact's _ID value
    long mContactId;

    // The contact's LOOKUP_KEY
    String mContactKey;
    // A content URI for the selected contact
    Uri mContactUri;
    // An adapter that binds the result Cursor to the ListView
    private SimpleCursorAdapter mCursorAdapter;

    private static final int PERMISSION_REQUEST_READ_CONTACTS = 1;

    private final String TAG = getClass().getSimpleName();

    /*
 * Defines an array that contains column names to move from
 * the Cursor to the ListView.
 */
    @SuppressLint("InlinedApi")
    private final static String[] FROM_COLUMNS = {
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
    };

    /*
     * Defines an array that contains resource ids for the layout views
     * that get the Cursor column contents. The id is pre-defined in
     * the Android framework, so it is prefaced with "android.R.id"
     */
    private final static int[] TO_IDS = {
            R.id.contact_info
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contact_list, container, false);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContactsListView = (ListView) getActivity().findViewById(R.id.contact_list_view);

        requestContacts();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setSubtitle(getResources().getString(R.string.contacts));
            }
        }
        catch (NullPointerException npe) {
            Log.e(TAG, "Could not set subtitle");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasReadContactPermission()) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                ContactPermissionDeniedDialogFragment dialogFragment = new ContactPermissionDeniedDialogFragment();
                dialogFragment.show(fm, "contact_perm_denied");
            }
        }
    }


    private void requestContacts() {
        Log.d(TAG, "requestContacts()");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasReadContactPermission()) {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                        PERMISSION_REQUEST_READ_CONTACTS);
            }
            else {
                showContacts();
            }
        }
        else {
            showContacts();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean hasReadContactPermission() {
        return getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showContacts();
            }
            else {
                Log.e(TAG, "Error: Permission denied to read contacts");
                Toast.makeText(getActivity(), getResources().getString(R.string.read_contacts_permission_denied), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showContacts() {
        Log.d(TAG, "showContacts()");

        // Gets a CursorAdapter
        mCursorAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.list_item_contact,
                null,
                FROM_COLUMNS,
                TO_IDS,
                0);
        // Sets the adapter for the ListView
        mContactsListView.setAdapter(mCursorAdapter);

        // Initializes the loader
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                ContactsContract.Contacts.CONTENT_URI,
                PROJECTION,
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC"
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Put the result Cursor in the adapter for the ListView
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
