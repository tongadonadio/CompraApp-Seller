package compraapp.appseller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import static compraapp.appseller.Utils.seller_id;

public class Offer extends AppCompatActivity {

    String id;
    TextView textClientName;
    TextView textProductName;
    TextView textProductDescription;
    TextView textPrice;
    Spinner spinnerState;
    Spinner spinnerShipping;
    Button btnTakePhoto;
    Button btnMakeOffer;
    ImageView imgTaken;
    Bitmap image;
    static final int REQUEST_IMAGE_CAPUTER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);

        this.setValues();
        this.loadSpinners();
        this.setExtras();

    }

    private void setValues(){
        this.spinnerState = (Spinner)findViewById(R.id.spnState);
        this.spinnerShipping = (Spinner)findViewById(R.id.spnShipping);
        this.textClientName = (TextView)findViewById(R.id.txtClientOffer);
        this.textProductName = (TextView)findViewById(R.id.txtProductOffer);
        this.textProductDescription = (TextView)findViewById(R.id.txtDescriptionOffer);
        this.textPrice = (TextView)findViewById(R.id.etxtPriceOffered);
        this.btnTakePhoto = (Button)findViewById(R.id.btnTakePhoto);
        this.btnMakeOffer = (Button)findViewById(R.id.btnMakeOffer);
        this.imgTaken = (ImageView)findViewById(R.id.imgTaken);

        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        btnMakeOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeOffer();
            }
        });
    }

    private void loadSpinners(){
        String[] states = new String[] {"Nuevo", "Usado"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, states);
        String[] shippings = new String[] {"A domicilio", "Retirar"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, shippings);

        this.spinnerState.setAdapter(adapter1);
        this.spinnerShipping.setAdapter(adapter2);
    }

    private void setExtras(){
        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if(b != null){
            this.id = b.getString("ID");
            this.textClientName.setText(b.getString("CLIENT"));
            this.textProductName.setText(b.getString("PRODUCT"));
            this.textProductDescription.setText(b.getString("DESCRIPTION"));
        }
    }

    private void takePhoto(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPUTER);
        }
    }

    private void makeOffer(){
        RequestQueue queue = Volley.newRequestQueue(this);

        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("IdSeller", seller_id + "");
        postParam.put("IdPublication", this.id);
        postParam.put("State", "0");
        postParam.put("PriceItem", this.textPrice.getText().toString());
        postParam.put("DeliveryItem",this.spinnerShipping.getSelectedItemPosition() + "");
        postParam.put("DescriptionItem",this.textProductDescription.getText().toString());
        postParam.put("StateItem", this.spinnerState.getSelectedItemPosition() + "");

        if(this.image != null){
            Bitmap immagex=this.image;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            immagex.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            String imageEncoded = Base64.encodeToString(b,Base64.DEFAULT);
            postParam.put("PhotoItem",imageEncoded);
        }

        String url = Utils.SERVER_URL + "offer";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST,
                url,
                new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        useResponse(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showErrorMsg(error.getMessage());
            }
        })
        {
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                //headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        queue.add(jsonObjReq);

    }

    private void useResponse(JSONObject response){
        try {
            int id = response.getInt("Id");
            if(id>0) {

                setResult(RESULT_OK, null);
                showOkMsg("Se realizó la oferta correctamente");
                finish();
            }
            else{
                showErrorMsg("Error al guardar en el servidor.");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showErrorMsg("Error json: " + e.getMessage());
        }
    }

    private void showOkMsg(String msg){
        Context context = getApplicationContext();
        CharSequence text = msg;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void showErrorMsg(String msg){
        Context context = getApplicationContext();
        CharSequence text = msg;
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_IMAGE_CAPUTER && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            this.image = (Bitmap) extras.get("data");
            this.imgTaken.setImageBitmap(this.image);
            this.imgTaken.setVisibility(View.VISIBLE);
            this.btnTakePhoto.setVisibility(View.INVISIBLE);
        }
    }
}