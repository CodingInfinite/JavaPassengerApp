package spartons.com.javapassengerapp.app;

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class MyCustomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
}
