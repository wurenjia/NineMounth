package com.example.ninemounth;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private MapView mMapView=null;
    private BaiduMap mBaiduMap=null;
    private LocationClient mLocationClient=null;
    private Boolean firstLocation=true;
    Button normalmap_btn;
    Button satellitemap_btn;
    Button hotmap_btn;
    Button rcdmap_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapView=findViewById(R.id.baiduMap);
        normalmap_btn=findViewById(R.id.normalmap_btn);
        satellitemap_btn=findViewById(R.id.satellitemap_btn);
        hotmap_btn=findViewById(R.id.hotmap_btn);
        rcdmap_btn=findViewById(R.id.rcdmap_btn);
        mBaiduMap=mMapView.getMap();
        UiSettings mUiSetting = mBaiduMap.getUiSettings();
        //普通地图 ,mBaiduMap是地图控制器对象
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //开启UiSetting
        mUiSetting.setZoomGesturesEnabled(true);
        //开启地图的定位图层
        mBaiduMap.setMyLocationEnabled(true);

        normalmap_btn.setOnClickListener(this);
        satellitemap_btn.setOnClickListener(this);
        hotmap_btn.setOnClickListener(this);
        rcdmap_btn.setOnClickListener(this);

        //定位初始化
        mLocationClient = new LocationClient(MainActivity.this);
        //注册LocationListener监听器
        MyLocationListener myLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myLocationListener);
        //初始化经纬度
        initLocation();

        //开启地图定位图层
        mLocationClient.start();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.normalmap_btn:
                //普通地图 ,mBaiduMap是地图控制器对象
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
            //    Toast.makeText(this, "开启普通地图", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, mLocationClient.toString(), Toast.LENGTH_SHORT).show();

                break;
            case R.id.satellitemap_btn:
                //卫星地图
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                Toast.makeText(this, "开启卫星地图", Toast.LENGTH_SHORT).show();
                break;
            case R.id.hotmap_btn:
                //开启热力图
                mBaiduMap.setBaiduHeatMapEnabled(true);
                Toast.makeText(this, "开启热力图", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rcdmap_btn:
                //开启交通图
                mBaiduMap.setTrafficEnabled(true);
                mBaiduMap.setCustomTrafficColor("#ffba0101", "#fff33131", "#ffff9e19", "#00000000");
                //  对地图状态做更新，否则可能不会触发渲染，造成样式定义无法立即生效。
                MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(13);
                mBaiduMap.animateMapStatus(u);
                Toast.makeText(this, "开启交通地图", Toast.LENGTH_SHORT).show();
                break;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
    @Override
    protected void onDestroy() {
        mLocationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //mapView 销毁后不在处理新接收的位置
            if (location == null || mMapView == null){
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            Toast.makeText(MainActivity.this, "经度："+location.getDirection()+"纬度："+location.getLongitude()
                    , Toast.LENGTH_SHORT).show();
            mBaiduMap.setMyLocationData(locData);
            // 第一次定位时，将地图位置移动到当前位置
            if (firstLocation)
            {
                firstLocation = false;
                LatLng xy = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate status = MapStatusUpdateFactory.newLatLng(xy);
                mBaiduMap.animateMapStatus(status);
            }
        }
    }

    private void initLocation() {

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        /**可选，设置定位模式，默认高精度LocationMode.Hight_Accuracy：高精度；
         * LocationMode. Battery_Saving：低功耗；LocationMode. Device_Sensors：仅使用设备；*/

        option.setCoorType("gcj02gcj02");
        /**可选，设置返回经纬度坐标类型，默认gcj02gcj02：国测局坐标；bd09ll：百度经纬度坐标；bd09：百度墨卡托坐标；
         海外地区定位，无需设置坐标类型，统一返回wgs84类型坐标*/

        option.setScanSpan(3000);
        /**可选，设置发起定位请求的间隔，int类型，单位ms如果设置为0，则代表单次定位，即仅定位一次，默认为0如果设置非0，需设置1000ms以上才有效*/

        option.setOpenGps(true);
        /**可选，设置是否使用gps，默认false使用高精度和仅用设备两种定位模式的，参数必须设置为true*/

        option.setLocationNotify(true);
        /**可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false*/

        option.setIgnoreKillProcess(false);
        /**定位SDK内部是一个service，并放到了独立进程。设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)*/

        option.SetIgnoreCacheException(false);
        /**可选，设置是否收集Crash信息，默认收集，即参数为false*/
        option.setIsNeedAltitude(true);/**设置海拔高度*/

        option.setWifiCacheTimeOut(5 * 60 * 1000);
        /**可选，7.2版本新增能力如果设置了该接口，首次启动定位时，会先判断当前WiFi是否超出有效期，若超出有效期，会先重新扫描WiFi，然后定位*/

        option.setEnableSimulateGps(false);
        /**可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false*/

        option.setIsNeedAddress(true);
        /**可选，设置是否需要地址信息，默认不需要*/

        mLocationClient.setLocOption(option);
        /**mLocationClient为第二步初始化过的LocationClient对象需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用*/
    }
}

