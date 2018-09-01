package compraapp.appseller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AdaptadorHistorical extends BaseAdapter {
    private static LayoutInflater inflater = null;
    Context context;
    String[][] products;

    public AdaptadorHistorical(Context context, String[][] data){
        this.context = context;
        this.products = data;

        inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        final View view = inflater.inflate(R.layout.list_view_historical, null);

        TextView txtProduct = (TextView)view.findViewById(R.id.txtProductH);
        TextView txtDescription = (TextView)view.findViewById(R.id.txtDescriptionH);
        TextView txtPrice = (TextView)view.findViewById(R.id.txtPriceH);
        TextView txtState = (TextView)view.findViewById(R.id.txtStateH);
        TextView txtTime = (TextView)view.findViewById(R.id.txtTimeH);
        ImageView imgPreView = (ImageView)view.findViewById(R.id.imgPreviewH);

        txtProduct.setText(this.products[i][0]);
        txtDescription.setText(this.products[i][1]);
        txtPrice.setText(this.products[i][2]);
        txtState.setText(this.products[i][3]);
        txtTime.setText(this.products[i][4]);

        if(this.products[i][5] != null){
            byte[] decodedString = Base64.decode(this.products[i][5], Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imgPreView.setImageBitmap(decodedByte);
        }

        return view;
    }

    @Override
    public int getCount() {
        return products.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
