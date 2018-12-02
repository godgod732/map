package com.example.godgo.map;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple4;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/* import view */
/*import org.web3j*/

public class DroneLogActivity extends AppCompatActivity {

    Credentials credentials;
    String contractAddr;
    BigInteger gasPrice = BigInteger.valueOf(660000000240L);
    BigInteger gasLimit = BigInteger.valueOf(200000L);
    Future<String> a, b;
    Web3j web3;

    Dronechain droneChain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            credentials = WalletUtils.loadCredentials("tjrwns92!", "/storage/emulated/0/Pictures/UTC--2018-10-30T06-07-25.479Z--21ae06c29be5c4a899a2545519a29d976e2fdd24");
            //t3.setText(credentials.getAddress());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CipherException e) {
            e.printStackTrace();
        }

        contractAddr = "0x5D02F24c421652aB804d92d5c0dCd18cB24247d9";
        web3 = Web3jFactory.build(new HttpService("https://rinkeby.infura.io/v3/faa0a68fa9bc43b0a56c79f82069e283"));
        droneChain = Dronechain.load(contractAddr,web3,credentials,gasPrice,gasLimit);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        initUi();
//
//        ListView listView = (ListView) findViewById(R.id.listView);
//        ArrayList<LogItem> Log = new ArrayList<>();
//
//        AdapterItem adapter = new AdapterItem(this, R.layout.adapter_log, Log);
//        listView.setAdapter(adapter);

    }

    private void initUi() {

        //리스트 처리

        //버튼처리
        Button logButton = (Button) findViewById(R.id.button);
        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLog();
            }
        });
    }

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
        protected List<Listviewitem> doInBackground(BigInteger... params) {
            List<Listviewitem> result = new ArrayList<>();
            try {
                List<String> droneAddress = droneChain.getDrones().send(); //보내온 드론 List
                Tuple4<List<BigInteger>, List<BigInteger>, List<String>, BigInteger> droneLog; // 드론 List -> 그 드론의 Mission 기록들 List 를 볼거임
                //받아온 드론 주소에 대해서 조사 실시
                for (String i : droneAddress) {
                    droneLog = droneChain.traceFlightHistory(i, droneChain.getDroneStateByAddr(i).send().getValue3()).send(); // 갖고온 state로 확인하는것 두가지 방식이 있지만 여기서는 그냥 drone 의 state 를 따라가는걸로 설정
                    int k = 0;
                    for (BigInteger j = BigInteger.valueOf(0); j.compareTo(droneLog.getValue4()) < 0; j = j.add(BigInteger.ONE), k++) { // j=0 ; j<dronelog.count; j++;
                        Listviewitem listviewitem = new Listviewitem(droneLog.getValue1().get(k), droneLog.getValue2().get(k), droneLog.getValue3().get(k), droneLog.getValue4()); // 이해의 편의상 값처리 안하고 넣어줌
                        result.add(listviewitem); //listviewitem 에 추가함
                    }
                }
            } catch (Exception e) {

            }
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    listView.setAdapter(adapter);
//                }
//            });
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

            ListView listView = (ListView) findViewById(R.id.listView);
            ArrayList<LogItem> Log = new ArrayList<>();
            AdapterItem adapter = new AdapterItem(DroneLogActivity.this, R.layout.adapter_log, Log);
            listView.setAdapter(adapter);

            for (Listviewitem i : result) {
                lon = Double.valueOf(i.getDstlon().toString()) / Double.valueOf("1000000");
                lat = Double.valueOf(i.getDstlat().toString()) / Double.valueOf("1000000");
                cmd = i.getCommander();
                cnt = Double.valueOf(i.getCount().toString());
                //여기서 문제가 발생하는듯... 0으로 컨트랙트에서 선언되었기 때문에 새로운 로그를 받으면 0으로 갱신되서 계속 0 부터 시작함..
                LogItem data = new LogItem(lon, lat, cmd, cnt);
                Log.add(data);
            }

            adapter.notifyDataSetChanged();
        }
    }

}
