package dev.romdev.com.m2pad;

import java.util.List;

/**
 * Created by LCL on 2017/11/16.
 */

public class UpdateAPPResponse {


    public String code;
    public boolean flag;
    public String msg;
    public int page;
    public int pageSize;
    public int total;
    public int totalNum;
    public List<DataBean> data;

    public static class DataBean {
        public VersionRecordBean versionRecord;

        public static class VersionRecordBean {
            public String downloadUrl;
            public String forcedUpgrade;
            public String moduleType;
            public String releaseDate;
            public String statusCd;
            public String uploadDetail;
            public String uploadTitle;
            public String versionCode;
            public int versionId;
            public String versionSize;
        }
    }
}
