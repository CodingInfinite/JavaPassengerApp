package spartons.com.javapassengerapp.interfaces;


import spartons.com.javapassengerapp.model.Driver;

public interface FirebaseDriverListener {

    void onDriverAdded(Driver driver);

    void onDriverRemoved(Driver driver);

    void onDriverUpdated(Driver driver);

}
