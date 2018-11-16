package spartons.com.javapassengerapp.helpers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import spartons.com.javapassengerapp.interfaces.FirebaseDriverListener;
import spartons.com.javapassengerapp.model.Driver;

public class FirebaseEventListenerHelper implements ChildEventListener {

    private final FirebaseDriverListener firebaseDriverListener;

    public FirebaseEventListenerHelper(FirebaseDriverListener firebaseDriverListener) {
        this.firebaseDriverListener = firebaseDriverListener;
    }


    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        Driver driver = dataSnapshot.getValue(Driver.class);
        firebaseDriverListener.onDriverAdded(driver);
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        Driver driver = dataSnapshot.getValue(Driver.class);
        firebaseDriverListener.onDriverUpdated(driver);
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
        Driver driver = dataSnapshot.getValue(Driver.class);
        firebaseDriverListener.onDriverRemoved(driver);
    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
    }
}
