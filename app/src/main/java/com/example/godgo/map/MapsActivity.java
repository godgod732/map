package com.example.godgo.map;


import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
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


import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    HashMap<Integer, MarkerItem> waypoints = new HashMap<>();
    Integer waypointCnt=1;
    TextView t;
    Button displayButton,selectButton,sendButton;
    private GoogleMap mMap;
    Credentials credentials;
    MarkerItem selectedDrone;
    String contractAddr;
    BigInteger gasPrice = BigInteger.valueOf(440000000240L);
    BigInteger gasLimit = BigInteger.valueOf(900000L);
    Web3j web3;
    Dronechain droneChain;
    DBManager db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //load wallet
        try {
            credentials = WalletUtils.loadCredentials("tjrwns92!", "/storage/emulated/0/Pictures/UTC--2018-10-30T06-07-25.479Z--21ae06c29be5c4a899a2545519a29d976e2fdd24");
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
        contractAddr= "0xE1B218Bd342F926Fa7229B5Ca4b237127A8aEe43";
        contractAddr= "0x5D02F24c421652aB804d92d5c0dCd18cB24247d9";
        web3 = Web3jFactory.build(new HttpService("https://rinkeby.infura.io/v3/faa0a68fa9bc43b0a56c79f82069e283"));

        droneChain = Dronechain.load(contractAddr,web3,credentials,gasPrice,gasLimit);
        db = new DBManager(MapsActivity.this);
        initUi();

    }


    private void initUi(){
        displayButton = (Button) findViewById(R.id.display);
        displayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDrone();
            }
        });

        selectButton = (Button) findViewById(R.id.select);
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDrone();
            }
        });
        selectButton.setEnabled(false);

        Button sendButton = (Button) findViewById(R.id.send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMission();
            }
        });
        t = (TextView) findViewById(R.id.textView);

    }

    private void selectDrone() {
        if(!(selectedDrone.isSelected())){
            selectedDrone.selectMarker(true);
        }
    }

    private void sendMission() {
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

    private void displayDrone() {
        try {
            ReadDrone readDrones = new ReadDrone();
            readDrones.execute();
        } catch (Exception e) {
            Toast.makeText(MapsActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
        }
    }

    private class ReadDrone extends AsyncTask<BigInteger, String,List<MarkerItem>> {
        @Override
        protected  List<MarkerItem> doInBackground(BigInteger... params) {
            List<MarkerItem> result = new ArrayList<MarkerItem>();

            try {
                List<String> droneAddr = new ArrayList<String>();
                droneAddr=droneChain.getDrones().send();
                Tuple3<BigInteger,BigInteger,BigInteger> droneInfo;
                //받아온 드론 주소에 대해서 조사 실시
                for(String i : droneAddr){
                    droneInfo = droneChain.getDroneStateByAddr(i).send();
                    MarkerItem markerItem = new MarkerItem(Double.valueOf(droneInfo.getValue1().toString())/Double.valueOf("1000000"),Double.valueOf(droneInfo.getValue2().toString())/Double.valueOf("1000000"),droneInfo.getValue3(),i);
                    //대기 중인 상태(state == 0) 의 드론들만 마커 리스트에 추가
                    if(markerItem.getState().equals(BigInteger.valueOf(0))){
                        result.add(markerItem);
                    }
                }
            } catch (Exception e) {
                result= result;
            }
            return result;
        }
        @Override
        protected void onPostExecute(List<MarkerItem> result) {
            super.onPostExecute(result);
            //드론 표시
            for(MarkerItem i : result){
                mMap.addMarker(new MarkerOptions().position(i.getCoord()).title("Drones").snippet(i.getAddr()));
            }
        }
    }

    private class SetMission extends AsyncTask<BigInteger, String,String> {
        @Override
        protected  String doInBackground(BigInteger... params) {
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
                publishProgress(paraData);

            } catch (Exception e) {
                result = "전송 실패";
            }
            return result;
        }
        @Override
        protected  void onProgressUpdate(String... para){
            super.onProgressUpdate(para);
            List<MissinInfo> dbData = db.selectAll();
            for(MissinInfo i : dbData){
                t.setText(i.getDroneAddr() + " " +i.getMissionIndex().toString() +" "+dbData.size());
            }

        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(MapsActivity.this,result,Toast.LENGTH_SHORT).show();
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
