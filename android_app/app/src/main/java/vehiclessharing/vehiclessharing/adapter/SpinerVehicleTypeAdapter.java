package vehiclessharing.vehiclessharing.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import vehiclessharing.vehiclessharing.R;

/**
 * Created by Hihihehe on 10/14/2017.
 */

public class SpinerVehicleTypeAdapter extends ArrayAdapter<String> {

    private List<String> vehicleList;
    private Context context;
    public SpinerVehicleTypeAdapter(@NonNull Context context, @NonNull List<String> objects) {
        super(context, R.layout.vehicle_type_row, objects);
        this.context=context;
        vehicleList=objects;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.vehicle_type_row, parent, false);

            TextView textView = (TextView) row.findViewById(R.id.txtTypeVehicle);
            textView.setText(vehicleList.get(position));

            ImageView imageView = (ImageView)row.findViewById(R.id.imgIconSpinner);
            if(position==1)
            {
                imageView.setImageResource(R.drawable.ic_directions_car_indigo_700_24dp);
            }else {
                imageView.setImageResource(R.drawable.ic_directions_bike_indigo_700_24dp);
            }
            //imageView.setImageResource(imageArray[position]);

            return row;
    }

}
