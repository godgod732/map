package com.example.godgo.map;


import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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


import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Array;
import org.web3j.abi.datatypes.Event;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tuples.generated.Tuple4;


import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    HashMap<Integer, MarkerItem> waypoints = new HashMap<>();
    Integer waypointCnt=1;
    Button displayButton,selectButton,sendButton,searchButton,logButton,clearButton;
    private GoogleMap mMap;
    Credentials credentials;
    MarkerItem selectedDrone;
    String contractAddr;
    BigInteger gasPrice = BigInteger.valueOf(440000000240L);
    BigInteger gasLimit = BigInteger.valueOf(900000L);
    Web3j web3;
    Dronechain droneChain;
    DBManager db;
    Intent intent;
    TextView t;
    List<Double> lst = new ArrayList<Double>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //load wallet
        try {
            credentials = WalletUtils.loadCredentials("tjrwns92!", Uri.parse(Environment.getExternalStorageDirectory() + "/Pictures/UTC--2018-10-30T06-07-25.479Z--21ae06c29be5c4a899a2545519a29d976e2fdd24").toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CipherException e) {
            e.printStackTrace();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //initialize
        contractAddr= "0xa56e93d7a1Bf923Aa2A8DD863535d124BBD776EA";
        web3 = Web3jFactory.build(new HttpService("https://rinkeby.infura.io/v3/8ff6512d7e094fe9ac1190b231614703"));

        droneChain = Dronechain.load(contractAddr,web3,credentials,gasPrice,gasLimit);
        db = new DBManager(MapsActivity.this);
        initUi();

    }


    private void initUi(){
        displayButton = (Button) findViewById(R.id.display);
        displayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ReadDrone readDrones = new ReadDrone();
                    readDrones.execute();
                } catch (Exception e) {
                    Toast.makeText(MapsActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });

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

        sendButton = (Button) findViewById(R.id.send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!waypoints.isEmpty() && selectedDrone.isSelected()){
                    try {
                        SetMission sendMission = new SetMission();
                        sendMission.execute();
                    }
                    catch(Exception e) {
                        Toast.makeText(MapsActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        searchButton = (Button) findViewById(R.id.search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SearchMission searchMission = new SearchMission();
                    searchMission.execute();
                } catch (Exception e) {
                    Toast.makeText(MapsActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private class ReadDrone extends AsyncTask<Void, String,List<MarkerItem>> {

        ProgressDialog asyncDialog = new ProgressDialog(
                MapsActivity.this);
        @Override
        protected void onPreExecute(){
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("로딩중입니다..");

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
            t.setText(para[0]);
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
                Tuple4<List<BigInteger>, List<BigInteger>, List<String>, BigInteger> droneLog; // 드론 List -> 그 드론의 Mission 기록들 List 를 볼거임
                //받아온 드론 주소에 대해서 조사 실시
                droneLog = droneChain.traceFlightHistory(params[0], BigInteger.valueOf(2)).send();// 갖고온 state로 확인하는것 두가지 방식이 있지만 여기서는 그냥 drone 의 state 를 따라가는걸로 설정
                double inputLat,inputLon;
                BigInteger inputState;
                String inputAddr;
                for(int i = 0; i < droneLog.getValue1().size(); i++) {
                    inputLat = Double.valueOf(droneLog.getValue1().get(i).toString())/Double.valueOf("1000000");
                    inputLon = Double.valueOf(droneLog.getValue2().get(i).toString())/Double.valueOf("1000000");
                    inputState = droneLog.getValue4();
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
                    selectButton.setEnabled(true);
                    selectedDrone = new MarkerItem(marker.getPosition().latitude,marker.getPosition().longitude,BigInteger.valueOf(-1),marker.getSnippet());
                    //비행 이력 수신
                    try{
                        ReadFlightHistory readFlightHistory = new ReadFlightHistory();
                        readFlightHistory.execute(marker.getSnippet());
                    }catch(Exception e){

                    }

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
                //드론 마커가 선택된 상황이 아니기 때문에 비활성화
                selectButton.setEnabled(false);
                waypoints.put(waypointCnt,new MarkerItem(point.latitude,point.longitude,BigInteger.valueOf(-1),""));
                mMap.addMarker(new MarkerOptions().position(point).snippet(waypointCnt.toString()).title("waypoint"));
                waypointCnt++;
            }
        });
    }
}
