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
    Intent intent;
    BitmapDrawable bitmapdraw;
    Bitmap waitingBitmap,  executeBitmap, finishBitmap, rejectBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //initialize for FAB button
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

        //itnitailize for marker icon
        bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.waiting);
        waitingBitmap= Bitmap.createScaledBitmap(bitmapdraw.getBitmap(),50,50,false);
        bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.execute);
        executeBitmap= Bitmap.createScaledBitmap(bitmapdraw.getBitmap(),50,50,false);
        bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.finish);
        finishBitmap= Bitmap.createScaledBitmap(bitmapdraw.getBitmap(),50,50,false);
        bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.reject);
        rejectBitmap= Bitmap.createScaledBitmap(bitmapdraw.getBitmap(),50,50,false);


        //initialize for blockchain
        contractAddr= "0xE3eAbe98469DaDB4088c3009Cc497207B9B3d24D";
        web3 = Web3jFactory.build(new HttpService("https://rinkeby.infura.io/v3/8ff6512d7e094fe9ac1190b231614703"));
        droneChain = Dronechain.load(contractAddr,web3,credentials,gasPrice,gasLimit);

        db = new DBManager(MapsActivity.this,contractAddr);
        intent = new Intent(MapsActivity.this, DroneLogActivity.class);
    }
    //DroneLogActivity 에서 액티비티 전환될때의 콜백
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
            }
        }
    }
    //fab버튼 콜백처리
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
                }else
                    Toast.makeText(MapsActivity.this,"드론을 선택해주세요",Toast.LENGTH_SHORT).show();
                break;

            //my mission button
            case R.id.fab3:
                anim();
                if(selectedDrone.isSelected())
                    startActivityForResult(intent, 1);
                break;

            //send mission button
            case R.id.fab4:
                anim();
                if(!waypoints.isEmpty() && selectedDrone.isSelected()) {
                    try {
                        SetMission sendMission = new SetMission();
                        sendMission.execute();
                    } catch (Exception e) {
                        Toast.makeText(MapsActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }else if(waypoints.isEmpty() && !selectedDrone.isSelected()){
                    Toast.makeText(MapsActivity.this,"미션과 드론을 설정해주세요",Toast.LENGTH_SHORT).show();
                }else if(waypoints.isEmpty()){
                    Toast.makeText(MapsActivity.this,"미션을 설정해주세요",Toast.LENGTH_SHORT).show();
                }else if(!selectedDrone.isSelected()) {
                    Toast.makeText(MapsActivity.this, "드론을 선택해주세요", Toast.LENGTH_SHORT).show();
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
    //FAB animation
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

    //인스턴스화 시점 : DroneLogActivity 에서 intent 전환시 발생하는 콜백함수에서 인스턴스화
    private class MyMissonDrone extends AsyncTask<String, String,MarkerItem> {
        //doInBackGround : 인자로 드론의 지갑주소를 받아 result 드론 정보를 삽입 후 onPostExecute 로 넘김
        @Override
        protected  MarkerItem doInBackground(String... addr) {
            MarkerItem result;
            Tuple3<BigInteger,BigInteger,BigInteger> droneInfo = new Tuple3<BigInteger,BigInteger,BigInteger>(BigInteger.valueOf(0),BigInteger.valueOf(0),BigInteger.valueOf(0));
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
        //onPostExecute : doInBackGround 에서 받은 드론 정보를 구글맵에 마커를 그림
        @Override
        protected void onPostExecute(MarkerItem result) {
            BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.mavic);
            Bitmap droneBitmap= Bitmap.createScaledBitmap(bitmapdraw.getBitmap(),100,100,false);
            mMap.addMarker(new MarkerOptions().position(result.getCoord()).snippet(result.getAddr()).icon(BitmapDescriptorFactory.fromBitmap(droneBitmap)));
        }
    }

    //인스턴스화 시점 : DroneLogActivity 에서 intent 전환시 발생하는 콜백함수에서 인스턴스화
    private class GetMission extends AsyncTask<MissinInfo, String,List<MarkerItem>> {
        //로딩 dialog 를 위한 객체 선언
        ProgressDialog asyncDialog = new ProgressDialog(
                MapsActivity.this);
        //로딩 dialog 표시
        @Override
        protected void onPreExecute(){
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("미션 불러오는 중입니다...");

            // show dialog
            asyncDialog.show();
            super.onPreExecute();
        }
        //doInBackGround :파라미터로 전달받은 MissionInfo 를 통해서 미션 조회 후, 해당 정보를 onPostExecute 에 전달
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
        //onPostExecute : doInBackGround 에서 받은 미션 정보를 구글맵에 표시
        @Override
        protected void onPostExecute(List<MarkerItem> result) {
            super.onPostExecute(result);
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

    //인스턴스화 시점 : 드론 표시 FAB 버튼을 눌렀을 때
    private class ReadDrone extends AsyncTask<Void, String,List<MarkerItem>> {
        //로딩 dialog 를 위한 객체 선언
        ProgressDialog asyncDialog = new ProgressDialog(
                MapsActivity.this);

        //로딩 dialog 표시
        @Override
        protected void onPreExecute(){
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("드론을 불러오는 중입니다.");
            // show dialog
            asyncDialog.show();
            super.onPreExecute();
        }

        //doInBackGround : 블록체인에 존재하는 모든 드론의 정보를 받아오고 그 정보를 바탕으로 List를 생성한다.
        @Override
        protected  List<MarkerItem> doInBackground(Void... arg0) {
            List<MarkerItem> result = new ArrayList<MarkerItem>();
            List<String> droneAddr = new ArrayList<String>();
            Tuple3<BigInteger,BigInteger,BigInteger> droneInfo;
            double inputLat;
            double inputLng;
            try {
                droneAddr = droneChain.getDrones().send();

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
        //onPostExecute : doInBackground 에서 생성한 List 정보를 바탕으로 드론 마커 표시
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

    /*
    내용 : 특정 드론 주소에 미션을 등록
    인스턴스화 시점 : 드론 표시 FAB 버튼을 눌렀을 때
    */
    private class SetMission extends AsyncTask<Void, String,String> {
        //로딩 dialog 를 위한 객체 선언
        ProgressDialog asyncDialog = new ProgressDialog(
                MapsActivity.this);

        //로딩 dialog 표시
        @Override
        protected void onPreExecute(){
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("미션 전송중입니다...");

            // show dialog
            asyncDialog.show();
            super.onPreExecute();

        }
        //doInBackGround : waypoints와 selectedDrone에 저장된 미션 정보를 블록체인에 등록
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
                waypoints.clear();
                //전송
                TransactionReceipt transactionReceipt = droneChain.setWayPoint(selectedDrone.getAddr(),latList,lonList).send();
                //sql 데이터 쓰기(타겟 드론, 인덱스)
                List<Log> returnList = transactionReceipt.getLogs();
                String paraData = String.valueOf(Integer.parseInt(returnList.get(returnList.size()-1).getData().substring(2),16));
                MissinInfo insertData = new MissinInfo(selectedDrone.getAddr(),Integer.parseInt(paraData));
                db.insertData(insertData);
            } catch (Exception e) {
                result = e.toString();
            }
            return result;
        }
        //onPostExecute : 에러 발생시 에러 메세지 출력
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(MapsActivity.this,result,Toast.LENGTH_SHORT).show();
            asyncDialog.dismiss();
        }

    }

    /*
    내용 : 특정 드론의 비행이력을 조회
    인스턴스화 시점 : 이력 조회 FAB 버튼을 눌렀을 때
    */
    private class ReadFlightHistory extends AsyncTask<String, String,List<MarkerItem>> {
        //로딩 dialog 를 위한 객체 선언
        ProgressDialog asyncDialog = new ProgressDialog(
                MapsActivity.this);

        //로딩 dialog 표시
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("비행이력을 불러오는 중입니다...");
            // show dialog
            asyncDialog.show();

        }
        //doInBackGround : selectedDrone에 저장된 드론 정보를 바탕으로 해당 드론의 비행이력(미션정보)을 블록체인에서 가져옴
        @Override
        protected  List<MarkerItem> doInBackground(String... params) {
            List<MarkerItem> result = new ArrayList<MarkerItem>();
            try {
                Tuple4<List<BigInteger>, List<BigInteger>, List<String>, List<BigInteger>> droneLog;
                //받아온 드론 주소에 대해서 조사 실시
                droneLog = droneChain.traceFlightHistory(params[0]).send();
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
        //onPostExecute : doInBackGround 로 부터 전달받은 비행이력(미션 정보)를 바탕으로 맵에 미션 정보 표시
        @Override
        protected void onPostExecute(List<MarkerItem> result) {
            super.onPostExecute(result);
            Bitmap inputIcon;
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

    //구글맵 생성시 발생하는 콜백 함수
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //구글맵 초기 시점 설정
        LatLng KAU = new LatLng(37.600981, 126.864935);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(KAU));

        //마커 클릭 이벤트 리스너
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(MapsActivity.this,marker.getSnippet(),Toast.LENGTH_SHORT).show();
                //드론 마커 클릭시, 해당 드론 정보를 등록한다.
                if(marker.getTitle().equals("Drones")) {
                    selectedDrone = new MarkerItem(marker.getPosition().latitude,marker.getPosition().longitude,BigInteger.valueOf(-1),marker.getSnippet());

                    selectedDrone.selectMarker(true);
                    intent.putExtra("DroneAdd",String.valueOf(marker.getSnippet()));
                }
                //웨이포인트 마커일 경우 삭제(잘못 선택된 경우를 고려)
                if(marker.getTitle().equals("waypoint")){
                    marker.remove();
                    waypoints.remove(Integer.valueOf(marker.getSnippet()));
                }
                return true;
            }
        });
        //맵 클릭 리스너 등록
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            //해당 포인트를 미션 웨이포인트에 등록
            public void onMapClick(LatLng point) {
                waypoints.put(waypointCnt,new MarkerItem(point.latitude,point.longitude,BigInteger.valueOf(-1),""));
                mMap.addMarker(new MarkerOptions().position(point).snippet(waypointCnt.toString()).title("waypoint"));
                waypointCnt++;
            }
        });
    }
}
