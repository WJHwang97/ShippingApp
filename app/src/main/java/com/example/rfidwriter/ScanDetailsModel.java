package com.example.rfidwriter;

public class ScanDetailsModel {

    private String LOTNO;
    private String EDIPO;
    private String QTY;


    public ScanDetailsModel(String LOTNO, String EDIPO, String QTY) {
        this.LOTNO = LOTNO;
        this.EDIPO = EDIPO;
        this.QTY = QTY;
    }

    public String getLOTNO() {
        return LOTNO;
    }

    public String getEDIPO() {
        return EDIPO;
    }

    public String getQTY() {
        return QTY;
    }


}
