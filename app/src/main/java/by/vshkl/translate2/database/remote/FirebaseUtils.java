package by.vshkl.translate2.database.remote;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import by.vshkl.translate2.mvp.model.Stop;
import by.vshkl.translate2.mvp.model.Updated;
import io.reactivex.Observable;

public class FirebaseUtils {

    private static final String REF_UPDATED = "updated";
    private static final String REF_STOPS = "stops";

    public static Observable<Updated> getUpdatedTimestamp() {
        return Observable.create(emitter -> {
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference(REF_UPDATED);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Updated updated = dataSnapshot.getValue(Updated.class);
                    emitter.onNext(updated);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        });
    }

    public static Observable<List<Stop>> getAllStops() {
        return Observable.create(emitter -> {
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference(REF_STOPS);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<Stop> stopList = new ArrayList<>();
                    dataSnapshot.getChildren().forEach(stopSnapshot -> stopList.add(stopSnapshot.getValue(Stop.class)));
                    emitter.onNext(stopList);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        });
    }
}
