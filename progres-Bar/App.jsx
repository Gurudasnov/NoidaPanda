import React from 'react';
class App extends React.Component {
	 constructor(props) {
    super(props);
    this.state = {isToggleOn: true};

    // This binding is necessary to make `this` work in the callback
    this.move = this.move.bind(this);
  }

  move() {
  var elem = document.getElementById("myBar");   
  console.log(elem);
  var width = 1;
  var id = setInterval(frame, 10);
  function frame() {
    if (width >= 100) {
      clearInterval(id);
    } else {
      width++; 
      elem.style.width = width + '%'; 
    }
  }
}
render() {
	
return (
<div>
<div className="w3-container">
  <h2>My Progress Bar</h2>

  <div className="w3-light-grey">
    <div id="myBar" className="w3-green myclass"></div>
  </div>
 
   <br>
   </br>
  <button className="w3-button w3-green" onClick={this.move}>Show-progress</button> 
</div>
</div>
);
}
}
export default App;