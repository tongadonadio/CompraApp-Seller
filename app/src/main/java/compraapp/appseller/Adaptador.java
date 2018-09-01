package compraapp.appseller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class Adaptador extends BaseAdapter {
    private static LayoutInflater inflater = null;
    Context context;
    String[][] products;
    int[] imgs;

    public Adaptador(Context context, String[][] data, int[] imgs){
        this.context = context;
        this.products = data;
        this.imgs = imgs;

        inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        final View view = inflater.inflate(R.layout.list_view_element, null);

        TextView txtProduct = (TextView)view.findViewById(R.id.txtProduct);
        TextView txtDescription = (TextView)view.findViewById(R.id.txtDescriptionH);
        TextView txtName = (TextView)view.findViewById(R.id.txtName);
        TextView txtTime = (TextView)view.findViewById(R.id.txtTime);
        ImageView imgPreView = (ImageView)view.findViewById(R.id.imgPreview);

        txtProduct.setText(this.products[i][1]);
        txtDescription.setText(this.products[i][2]);
        txtName.setText(this.products[i][3]);
        txtTime.setText(""/*this.products[i][4]*/);
        imgPreView.setImageResource(this.imgs[i]);

        return view;
    }

    @Override
    public int getCount() {
        return imgs.length;
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
