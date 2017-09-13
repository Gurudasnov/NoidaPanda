import React from 'react';
import ReactDOM from 'react-dom';

import {Link} from 'react-router-dom';
import {Switch,Route} from 'react-router-dom';
import Home from './home.jsx';
import About from './about.jsx';
import Contact from './contact.jsx';

class App extends React.Component 
{
   render() 
   {
      return (
	  
         <div>
            <ul>
				<Link to='/'>    Home     </Link> 
               <Link to='/about'>   About    </Link> 
              <Link to='/contact'> Contact  </Link> 
            </ul>
		         		   
		 <Switch>
		 <Route exact path='/' component={Home}/>
		 <Route path='/about' component={About}/>
		 <Route path='/contact' component={Contact}/>
		 </Switch>
        
		 </div>
		
      )
   }
}

export default App;




