package com.example.godgo.map;

import com.google.android.gms.maps.model.LatLng;

import org.web3j.tuples.generated.Tuple2;

import java.math.BigInteger;
import java.util.List;

public class MarkerItem {
        Double lat;
        Double lon;
        BigInteger state;
        String address;
        boolean selectedState;

        public MarkerItem(double lat, double lon, BigInteger state,String address) {
            this.lat = lat;
            this.lon = lon;
            this.state = state;
            this.address = address;
            this.selectedState = false;
        }

        public LatLng getCoord(){
            return new LatLng(lat,lon);
        }

        public Tuple2<Double, Double> getCoordToDouble() {
            return (new Tuple2<Double,Double>(lat,lon));
        }

        public String getAddr(){
            return this.address;
        }

        public boolean isSelected(){
            return selectedState;
        }

        public void selectMarker(boolean state){
            this.selectedState = state;
        }

        public BigInteger getState() {
            return state;
        }
}
