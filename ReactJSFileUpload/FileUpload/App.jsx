import React from 'react';
import Request from 'react-http-request';
import Fetch from "isomorphic-fetch";
import Dropzone from 'react-dropzone';

var Promise = require('es6-promise').Promise;

// var Promise = require("es6-promise")
 Promise.polyfill();
// var axios = require("axios");

var apiBaseUrl = "http://localhost:8080/FileUploaderFromReact/getFileFromReact";
var request = require('superagent');

class App extends React.Component {

  constructor() {
    super()
    this.state = { files: [] }
  }
 
 handleClick(event){
  console.log("handleClick",event);
  var self = this;
  var formData = new FormData();
  if(this.state.files.length>0){
    var filesArray = this.state.files;
     for(var i in filesArray){
         console.log(filesArray[i]);
         formData.append(this.state.files+i, filesArray[i]);
    }
    	fetch("http://localhost:8080/FileUploaderFromReact/getFileFromReact", {
      mode: 'no-cors',
      method: "POST",
      body: formData
    }).then(function (res) {
      if (res.ok) {
        alert("File Uploaded Successfully! ");
      } else if (res.status == 401) {
        alert("Oops! ");
      }
    }, function (e) {
      alert("Error submitting form!");
    });
  }
}
  onDrop(files) {
    this.setState({
      files
    });
  }

  render() {
    return (
      <section>
        <div className="dropzone">
          <Dropzone onDrop={this.onDrop.bind(this)}>
            <p>Try dropping some files here, or click to select files to upload.</p>
          </Dropzone>
          <h1>
           
               <button onClick={(event)=>this.handleClick(event)}> Upload</button>
          </h1>
         </div>
        <aside>
          <h2>Dropped files</h2>
          <ul>
            {
              this.state.files.map(f => <li>{f.name} - {f.size} bytes</li>)
            }
          </ul>
        </aside>
      </section>
    );
  }
}
export default App;

