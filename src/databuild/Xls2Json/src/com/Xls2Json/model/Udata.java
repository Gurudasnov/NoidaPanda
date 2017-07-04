package com.Xls2Json.model;

import java.util.List;

public class Udata {
@Override
	public String toString() {
		return "Udata [MOs=" + MOs + "]";
	}

private List<UMOs> MOs;

public List<UMOs> getMos() {
	return MOs;
}

public void setMos(List<UMOs> umoList) {
	MOs = umoList;
}

}
