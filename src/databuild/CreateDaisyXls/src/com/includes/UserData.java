package com.includes;

public class UserData {
	
public String[] moNames;

public String[] setDummyValuesToMO()
{
	moNames = new String[9];
	moNames[0] = "RMOD";
	moNames[1] = "SMOD";
	moNames[2] = "SitePar";
	moNames[3] = "CellPar";
	moNames[4] = "RET";
	moNames[5] = "MHA";
	moNames[6] = "IdleIntra";
	moNames[7] = "LNCEL";
	moNames[8] = "HOInterLTE";
	return moNames;
}
}
