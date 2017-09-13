import React from 'react';
import ReactDOM from 'react-dom';
import App from './App.jsx';
//import {Router, Route, Link, browserHistory, IndexRoute} from 'react-router';
//import {Home,About,Contact} from './App.jsx';
import {BrowserRouter} from 'react-router-dom';


//ReactDOM.render(<App />, document.getElementById('app'));

ReactDOM.render((
<BrowserRouter>
   <App/>
   </BrowserRouter>
	
), document.getElementById('app'));

//ReactDOM.render(<App5 />, document.getElementById('container'));
//ReactDOM.render(<App5 />, document.querySelector('.container'));