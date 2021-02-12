package com.adriansng.projet_7_go4lunch.view.mainActivity.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.adriansng.projet_7_go4lunch.R;
import com.adriansng.projet_7_go4lunch.model.Restaurant;
import com.adriansng.projet_7_go4lunch.model.Workmate;
import com.adriansng.projet_7_go4lunch.utils.Utils;
import com.adriansng.projet_7_go4lunch.view.detailRestaurant.DetailRestaurantActivity;
import com.adriansng.projet_7_go4lunch.viewModel.Injection;
import com.adriansng.projet_7_go4lunch.viewModel.mainActivity.ViewModeMainActivity;
import com.adriansng.projet_7_go4lunch.viewModel.mainActivity.ViewModelFactoryMainActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Objects;


public class MapFragment extends Fragment implements GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, LocationSource, OnMapReadyCallback {

    // ---- FOR DATA ---

    private GoogleMap mapGoogle;

    private ViewModeMainActivity viewModeMainActivity;

    private List<Restaurant> restaurantList;
    private List<Workmate> workmateList;

    // --- FRAGMENT INSTANCE ---

    public MapFragment() {
    }

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    // ------------------
    // TO CREATE
    // ------------------

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.map_fragment, container, false);
        configViewModel();
        if (mapGoogle == null) configMap();
        return root;
    }

    // ------------------
    // VIEW MODEL
    // ------------------

    private void configViewModel() {
        ViewModelFactoryMainActivity viewModelFactoryMainActivity = Injection.viewModelFactoryMainActivity();
        viewModeMainActivity = new ViewModelProvider(requireActivity(), viewModelFactoryMainActivity).get(ViewModeMainActivity.class);
    }

    // ------------------
    // PLACES
    // ------------------

    private void getPlaces(List<Workmate> workmateList) {
        viewModeMainActivity.getListPlaces().observe(getViewLifecycleOwner(), restaurants -> {
            restaurantList = restaurants;
            getMarkers(restaurantList, workmateList);
        });
    }

    // ------------------
    // WORKMATES FOR PLACE
    // ------------------

    private void getWorkmateForPlace() {
        viewModeMainActivity.getWorkmates().observe(getViewLifecycleOwner(), workmates -> {
            workmateList = workmates;
            getPlaces(workmateList);
        });

    }

    // ------------------
    // GOOGLE MAP
    // ------------------

    // --- MAP ---

    private void configMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);
        Objects.requireNonNull(mapFragment).getMapAsync(this);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapGoogle = googleMap;
        Location mLocation = Utils.getLastKnowLocation(requireContext(), requireActivity());
        if (mLocation != null) {
            mapGoogle.setMyLocationEnabled(true);
            mapGoogle.setOnMyLocationClickListener(this);
            mapGoogle.setOnMyLocationButtonClickListener(this);
            zoomLocation(mLocation);
            getWorkmateForPlace();
        }
    }

    // --- LOCATION ---

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
    }

    @Override
    public void deactivate() {
    }

    // --- ZOOM LOCATION ---

    private void zoomLocation(Location location) {
        mapGoogle.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), 15));
    }

    // ------------------
    // MARKER PLACE
    // ------------------

    // --- MARKERS ---

    private void getMarkers(List<Restaurant> restaurants, List<Workmate> workmateList) {
        if (mapGoogle != null) mapGoogle.clear();
        int size = restaurants.size();
        for (int i = 0; i < size; i++) {
            Restaurant restaurantMarker = restaurants.get(i);
            LatLng latLng = new LatLng(restaurantMarker.getLocation().getLat(), restaurantMarker.getLocation().getLng());
            if (viewModeMainActivity.checkWorkmateForTheRestaurant(workmateList, restaurantMarker.getPlaceId())) {
                mapGoogle.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_place_green))
                        .title(restaurantMarker.getName()));
            } else {
                mapGoogle.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_place_red))
                        .title(restaurantMarker.getName()));
            }
            clickOnMarker(mapGoogle, restaurants);
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int drawable) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, drawable);
        Objects.requireNonNull(vectorDrawable).setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    // --- CLICK MARKER ---

    private void clickOnMarker(GoogleMap mapGoogle, List<Restaurant> restaurants) {
        mapGoogle.setOnMarkerClickListener(marker -> {
            Context context = getContext();
            Intent intent = new Intent(context, DetailRestaurantActivity.class);
            int size = restaurants.size();
            for (int e = 0; e < size; e++) {
                if (restaurants.get(e).getName().matches(marker.getTitle())) {
                    intent.putExtra("Restaurant", restaurants.get(e));
                    Objects.requireNonNull(context).startActivity(intent);
                }
            }
            Toast.makeText(getContext(), marker.getTitle(), Toast.LENGTH_SHORT).show();
            return false;
        });
    }
}