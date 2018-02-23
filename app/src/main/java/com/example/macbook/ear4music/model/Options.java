package com.example.macbook.ear4music.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by au185034 on 21/02/2018.
 */

@Entity
public class Options {
    @NotNull
    long version = 0L;

    @Generated(hash = 1421421304)
    public Options(long version) {
        this.version = version;
    }

    @Generated(hash = 2110522450)
    public Options() {
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}
