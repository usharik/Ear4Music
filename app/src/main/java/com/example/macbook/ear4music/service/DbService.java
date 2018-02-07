package com.example.macbook.ear4music.service;

import android.app.Application;
import android.database.Cursor;

import com.example.macbook.ear4music.model.DaoMaster;
import com.example.macbook.ear4music.model.DaoSession;
import com.example.macbook.ear4music.model.Options;
import com.example.macbook.ear4music.model.Task;

import org.greenrobot.greendao.database.Database;

import java.util.List;

import static com.example.macbook.ear4music.service.InitData.initData;

/**
 * Created by au185034 on 23/02/2018.
 */

public class DbService {
    private DaoSession daoSession;
    private DaoMaster.DevOpenHelper helper;

    public DbService(Application application) {
        helper = new DaoMaster.DevOpenHelper(application, "ear4-music-db");
        Database db = helper.getWritableDb();
        DaoMaster.createAllTables(db, true);
        daoSession = new DaoMaster(db).newSession();
        List<Options> options = daoSession.getOptionsDao().queryBuilder().limit(1).list();
        if (options.size() != 1) {
            DaoMaster.dropAllTables(db, true);
            DaoMaster.createAllTables(db, false);
            initData(daoSession);
        } else if (options.get(0).getVersion() < InitData.dbVersion) {
            DaoMaster.dropAllTables(db, true);
            DaoMaster.createAllTables(db, false);
            initData(daoSession);
        }
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public void updateTaskDonePercent(Task task) {
        Database db = helper.getWritableDb();
        Cursor cursor1 = db.rawQuery("select count(*) from SUB_TASK where TASK_ID = ?", new String[]{ Long.toString(task.getId()) });
        Cursor cursor2 = db.rawQuery("select count(*) from SUB_TASK where TASK_ID = ? and CORRECT_ANSWER_PERCENT != 0", new String[]{ Long.toString(task.getId()) });
        if (cursor1.moveToNext() && cursor2.moveToNext()) {
            int allCnt = cursor1.getInt(0);
            int doneCnt = cursor2.getInt(0);
            task.setDonePercent(doneCnt * 100 / allCnt);
            daoSession.getTaskDao().update(task);
        }
    }
}
