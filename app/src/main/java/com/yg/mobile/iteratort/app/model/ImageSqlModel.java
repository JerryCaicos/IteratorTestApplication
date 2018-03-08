package com.yg.mobile.iteratort.app.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by chenaxing on 2018/3/8.
 */
@DatabaseTable(tableName = TableConstants.KEY_TABLE_IMG)
public class ImageSqlModel
{
    @DatabaseField(generatedId = true,columnName = TableConstants.KEY_ID)
    public long keyId;
    @DatabaseField(canBeNull = false,columnName = TableConstants.KEY_IMG_PATH)
    public String path;
    @DatabaseField(canBeNull = false,columnName = TableConstants.KEY_IMG_TIME)
    public long lastTime;
}
