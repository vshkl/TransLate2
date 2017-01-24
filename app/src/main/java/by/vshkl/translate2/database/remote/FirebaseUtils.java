package by.vshkl.translate2.database.remote;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import by.vshkl.translate2.mvp.model.Stop;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class FirebaseUtils {

    public static Observable<List<Stop>> getAllStops() {
        return Observable.create(new ObservableOnSubscribe<List<Stop>>() {
            @Override
            public void subscribe(final ObservableEmitter<List<Stop>> emitter) throws Exception {
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference("stops");
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Stop> stopList = new ArrayList<>();
                        for (DataSnapshot stopSnapshot : dataSnapshot.getChildren()) {
                            stopList.add(stopSnapshot.getValue(Stop.class));
                        }
                        emitter.onNext(stopList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}
