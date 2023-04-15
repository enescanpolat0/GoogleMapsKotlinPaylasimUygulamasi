package com.enescanpolat.googlemapskotlin.view

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.enescanpolat.googlemapskotlin.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.enescanpolat.googlemapskotlin.databinding.ActivityMapsBinding
import com.enescanpolat.googlemapskotlin.model.place
import com.enescanpolat.googlemapskotlin.roomdb.placeDao
import com.enescanpolat.googlemapskotlin.roomdb.placedatabase
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MapsActivity : AppCompatActivity(), OnMapReadyCallback , GoogleMap.OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationmanager :LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var permissionlauncher: ActivityResultLauncher<String>
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var db: placedatabase
    private lateinit var placedao : placeDao
    private val mdisposable = CompositeDisposable()
    private var trackbolean : Boolean?=null
    private var selectedLatitude : Double? = null
    private var selectedLongitude : Double? = null
    var placeFromMain : place?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        registerLauncher()

        sharedPreferences=this.getSharedPreferences("com.enescanpolat.googlemapskotlin", MODE_PRIVATE)
        trackbolean==false
        selectedLatitude=0.0
        selectedLongitude=0.0

        db= Room.databaseBuilder(applicationContext,placedatabase::class.java,"Places")

            .build()
        placedao=db.placedao()

        binding.saveButton.isEnabled=false


    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(this)

        val intent = intent
        val info = intent.getStringExtra("info")

        if (info =="new"){

            binding.saveButton.visibility= View.VISIBLE
            binding.delete.visibility=View.GONE

            //CASTING as işlemi
            locationmanager = this.getSystemService(LOCATION_SERVICE) as LocationManager

            locationListener = object : LocationListener{
                override fun onLocationChanged(p0: Location) {
                    trackbolean==sharedPreferences.getBoolean("trackbolean",false)
                    if (trackbolean==false){
                        val userLocation = LatLng(p0.latitude,p0.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15f))
                        sharedPreferences.edit().putBoolean("trackbolean",true).apply()
                    }



                }



            }

            if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
                //izin yok izin iste
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                    Snackbar.make(binding.root,"Konumunuzu almak istiyoruz izin verirmisiniz",Snackbar.LENGTH_INDEFINITE).setAction("Izin ver"){
                        // izin iste
                        permissionlauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)

                    }.show()
                }else{
                    permissionlauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)

                }
            }else{
                //izin var devam et
                locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,locationListener)
                val lastlocation = locationmanager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (lastlocation!=null){
                    val lastuserLocation = LatLng(lastlocation.latitude,lastlocation.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastuserLocation,15f))
                }
                mMap.isMyLocationEnabled= true
            }


        }else{

            mMap.clear()
            placeFromMain = intent.getSerializableExtra("selectedPlace") as? place

            placeFromMain?.let {

                val latlng = LatLng(it.latitude,it.longitude)
                mMap.addMarker(MarkerOptions().position(latlng).title(it.name))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,15f))

                binding.placeText.setText(it.name)
                binding.saveButton.visibility=View.GONE
                binding.delete.visibility=View.VISIBLE

            }


        }








    }

    private fun registerLauncher(){

        permissionlauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ result->

            if (result){
                //izin verildi
                if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                    locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,locationListener)
                    val lastlocation = locationmanager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (lastlocation!=null){
                        val lastuserLocation = LatLng(lastlocation.latitude,lastlocation.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastuserLocation,15f))
                    }
                    mMap.isMyLocationEnabled= true
                }

            }else{
                //izin verilmedi
                Toast.makeText(this,"izine ihtiyacımız var",Toast.LENGTH_LONG).show()
            }

        }

    }

    override fun onMapLongClick(p0: LatLng) {

        mMap.clear()
        mMap.addMarker(MarkerOptions().position(p0))

        selectedLatitude=p0.latitude
        selectedLongitude=p0.longitude
        binding.saveButton.isEnabled=false

    }


    fun save(view:View){

        if (selectedLatitude!=null && selectedLongitude!=null){

            val place = place(binding.placeText.text.toString(),selectedLatitude!!,selectedLongitude!!)
            mdisposable.add(
                placedao.insert(place)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleResponse)

            )


        }



    }

    private fun handleResponse(){
        val intent = Intent(this,MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    fun delete(view: View){

        placeFromMain?.let {
            mdisposable.add(

                placedao.delete(it)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleResponse)
            )
        }




    }

    override fun onDestroy() {
        super.onDestroy()
        mdisposable.clear()
    }

}