package com.includes.Model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class Data implements Serializable {
private ArrayList<DataMOs> MOs;

public Data() {
	// TODO Auto-generated constructor stub
}
@XmlElement
public List<DataMOs> getMos() {
	return MOs;
}

@XmlElementWrapper(name = "MoList")
@XmlElement(name = "Mos")
public void setMos(ArrayList<DataMOs> mos) {
	MOs = mos;
}



}
