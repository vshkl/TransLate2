package by.vshkl.translate2.database.local.entity;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "StopBookmark", id = "_id")
public class StopBookmarkEntity extends Model {

    @Column(name = "Id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public int id;
    @Column(name = "Name")
    public String name;
}
