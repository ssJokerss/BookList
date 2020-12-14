package com.jnu.booklist.data;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.jnu.booklist.R;
import com.jnu.booklist.data.model.Shop;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapViewFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MapViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapViewFragment newInstance(String param1, String param2) {
        MapViewFragment fragment = new MapViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    private MapView mapView = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_map_view, container, false);
        mapView = (MapView) view.findViewById(R.id.baidu_map_view);
        LatLng centerPoint = new LatLng(22.2559,113.541112);
        //修改百度地图的初始位置
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(centerPoint).zoom(17).build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);

        BaiduMap baiduMap = mapView.getMap();

        baiduMap.setMapStatus(mMapStatusUpdate);

        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.book_icon);
        MarkerOptions markerOption = new MarkerOptions().icon(bitmap).position(centerPoint);
        Marker marker = (Marker) baiduMap.addOverlay(markerOption);

        OverlayOptions textOption = new TextOptions().bgColor(0xAAFFFF00).fontSize(50)
                .fontColor(0xFFFF00FF).text("我的学校").rotate(0).position(centerPoint);
        baiduMap.addOverlay(textOption);

        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker arg0) {
                Toast.makeText(getContext(), "Marker被点击了！", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        final ShopLoader shopLoader=new ShopLoader();
        Handler handler = new Handler(){
            public void handleMessage(Message msg){
                drawShops(shopLoader.getShops());
            }
        };
        shopLoader.load(handler,"http://file.nidama.net/class/mobile_develop/data/bookstore.json");
        return view;
    }
    void drawShops(ArrayList<Shop>shops)
    {
        if(mapView==null)return;
        BaiduMap baiduMap = mapView.getMap();
        for(int i=0;i<shops.size();i++) {
            Shop shop = shops.get(i);
            LatLng cenpt = new LatLng(shop.getLatitude(),shop.getLongitude());//设定中心点坐标

            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.book_icon);
            MarkerOptions markerOption = new MarkerOptions().icon(bitmap).position(cenpt);
            Marker marker = (Marker) baiduMap.addOverlay(markerOption);

            OverlayOptions textOption = new TextOptions().bgColor(0xAAFFFF00).fontSize(50).fontColor(0xFFFF00FF).text("我的学校").rotate(0).position(cenpt);
            baiduMap.addOverlay(textOption);

            baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker arg0) {
                    Toast.makeText(getContext(), "Marker被点击了！", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });

        }
    }
}