package com.itzikpich.intentsandpermissions;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ContactInfoFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>  {

    private static final String TAG = "ContactInfoFragment";

    private static final String[] PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };

    private void getContactList() {
        ContentResolver cr = getActivity().getContentResolver();
        ArrayList<Pair<String, String>> contactList = new ArrayList<>();
        Cursor cursor = cr.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                PROJECTION,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY + " ASC"
        );

        if (cursor != null) {
            HashSet<String> mobileNoSet = new HashSet<String>();
            try {
                final int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY);
                final int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                String name, number;
                while (cursor.moveToNext()) {
                    name = cursor.getString(nameIndex);
                    number = cursor.getString(numberIndex);
                    number = number.replace(" ", "");
                    if (!mobileNoSet.contains(number)) {
                        contactList.add(new Pair<String, String>(name, number));
                        mobileNoSet.add(number);
                    }
                }
            } finally {
                Log.d(TAG, "getContactList: " + contactList);
                cursor.close();
            }
        }
    }

//    private static final String[] PROJECTION =
//            {
//                    ContactsContract.Data._ID,
//                    ContactsContract.Data.MIMETYPE,
//                    ContactsContract.Data.DATA1,
//                    ContactsContract.Data.DATA2,
//                    ContactsContract.Data.DATA3,
//                    ContactsContract.Data.DATA4,
//                    ContactsContract.PhoneLookup.PHOTO_URI,
//                    ContactsContract.Data.PHOTO_URI,
//                    ContactsContract.CommonDataKinds.Email._ID,
//                    ContactsContract.CommonDataKinds.Email.ADDRESS,
//                    ContactsContract.CommonDataKinds.Email.TYPE,
//                    ContactsContract.CommonDataKinds.Email.LABEL,
//                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
//                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
//                    ContactsContract.CommonDataKinds.Phone.NUMBER
//            };

//    @SuppressLint("InlinedApi")
//    private static final String[] PROJECTION = {
//            ContactsContract.Contacts._ID,
//            ContactsContract.Contacts.LOOKUP_KEY,
//            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
//            ContactsContract.Data.PHOTO_URI,
//    };

    private static final String SELECTION = ContactsContract.Data.LOOKUP_KEY + " LIKE ?";
//    private static final String SELECTION = ContactsContract.Contacts._ID + " LIKE ?";


    private static final int DETAILS_QUERY_ID = 1;

    String searchString = "%" + "" + "%";
    String[] selectionArgs = { "" };

    private static final String SORT_ORDER = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY + " ASC";

    public ContactInfoFragment() {
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        LoaderManager.getInstance(this).initLoader(DETAILS_QUERY_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        getContactList();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Choose the proper action
        if (id == DETAILS_QUERY_ID) {// Assigns the selection parameter
            selectionArgs[0] = searchString;
            // Starts the query
            return new CursorLoader(
                    getActivity(),
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    PROJECTION,
                    SELECTION,
                    selectionArgs,
                    SORT_ORDER
            );
        }
        throw new IllegalStateException("Unexpected value: " + id);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (loader.getId() == DETAILS_QUERY_ID) {
            List<String> contacts = contactsFromCursor(cursor);
            Log.d(TAG, "onLoadFinished: " + contacts);
        } else {
            throw new IllegalStateException("Unexpected value: " + loader.getId());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == DETAILS_QUERY_ID) {/*
         * If you have current references to the Cursor,
         * remove them here.
         */
        }
    }

    private List<String> contactsFromCursor(Cursor cursor) {
        List<String> contacts = new ArrayList<String>();

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                contacts.add(name);
            } while (cursor.moveToNext());
        }

        return contacts;
    }

}
