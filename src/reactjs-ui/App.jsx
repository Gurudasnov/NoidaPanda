import React from 'react';

class App extends React.Component {
   render() {
      return (
         <div>
           <link rel='stylesheet' type='text/css' href='style.css'/>
            <ul className='unordered-list'>
              <li className='list'><a className='list-link' href="#home">Home</a></li>
              <li className='list'><a className='list-link' href="#news">News</a></li>
              <li className='list'><a className='list-link' href="#contact">Contact</a></li>
              <li className='list'><a className='list-link' href="#about">About</a></li>
            </ul>
         </div>
      );
   }
}

export default App;