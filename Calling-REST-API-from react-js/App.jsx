import React, { Component } from 'react';
import Request from 'react-http-request';
 

 
class App extends React.Component {
	constructor() {
  	super();
 		 this.state={items:[]};
  }
  componentDidMount(){
  	fetch(`http://localhost:8100/NPO-restCall/rest/json/data/details`)
 		.then(result=>result.json())
    .then(items=>this.setState({items}))
	//console.lpg(items +"Vijay");
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