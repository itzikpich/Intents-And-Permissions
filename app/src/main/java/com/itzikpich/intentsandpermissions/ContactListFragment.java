package com.itzikpich.intentsandpermissions;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ContactListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG="ContactListFragment";
    private ContactListFragment fragment;
    // Defines the text expression
    @SuppressLint("InlinedApi")
    private static final String SELECTION = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " LIKE ?";
//    private static final String SELECTION = ContactsContract.Data.LOOKUP_KEY + " = ?";

    String searchString = "%" + "" + "%";
    String[] selectionArgs = { "" };

    private static final String SORT_ORDER = ContactsContract.Contacts.SORT_KEY_PRIMARY;

    @SuppressLint("InlinedApi")
    private static final String[] PROJECTION = {
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.LOOKUP_KEY,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.Data.PHOTO_URI,
    };

    @SuppressLint("InlinedApi")
    private final static String[] FROM_COLUMNS = {
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.Data.PHOTO_URI,
            ContactsContract.Contacts._ID,
//            ContactsContract.CommonDataKinds.Phone.NUMBER,
    };

    private final static int[] TO_IDS = {
            R.id.contacts_list_item_name,
            R.id.contacts_list_item_id
    };

    // The column index for the _ID column
    private static final int CONTACT_ID_INDEX = 0;
    // The column index for the CONTACT_KEY column
    private static final int CONTACT_KEY_INDEX = 1;

    static int DETAILS_QUERY_ID = 0;

    long contactId;
    String contactKey;
    Uri contactUri;
    private SimpleCursorAdapter cursorAdapter;

    public ContactListFragment() {}

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragment = this;
        // Initializes the loader
//        LoaderManager.getInstance(this).initLoader(DETAILS_QUERY_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_contacts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView contactsListView = view.findViewById(R.id.contact_list_view);
        cursorAdapter = new SimpleCursorAdapter(view.getContext(), R.layout.contacts_list_item, null, FROM_COLUMNS, TO_IDS, 0);
        contactsListView.setAdapter(cursorAdapter);
        contactsListView.setOnItemClickListener((parent, view1, position, id) -> {
            Cursor cursor = cursorAdapter.getCursor();
            cursor.moveToPosition(position);
            contactId = cursor.getLong(CONTACT_ID_INDEX);
            contactKey = cursor.getString(CONTACT_KEY_INDEX);
            contactUri = ContactsContract.Contacts.getLookupUri(contactId, ContactsContract.Contacts.LOOKUP_KEY);
        });

        ((AppCompatEditText)view.findViewById(R.id.list_editText)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchString = "%" + s.toString() + "%";
            }

            @Override
            public void afterTextChanged(Editable s) {
                LoaderManager.getInstance(fragment).restartLoader(DETAILS_QUERY_ID, null, fragment);
            }
        });

//        getContacts();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable @org.jetbrains.annotations.Nullable Bundle args) {
        if (id == DETAILS_QUERY_ID) {
//            searchString = "%" + "ארזו" + "%";
            selectionArgs[0] = searchString;
            return new CursorLoader(
                    getActivity(),
                    ContactsContract.Contacts.CONTENT_URI,
                    PROJECTION,
                    SELECTION,
                    selectionArgs,
                    SORT_ORDER
            );
        }
        throw new IllegalStateException("Unexpected value: " + id);
    }

    @Override
    public void onLoadFinished(@NonNull @NotNull Loader<Cursor> loader, Cursor cursor) {
        if (loader.getId() == DETAILS_QUERY_ID) {
//            List<String> contacts = contactsFromCursor(cursor);
//            Log.d(TAG, "onLoadFinished: " + contacts);
            cursorAdapter.swapCursor(cursor);
        } else {
            throw new IllegalStateException("Unexpected value: " + loader.getId());
        }
    }

    @Override
    public void onLoaderReset(@NonNull @NotNull Loader<Cursor> loader) {
        if (loader.getId() == DETAILS_QUERY_ID) {
            cursorAdapter.swapCursor(null);
        } else {
            throw new IllegalStateException("Unexpected value: " + loader.getId());
        }
    }

    private void getContacts() {
        ContentResolver cr = getActivity().getContentResolver();
        Cursor cursor = cr.query(
                ContactsContract.Contacts.CONTENT_URI,
                PROJECTION,
                SELECTION,
                selectionArgs,
                SORT_ORDER
        );
        if (cursor.moveToFirst()) {
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phones = cr.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " LIKE ?",
                    null,
                    null
            );
            while (phones.moveToNext()) {
                String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                int type = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                Log.d(TAG, "getContacts type: "+type);
                Log.d(TAG, "getContacts number: "+number);
                switch (type) {
                    case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                        // do something with the Home number here...
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                        // do something with the Mobile number here...
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                        // do something with the Work number here...
                        break;
                }
            }
            phones.close();
        }
        cursor.close();
    }

    private List<String> contactsFromCursor(Cursor cursor) {
        List<String> contacts = new ArrayList<String>();

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
                contacts.add(name);
            } while (cursor.moveToNext());
        }

        return contacts;
    }
}
