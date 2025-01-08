package com.example.rfidwriter;

public class LoadScanModel {


    private String PartNo;
    private String SEQNo;
    private String InputNo;
    private String AutoScan;
    private String ManualScan;
    private String Sachrkey;


    public LoadScanModel(String PartNo, String SEQNo, String InputNo, String AutoScan, String ManualScan, String Sachrkey) {

        this.PartNo = PartNo;
        this.SEQNo = SEQNo;
        this.InputNo = InputNo;
        this.AutoScan = AutoScan;
        this.ManualScan = ManualScan;
        this.Sachrkey = Sachrkey;
    }

    public String getSachrkey() {
        return Sachrkey;
    }

    public String getPartNo() {
        return PartNo;
    }

    public String getSEQNo() {
        return SEQNo;
    }

    public String getInputNo() {
        return InputNo;
    }

    public String AutoScan() {return AutoScan;}

    public String getManualScan() {return ManualScan;}
}
