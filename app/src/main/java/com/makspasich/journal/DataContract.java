package com.makspasich.journal;

import android.provider.BaseColumns;

public class DataContract {

    public DataContract() {
    }
    public static final class DataEntry implements BaseColumns {
        public static final String TABLE_NAME = "dataMissing";

        public static final String COLUMN_DATE_MISSING = "dateMissing";

        public static final String COLUMN_ID_STUDENT = "idStudent";
        public static final String COLUMN_STUDENT = "nameStudent";
        public static final String COLUMN_STATUS_MISSISNG = "statusMissing";
        public static final String COLUMN_REASON_MISSING = "reasonMissing";



    }
}

