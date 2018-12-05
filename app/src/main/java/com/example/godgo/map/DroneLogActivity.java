package com.example.godgo.map;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tuples.generated.Tuple4;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/* import view */
/*import org.web3j*/

public class DroneLogActivity extends AppCompatActivity {

    Credentials credentials;
    String contractAddr;
    BigInteger gasPrice = BigInteger.valueOf(660000000240L);
    BigInteger gasLimit = BigInteger.valueOf(200000L);
    Future<String> a, b;
    Web3j web3;

    DBManager db;
    Dronechain droneChain;

    //    if(Build.VERSION.SDK_INT>22){
//        requestPermissions(new String[] {YOUR_PERMISSIONS AS STRING}, 1);
//    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (!(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    //Toast.makeText(addAlarm.this, "Permission denied to access your location.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        db = new DBManager(DroneLogActivity.this);
        if (Build.VERSION.SDK_INT > 22) {
            requestPermissions(new String[]{READ_EXTERNAL_STORAGE,WRITE_EXTERNAL_STORAGE}, 1);
        }
        try {
            credentials = WalletUtils.loadCredentials("tjrwns92!", Uri.parse(Environment.getExternalStorageDirectory() + "/Pictures/UTC--2018-10-30T06-07-25.479Z--21ae06c29be5c4a899a2545519a29d976e2fdd24").toString());
            //t3.setText(credentials.getAddress());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CipherException e) {
            e.printStackTrace();
        }

        contractAddr = "0xa56e93d7a1Bf923Aa2A8DD863535d124BBD776EA";
        web3 = Web3jFactory.build(new HttpService("https://rinkeby.infura.io/v3/faa0a68fa9bc43b0a56c79f82069e283"));
        droneChain = Dronechain.load(contractAddr, web3, credentials, gasPrice, gasLimit);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        //Intent intent = new Intent(this.getIntent());

        getLog();
    }

//        initUi();
//
//        ListView listView = (ListView) findViewById(R.id.listView);
//        ArrayList<LogItem> Log = new ArrayList<>();
//
//        AdapterItem adapter = new AdapterItem(this, R.layout.adapter_log, Log);
//        listView.setAdapter(adapter);
//    private void initUi() {
//        Button logButton = (Button) findViewById(R.id.button);
//        logButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getLog();
//            }
//        });
//    }

    public void getLog() {
        try {
            ReadDrone readDrone = new ReadDrone();
            readDrone.execute();
//            droneChain.traceFlightHistory()
        } catch (Exception e) {
            //읽어오는데 에러가 발생했을경우 발생하는 토스트 실행구문
        }
    }

    private class ReadDrone extends AsyncTask<BigInteger, String, List<Listviewitem>> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            Toast.makeText(DroneLogActivity.this,String.valueOf(db.selectAll().size()),Toast.LENGTH_SHORT).show();
        }
        @Override
        protected List<Listviewitem> doInBackground(BigInteger... params) {
            List<Listviewitem> result = new ArrayList<>();
            try {

                Intent intent = getIntent();
                String s = intent.getStringExtra("DroneAdd");

                Tuple3<List<BigInteger>, List<BigInteger>, BigInteger> getMissionInfo;
                List<MissinInfo> missionData = db.selectAll();
                //받아온 드론 주소에 대해서 조사 실시
                for (MissinInfo i : missionData) {

                    //droneLog = droneChain.traceFlightHistory(i, droneChain.getDroneStateByAddr(i).send().getValue3()).send(); // 갖고온 state로 확인하는것 두가지 방식이 있지만 여기서는 그냥 drone 의 state 를 따라가는걸로 설정
                    //int k = 0;
                    //for (BigInteger j = BigInteger.valueOf(0); j.compareTo(droneLog.getValue4()) < 0; j = j.add(BigInteger.ONE), k++) { // j=0 ; j<dronelog.count; j++;
                    //Listviewitem listviewitem = new Listviewitem(droneLog.getValue1().get(k), droneLog.getValue2().get(k), droneLog.getValue3().get(k), droneLog.getValue4()); // 이해의 편의상 값처리 안하고 넣어줌
                    //result.add(listviewitem); //listviewitem 에 추가함
                    /*
                    if(!i.getDroneAddr().equals(s)) {
                        continue;
                    }*/
                    getMissionInfo = droneChain.getMission(i.getDroneAddr(), BigInteger.valueOf(i.getMissionIndex())).send();
                    List<BigInteger> lat = getMissionInfo.getValue1();
                    for (int j = 0; j < lat.size(); j++) {
//                            inputLat = Double.valueOf(lat.get(j).toString()) / Double.valueOf("1000000");
//                            inputLon = Double.valueOf(lon.get(j).toString()) / Double.valueOf("1000000");
                        result.add(new Listviewitem(getMissionInfo.getValue1().get(j),getMissionInfo.getValue2().get(j),s,getMissionInfo.getValue3(),i.getMissionIndex()));
                    }
                }
            } catch (Exception e) {

            }
            return result;
        }
        @Override
        protected void onPostExecute(List<Listviewitem> result) {
            super.onPostExecute(result);
            //드론 로그 표시
            double lon;
            double lat;
            String cmd;
            double cnt;
            int ind;

            ListView listView = (ListView) findViewById(R.id.listView);
            ArrayList<LogItem> Log = new ArrayList<>();
            AdapterItem adapter = new AdapterItem(DroneLogActivity.this, R.layout.adapter_log, Log);
            listView.setAdapter(adapter);


            for (Listviewitem i : result) {
                lon = Double.valueOf(i.getDstlon().toString()) / Double.valueOf("1000000");
                lat = Double.valueOf(i.getDstlat().toString()) / Double.valueOf("1000000");
                cmd = i.getCommander();
                cnt = Double.valueOf(i.getCount().toString());
                ind = i.getIndex();
                LogItem data = new LogItem(lon, lat, cmd, cnt, ind);
                Log.add(data);
            }

            adapter.notifyDataSetChanged();

            /*starts here*/
            //리스트뷰 onclickListener
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = getIntent();

                    Toast.makeText(DroneLogActivity.this, Log.get(position).getCommander(), Toast.LENGTH_LONG).show();
                    //Intent intent =new Intent(DroneLogActivity.this,MapsActivity.class);
                    intent.putExtra("address", Log.get(position).getCommander());
                    intent.putExtra("index",Log.get(position).getIndex());
                    setResult(RESULT_OK, intent);
                    //startActivity(intent);
                    finish();
                }
            });


        }
    }

}