package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.internal.NavigationMenu;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    List<Result> movieList;
    RecyclerView recyclerView;
    AdapterClass recyclerAdapter;
    NestedScrollView nestedScrollView;
    int pageno = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nestedScrollView =  findViewById(R.id.nestedscrollview);
        movieList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerAdapter = new AdapterClass(this,movieList);
        recyclerView.setLayoutManager(layoutManager);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Top Rated Movies");

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_bar);
        navigationView.setNavigationItemSelectedListener(this);

        ApiInterface apiService = ApiClinet.getClient().create(ApiInterface.class);
        Call<MovieModel> call = apiService.getTopRated();

        call.enqueue(new Callback<MovieModel>() {

            @Override
            public void onResponse(@NonNull Call<MovieModel> call, @NonNull Response<MovieModel> response) {
                assert response.body() != null;
                movieList.addAll(response.body().getResults());
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(movieList);
                recyclerAdapter.setMovieList(movieList);
                recyclerView.setAdapter(recyclerAdapter);
            }

            @Override
            public void onFailure(@NonNull Call<MovieModel> call, @NonNull Throwable t) {
                Log.d("TAG","Response = "+t.toString());
            }
        });


        setupPagination(true);
    }

    private void setupPagination(boolean isPaginationAllowed) {

        if (isPaginationAllowed){
            nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if(scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()){
                        pageno = pageno+1;
                        fetchData(pageno);
                    }
                }
            });
        }

    }

    private void fetchData(int i) {
        String API_KEY = "02514a833edd3813d9165daab702f5b8";
        ApiInterface apiService = ApiClinet.getClient().create(ApiInterface.class);
        Call<MovieModel> call = apiService.getLoadTopRated(API_KEY,i);

        call.enqueue(new Callback<MovieModel>() {

            @Override
            public void onResponse(@NonNull Call<MovieModel> call, @NonNull Response<MovieModel> response) {
                assert response.body() != null;
                movieList.addAll(response.body().getResults());
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(movieList);
                recyclerAdapter.setMovieList(movieList);
                recyclerView.setAdapter(recyclerAdapter);
            }

            @Override
            public void onFailure(@NonNull Call<MovieModel> call, @NonNull Throwable t) {
                Log.d("TAG","Response = "+t.toString());
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_top_rated){
//            startActivity(new Intent(this,MainActivity.class));
            Toast.makeText(this,"Already in Top Rated List",Toast.LENGTH_SHORT).show();
        }
        else if(id == R.id.nav_popular){
            startActivity(new Intent(this,Popular.class));
            Toast.makeText(this,"Popular List",Toast.LENGTH_SHORT).show();
        }
        else if(id == R.id.nav_upcoming){
            startActivity(new Intent(this,Upcoming.class));
            Toast.makeText(this,"Upcoming List",Toast.LENGTH_SHORT).show();
        }
        else if(id == R.id.nav_fav){
            startActivity(new Intent(this,FavActivity.class));
            Toast.makeText(this,"Your Favorite List",Toast.LENGTH_SHORT).show();
        }
        else if(id == R.id.nav_logout){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
            Toast.makeText(this,"Logged Out",Toast.LENGTH_SHORT).show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerlayout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }
}
