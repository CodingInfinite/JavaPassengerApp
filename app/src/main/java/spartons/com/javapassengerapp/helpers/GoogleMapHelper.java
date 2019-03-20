package spartons.com.javapassengerapp.helpers;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import spartons.com.javapassengerapp.R;

public class GoogleMapHelper {

    private static final int ZOOM_LEVEL = 18;
    private static final int TILT_LEVEL = 25;

    /**
     * @param latLng in which position to Zoom the camera.
     * @return the [CameraUpdate] with Zoom and Tilt level added with the given position.
     */

    public CameraUpdate buildCameraUpdate(LatLng latLng) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .tilt(TILT_LEVEL)
                .zoom(ZOOM_LEVEL)
                .build();
        return CameraUpdateFactory.newCameraPosition(cameraPosition);
    }

    
    /**
     * @param position where to draw the [com.google.android.gms.maps.model.Marker]
     * @return the [MarkerOptions] with given properties added to it.
     */

    public MarkerOptions getDriverMarkerOptions(LatLng position) {
        return  getMarkerOptions(position, R.drawable.car_icon);
    }

    /**
     * @param position where to draw the user current location marker;
     * @return the [{@link MarkerOptions}] with the given properties add to it.
     */

    public MarkerOptions getCurrentLocationMarker(LatLng position) {
        return getMarkerOptions(position, 0);
    }

    private MarkerOptions getMarkerOptions(LatLng position, int resource) {
        BitmapDescriptor bitmapDescriptor;
        if (resource != 0)
            bitmapDescriptor = BitmapDescriptorFactory.fromResource(resource);
        else
            bitmapDescriptor = BitmapDescriptorFactory.defaultMarker();
        return new MarkerOptions()
                .icon(bitmapDescriptor)
                .position(position)
                .flat(true);
    }
}
