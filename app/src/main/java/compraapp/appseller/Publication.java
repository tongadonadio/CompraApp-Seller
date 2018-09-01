package compraapp.appseller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Publication extends AppCompatActivity {

    ImageView img;
    TextView productName;
    TextView description;
    TextView clientName;
    TextView date;
    TextView price;
    Button btnOffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publication);

        this.btnOffer = (Button)findViewById(R.id.btnOffer);

        this.img = (ImageView)findViewById(R.id.imgProduct);
        this.productName = (TextView)findViewById(R.id.txtTitleProduct);
        this.description = (TextView)findViewById(R.id.txtDescriptionProduct);
        this.clientName = (TextView)findViewById(R.id.txtNameClient);
        this.date = (TextView)findViewById(R.id.txtDate);
        this.price = (TextView)findViewById(R.id.txtMountProduct);


        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if(b != null){
            this.img.setImageResource(b.getInt("IMG"));
            this.productName.setText(b.getString("PRODUCT"));
            this.description.setText(b.getString("DESCRIPTION"));
            this.clientName.setText(b.getString("NAME"));
            this.date.setText(""/*b.getString("DATE")*/);
            this.price.setText(b.getString("PRICE"));
        }

        btnOffer.setTag(b);

        btnOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = (Bundle)v.getTag();
                Intent intent = new Intent(v.getContext(), Offer.class);
                intent.putExtra("ID",b.getString("ID"));
                intent.putExtra("CLIENT",b.getString("NAME"));
                intent.putExtra("PRODUCT",b.getString("PRODUCT"));
                intent.putExtra("DESCRIPTION",b.getString("DESCRIPTION"));
                startActivity(intent);
            }
        });
    }
}