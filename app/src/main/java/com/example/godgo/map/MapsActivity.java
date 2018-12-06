package com.example.godgo.map;

import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
//import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.objectweb.asm.tree.analysis.Frame;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tuples.generated.Tuple4;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;




public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback ,View.OnClickListener{
    HashMap<Integer, MarkerItem> waypoints = new HashMap<>();

    Integer waypointCnt=1;
    private GoogleMap mMap;
    Credentials credentials;
    MarkerItem selectedDrone;
    String contractAddr;
    BigInteger gasPrice = BigInteger.valueOf(440000000240L);
    BigInteger gasLimit = BigInteger.valueOf(900000L);
    Web3j web3;
    Dronechain droneChain;
    DBManager db;
    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fab1, fab2, fab3, fab4, fab5;
    LayoutInflater inflater;
    List<Double> lst = new ArrayList<Double>();
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        selectedDrone = new MarkerItem();
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fab4 = (FloatingActionButton) findViewById(R.id.fab4);
        fab5 = (FloatingActionButton) findViewById(R.id.fab5);

        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fab3.setOnClickListener(this);
        fab4.setOnClickListener(this);
        fab5.setOnClickListener(this);

        //load wallet
        try {
            credentials = WalletUtils.loadCredentials("tjrwns92!", Uri.parse(Environment.getExternalStorageDirectory() + "/Pictures/UTC--2018-10-30T06-07-25.479Z--21ae06c29be5c4a899a2545519a29d976e2fdd24").toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CipherException e) {
            e.printStackTrace();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //initialize
        contractAddr= "0x9ed14fdE442b5721918ee713dA184d92ea0A58bC";
        web3 = Web3jFactory.build(new HttpService("https://rinkeby.infura.io/v3/8ff6512d7e094fe9ac1190b231614703"));

        droneChain = Dronechain.load(contractAddr,web3,credentials,gasPrice,gasLimit);
        db = new DBManager(MapsActivity.this,contractAddr);
        intent = new Intent(MapsActivity.this, DroneLogActivity.class);
        initUi();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mMap.clear();
        Tuple3<BigInteger,BigInteger,BigInteger> droneInfo;
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {

                MyMissonDrone myMissonDrone = new MyMissonDrone();
                myMissonDrone.execute(data.getStringExtra("address"));
                GetMission getMission = new GetMission();
                getMission.execute(new MissinInfo(data.getStringExtra("address"),(data.getIntExtra("index",-1))));
                Toast.makeText(MapsActivity.this, "address 넘어옴."+data.getStringExtra("address"), Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            //메뉴 활성화 버튼
            case R.id.fab:
                anim();
                break;

            //clear button
            case R.id.fab1:
                anim();
                mMap.clear();
                waypoints.clear();
                selectedDrone.selectMarker(false);
                break;

            //log button
            case R.id.fab2:
                anim();
                if(selectedDrone.isSelected()) {
                    try {
                        ReadFlightHistory readFlightHistory = new ReadFlightHistory();
                        readFlightHistory.execute(selectedDrone.getAddr());
                    } catch (Exception e) {

                    }
                }
                break;

            //my mission button
            case R.id.fab3:
                anim();/*
                try {
                    SearchMission searchMission = new SearchMission();
                    searchMission.execute();
                } catch (Exception e) {
                    Toast.makeText(MapsActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                }*/
                if(selectedDrone.isSelected())
                    startActivityForResult(intent, 1);
                break;

            //send mission button
            case R.id.fab4:
                anim();
                if(!waypoints.isEmpty() && selectedDrone.isSelected()){
                    try {
                        SetMission sendMission = new SetMission();
                        sendMission.execute();
                    }
                    catch(Exception e) {
                        Toast.makeText(MapsActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            //display drone button
            case R.id.fab5:
                anim();
                try {
                    ReadDrone readDrones = new ReadDrone();
                    readDrones.execute();
                } catch (Exception e) {
                    Toast.makeText(MapsActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    public void anim() {

        if (isFabOpen) {
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab3.startAnimation(fab_close);
            fab4.startAnimation(fab_close);
            fab5.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            fab3.setClickable(false);
            fab4.setClickable(false);
            fab5.setClickable(false);
            isFabOpen = false;
        } else {
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab3.startAnimation(fab_open);
            fab4.startAnimation(fab_open);
            fab5.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            fab3.setClickable(true);
            fab4.setClickable(true);
            fab5.setClickable(true);
            isFabOpen = true;
        }
    }

    private void initUi(){
        /*
        selectButton = (Button) findViewById(R.id.select);
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(selectedDrone.isSelected())){
                    selectedDrone.selectMarker(true);
                }
            }
        });
        selectButton.setEnabled(false);
 */
    }
    private class MyMissonDrone extends AsyncTask<String, String,MarkerItem> {
        @Override
        protected  MarkerItem doInBackground(String... addr) {
            MarkerItem result;
            Tuple3<BigInteger,BigInteger,BigInteger> droneInfo = new Tuple3<BigInteger,BigInteger,BigInteger>(BigInteger.valueOf(0),BigInteger.valueOf(0),BigInteger.valueOf(0));
            MarkerItem markerItem;
            double droneLat=0;
            double droneLng=0;
            try {
                    droneInfo = droneChain.getDroneStateByAddr(addr[0]).send();
                    droneLat = Double.valueOf(droneInfo.getValue1().toString())/Double.valueOf("1000000.0");
                    droneLng =Double.valueOf(droneInfo.getValue2().toString())/Double.valueOf("1000000.0");
            } catch(Exception e){
                    Toast.makeText(MapsActivity.this,e.toString(),Toast.LENGTH_LONG).show();
            }
            result = new MarkerItem(droneLat,droneLng,droneInfo.getValue3(),addr[0]);
            return result;
        }

        @Override
        protected void onPostExecute(MarkerItem result) {
            BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.mavic);
            Bitmap droneBitmap= Bitmap.createScaledBitmap(bitmapdraw.getBitmap(),100,100,false);
            mMap.addMarker(new MarkerOptions().position(result.getCoord()).snippet(result.getAddr()).icon(BitmapDescriptorFactory.fromBitmap(droneBitmap)));
        }
    }
    private class ReadDrone extends AsyncTask<Void, String,List<MarkerItem>> {

        ProgressDialog asyncDialog = new ProgressDialog(
                MapsActivity.this);
        @Override
        protected void onPreExecute(){
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("로딩 중입니다.");
            // show dialog
            asyncDialog.show();
            super.onPreExecute();
        }

        @Override
        protected  List<MarkerItem> doInBackground(Void... arg0) {
            List<MarkerItem> result = new ArrayList<MarkerItem>();
            List<String> droneAddr = new ArrayList<String>();
            Tuple3<BigInteger,BigInteger,BigInteger> droneInfo;
            MarkerItem markerItem;
            double inputLat;
            double inputLng;

            try {
                droneAddr = droneChain.getDrones().send();
                String s="";

                for(int i = 0; i<droneAddr.size();i++){
                    droneInfo = droneChain.getDroneStateByAddr(droneAddr.get(i)).send();
                    inputLat = Double.valueOf(droneInfo.getValue1().toString())/Double.valueOf("1000000.0");
                    inputLng = Double.valueOf(droneInfo.getValue2().toString())/Double.valueOf("1000000.0");
                    result.add(new MarkerItem(inputLat,inputLng,droneInfo.getValue3(),droneAddr.get(i)));
                }
            } catch (Exception e) {
            }
            return result;
        }
        @Override
        protected  void onProgressUpdate(String... para){

        }
        @Override
        protected void onPostExecute(List<MarkerItem> result) {
            super.onPostExecute(result);
            //드론 표시

            BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.mavic);
            Bitmap droneBitmap= Bitmap.createScaledBitmap(bitmapdraw.getBitmap(),100,100,false);
            for(MarkerItem i : result){
                //t.setText(i.getCoordToDouble().toString());
                mMap.addMarker(new MarkerOptions().position(i.getCoord()).title("Drones").snippet(i.getAddr()).icon(BitmapDescriptorFactory.fromBitmap(droneBitmap)));
            }
            asyncDialog.dismiss();

        }
    }
    private class GetMission extends AsyncTask<MissinInfo, String,List<MarkerItem>> {
        ProgressDialog asyncDialog = new ProgressDialog(
                MapsActivity.this);
        @Override
        protected void onPreExecute(){
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("미션 전송중입니다...");

            // show dialog
            asyncDialog.show();
            super.onPreExecute();
        }

        @Override
        protected  List<MarkerItem> doInBackground(MissinInfo... params) {
            List<MarkerItem> result = new ArrayList<MarkerItem>();
            Tuple3<List<BigInteger>,List<BigInteger>,BigInteger> missionData;
            double inputLat, inputLon;
            BigInteger inputState;
            try {

                //전송
                missionData = droneChain.getMission(params[0].getDroneAddr(),BigInteger.valueOf(params[0].getMissionIndex())).send();

                for(int i = 0; i < missionData.getValue1().size(); i++) {
                    inputLat = Double.valueOf(missionData.getValue1().get(i).toString())/Double.valueOf("1000000");
                    inputLon = Double.valueOf(missionData.getValue2().get(i).toString())/Double.valueOf("1000000");
                    inputState = missionData.getValue3();
                    result.add(new MarkerItem(inputLat,inputLon,inputState,""));
                }
            } catch (Exception e) {
                result= result;
            }
            return result;
        }

        @Override
        protected void onPostExecute(List<MarkerItem> result) {
            super.onPostExecute(result);
            BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.waiting);
            Bitmap waitingBitmap= Bitmap.createScaledBitmap(bitmapdraw.getBitmap(),50,50,false);
            bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.execute);
            Bitmap executeBitmap= Bitmap.createScaledBitmap(bitmapdraw.getBitmap(),50,50,false);
            bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.finish);
            Bitmap finishBitmap= Bitmap.createScaledBitmap(bitmapdraw.getBitmap(),50,50,false);
            bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.reject);
            Bitmap rejectBitmap= Bitmap.createScaledBitmap(bitmapdraw.getBitmap(),50,50,false);
            for(MarkerItem i : result){
                Bitmap inputIcon = waitingBitmap;
                if(BigInteger.valueOf(0).equals(i.getState()))
                    inputIcon = waitingBitmap;
                else if(BigInteger.valueOf(1).equals(i.getState()))
                    inputIcon = executeBitmap;
                else if(BigInteger.valueOf(2).equals(i.getState()))
                    inputIcon = finishBitmap;
                else if(BigInteger.valueOf(3).equals(i.getState()))
                    inputIcon = rejectBitmap;

                mMap.addMarker(new MarkerOptions().position(i.getCoord()).title("Mission").snippet(i.getAddr()).icon(BitmapDescriptorFactory.fromBitmap(inputIcon)));
            }
            asyncDialog.dismiss();
        }

    }
    private class SetMission extends AsyncTask<Void, String,String> {
        ProgressDialog asyncDialog = new ProgressDialog(
                MapsActivity.this);
        @Override
        protected void onPreExecute(){
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("미션 전송중입니다...");

            // show dialog
            asyncDialog.show();
            super.onPreExecute();
        }

        @Override
        protected  String doInBackground(Void... params) {
            String result = "미션이 성공적으로 전송되었습니다.";
            List<BigInteger> latList = new ArrayList<>();
            List<BigInteger> lonList = new ArrayList<>();

            try {
                //전송 리스트 생성
                Iterator<Integer> iter = waypoints.keySet().iterator();
                while(iter.hasNext()){
                    int key = iter.next();
                    MarkerItem waypointMarker = waypoints.get(key);
                    Double value = (waypointMarker.lat*Double.valueOf(1000000.0));
                    latList.add(BigInteger.valueOf(value.intValue()));
                    value = (waypointMarker.lon*Double.valueOf(1000000.0));
                    lonList.add(BigInteger.valueOf(value.intValue()));
                }
                //전송
                TransactionReceipt transactionReceipt = droneChain.setWayPoint(selectedDrone.getAddr(),latList,lonList).send();
                //sql 데이터 쓰기(타겟 드론, 인덱스)
                List<Log> returnList = transactionReceipt.getLogs();
                String paraData = String.valueOf(Integer.parseInt(returnList.get(returnList.size()-1).getData().substring(2),16));
                MissinInfo insertData = new MissinInfo(selectedDrone.getAddr(),Integer.parseInt(paraData));
                db.insertData(insertData);
            } catch (Exception e) {
                result = "전송 실패";
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(MapsActivity.this,result,Toast.LENGTH_SHORT).show();
            asyncDialog.dismiss();
        }

    }

    private class SearchMission extends AsyncTask<Void, String,List<MarkerItem>> {
        ProgressDialog asyncDialog = new ProgressDialog(
                MapsActivity.this);
        @Override
        protected void onPreExecute(){
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("미션 로딩중입니다...");

            // show dialog
            asyncDialog.show();
            super.onPreExecute();
        }
        @Override
        protected  List<MarkerItem> doInBackground(Void... params) {
            //데이터 읽어와서 마커 표시하기
            List<MarkerItem> result = new ArrayList<MarkerItem>();
            Tuple3<List<BigInteger>,List<BigInteger>,BigInteger> getMissionInfo;
            List<MissinInfo> missionData= db.selectAll();
            double inputLat;
            double inputLon;
            BigInteger inputState;
            try{
                //기능 구헌
                for(MissinInfo i : missionData) {
                    getMissionInfo = droneChain.getMission(i.getDroneAddr(), BigInteger.valueOf(i.getMissionIndex())).send();

                    List<BigInteger> lat  = getMissionInfo.getValue1();
                    List<BigInteger> lon  = getMissionInfo.getValue2();
                    inputState =  getMissionInfo.getValue3();
                    for(int j = 0; j < lat.size(); j++){
                        inputLat = Double.valueOf(lat.get(j).toString())/Double.valueOf("1000000");
                        inputLon = Double.valueOf(lon.get(j).toString())/Double.valueOf("1000000");
                        result.add(new MarkerItem(inputLat,inputLon,inputState,""));
                    }
                }
            }catch (Exception e){
                result = result;
            }
            return result;
        }
        @Override
        protected void onPostExecute(List<MarkerItem> result) {
            //마커 그리기
            super.onPostExecute(result);
            //드론 표시
            BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.waiting);
            Bitmap waitingBitmap= Bitmap.createScaledBitmap(bitmapdraw.getBitmap(),50,50,false);
            bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.execute);
            Bitmap executeBitmap= Bitmap.createScaledBitmap(bitmapdraw.getBitmap(),50,50,false);
            bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.finish);
            Bitmap finishBitmap= Bitmap.createScaledBitmap(bitmapdraw.getBitmap(),50,50,false);
            bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.reject);
            Bitmap rejectBitmap= Bitmap.createScaledBitmap(bitmapdraw.getBitmap(),50,50,false);
            for(MarkerItem i : result){
                Bitmap inputIcon = waitingBitmap;
                if(BigInteger.valueOf(0).equals(i.getState()))
                    inputIcon = waitingBitmap;
                else if(BigInteger.valueOf(1).equals(i.getState()))
                    inputIcon = executeBitmap;
                else if(BigInteger.valueOf(2).equals(i.getState()))
                    inputIcon = finishBitmap;
                else if(BigInteger.valueOf(3).equals(i.getState()))
                    inputIcon = rejectBitmap;

                mMap.addMarker(new MarkerOptions().position(i.getCoord()).title("Mission").snippet(i.getAddr()).icon(BitmapDescriptorFactory.fromBitmap(inputIcon)));
            }
            asyncDialog.dismiss();
        }

    }
    //비행 이력 조회
    private class ReadFlightHistory extends AsyncTask<String, String,List<MarkerItem>> {
        ProgressDialog asyncDialog = new ProgressDialog(
                MapsActivity.this);
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("비행이력을 불러오는 중입니다...");
            // show dialog
            asyncDialog.show();

        }
        @Override
        protected  List<MarkerItem> doInBackground(String... params) {
            List<MarkerItem> result = new ArrayList<MarkerItem>();
            try {
                Tuple4<List<BigInteger>, List<BigInteger>, List<String>, List<BigInteger>> droneLog; // 드론 List -> 그 드론의 Mission 기록들 List 를 볼거임
                //받아온 드론 주소에 대해서 조사 실시
                droneLog = droneChain.traceFlightHistory(params[0]).send();// 갖고온 state로 확인하는것 두가지 방식이 있지만 여기서는 그냥 drone 의 state 를 따라가는걸로 설정
                double inputLat,inputLon;
                BigInteger inputState;
                String inputAddr;
                for(int i = 0; i < droneLog.getValue1().size(); i++) {
                    inputLat = Double.valueOf(droneLog.getValue1().get(i).toString())/Double.valueOf("1000000");
                    inputLon = Double.valueOf(droneLog.getValue2().get(i).toString())/Double.valueOf("1000000");
                    inputState = droneLog.getValue4().get(i);
                    inputAddr = droneLog.getValue3().get(i);
                    result.add(new MarkerItem(inputLat,inputLon,inputState,inputAddr));
                }
            }catch (Exception e) {

            }
            return result;
        }

        @Override
        protected void onPostExecute(List<MarkerItem> result) {
            super.onPostExecute(result);
            Bitmap inputIcon;
            BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.waiting);
            Bitmap waitingBitmap= Bitmap.createScaledBitmap(bitmapdraw.getBitmap(),50,50,false);
            bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.execute);
            Bitmap executeBitmap= Bitmap.createScaledBitmap(bitmapdraw.getBitmap(),50,50,false);
            bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.finish);
            Bitmap finishBitmap= Bitmap.createScaledBitmap(bitmapdraw.getBitmap(),50,50,false);
            bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.reject);
            Bitmap rejectBitmap= Bitmap.createScaledBitmap(bitmapdraw.getBitmap(),50,50,false);
            for(MarkerItem i : result){
                if(BigInteger.valueOf(0).equals(i.getState()))
                    inputIcon = waitingBitmap;
                else if(BigInteger.valueOf(1).equals(i.getState()))
                    inputIcon = executeBitmap;
                else if(BigInteger.valueOf(2).equals(i.getState()))
                    inputIcon = finishBitmap;
                else
                    inputIcon = rejectBitmap;

                mMap.addMarker(new MarkerOptions().position(i.getCoord()).title("Mission").snippet(i.getAddr()).icon(BitmapDescriptorFactory.fromBitmap(inputIcon)));
            }
            asyncDialog.dismiss();
        }

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng KAU = new LatLng(37.600981, 126.864935);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(KAU));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(MapsActivity.this,marker.getSnippet(),Toast.LENGTH_SHORT).show();
                if(marker.getTitle().equals("Drones")) {
                    selectedDrone = new MarkerItem(marker.getPosition().latitude,marker.getPosition().longitude,BigInteger.valueOf(-1),marker.getSnippet());

                    selectedDrone.selectMarker(true);
                    //비행 이력
                    intent.putExtra("DroneAdd",String.valueOf(marker.getSnippet()));
                }
                if(marker.getTitle().equals("waypoint")){
                    marker.remove();
                    waypoints.remove(Integer.valueOf(marker.getSnippet()));
                }
                return true;
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                waypoints.put(waypointCnt,new MarkerItem(point.latitude,point.longitude,BigInteger.valueOf(-1),""));
                mMap.addMarker(new MarkerOptions().position(point).snippet(waypointCnt.toString()).title("waypoint"));
                waypointCnt++;
            }
        });
    }
}
