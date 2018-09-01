package compraapp.appseller;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

import compraapp.appseller.entities.Offer;

public class Historical extends AppCompatActivity {
    ListView offersList;
    BottomNavigationView bottomNavigationView;
    String[][] offers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historical);
        this.bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottomNavigationView);
        this.bottomNavigationView.setSelectedItemId(R.id.navigation_historical);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navigation_home:
                        redirect(Home.class);
                        break;
                    case R.id.navigation_logout:
                        redirect(Login.class);
                        break;
                };
                return true;
            }
        });

        loadOffers();
    }

    private void redirect(Class t){
        Intent newIntent = new Intent(this,t);
        startActivity(newIntent);
    }

    private void loadOffers(){
        try{
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = Utils.SERVER_URL + "offer?idSeller=" + Utils.seller_id;

            StringRequest jsonObjReq = new StringRequest(
                    Request.Method.GET,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            useResponse(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorConnection(error);
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
        }
    }

    private void useResponse(String response) {
        try {
            Gson gson = new GsonBuilder().create();
            compraapp.appseller.entities.Offer[] offersObjects = gson.fromJson(response.toString(), compraapp.appseller.entities.Offer[].class);
            this.addOffersToListView(offersObjects);
            refreshAdapter();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMsg("Error request: " + e.getMessage());
        }
    }

    private void addOffersToListView(compraapp.appseller.entities.Offer[] offersObjects){
        offers = new String[offersObjects.length][6];

        int i = 0;
        for (compraapp.appseller.entities.Offer offer: offersObjects) {
            offers[i][0] = offer.getIdPublication()+"";
            offers[i][1] = offer.getDescriptionItem();
            offers[i][2] = "$"+offer.getPriceItem();
            offers[i][3] = mapperState(offer.getState());
            offers[i][4] = "18:30";
            offers[i][5] = offer.getPhotoItem();
            i++;
        }
    }

    public String mapperState(int state){
        switch (state){
            case 0: return "En espera";
            case 1: return "Rechazado";
            case 2: return "Aceptado";
            case 3: return "Cancelado";
        };
        return "";
    }

    private void refreshAdapter(){
        offersList = (ListView) findViewById(R.id.lstVProductsH);
        offersList.setAdapter(new AdaptadorHistorical(this, offers));
        /*offersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent productDetail = new Intent(view.getContext(), compraapp.appseller.Publication.class);
                productDetail.putExtra("ID", publications[position][0]);
                productDetail.putExtra("PRODUCT", publications[position][1]);
                productDetail.putExtra("DESCRIPTION", publications[position][2]);
                productDetail.putExtra("NAME", publications[position][3]);
                productDetail.putExtra("DATE", publications[position][4]);
                productDetail.putExtra("IMG", imgs[position]);
                startActivity(productDetail);
            }
        });*/
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
