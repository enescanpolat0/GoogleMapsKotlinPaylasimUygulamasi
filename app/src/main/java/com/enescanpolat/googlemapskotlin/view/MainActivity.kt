package com.enescanpolat.googlemapskotlin.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.enescanpolat.googlemapskotlin.R
import com.enescanpolat.googlemapskotlin.adapter.placeAdapter
import com.enescanpolat.googlemapskotlin.databinding.ActivityMainBinding
import com.enescanpolat.googlemapskotlin.model.place
import com.enescanpolat.googlemapskotlin.roomdb.placedatabase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val compositedisposable = CompositeDisposable()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val db = Room.databaseBuilder(applicationContext,placedatabase::class.java,"Places").build()
        val placedao = db.placedao()

        compositedisposable.add(
            placedao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse)

        )


    }

    private fun handleResponse(placeList : List<place>){

        binding.recyclerView.layoutManager= LinearLayoutManager(this)
        val adapter = placeAdapter(placeList)
        binding.recyclerView.adapter=adapter
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuinflater = menuInflater
        menuinflater.inflate(R.menu.place_menu,menu)

        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId== R.id.add_place){
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("info","new")
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }
}