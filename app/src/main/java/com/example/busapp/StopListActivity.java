package com.example.busapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import com.example.busapp.databinding.ActivityBusListBinding;
import com.example.busapp.databinding.ActivityStopListBinding;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class StopListActivity extends Activity {

    private TextView mTextView;
    private ActivityStopListBinding binding;

    private String data;
    private double latitude;
    private double longitude;
    private TagoThread tagoThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityStopListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //mTextView = binding.text;
        TextView textView3 = (TextView) findViewById(R.id.textView3);
//        WearableRecyclerView recyclerView = findViewById(R.id.wearableRecyclerView);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setEdgeItemsCenteringEnabled(true);
//        recyclerView.setLayoutManager(new WearableLinearLayoutManager(this));

        Intent intent =getIntent();
        latitude = intent.getDoubleExtra("latitude", 0);
        longitude = intent.getDoubleExtra("longitude", 0);
        data = getTagoXmlData();
        Log.d("data",data);
        textView3.setText(data);
        //adapter = getTagoXmlData();
        //recyclerView.setAdapter(adapter);
    }


    public String getTagoXmlData() {
        String lat_str = Double.toString(latitude);
        String lon_str = Double.toString(longitude);
        StringBuffer buffer = new StringBuffer();

        Thread t = new Thread(() -> {

            String queryUrl = "http://apis.data.go.kr/1613000/BusSttnInfoInqireService/getCrdntPrxmtSttnList?serviceKey="
                    + "%2BaCrLa%2Fp1lfYP3wx954IxePqBKnfeZ8EC0pcOupGbRWhxUuOf5HW52ieQEZojO%2FEXE0ES1My6X68c50H4dWVLw%3D%3D"
                    + "&numOfRows=10&pageNo=1&_type=xml"
                    + "&gpsLati=" + lat_str + "&gpsLong=" + lon_str;
            try {
                URL url = new URL(queryUrl);//문자열로 된 요청 url을 URL 객체로 생성.
                InputStream is = url.openStream(); //url위치로 입력스트림 연결

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(new InputStreamReader(is, "UTF-8")); //inputstream 으로부터 xml 입력받기
                String tag;
                xpp.next();
                int eventType = xpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    switch (eventType) {
                        case XmlPullParser.START_DOCUMENT:
                            buffer.append("파싱 시작...\n\n");
                            break;

                        case XmlPullParser.START_TAG:
                            tag = xpp.getName();//테그 이름 얻어오기

                            if (tag.equals("item")) ;// 첫번째 검색결과
                            else if (tag.equals("gpslati")) {
                                buffer.append("lat : ");
                                xpp.next();
                                buffer.append(xpp.getText());//title 요소의 TEXT 읽어와서 문자열버퍼에 추가
                                buffer.append("\n"); //줄바꿈 문자 추가
                            } else if (tag.equals("gpslong")) {
                                buffer.append("long : ");
                                xpp.next();
                                buffer.append(xpp.getText());//category 요소의 TEXT 읽어와서 문자열버퍼에 추가
                                buffer.append("\n");//줄바꿈 문자 추가
                            } else if (tag.equals("nodeid")) {
                                buffer.append("nodeid :");
                                xpp.next();


                                buffer.append(xpp.getText());//description 요소의 TEXT 읽어와서 문자열버퍼에 추가
                                buffer.append("\n");//줄바꿈 문자 추가
                            } else if (tag.equals("nodenm")) {
                                buffer.append("nodenm :");
                                xpp.next();

                                buffer.append(xpp.getText());//telephone 요소의 TEXT 읽어와서 문자열버퍼에 추가
                                buffer.append("\n");//줄바꿈 문자 추가
                            } else if (tag.equals("nodeno")) {
                                buffer.append("nodeno :");
                                xpp.next();
                                buffer.append(xpp.getText());//address 요소의 TEXT 읽어와서 문자열버퍼에 추가
                                buffer.append("\n");//줄바꿈 문자 추가
                            }
                            break;

                        case XmlPullParser.TEXT:
                            break;

                        case XmlPullParser.END_TAG:
                            tag = xpp.getName(); //테그 이름 얻어오기

                            if (tag.equals("item")) buffer.append("\n");// 첫번째 검색결과종료..줄바꿈
                            break;
                    }
                    eventType = xpp.next();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch blocke.printStackTrace();
            }
            //buffer.append("파싱 끝\n");
        });
        try {
            t.start();
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return buffer.toString();//StringBuffer 문자열 객체 반환
        //return adapter;
    }

}