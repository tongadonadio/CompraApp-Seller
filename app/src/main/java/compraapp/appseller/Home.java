package compraapp.appseller;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import compraapp.appseller.entities.Buyer;
import compraapp.appseller.entities.Publication;
import static compraapp.appseller.Utils.seller_id;

public class Home extends AppCompatActivity {
    ListView publicationsList;
    BottomNavigationView bottomNavigationView;
    String[][] publications;
    int[] imgs;
    GoogleApiClient mGoogleApiClient;
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        this.bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navigation_historical:
                        redirect(Historical.class);
                        break;
                    case R.id.navigation_logout:
                        signOut();
                        redirect(Login.class);
                        break;
                };
                return true;
            }
        });

        loadProducts();

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swip);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadProducts();
            }
        });
    }

    public void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        seller_id = 0;
                        Toast.makeText(getApplicationContext(),"Gracias por utilizar CompraApp",Toast.LENGTH_SHORT).show();
                        Intent i=new Intent(getApplicationContext(),Login.class);
                        startActivity(i);
                    }
                });
    }

    private void redirect(Class t){
        Intent newIntent = new Intent(this,t);
        startActivity(newIntent);
    }

    private void loadProducts(){
        try{
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = Utils.SERVER_URL + "publication?status=" + Utils.PUBLICATION_OPEN;

            StringRequest jsonObjReq = new StringRequest(
                    Request.Method.GET,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            useResponse(response);
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorConnection(error);
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    return headers;
                }
            };
            queue.add(jsonObjReq);
        } catch (Exception e){
            showErrorMsg(e.getMessage());
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void useResponse(String response) {
        try {
            Gson gson = new GsonBuilder().create();
            Publication[] publicationsObjects = gson.fromJson(response.toString(), Publication[].class);
            this.addPublicationsToListView(publicationsObjects);
            refreshAdapter();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMsg("Error request: " + e.getMessage());
        }
    }

    private void addPublicationsToListView(Publication[] publicationsObjects){
        imgs = new int[publicationsObjects.length];
        publications = new String[publicationsObjects.length][6];

        int i = 0;
        for (Publication publication: publicationsObjects) {
            publications[i][0] = publication.getId()+"";
            publications[i][1] = publication.getDescription();
            publications[i][2] = publication.getDescriptionItem();
            publications[i][3] = publication.getNameBuyer();
            Calendar calendar = Calendar.getInstance();
            publications[i][4] = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
            publications[i][5] = publication.getPrice() + "";
            imgs[i] = R.drawable.cube_green;
            i++;
        }
    }

    private void refreshAdapter(){
        publicationsList = (ListView) findViewById(R.id.lstVProducts);
        publicationsList.setAdapter(new Adaptador(this, publications, imgs));
        publicationsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent productDetail = new Intent(view.getContext(), compraapp.appseller.Publication.class);
                productDetail.putExtra("ID", publications[position][0]);
                productDetail.putExtra("PRODUCT", publications[position][1]);
                productDetail.putExtra("DESCRIPTION", publications[position][2]);
                productDetail.putExtra("NAME", publications[position][3]);
                productDetail.putExtra("DATE", publications[position][4]);
                productDetail.putExtra("PRICE", publications[position][5]);
                productDetail.putExtra("IMG", imgs[position]);
                startActivity(productDetail);
            }
        });
    }

    private void errorConnection(Exception e){
        showErrorMsg("onErrorResponse: " + e.getMessage());
        ImageView imgError = findViewById(R.id.imgErrorCloud);
        imgError.setVisibility(View.VISIBLE);
    }

    private void showErrorMsg(String msg){
        Context context = getApplicationContext();
        CharSequence text = msg;
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
