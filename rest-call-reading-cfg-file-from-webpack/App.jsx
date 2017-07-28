import React, { Component } from 'react';
import Request from 'react-http-request';
 

 
class App extends React.Component {
	constructor() {
  	super();
 		 this.state={items:[]};
  }
  componentDidMount(){
	  var Config = require('Config');
	 // console.log("Vijay1 "+fetch(Config.serverUrl + '/details/'))
	  //console.log("Vijay" +Config);
		fetch(Config.serverUrl + '/details/')
		.then(result=>result.json())
    .then(items=>this.setState({items}))
	//console.log(items +"Vijay");
  }
  render() {
  	return(
    	<ol>
          {this.state.items.length ?
          	this.state.items.map(item=><li key={item.id}>{item.id + " " +item.title + " " + item.singer}</li>) 
				 
            : <li>Loading...</li>
          }
      </ol>
	  
   )
  }
}

export default App;