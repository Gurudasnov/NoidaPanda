

import React, { Component } from 'react';
import FilterList  from './FilterList';


 
class App extends Component {
  render() {
    return (
        <div>
           <FilterList words={this.props.words}/>
            </div>
      
    );
  }
}
export default App;
