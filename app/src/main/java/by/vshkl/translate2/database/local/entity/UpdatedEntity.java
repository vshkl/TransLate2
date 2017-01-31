package by.vshkl.translate2.database.local.entity;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Updated")
public class UpdatedEntity extends Model {

    @Column(name = "Updated", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public long undatedTimestamp;
}
